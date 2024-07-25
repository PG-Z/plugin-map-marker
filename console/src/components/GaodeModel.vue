<script lang="ts" setup>
import {VModal} from "@halo-dev/components";
import {onMounted, ref, computed, watch, reactive} from "vue";
import AMapLoader from '@amap/amap-jsapi-loader';
import {
  consoleApiClient
} from "@halo-dev/api-client";

const props = withDefaults(
  defineProps<{
    visible: boolean;
  }>(),
  {
    visible: false,
  }
);

const emit = defineEmits<{
  (event: "update:visible", value: boolean): void;
  (event: "close"): void;
}>();

const formVisible = ref(false);
const modalTitle = computed(() => {
  return "地图解析";
});

const onVisibleChange = (visible: boolean) => {
  emit("update:visible", visible);
  if (!visible) {
    emit("close");
  }
};

watch(
  () => props.visible,
  (visible) => {
    if (visible) {
      formVisible.value = true;
    }
  }
);
const map = ref(null);
const mapData = reactive({
  map: {},
  keyword: '',
  selectedLocation: {},
  selectedAddress: '',
});
const mapLonLat = reactive({
  lon: '',
  lat: '',
});

const gdKey = reactive({
  key: '',
  securityJsCode: '',
});

onMounted(() => {

  consoleApiClient.plugin.plugin.fetchPluginConfig({"name": "PluginMaps"})
    .then(response => {
      let data = response.data.data;
      // Parse the gaode field
      let gaodeConfig = JSON.parse(data.gaode);
      // gaodeKey = gaodeConfig.key;
      // gaodeSecurityJsCode = gaodeConfig.securityJsCode;
      gdKey.key = gaodeConfig.key;
      gdKey.securityJsCode = gaodeConfig.securityJsCode;
      // Initialize the map only after fetching the gaode config
      initializeMap();
    })
    .catch(error => {
      console.error("Error fetching plugin config: ", error);
    });

});

function initializeMap() {
  window._AMapSecurityConfig = {
    securityJsCode: gdKey.securityJsCode, // Set the securityJsCode
  };

  AMapLoader.load({
    key: gdKey.key, // Set the key value
    version: '2.0',
    plugins: ['AMap.PlaceSearch'],
  })
    .then((AMap) => {
      const mapInstance = new AMap.Map('container', {
        viewMode: '2D',
        zoom: 11,
        center: [113.984358, 35.288668],
        layers: [
          new AMap.TileLayer.RoadNet(),
          new AMap.TileLayer.WMS({
            url: 'https://ahocevar.com/geoserver/wms', // wms服务的url地址
            blend: false, // 地图级别切换时，不同级别的图片是否进行混合
            tileSize: 512, // 加载WMS图层服务时，图片的分片大小
            params: {
              'LAYERS': 'topp:states',
              VERSION: '1.3.0'
            } // OGC标准的WMS地图服务的GetMap接口的参数
          })
        ],
      });

      mapInstance.on('click', (e) => {
        //点击位置获取经纬度
        const lng = e.lnglat.getLng();
        const lat = e.lnglat.getLat();
        console.log(lng, lat);
        mapLonLat.lat = lat;
        mapLonLat.lon = lng;
      });

      mapData.map = mapInstance;
    })
    .catch((e) => {
      console.log(e);
    });
}

const search = () => {
  console.log(mapData.keyword);
  if (mapData.keyword) {
    AMapLoader.load({
      key: gdKey.key, // Use the same key value
      version: '2.0',
      plugins: ['AMap.PlaceSearch'],
    })
      .then((AMap) => {
        const placeSearch = new AMap.PlaceSearch({
          city: '',
          map: mapData.map,
        });
        console.log(mapData.keyword);
        placeSearch.search(mapData.keyword, (status, result) => {
          console.log(status, result.info);
          if (status === 'complete' && result.info === 'OK') {
            const pois = result.poiList.pois;
            if (pois.length > 0) {
              const {location} = pois[0];
              mapData.map.setCenter(location);
            }
          } else {
            console.log('搜索失败或无结果');
          }
        });
      })
      .catch((e) => {
        console.log(e);
      });
  }
};

const copy = () => {
  const lonLat = `${mapLonLat.lat},${mapLonLat.lon}`;
  navigator.clipboard.writeText(lonLat).then(() => {
    console.log('经纬度已复制:', lonLat);
  }).catch(err => {
    console.error('复制失败:', err);
  });
};

</script>

<template>
  <VModal
    :title="modalTitle"
    :visible="visible"
    :width="650"
    @update:visible="onVisibleChange"
  >
    <template #actions>
      <slot name="append-actions"/>
    </template>

    <div>
      <input style="width: 80%; height: 30px" v-model="mapData.keyword" @keydown.enter="search"/>
      <button @click="search">搜索</button>
      <input style="width: 40%; height: 30px" v-model="mapLonLat.lat" readonly placeholder="纬度"/>
      <input style="width: 40%; height: 30px" v-model="mapLonLat.lon" readonly placeholder="经度"/>
      <button @click="copy">复制</button>
      <div id="container" class="map" style="height: 400px; border-radius: 5px"></div>
    </div>
  </VModal>
</template>

<style scoped>
.map {
  width: 100%;
}
</style>
