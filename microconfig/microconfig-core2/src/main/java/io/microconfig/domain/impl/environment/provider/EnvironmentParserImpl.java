package io.microconfig.domain.impl.environment.provider;

import io.microconfig.domain.Environment;
import io.microconfig.io.formats.Io;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class EnvironmentParserImpl implements EnvironmentParser {
    private static final String IP = "ip";
    private static final String PORT_OFFSET = "portOffset";
    private static final String INCLUDE = "include";
    private static final String INCLUDE_ENV = "env";
    private static final String EXCLUDE = "exclude";
    private static final String APPEND = "append";
    private static final String COMPONENTS = "components";

    private final Io io;

    @Override
    public Environment parse(String name, File file) {
        try {
//            return doParse(name, new Yaml().load(io.read(file)));
            return null;
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Can't parse '" + name + "' env", e);
        }
    }
}