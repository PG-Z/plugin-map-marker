package top.aiheiyo.map.api;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static run.halo.app.extension.router.QueryParamBuildUtil.buildParametersFromType;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.plugin.ReactiveSettingFetcher;
import top.aiheiyo.map.Map;
import top.aiheiyo.map.finders.MapFinder;
import top.aiheiyo.map.vo.MapConfig;
import top.aiheiyo.map.vo.MapGroupVo;
import top.aiheiyo.map.vo.MapQuery;

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
        return client.list(Map.class, query.toPredicate(),
                query.toComparator(),
                query.getPage(),
                query.getSize()
        );
    }

    private Mono<List<MapGroupVo>> mapGroups() {
        return mapFinder.groupBy()
                .collectList();
    }

    private Mono<String> configs() {
        return this.settingFetcher.get("mapbox").map(MapConfig::ofStr);
/*
        return Mono.just("""
                {
                    "api": "https://slykiten.com/wp-json/",
                    "nonce": "4fcad5daf3",
                    "launguagePACK": "https://cdn.aifeel.top/home/js/mapbox-gl-rtl-text.js",
                    "zoom": "1",
                    "maxZoom": "7",
                    "minZoom": "1",
                    "limit": "9",
                    "center": [
                        "128.14",
                        "33.87"
                    ],
                    "token": "pk.eyJ1IjoiZmF0ZXNpbmdlciIsImEiOiJjanc4bXFocG8wMXM1NDNxanB0MG5sa2ZpIn0.HqA5Q8Y4Jp1s3_TQ-sqVoQ"
                }""");*/
    }
}
