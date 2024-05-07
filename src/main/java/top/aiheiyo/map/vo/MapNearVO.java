package top.aiheiyo.map.vo;

import lombok.Data;
import run.halo.app.core.extension.content.Post;
import run.halo.app.extension.ListResult;

import java.io.Serializable;

/**
 * Description:
 *
 * @author : evan  Date: 2024/5/7
 */
@Data
public class MapNearVO implements Serializable {

    private String local;
    private ListResult<Post> result;

    public static MapNearVO of(String local, ListResult<Post> result) {
        MapNearVO of = of();
        of.setLocal(local);
        of.setResult(result);
        return of;
    }
    public static MapNearVO of() {
       return new MapNearVO();
    }
}
