package top.aiheiyo.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

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
        @Schema(required = true)
        private String url;

        @Schema(required = true)
        private String displayName;

        private String post;

        private String logo;

        private String description;

        private Integer priority;

        private String groupName;
    }

    public String groupName() {
        return this.getSpec().getGroupName();
    }
}
