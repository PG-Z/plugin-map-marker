package top.aiheiyo.map.api;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static run.halo.app.extension.router.QueryParamBuildUtil.buildParametersFromType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Post;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.utils.JsonUtils;
import run.halo.app.plugin.ReactiveSettingFetcher;
import top.aiheiyo.map.Map;
import top.aiheiyo.map.finders.MapFinder;
import top.aiheiyo.map.vo.MapConfig;
import top.aiheiyo.map.vo.MapGroupVo;
import top.aiheiyo.map.vo.MapQuery;

/**
 * Description: Router
 *
 * @author : evan  Date: 2024/4/25
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MapRouter {

    private final MapFinder mapFinder;
    private final ReactiveExtensionClient client;
    private final ReactiveSettingFetcher settingFetcher;

    private final String tag = "api.plugin.aiheiyo.top/v1alpha1/Link";

    @Bean
    RouterFunction<ServerResponse> mapTemplateRoute() {
        return route(GET("/maps"),
                request -> ServerResponse.ok()
                        .render("maps", java.util.Map.of("mapConfigs", configs())));
    }

    @Bean
    RouterFunction<ServerResponse> mapRoute() {
        return SpringdocRouteBuilder.route()
                .nest(RequestPredicates.path("/apis/api.plugin.aiheiyo.top/v1alpha1/plugins/PluginMaps"),
                        this::nested,
                        builder -> builder.operationId("PluginMapsEndpoints")
                                .description("Plugin maps Endpoints").tag(tag)
                )
                .build();
    }

    RouterFunction<ServerResponse> nested() {
        return SpringdocRouteBuilder.route()
                .GET("/maps", this::listMapByGroup,
                        builder -> {
                            builder.operationId("listMaps")
                                    .description("Lists map by query parameters")
                                    .tag(tag);
                            buildParametersFromType(builder, MapQuery.class);
                        }
                ).build();
    }

    Mono<ServerResponse> listMapByGroup(ServerRequest request) {
        MapQuery query = new MapQuery(request.exchange());
        return listMap(query)
                .flatMap(maps -> ServerResponse.ok().bodyValue(maps));
    }

    private Mono<ListResult<Map>> listMap(MapQuery query) {
        log.info("========= listMap query: {}", JsonUtils.objectToJson(query));
        return client.list(Map.class, query.toPredicate(), query.toComparator(), query.getPage(), query.getSize())
                .flatMap(result -> {
                    List<Map> maps = result.getItems();
                    Flux<Post> postFlux = Flux.fromIterable(maps)
                            .map(map -> map.getSpec().getPost())
                            .distinct()
                            .flatMap(postName -> client.list(Post.class, post -> post.getMetadata().getName().equals(postName), null).next());

                    return postFlux.collectList()
                            .map(posts -> {
                                java.util.Map<String, Post> postMap = posts.stream().collect(Collectors.toMap(post -> post.getMetadata().getName(), post -> post));
                                for (Map map : maps) {
                                    Post post = postMap.get(map.getSpec().getPost());
                                    if (Objects.nonNull(post)) {
                                        map.getSpec().setDisplayName(post.getSpec().getTitle());
                                        map.getSpec().setUrl(post.getStatus().getPermalink());
                                    }
                                }
                                return result;
                            });
                });
    }


    private Mono<List<MapGroupVo>> mapGroups() {
        return mapFinder.groupBy()
                .collectList();
    }

    private Mono<String> configs() {
        return this.settingFetcher.get("mapbox").map(MapConfig::ofStr);
    }
}
