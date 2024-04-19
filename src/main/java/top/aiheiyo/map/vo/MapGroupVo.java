package top.aiheiyo.map.vo;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import run.halo.app.extension.MetadataOperator;
import run.halo.app.theme.finders.vo.ExtensionVoOperator;
import top.aiheiyo.map.MapGroup;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Description: mapGroup vo
 *
 * @author : evan  Date: 2024/4/18
 */
@Value
@Builder
public class MapGroupVo implements ExtensionVoOperator {

    MetadataOperator metadata;

    MapGroup.MapGroupSpec spec;

    @With
    List<MapVo> links;

    public static MapGroupVo from(MapGroup mapGroup) {
        return MapGroupVo.builder()
            .metadata(mapGroup.getMetadata())
            .spec(mapGroup.getSpec())
            .links(List.of())
            .build();
    }

    public List<MapVo> getLinks() {
        return Optional.ofNullable(links).orElse(Collections.emptyList());
    }
}
