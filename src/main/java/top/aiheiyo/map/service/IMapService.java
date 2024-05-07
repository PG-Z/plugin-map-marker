package top.aiheiyo.map.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.aiheiyo.map.vo.MapFeature;
import top.aiheiyo.map.vo.MapNearVO;

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

    /**
     * Description: 获取map marker data
     *
     * @author : evan  Date: 2024/4/19
     */
    Mono<MapNearVO> nearMap(String post, Integer page, Integer size);

    /**
     * Description: syncMapSpec
     *
     * @author : evan  Date: 2024/4/25
     */
    void syncMapSpec();
}
