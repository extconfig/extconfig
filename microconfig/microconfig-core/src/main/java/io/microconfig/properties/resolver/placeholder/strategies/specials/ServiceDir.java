package io.microconfig.properties.resolver.placeholder.strategies.specials;

import io.microconfig.environments.Component;
import io.microconfig.environments.Environment;
import io.microconfig.properties.resolver.placeholder.strategies.SpecialPropertyResolverStrategy.SpecialProperty;

import java.util.Optional;

import static java.util.Optional.of;

public class ServiceDir implements SpecialProperty {
    @Override
    public String key() {
        return "serviceDir";
    }

    @Override
    public Optional<String> value(Component component, Environment environment) {
        return of("");
    }
}
