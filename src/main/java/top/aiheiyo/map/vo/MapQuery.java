package top.aiheiyo.map.vo;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;
import org.springframework.util.comparator.Comparators;
import org.springframework.web.server.ServerWebExchange;
import run.halo.app.core.extension.endpoint.SortResolver;
import run.halo.app.extension.Extension;
import run.halo.app.extension.router.IListRequest;
import top.aiheiyo.map.Map;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static java.util.Comparator.comparing;
import static run.halo.app.extension.router.selector.SelectorUtil.labelAndFieldSelectorToPredicate;

/**
 * Description: MapQuery
 *
 * @author : evan  Date: 2024/4/18
 */
public class MapQuery extends IListRequest.QueryListRequest {

    private ServerWebExchange exchange;

    public MapQuery(ServerWebExchange exchange) {
        super(exchange.getRequest().getQueryParams());
        this.exchange = exchange;
    }

    public MapQuery(MultiValueMap<String, String> queryParams) {
       super(queryParams);
    }

    @Schema(description = "Keyword to search maps under the group")
    public String getKeyword() {
        return queryParams.getFirst("keyword");
    }

    @Schema(description = "Map group name")
    public String getGroupName() {
        return queryParams.getFirst("groupName");
    }

    @Schema(description = "post name")
    public String getNotContainPost() {
        return queryParams.getFirst("notContainPost");
    }

    @ArraySchema(uniqueItems = true,
            arraySchema = @Schema(name = "sort",
                    description = "Sort property and direction of the list result. Supported fields: "
                            + "creationTimestamp, priority"),
            schema = @Schema(description = "like field,asc or field,desc",
                    implementation = String.class,
                    example = "creationTimestamp,desc"))
    public Sort getSort() {
        if (Objects.isNull(this.exchange)) {
            return Sort.by("status.lastModifyTime").descending();
        }
        return SortResolver.defaultInstance.resolve(exchange);
    }

    public Predicate<Map> toPredicate() {
        Predicate<Map> keywordPredicate = map -> {
            var keyword = getKeyword();
            if (StringUtils.isBlank(keyword)) {
                return true;
            }
            String keywordToSearch = keyword.trim().toLowerCase();
            return StringUtils.containsAnyIgnoreCase(map.getSpec().getDisplayName(),
                    keywordToSearch)
                    || StringUtils.containsAnyIgnoreCase(map.getSpec().getUrl(), keywordToSearch);
        };
        Predicate<Map> groupPredicate = map -> {
            var groupName = getGroupName();
            if (StringUtils.isBlank(groupName)) {
                return true;
            }
            return StringUtils.equals(groupName, map.getSpec().getGroupName());
        };

        Predicate<Map> notContainPredicate = map -> {
            var notContainPost = getNotContainPost();
            if (StringUtils.isBlank(notContainPost)) {
                return true;
            }
            return !StringUtils.equals(notContainPost, map.getSpec().getPost());
        };
        Predicate<Extension> labelAndFieldSelectorToPredicate =
                labelAndFieldSelectorToPredicate(getLabelSelector(), getFieldSelector());
        return groupPredicate.and(keywordPredicate).and(labelAndFieldSelectorToPredicate).and(notContainPredicate);
    }

    public Comparator<Map> toComparator() {
        var sort = getSort();
        var ctOrder = sort.getOrderFor("creationTimestamp");
        var priorityOrder = sort.getOrderFor("priority");
        List<Comparator<Map>> comparators = new ArrayList<>();
        if (ctOrder != null) {
            Comparator<Map> comparator =
                    comparing(map -> map.getMetadata().getCreationTimestamp());
            if (ctOrder.isDescending()) {
                comparator = comparator.reversed();
            }
            comparators.add(comparator);
        }
        if (priorityOrder != null) {
            Comparator<Map> comparator =
                    comparing(map -> map.getSpec().getPriority(),
                            Comparators.nullsLow());
            if (priorityOrder.isDescending()) {
                comparator = comparator.reversed();
            }
            comparators.add(comparator);
        }
        comparators.add(compareCreationTimestamp(false));
        comparators.add(compareName(true));
        return comparators.stream()
                .reduce(Comparator::thenComparing)
                .orElse(null);
    }

    public static <E extends Extension> Comparator<E> compareCreationTimestamp(boolean asc) {
        var comparator =
                Comparator.<E, Instant>comparing(e -> e.getMetadata().getCreationTimestamp());
        return asc ? comparator : comparator.reversed();
    }

    public static <E extends Extension> Comparator<E> compareName(boolean asc) {
        var comparator = Comparator.<E, String>comparing(e -> e.getMetadata().getName());
        return asc ? comparator : comparator.reversed();
    }
}
