package io.microconfig.core.properties;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Properties {
    String getConfigType();

    Properties resolveBy(Resolver resolver);

    Properties withoutTempValues();

    List<Property> getProperties();

    Map<String, String> propertiesAsKeyValue();

    Optional<Property> getPropertyWithKey(String key);

    <T> T save(PropertySerializer<T> serializer);
}