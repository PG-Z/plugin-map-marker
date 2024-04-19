package top.aiheiyo.map.service;

import reactor.core.publisher.Flux;
import top.aiheiyo.map.vo.MapFeature;

/**
 * Description: map service
 *
 * @author : evan  Date: 2024/4/19
 */
public interface IMapService {

    /**
     * Description: 获取map marker data
     *
     * @author : evan  Date: 2024/4/19
     */
    Flux<MapFeature.FeaturesDTO> mapFeature();

}
