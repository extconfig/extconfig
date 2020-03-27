package io.microconfig.core;

import io.microconfig.core.properties.PropertyResolveException;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static io.microconfig.core.ClasspathReader.classpathFile;
import static io.microconfig.core.Microconfig.searchConfigsIn;
import static io.microconfig.core.configtypes.ConfigTypeFilters.configType;
import static io.microconfig.core.configtypes.StandardConfigType.APPLICATION;
import static io.microconfig.core.properties.PropertySerializers.asString;
import static io.microconfig.utils.FileUtils.walk;
import static io.microconfig.utils.IoUtils.readFully;
import static io.microconfig.utils.Logger.error;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class MicroconfigTest {
    private final File root = classpathFile("repo");
    private final Microconfig microconfig = searchConfigsIn(root);

    @TestFactory
    List<DynamicTest> findTests() {
        return findTests(null);
    }

    @Test
    void testCyclicDetect() {
        assertThrows(PropertyResolveException.class, () -> build("cyclicDetect", "uat"));
    }

    private List<DynamicTest> findTests(String component) {
        try (Stream<Path> stream = walk(classpathFile("repo").toPath())) {
            return stream.map(Path::toFile)
                    .filter(this::isExpectation)
                    .filter(f -> component == null || f.getParentFile().getName().equals(component))
                    .map(this::toTest)
                    .collect(toList());
        }
    }

    private boolean isExpectation(File file) {
        return file.getName().startsWith("expect.");
    }

    //todo highlight error
    private DynamicTest toTest(File expectation) {
        String component = getComponentName(expectation);
        String env = getEnvName(expectation);
        return dynamicTest(component + "[" + env + "]", () -> {
            assertEquals(
                    readExpectation(expectation).trim(),
                    doBuild(component, env).trim()
            );
        });
    }

    private String getComponentName(File expectation) {
        String[] parts = expectation.getName().split("\\.");
        return parts.length == 3 ? parts[2] : expectation.getParentFile().getName();
    }

    private String getEnvName(File expectation) {
        return expectation.getName().split("\\.")[1];
    }

    private String doBuild(String component, String env) {
        try {
            return build(component, env);
        } catch (RuntimeException e) {
            error("Failed '" + component + ":[" + env + "]'");
            throw e;
        }
    }

    private String build(String component, String env) {
        return microconfig.environments()
                .getOrCreateByName(env)
                .getOrCreateComponentWithName(component)
                .getPropertiesFor(configType(APPLICATION))
                .resolveBy(microconfig.resolver())
                .first()
                .save(asString());
    }

    private String readExpectation(File expectation) {
        return readFully(expectation)
                .replace("${currentDir}", expectation.getParentFile().getAbsolutePath())
                .replace("${componentsDir}", new File(root, "components").getAbsolutePath())
                .replace("${space}", " ")
                .replace("#todo", "");
    }
}