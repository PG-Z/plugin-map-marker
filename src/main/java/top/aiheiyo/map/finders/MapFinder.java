package top.aiheiyo.map.finders;

import reactor.core.publisher.Flux;
import top.aiheiyo.map.vo.MapGroupVo;
import top.aiheiyo.map.vo.MapVo;

/**
 * Description: Finder
 *
 * @author : evan  Date: 2024/4/18
 */
public interface MapFinder {

    Flux<MapVo> listBy(String group);

    Flux<MapGroupVo> groupBy();
}
