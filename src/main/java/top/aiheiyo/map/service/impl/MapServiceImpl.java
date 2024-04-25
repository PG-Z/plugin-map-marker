package top.aiheiyo.map.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Post;
import run.halo.app.extension.ReactiveExtensionClient;
import top.aiheiyo.map.Map;
import top.aiheiyo.map.MapGroup;
import top.aiheiyo.map.service.IMapService;
import top.aiheiyo.map.vo.MapFeature;
import top.aiheiyo.map.vo.MapVo;

import java.util.List;

import static top.aiheiyo.map.finders.impl.MapFinderImpl.defaultGroupComparator;
import static top.aiheiyo.map.finders.impl.MapFinderImpl.defaultMapComparator;

/**
 * Description: map service 实现
 *
 * @author : evan  Date: 2024/4/19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MapServiceImpl implements IMapService {

    private final ReactiveExtensionClient client;

    @Override
    public Flux<MapFeature.FeaturesDTO> mapFeature() {
        Flux<Map> mapFlux = client.list(Map.class, null, defaultMapComparator());

        return groupOfFeature()
                .flatMap(group -> mapFlux
                        .filter(map -> StringUtils.equals(map.groupName(), group.getGroupName()))
                        .map(MapVo::from)
                        .collectList()
                        .flatMap((maps) -> loadData(group, maps))
                        .defaultIfEmpty(group)
                );
    }

    private Mono<MapFeature.FeaturesDTO> loadData(MapFeature.FeaturesDTO group, List<MapVo> maps) {
        List<String> postNames = maps.stream()
                .map(map -> map.getSpec().getPost())
                .filter(StringUtils::isNotBlank)
                .distinct()
                .toList();

        return client.list(Post.class, post -> postNames.contains(post.getMetadata().getName()), null)
                .collectList()
                .map(posts -> group.propertiesWithPost(posts, maps))
                .thenReturn(group);
    }

    Flux<MapFeature.FeaturesDTO> groupOfFeature() {
        return client.list(MapGroup.class, null, defaultGroupComparator())
                .map(MapFeature.FeaturesDTO::from);
    }

}
