package top.aiheiyo.map.api;

import lombok.AllArgsConstructor;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.endpoint.CustomEndpoint;
import top.aiheiyo.map.MapGroup;
import top.aiheiyo.map.service.IMapService;
import top.aiheiyo.map.vo.MapFeature;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;

/**
 * Description: A custom endpoint for {@link MapGroup}.
 *
 * @author : evan  Date: 2024/4/19
 */
@Component
@AllArgsConstructor
public class MapEndpoint implements CustomEndpoint {
    private final String tag = "api.plugin.aiheiyo.top/v1alpha1/Map";

    private final IMapService mapService;

    @Override
    public RouterFunction<ServerResponse> endpoint() {
        return SpringdocRouteBuilder.route()
                .GET("v1/geoJson", this::mapFeature,
                        builder -> builder.operationId("MapFeature")
                                .description("获取map marker data")
                                .tag(tag)
                                .response(responseBuilder()
                                        .implementation(MapFeature.class))
                )
                .build();
    }

    private Mono<ServerResponse> mapFeature(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue(mapService.mapFeature());
    }
}
