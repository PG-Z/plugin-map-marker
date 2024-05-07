package top.aiheiyo.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.CollectionUtils;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

import java.util.List;

/**
 * Description: map
 *
 * @author : evan  Date: 2024/4/18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@GVK(group = "map.aiheiyo.top", version = "v1alpha1",
        kind = "Map", plural = "maps", singular = "map")
public class Map extends AbstractExtension {

    private MapSpec spec;

    @Data
    public static class MapSpec {
        private String url;

        private String displayName;

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private String post;

        private String logo;

        private String description;

        private Integer priority;

        @Schema(description = "map marker name")
        private String groupName;
    }

    public String groupName() {
        return this.getSpec().getGroupName();
    }

    public static String groupName(List<Map> maps) {
        if (CollectionUtils.isEmpty(maps)) {
            return null;
        }
        return maps.get(0).getSpec().getGroupName();
    }
}
