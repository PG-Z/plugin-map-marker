package top.aiheiyo.map.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Post;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.ReactiveExtensionClient;
import top.aiheiyo.map.Map;
import top.aiheiyo.map.MapGroup;
import top.aiheiyo.map.service.IMapService;
import top.aiheiyo.map.service.helper.MarkerMapHelper;
import top.aiheiyo.map.vo.*;

import java.util.List;
import java.util.Optional;

import static top.aiheiyo.map.service.helper.MarkerMapHelper.defaultGroupComparator;
import static top.aiheiyo.map.service.helper.MarkerMapHelper.defaultMapComparator;

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
    private final MarkerMapHelper markerMapHelper;

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
                ).filter(MapFeature.FeaturesDTO::valid);
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

    @Async
    @Override
    public void syncMapSpec() {
        try {
            Flux<Map> mapFlux = client.list(Map.class, null, defaultMapComparator());
            mapFlux.flatMap(map -> client.get(Post.class, map.getSpec().getPost())
                            .map(post -> {
                                map.getSpec().setDisplayName(post.getSpec().getTitle());
                                map.getSpec().setUrl(post.getStatus().getPermalink());
                                return map;
                            }))
                    .subscribe(client::update);
        } catch (Exception ignored) {
        }
    }

    @Override
    public Mono<MapNearVO> nearMap(String post, Integer page, Integer size) {
        if (StringUtils.isBlank(post)) {
            return Mono.empty();
        }

        page = Optional.ofNullable(page).orElse(1);
        size = Optional.ofNullable(size).orElse(3);

        Integer finalPage = page;
        Integer finalSize = size;
        return client.list(Map.class, map -> StringUtils.equals(post, map.getSpec().getPost()), defaultMapComparator())
                .collectList()
                .flatMap(list -> {
                    if (list.isEmpty()) {
                        return Mono.empty();
                    }
                    String groupName = list.get(0).groupName();

                    Mono<MapGroup> mapGroupMono = client.get(MapGroup.class, groupName);

                    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
                    queryParams.add("groupName", groupName);
                    queryParams.add("notContainPost", post);
                    queryParams.add("page", String.valueOf(finalPage));
                    queryParams.add("size", String.valueOf(finalSize));
                    return markerMapHelper.listMap(new MapQuery(queryParams))
                            .flatMap(mapList -> Flux.fromStream(mapList.get())
                                    .concatMap(this::getRelationPost)
                                    .collectList()
                                    .map(momentVos -> new ListResult<>(mapList.getPage(), mapList.getSize(),
                                            mapList.getTotal(), momentVos)
                                    )
                            )
                            .defaultIfEmpty(new ListResult<>(finalPage, finalSize, 0L, List.of()))
                            .zipWith(mapGroupMono)
                            .map(tuple -> {
                                ListResult<Post> listResult = tuple.getT1();
                                MapGroup mapGroup = tuple.getT2();
                                return MapNearVO.of(mapGroup.getSpec().getDisplayName(), listResult);
                            });
                });
    }

    private Mono<Post> getRelationPost(Map map) {
        return client.get(Post.class, map.getSpec().getPost());
    }
}
