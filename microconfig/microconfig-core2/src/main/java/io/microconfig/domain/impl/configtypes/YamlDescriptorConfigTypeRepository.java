package io.microconfig.domain.impl.configtypes;

import io.microconfig.domain.ConfigType;
import io.microconfig.domain.ConfigTypeRepository;
import io.microconfig.io.formats.Io;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.microconfig.domain.impl.configtypes.ConfigTypeImpl.byName;
import static io.microconfig.domain.impl.configtypes.ConfigTypeImpl.byNameAndExtensions;
import static io.microconfig.io.Logger.announce;
import static io.microconfig.io.StreamUtils.forEach;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;

@RequiredArgsConstructor
public class YamlDescriptorConfigTypeRepository implements ConfigTypeRepository {
    private static final String DESCRIPTOR = "microconfig.yaml";

    private final File rootDir;
    private final Io io;

    @Override
    public List<ConfigType> getRepositories() {
        File descriptorFile = descriptorFile();
        if (!descriptorFile.exists()) return emptyList();

        List<ConfigType> types = getConfigTypes(descriptorFile);
        if (!types.isEmpty()) {
            announce("Using settings from " + DESCRIPTOR);
        }
        return types;
    }

    private File descriptorFile() {
        return new File(rootDir, DESCRIPTOR);
    }

    private List<ConfigType> getConfigTypes(File descriptor) {
        try {
            return new MicroconfigDescriptor(io.readFully(descriptor)).getConfigTypes();
        } catch (RuntimeException e) {
            throw new RuntimeException("Can't parse descriptor: " + descriptor, e);
        }
    }

    @RequiredArgsConstructor
    private static class MicroconfigDescriptor {
        private static final String CONFIG_TYPES = "configTypes";
        private static final String SOURCE_EXTENSIONS = "sourceExtensions";
        private static final String RESULT_FILE_NAME = "resultFileName";

        private final String content;

        @SuppressWarnings("unchecked")
        private List<ConfigType> getConfigTypes() {
            Map<String, Object> types = new Yaml().load(content);
            List<Object> configTypes = (List<Object>) types.getOrDefault(CONFIG_TYPES, emptyList());
            return forEach(configTypes, this::parse);
        }

        @SuppressWarnings("unchecked")
        private ConfigType parse(Object configTypeObject) {
            if (configTypeObject instanceof String) {
                return byName(configTypeObject.toString());
            }

            Map<String, Object> configType = (Map<String, Object>) configTypeObject;
            String type = configType.keySet().iterator().next();
            Map<String, Object> attributes = (Map<String, Object>) configType.get(type);
            Set<String> sourceExtensions = attributes.containsKey(SOURCE_EXTENSIONS) ? new LinkedHashSet<>((List<String>) attributes.get(SOURCE_EXTENSIONS)) : singleton(type);
            String resultFileName = (String) attributes.getOrDefault(RESULT_FILE_NAME, type);

            return byNameAndExtensions(type, sourceExtensions, resultFileName);
        }
    }
}