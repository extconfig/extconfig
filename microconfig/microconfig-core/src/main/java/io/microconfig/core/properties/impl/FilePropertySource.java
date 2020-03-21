package io.microconfig.core.properties.impl;

import io.microconfig.core.properties.PropertySource;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class FilePropertySource implements PropertySource {
    private final File source;
    private final int lineNumber; //starts from 0
    private final boolean yaml;

    public static FilePropertySource fileSource(File file, int lineNumber, boolean yaml) {
        return new FilePropertySource(file, lineNumber, yaml);
    }

    @Override
    public String getDeclaringComponent() {
        return source.getParentFile().getName();
    }

    @Override
    public String toString() {
        return source.getAbsolutePath() + ":" + (lineNumber + 1);
    }
}