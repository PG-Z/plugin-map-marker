# plugin-map-marker

Halo 2.0 适配插件, 在地图上标记文章。

<p align="center">
    <a href="/">
        <img src="https://img.shields.io/github/v/release/PG-Z/plugin-map-marker?color=F38181&amp;label=version&amp;logo=v&amp;logoColor=F38181&amp;style=for-the-badge" referrerpolicy="no-referrer" alt="plugin version" />
    </a>
    <a href="/">
        <img src="https://img.shields.io/github/downloads/PG-Z/plugin-map-marker/total?color=FCE38A&amp;logo=github&amp;logoColor=FCE38A&amp;style=for-the-badge" referrerpolicy="no-referrer" alt="github downloads" />
    </a>
    <a href="/">
        <img src="https://img.shields.io/github/release-date/PG-Z/plugin-map-marker?color=95E1D3&amp;label=release date&amp;logo=puppet&amp;logoColor=95E1D3&amp;style=for-the-badge" referrerpolicy="no-referrer" alt="release-date" />
    </a>
    <img src="https://img.shields.io/github/last-commit/PG-Z/plugin-map-marker?style=for-the-badge&amp;logo=lospec&amp;logoColor=a6d189" referrerpolicy="no-referrer" alt="last-commit" />
    <a href="/"><img src="https://img.shields.io/badge/halo-%3E=2.17.0-8caaee?style=for-the-badge&amp;logo=hexo&amp;logoColor=8caaee" referrerpolicy="no-referrer" alt="Required Halo version" /></a>
</p>

## 使用方式

1. 下载，目前提供以下方式：
    - GitHub Releases：访问 [Releases](https://github.com/PG-Z/plugin-map-marker/releases) 下载 Assets 中的 JAR 文件。
2. 安装，插件安装和更新方式可参考：<https://docs.halo.run/user-guide/plugins>
3. 安装完成之后，访问 Console 左侧的**地图**菜单项，即可进行管理。
4. 前台访问地址为 `/maps`，需要注意的是，此插件需要主题提供模板（maps.html）才能访问 `/maps`。

### mapbox

1. 申请[mapbox账号](https://account.mapbox.com/)
2. 申请TOKEN.
3. 将TOKEN配置到插件配置>> 地图设置 >> mapbox token .

#### 高德地图

1. 自行搜索申请[AK, SK]教程 (Web端类型 JS API).
2. 配置到插件配置>> 高德地图.

## 主题适配

目前此插件为主题端提供了 `/maps` 路由，模板为 `maps.html`。

#### 示例

```html
<th:block th:replace="~{plugin:PluginMaps:map-marker :: marker()}" /></th:block>
```

#### 相关地点组件

> 在文章详情页可以增加`相关地点`显示, 插件已提供html组件. 也可自定义, 已提供`Finder API` .

```html
<!--方法`marker-addon`传参的是文章唯一标识: `post.metadata.name`-->
<th:block th:replace="~{plugin:PluginMaps:map-marker-append :: marker-addon(${post.metadata.name})}" />
```

**可以先判断插件状态: 已使用/并启用**
```html
<th:block th:if="${pluginFinder.available('PluginMaps')}">
   <th:block th:replace="~{plugin:PluginMaps:map-marker-append :: marker-addon(${post.metadata.name})}" />
</th:block>
```

#### PJAX

若使用了PJAX, 可以参考一下配置:
```js

document.addEventListener('pjax:complete', function () {
  myMap();
});

/**
 * map-marker pjax
 */
function myMap() {
   try {
      let selector = document.querySelector("#map");
      if (selector == null) {
         return;
      }
      let configStr = document.getElementById('map-marker-config').innerText;
      if (configStr === null || configStr === '') {
         return;
      }
      MAP_MARKER = JSON.parse(configStr);
      fetch(MAP_MARKER.api).then(function (response) {
         return response.json().then(function (json) {
            new MYMAP({data: json})
         })
      })
   } catch (ignored) {
      console.error("myMap: " + ignored);
   }
}
```

**实际使用例子**
- [引用适配](https://github.com/PG-Z/Summer-Cat/blob/main/templates/maps.html)
- [相关地点](https://github.com/PG-Z/Summer-Cat/blob/main/templates/post.html)

## Finder API

**list(postName, page, size)**

**描述**
> 根据文章唯一标识获取文章关联地图标记分组下的其他文章。

**参数**

| 字段  |  描述 |
| ------------ | ------------ |
| postName  | 文章唯一标识  |
| page | int - 分页页码，从 1 开始 |
| size | int - 分页条数  |

## 使用技巧

- 创建分组未挂载文章时, 地图不会显示该分组. 但是在元数据新增: `key:show value:show`, 就会展示在地图上.
- 加载地图可能受网络影响.

## 开发环境

```bash
git clone git@github.com:PG-Z/plugin-map-marker.git
```

```bash
cd path/to/plugin-map-marker
```

```bash
# macOS / Linux
./gradlew pnpmInstall

# Windows
./gradlew.bat pnpmInstall
```

```bash
# macOS / Linux
./gradlew build

# Windows
./gradlew.bat build
```

修改 Halo 配置文件：

```yaml
halo:
  plugin:
    runtime-mode: development
    fixedPluginPath:
      - "/path/to/plugin-map-marker"
```
