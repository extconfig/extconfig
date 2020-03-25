package io.microconfig;

import io.microconfig.core.properties.Properties;
import io.microconfig.core.properties.Property;
import io.microconfig.core.properties.impl.PropertyResolveException;
import org.junit.jupiter.api.Test;

import static io.microconfig.ClasspathUtils.classpathFile;
import static io.microconfig.Microconfig.searchConfigsIn;
import static io.microconfig.core.configtypes.impl.ConfigTypeFilters.configType;
import static io.microconfig.core.configtypes.impl.StandardConfigType.APPLICATION;
import static io.microconfig.utils.StringUtils.splitKeyValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MicroconfigTest {
    private final Microconfig microconfig = searchConfigsIn(classpathFile("repo"));

    @Test
    void ip() {
        String value = buildComponent("ip1", "uat")
                .getPropertyWithKey("ip1.some-ip")
                .map(Property::getValue)
                .orElseThrow(IllegalStateException::new);

        assertEquals("1.1.1.1", value);
    }

    @Test
    void simpleInclude() {
        assertEquals(
                splitKeyValue("key1=1", "key2=2", "key3=3", "key4=4"),
                buildComponent("si1", "uat").getPropertiesAsKeyValue()
        );
    }

    @Test
    void cyclicInclude() {
        assertEquals(
                splitKeyValue("key1=1", "key2=2", "key3=3"),
                buildComponent("ci1", "uat").getPropertiesAsKeyValue()
        );
    }

    @Test
    void predefinedFunction() {
        assertEquals(
                splitKeyValue("notFound=", "xmx=0m", "xmxLine=Xmx100m"),
                buildComponent("predefinedFunctions", "uat").getPropertiesAsKeyValue()
        );
    }

    @Test
    void placeholderToSpel() {
        assertEquals(
                splitKeyValue("test.mq.address=tcp://:6872", "test.mq.address2=tcp://:68720"),
                buildComponent("pts", "dev").getPropertiesAsKeyValue()
        );
    }

    @Test
    void thisToVar() {
        assertEquals(
                splitKeyValue("c=3"),
                buildComponent("var", "dev").getPropertiesAsKeyValue()
        );
    }

    @Test
    void testCyclicDetect() {
        assertThrows(PropertyResolveException.class, () -> buildComponent("cyclicDetect", "uat"));
    }

    @Test
    void placeholderToAliases() {
        assertEquals(
                splitKeyValue("ips=172.30.162.4 172.30.162.5 172.30.162.5", "properties=node1 node3 node", "dir=" + nodeConfigDir()),
                buildComponent("placeholderToAlias", "aliases").getPropertiesAsKeyValue()
        );
    }

    @Test
    void placeholderToAnotherConfigType() {
        assertEquals(
                splitKeyValue("p1=pro", "p2=app", "p3=app", "p4=pro", "p5=app", "p6=pro"),
                buildComponent("configType", "dev").getPropertiesAsKeyValue()
        );
    }

    @Test
    void placeholderToAnotherComponentWithAnotherConfigType() {
        assertEquals(
                splitKeyValue("p1=pro3", "p2=app2", "p3=app3", "k1=pro4"),
                buildComponent("appType", "dev").getPropertiesAsKeyValue()
        );
    }

    @Test
    void aliasesAndThis() {
        testAliases("node1", 4);
        testAliases("node3", 5);
        testAliases("node", 5);
    }

    private void testAliases(String name, int ip) {
        assertEquals(
                splitKeyValue("app.ip=172.30.162." + ip, "app.name=" + name, "app.value=v1", "app.dir=" + nodeConfigDir()),
                buildComponent(name, "aliases").getPropertiesAsKeyValue()
        );
    }

    private String nodeConfigDir() {
        return classpathFile("repo/components/aliases/node/service.properties").getParent();
    }

    private Properties buildComponent(String component, String env) {
        return microconfig.inEnvironment(env)
                .getOrCreateComponentWithName(component)
                .getPropertiesFor(configType(APPLICATION))
                .resolveBy(microconfig.resolver())
                .withoutTempValues();
    }
}
