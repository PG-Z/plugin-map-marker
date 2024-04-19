package top.aiheiyo.map.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import run.halo.app.extension.ReactiveExtensionClient;
import top.aiheiyo.map.Map;
import top.aiheiyo.map.MapGroup;
import top.aiheiyo.map.service.IMapService;
import top.aiheiyo.map.vo.MapFeature;
import top.aiheiyo.map.vo.MapVo;

import static top.aiheiyo.map.finders.impl.MapFinderImpl.defaultGroupComparator;
import static top.aiheiyo.map.finders.impl.MapFinderImpl.defaultMapComparator;

/**
 * Description: map service 实现
 *
 * @author : evan  Date: 2024/4/19
 */
@Service
@RequiredArgsConstructor
public class MapServiceImpl implements IMapService {

    private final ReactiveExtensionClient client;

    @Override
    public Flux<MapFeature.FeaturesDTO> mapFeature() {

        Flux<Map> mapFlux = client.list(Map.class, null, defaultMapComparator());

        return groupOfFeature()
                .concatMap(group -> mapFlux
                        .filter(map -> StringUtils.equals(map.groupName(),
                                group.getGroupName())
                        )
                        .map(MapVo::from)
                        .collectList()
                        .map(group::withProperties)
                        .defaultIfEmpty(group)
                );
    }

    Flux<MapFeature.FeaturesDTO> groupOfFeature() {
        return client.list(MapGroup.class, null, defaultGroupComparator())
                .map(MapFeature.FeaturesDTO::from);
    }

}
