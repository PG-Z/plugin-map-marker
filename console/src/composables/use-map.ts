import type {MapGroup, MapList, MapGroupList} from "@/types";
import apiClient from "@/utils/api-client";
import { useQuery } from "@tanstack/vue-query";
import { ref, type Ref } from "vue";

export function useMapFetch(
  page: Ref<number>,
  size: Ref<number>,
  keyword?: Ref<string>,
  group?: Ref<string>
) {
  const total = ref(0);

  const {
    data: links,
    isLoading,
    refetch,
  } = useQuery({
    queryKey: ["links", page, size, group, keyword],
    queryFn: async () => {
      const { data } = await apiClient.get<MapList>(
        "/apis/api.plugin.aiheiyo.top/v1alpha1/plugins/PluginMaps/maps",
        {
          params: {
            page: page.value,
            size: size.value,
            keyword: keyword?.value,
            groupName: group?.value,
            sort: "priority,asc",
          },
        }
      );

      total.value = data.total;

      return data.items;
    },
    refetchOnWindowFocus: false,
    refetchInterval(data) {
      const deletingLinks = data?.filter(
        (link) => !!link.metadata.deletionTimestamp
      );
      return deletingLinks?.length ? 1000 : false;
    },
  });

  return {
    links,
    isLoading,
    refetch,
    total,
  };
}

export function useMapGroupFetch() {
  const {
    data: groups,
    isLoading,
    refetch,
  } = useQuery<MapGroup[]>({
    queryKey: ["link-groups"],
    queryFn: async () => {
      const { data } = await apiClient.get<MapGroupList>(
        "/apis/map.aiheiyo.top/v1alpha1/mapgroups"
      );

      return data.items
        .map((group) => {
          if (group.spec) {
            group.spec.priority = group.spec.priority || 0;
          }
          return group;
        })
        .sort((a, b) => {
          return (a.spec?.priority || 0) - (b.spec?.priority || 0);
        });
    },
    refetchOnWindowFocus: false,
    refetchInterval(data) {
      const deletingGroups = data?.filter((group) => {
        return !!group.metadata.deletionTimestamp;
      });
      return deletingGroups?.length ? 1000 : false;
    },
  });

  return {
    groups,
    isLoading,
    refetch,
  };
}
