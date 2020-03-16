package io.microconfig.domain.impl.properties.resolvers.placeholder.strategies.component.properties;

import io.microconfig.domain.impl.properties.repository.ComponentGraph;
import io.microconfig.domain.impl.properties.resolvers.placeholder.strategies.component.ComponentProperty;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.of;

@RequiredArgsConstructor
public class ComponentPropertiesFactory {
    private final ComponentGraph componentGraph;
    private final File rootDir;
    private final File destinationComponentDir;

    public Map<String, ComponentProperty> get() {
        return of(
                new ComponentNameProperty(),
                new ComponentConfigDirProperty(componentGraph),
                new ResultDirProperty(destinationComponentDir),
                new ConfigRootDirProperty(rootDir)
        ).collect(toMap(ComponentProperty::key, identity()));
    }
}