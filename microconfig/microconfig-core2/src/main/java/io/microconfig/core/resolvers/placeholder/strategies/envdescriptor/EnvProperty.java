package io.microconfig.core.resolvers.placeholder.strategies.envdescriptor;

import io.microconfig.core.environments.Environment;

import java.util.Optional;

public interface EnvProperty {
    String key();

    Optional<String> value(String componentName, String componentType, Environment environment);
}