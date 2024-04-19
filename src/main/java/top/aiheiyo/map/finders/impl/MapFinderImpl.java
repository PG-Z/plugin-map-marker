package top.aiheiyo.map.finders.impl;

import java.time.Instant;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.comparator.Comparators;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.theme.finders.Finder;
import top.aiheiyo.map.Map;
import top.aiheiyo.map.MapGroup;
import top.aiheiyo.map.finders.MapFinder;
import top.aiheiyo.map.vo.MapGroupVo;
import top.aiheiyo.map.vo.MapVo;

/**
 * Description: map Finder
 *
 * @author : evan  Date: 2024/4/18
 */
@Finder("mapMarkerFinder")
public class MapFinderImpl implements MapFinder {
    private final ReactiveExtensionClient client;

    public MapFinderImpl(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public Flux<MapVo> listBy(String groupName) {
        return listAll(map -> StringUtils.equals(map.getSpec().getGroupName(), groupName))
                .map(MapVo::from);
    }

    @Override
    public Flux<MapGroupVo> groupBy() {
        Flux<Map> mapFlux = listAll(null);
        return listAllGroups()
                .concatMap(group -> mapFlux
                        .filter(map -> StringUtils.equals(map.getSpec().getGroupName(),
                                group.getMetadata().getName())
                        )
                        .map(MapVo::from)
                        .collectList()
                        .map(group::withLinks)
                        .defaultIfEmpty(group)
                )
                .mergeWith(Mono.defer(() -> ungrouped()
                        .map(MapGroupVo::from)
                        .flatMap(mapGroup -> mapFlux.filter(
                                        map -> StringUtils.isBlank(map.getSpec().getGroupName()))
                                .map(MapVo::from)
                                .collectList()
                                .map(mapGroup::withLinks)
                                .defaultIfEmpty(mapGroup)
                        ))
                );
    }

    Mono<MapGroup> ungrouped() {
        MapGroup mapGroup = new MapGroup();
        mapGroup.setMetadata(new Metadata());
        mapGroup.getMetadata().setName("ungrouped");
        mapGroup.setSpec(new MapGroup.MapGroupSpec());
        mapGroup.getSpec().setDisplayName("");
        mapGroup.getSpec().setCoordinates("");
        mapGroup.getSpec().setPriority(0);
        return Mono.just(mapGroup);
    }

    Flux<Map> listAll(@Nullable Predicate<Map> predicate) {
        return client.list(Map.class, predicate, defaultMapComparator());
    }

    Flux<MapGroupVo> listAllGroups() {
        return client.list(MapGroup.class, null, defaultGroupComparator())
                .map(MapGroupVo::from);
    }

    public static Comparator<MapGroup> defaultGroupComparator() {
        Function<MapGroup, Integer> priority = group -> group.getSpec().getPriority();
        Function<MapGroup, Instant> createTime =
                group -> group.getMetadata().getCreationTimestamp();
        Function<MapGroup, String> name = group -> group.getMetadata().getName();
        return Comparator.comparing(priority, Comparators.nullsLow())
                .thenComparing(createTime)
                .thenComparing(name);
    }

    public static Comparator<Map> defaultMapComparator() {
        Function<Map, Integer> priority = map -> map.getSpec().getPriority();
        Function<Map, Instant> createTime = map -> map.getMetadata().getCreationTimestamp();
        Function<Map, String> name = map -> map.getMetadata().getName();
        return Comparator.comparing(priority, Comparators.nullsLow())
                .thenComparing(createTime)
                .thenComparing(name);
    }
}
