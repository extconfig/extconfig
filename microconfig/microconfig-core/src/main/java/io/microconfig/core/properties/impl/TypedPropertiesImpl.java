package io.microconfig.core.properties.impl;

import io.microconfig.core.configtypes.ConfigType;
import io.microconfig.core.properties.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;

import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;

import static io.microconfig.utils.StreamUtils.*;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static lombok.AccessLevel.PRIVATE;

@EqualsAndHashCode
@RequiredArgsConstructor
public class TypedPropertiesImpl implements TypedProperties {
    private final ConfigType configType;
    @Getter
    private final String component;
    private final String environment;
    @Getter
    @With(PRIVATE)
    private final Map<String, Property> properties;

    @Override
    public String getConfigType() {
        return configType.getName();
    }

    @Override
    public TypedProperties resolveBy(Resolver resolver) {
        return withProperties(
                forEach(properties.values(), resolveUsing(resolver), toPropertyMap())
        );
    }

    @Override
    public TypedProperties withoutTempValues() {
        return withProperties(
                filter(properties.values(), p -> !p.isTemp(), toPropertyMap())
        );
    }

    @Override
    public Map<String, String> propertiesAsKeyValue() {
        return properties.values()
                .stream()
                .collect(toLinkedMap(Property::getKey, Property::getValue));
    }

    @Override
    public Optional<Property> getPropertyWithKey(String key) {
        return ofNullable(properties.get(key));
    }

    @Override
    public <T> T save(PropertySerializer<T> serializer) {
        return serializer.serialize(properties.values(), configType, component, environment);
    }

    @Override
    public String toString() {
        return currentComponent().toString();
    }

    private UnaryOperator<Property> resolveUsing(Resolver resolver) {
        ComponentWithEnv root = currentComponent();
        return property -> property.resolveBy(resolver, root);
    }

    private ComponentWithEnv currentComponent() {
        return new ComponentWithEnv(configType.getName(), component, environment);
    }

    private Collector<Property, ?, Map<String, Property>> toPropertyMap() {
        return toLinkedMap(Property::getKey, identity());
    }
}