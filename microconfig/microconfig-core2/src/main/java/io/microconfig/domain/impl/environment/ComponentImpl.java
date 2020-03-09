package io.microconfig.domain.impl.environment;

import io.microconfig.domain.Component;
import io.microconfig.domain.ComponentProperties;
import io.microconfig.domain.ConfigType;
import io.microconfig.domain.ConfigTypeSupplier;
import io.microconfig.domain.impl.properties.PropertiesProvider;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class ComponentImpl implements Component {
    private final Map<ConfigType, PropertiesProvider> providerByConfigType;

    private final String name;
    private final String type;
    private final String env;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<ComponentProperties> buildPropertiesForEachConfigType() {
        return providerByConfigType.values().stream()
                .map(this::buildPropertiesUsing)
                .collect(toList());
    }

    @Override
    public ComponentProperties buildPropertiesFor(ConfigTypeSupplier configTypeSupplier) {
        ConfigType configType = configTypeSupplier.chooseType(providerByConfigType.keySet());
        return buildPropertiesUsing(providerFor(configType));
    }

    private ComponentProperties buildPropertiesUsing(PropertiesProvider propertiesProvider) {
        return propertiesProvider.buildProperties(name, type, env);
    }

    private PropertiesProvider providerFor(ConfigType configType) {
        PropertiesProvider provider = providerByConfigType.get(configType);
        if (provider == null) {
            throw new IllegalArgumentException("Config type '" + configType + "' is not configured." +
                    " Supported types: " + providerByConfigType.keySet());
        }
        return provider;
    }
}