package top.aiheiyo.map.finders;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.aiheiyo.map.vo.MapNearVO;
import top.aiheiyo.map.vo.MapVo;

/**
 * Description: Finder
 *
 * @author : evan  Date: 2024/4/18
 */
public interface MapFinder {

    Flux<MapVo> listBy(String group);

    /**
     * Description: 获取map marker data
     *
     * @author : evan  Date: 2024/4/19
     */
    Mono<MapNearVO> nearMap(String post, Integer page, Integer size);

}
