package top.aiheiyo.map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.PluginContext;
import run.halo.app.theme.dialect.TemplateHeadProcessor;
import org.springframework.util.PropertyPlaceholderHelper;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import reactor.core.publisher.Mono;

import java.util.Properties;

/**
 * Description: Processor
 *
 * @author : evan  Date: 2024/4/18
 */
@Component
@RequiredArgsConstructor
public class MapProcessor implements TemplateHeadProcessor {

    static final PropertyPlaceholderHelper PROPERTY_PLACEHOLDER_HELPER = new PropertyPlaceholderHelper("${", "}");

    private final PluginContext pluginContext;

    @Override
    public Mono<Void> process(ITemplateContext context, IModel model,
                              IElementModelStructureHandler structureHandler) {
        final IModelFactory modelFactory = context.getModelFactory();
        model.add(modelFactory.createText(mapMarkerScript()));
        return Mono.empty();
    }

    private String mapMarkerScript() {

        final Properties properties = new Properties();
        properties.setProperty("version", pluginContext.getVersion());

        return PROPERTY_PLACEHOLDER_HELPER.replacePlaceholders("""
                <!-- PluginMaps start -->
                <script src="/plugins/PluginMaps/assets/static/js/map.js?version=${version}" defer async></script>
                <script src="/plugins/PluginMaps/assets/static/js/mappack.js?version=${version}" defer async></script>
                <link rel="stylesheet" href="/plugins/PluginMaps/assets/static/css/style.css?version=${version}" />
                <!-- PluginMaps end -->
                """, properties);
    }
}