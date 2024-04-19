package top.aiheiyo.map.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import run.halo.app.infra.utils.JsonUtils;

import java.util.List;

/**
 * Description: Config
 *
 * @author : evan  Date: 2024/4/19
 */
@NoArgsConstructor
@Data
public class MapConfig {

    @JsonProperty("api")
    private String api;

    @JsonProperty("launguagePACK")
    private String launguagePACK;

    @JsonProperty("zoom")
    private String zoom;

    @JsonProperty("maxZoom")
    private String maxZoom;

    @JsonProperty("minZoom")
    private String minZoom;

    @JsonProperty("limit")
    private String limit;

    @JsonProperty("center")

    private List<String> center;

    @JsonProperty("token")
    private String token;

    public static MapConfig of() {
        return new MapConfig();
    }

    public static MapConfig of(JsonNode jsonNode) {
        MapConfig of = of();
        of.setApi(jsonNode.get("api").asText());
        of.setLimit(jsonNode.get("limit").asText());
        of.setZoom(jsonNode.get("zoom").asText());
        of.setMaxZoom(jsonNode.get("maxZoom").asText());
        of.setMinZoom(jsonNode.get("minZoom").asText());
        of.setToken(jsonNode.get("token").asText());
        of.setLaunguagePACK(jsonNode.get("launguagePACK").asText());
        of.setCenter(List.of(StringUtils.split(jsonNode.get("center").asText(), ',')));
        return of;
    }

    public static String ofStr(JsonNode jsonNode) {
        return JsonUtils.objectToJson(of(jsonNode));
    }
}
