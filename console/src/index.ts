import "./styles/tailwind.css";
import "./styles/index.css";
import { definePlugin } from "@halo-dev/console-shared";
import MapList from "@/views/MapList.vue";
import { markRaw } from "vue";
import MajesticonsMapMarkerPath from '~icons/majesticons/map-marker-path';

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
            icon: markRaw(MajesticonsMapMarkerPath),
          },
        },
      },
    },
  ],
});
