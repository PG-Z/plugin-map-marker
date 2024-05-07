package top.aiheiyo.map;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import run.halo.app.extension.SchemeManager;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;

/**
 * Description: 启动
 *
 * @author : evan  Date: 2024/4/18
 */
@EnableAsync
@Component
public class MapPlugin extends BasePlugin {

    private final SchemeManager schemeManager;

    public MapPlugin(PluginContext context, SchemeManager schemeManager) {
        super(context);
        this.schemeManager = schemeManager;
    }

    @Override
    public void start() {
        schemeManager.register(Map.class);
        schemeManager.register(MapGroup.class);
    }

    @Override
    public void stop() {
        schemeManager.unregister(schemeManager.get(Map.class));
        schemeManager.unregister(schemeManager.get(MapGroup.class));
    }
}
