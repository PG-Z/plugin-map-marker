import "./styles/tailwind.css";
import "./styles/index.css";
import { definePlugin } from "@halo-dev/console-shared";
import MapList from "@/views/MapList.vue";
import { markRaw } from "vue";
import RiLinksLine from "~icons/ri/links-line";

export default definePlugin({
  components: {},
  routes: [
    {
      parentName: "Root",
      route: {
        path: "/maps",
        name: "Maps",
        component: MapList,
        meta: {
          permissions: ["plugin:maps:view"],
          menu: {
            name: "地图",
            group: "content",
            icon: markRaw(RiLinksLine),
          },
        },
      },
    },
  ],
});
