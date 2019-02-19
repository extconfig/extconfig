package io.microconfig.properties.resolver.placeholder.strategies.specials;

import io.microconfig.environments.Component;
import io.microconfig.environments.Environment;
import io.microconfig.properties.resolver.placeholder.strategies.SpecialPropertyResolverStrategy.SpecialProperty;

import java.util.Optional;

import static io.microconfig.utils.FileUtils.userHomeString;
import static io.microconfig.utils.StringUtils.unixLikePath;
import static java.util.Optional.of;

public class UserHome implements SpecialProperty {
    @Override
    public String key() {
        return "userHome";
    }

    @Override
    public Optional<String> value(Component component, Environment environment) {
        return of(unixLikePath(userHomeString()));
    }
}
