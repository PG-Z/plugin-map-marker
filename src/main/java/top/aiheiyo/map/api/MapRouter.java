package top.aiheiyo.map.api;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static run.halo.app.extension.router.QueryParamBuildUtil.buildParametersFromType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ReactiveSettingFetcher;
import top.aiheiyo.map.service.helper.MarkerMapHelper;
import top.aiheiyo.map.vo.MapConfig;
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

    private final MarkerMapHelper markerMapHelper;
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
        return markerMapHelper.listMap(query)
                .flatMap(maps -> ServerResponse.ok().bodyValue(maps));
    }

    private Mono<String> configs() {
        return this.settingFetcher.get("mapbox").map(MapConfig::ofStr);
    }
}
