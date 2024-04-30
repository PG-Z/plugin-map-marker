package top.aiheiyo.map.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.MapUtils;
import run.halo.app.core.extension.content.Post;
import top.aiheiyo.map.MapGroup;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author : evan  Date: 2024/4/19
 */
@Slf4j
@NoArgsConstructor
@Data
public class MapFeature implements Serializable {

    private String type;
    private List<FeaturesDTO> features;

    @NoArgsConstructor
    @Data
    public static class FeaturesDTO {
        private String type;
        @JsonIgnore
        private String groupName;
        @JsonIgnore
        private List<String> metas;
        private GeometryDTO geometry;
        private PropertiesDTO properties;

        @NoArgsConstructor
        @Data
        public static class GeometryDTO {
            private String type;
            private List<Double> coordinates;

            public static GeometryDTO of() {
                return new GeometryDTO();
            }

            public static GeometryDTO of(List<Double> coordinates) {
                GeometryDTO of = of();
                of.setCoordinates(coordinates);
                return of;
            }

            public String getType() {
                return Optional.ofNullable(type).orElse("Point");
            }
        }

        public boolean valid() {

            if (Objects.isNull(this.getProperties())) {
                return false;
            }

            boolean valid = !CollectionUtils.isEmpty(this.getProperties().getPermalink()) && !CollectionUtils.isEmpty(this.getProperties().getDescription());
            if (!valid) {
                // 已打为可展示标记跳过
                if (this.getMetas().contains(Constant.Common.SHOW)) {
                    this.getProperties().setNopost(true);
                    this.getProperties().setImage(Constant.Common.DEFAULT_IMAGE);
                    return true;
                }
            }

            return valid;
        }

        @NoArgsConstructor
        @Data
        public static class PropertiesDTO {
            private String title;
            private List<String> description;
            private List<String> permalink;
            private String image;
            private Boolean nopost;

            public Boolean getNopost() {
                return BooleanUtils.toBooleanDefaultIfNull(nopost, false);
            }

            public static PropertiesDTO of() {
                return new PropertiesDTO();
            }

            public static PropertiesDTO of(String title, List<String> description, List<String> permalink, String image) {
                PropertiesDTO of = of();
                of.setTitle(title);
                of.setDescription(description);
                of.setPermalink(permalink);
                of.setImage(image);

                return of;
            }
        }

        public static FeaturesDTO of() {
            return new FeaturesDTO();
        }

        public static FeaturesDTO of(GeometryDTO geometry, PropertiesDTO properties) {
            FeaturesDTO of = of();
            of.setMetas(Lists.newArrayList());
            of.setGeometry(geometry);
            of.setProperties(properties);
            return of;
        }

        public String getType() {
            return Optional.ofNullable(type).orElse("Feature");
        }

        public static FeaturesDTO from(MapGroup group) {
            if (Objects.isNull(group)) {
                return FeaturesDTO.of();
            }
            MapFeature.FeaturesDTO.GeometryDTO geometry = MapFeature.FeaturesDTO.GeometryDTO.of(Lists.newArrayList(StringUtils.split(group.getSpec().getCoordinates(), ',')).stream().filter(StringUtils::isNotBlank).map(Double::valueOf).collect(Collectors.toList()));
            MapFeature.FeaturesDTO.PropertiesDTO properties = MapFeature.FeaturesDTO.PropertiesDTO.of(group.getSpec().getDisplayName(), Lists.newArrayList(), Lists.newArrayList(), null);
            FeaturesDTO dto = of(geometry, properties);
            dto.setGroupName(group.getMetadata().getName());
            if (!MapUtils.isEmpty(group.getMetadata().getAnnotations())) {
                if (group.getMetadata().getAnnotations().containsKey(Constant.Common.SHOW)) {
                    dto.getMetas().add(Constant.Common.SHOW);
                }
            }
            return dto;
        }

        public FeaturesDTO propertiesWithPost(List<Post> posts, List<MapVo> maps) {
            this.getProperties().setDescription(posts.stream().map(x -> x.getSpec().getTitle()).toList());
            this.getProperties().setPermalink(posts.stream().map(x -> x.getStatus().getPermalink()).toList());
            String image = posts.stream().map(x -> x.getSpec().getCover()).filter(StringUtils::isNotBlank).findFirst().orElse(maps.stream().map(map -> map.getSpec().getLogo()).filter(StringUtils::isNotBlank).findAny().orElse(null));
            this.getProperties().setImage(image);
            return this;
        }

    }

    public String getType() {
        return Optional.ofNullable(type).orElse("FeatureCollection");
    }

    public List<FeaturesDTO> getFeatures() {
        return Optional.ofNullable(features).orElse(Collections.emptyList());
    }

    public static MapFeature of() {
        return new MapFeature();
    }

    public static MapFeature of(List<FeaturesDTO> features) {
        MapFeature of = of();
        of.setFeatures(features);
        return of;
    }

}
