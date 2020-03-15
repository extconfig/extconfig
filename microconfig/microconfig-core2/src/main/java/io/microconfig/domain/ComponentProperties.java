package io.microconfig.domain;

import java.util.List;
import java.util.Optional;

public interface ComponentProperties {
    String getConfigType();

    ComponentProperties resolveBy(StatementResolver resolver);

    List<Property> getProperties();

    Optional<Property> getPropertyWithKey(String key);

    <T> T save(PropertySerializer<T> serializer);
}