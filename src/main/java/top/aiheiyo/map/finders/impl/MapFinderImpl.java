package top.aiheiyo.map.finders.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.theme.finders.Finder;
import top.aiheiyo.map.finders.MapFinder;
import top.aiheiyo.map.service.IMapService;
import top.aiheiyo.map.service.helper.MarkerMapHelper;
import top.aiheiyo.map.vo.MapNearVO;
import top.aiheiyo.map.vo.MapVo;

/**
 * Description: map Finder
 *
 * @author : evan  Date: 2024/4/18
 */

@RequiredArgsConstructor
@Finder("mapMarkerFinder")
public class MapFinderImpl implements MapFinder {

    private final IMapService mapService;
    private final MarkerMapHelper markerMapHelper;

    @Override
    public Flux<MapVo> listBy(String groupName) {
        return markerMapHelper.listAll(map -> StringUtils.equals(map.getSpec().getGroupName(), groupName))
                .map(MapVo::from);
    }

    @Override
    public Mono<MapNearVO> nearMap(String post, Integer page, Integer size) {
        return mapService.nearMap(post, page, size);
    }
}
