package top.aiheiyo.map.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ApiVersion;
import top.aiheiyo.map.service.IMapService;
import top.aiheiyo.map.vo.MapFeature;

/**
 * Description: api
 *
 * @author : evan  Date: 2024/4/19
 */
@ApiVersion("v1alpha1")
@RequestMapping("/map/v1")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MapApi {

    private final IMapService mapService;

    @GetMapping("/mapFeature")
    public Mono<MapFeature> mapFeature() {
        return mapService.mapFeature()
                .collectList()
                .map(MapFeature::of);
    }

}
