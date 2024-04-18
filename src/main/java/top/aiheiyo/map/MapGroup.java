package top.aiheiyo.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

/**
 * Description: mapGroup
 *
 * @author : evan  Date: 2024/4/18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@GVK(group = "map.aiheiyo.top", version = "v1alpha1", kind = "MapGroup", plural = "mapgroups", singular = "mapgroup")
public class MapGroup extends AbstractExtension {

    private MapGroupSpec spec;

    @Data
    public static class MapGroupSpec {
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        private String displayName;

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "坐标系, 使用英文逗号分隔. lat,lon.")
        private String coordinates;

        private Integer priority;

    }
}
