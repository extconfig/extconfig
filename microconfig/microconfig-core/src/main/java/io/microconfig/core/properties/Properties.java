package io.microconfig.core.properties;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public interface Properties {
    Properties resolveBy(Resolver resolver);

    Properties and(Consumer<Properties> callback);

    Properties withoutTempValues();

    Map<String, Property> getPropertiesAsMap();

    Map<String, String> getPropertiesAsKeyValue();

    Collection<Property> getProperties();

    Optional<Property> getPropertyWithKey(String key);

    <T> List<T> save(PropertySerializer<T> serializer);

    List<TypedProperties> asTypedProperties();

    TypedProperties first();
}