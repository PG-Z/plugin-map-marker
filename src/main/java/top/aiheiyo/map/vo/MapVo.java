package top.aiheiyo.map.vo;

import lombok.Builder;
import lombok.Value;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.MetadataOperator;
import run.halo.app.theme.finders.vo.ExtensionVoOperator;
import top.aiheiyo.map.Map;

/**
 * Description: map vo
 *
 * @author : evan  Date: 2024/4/18
 */
@Value
@Builder
public class MapVo implements ExtensionVoOperator {

    MetadataOperator metadata;

    Map.MapSpec spec;

    public static MapVo from(Map map) {
        return MapVo.builder()
                .metadata(map.getMetadata())
                .spec(map.getSpec())
                .build();
    }

    public static MapVo empty() {
        return MapVo.builder()
                .metadata(new Metadata())
                .spec(new Map.MapSpec())
                .build();

    }
}
