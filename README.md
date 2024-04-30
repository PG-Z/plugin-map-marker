# plugin-map-marker

Halo 2.0 适配插件, 在地图上标记文章。

## 使用方式

1. 下载，目前提供以下两个下载方式：
    - GitHub Releases：访问 [Releases](https://github.com/PG-Z/plugin-map-marker/releases) 下载 Assets 中的 JAR 文件。
2. 安装，插件安装和更新方式可参考：<https://docs.halo.run/user-guide/plugins>
3. 安装完成之后，访问 Console 左侧的**地图**菜单项，即可进行管理。
4. 前台访问地址为 `/maps`，需要注意的是，此插件需要主题提供模板（maps.html）才能访问 `/maps`。

## 主题适配

目前此插件为主题端提供了 `/maps` 路由，模板为 `maps.html`。

### 示例

```html
<th:block th:replace="~{plugin:PluginMaps:map-marker :: marker()}" /></th:block>
```

## 使用技巧

- 创建分组未挂载文章时, 地图不会显示该分组. 但是在元数据新增: `key:show value:show`, 就会展示在地图上.

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
