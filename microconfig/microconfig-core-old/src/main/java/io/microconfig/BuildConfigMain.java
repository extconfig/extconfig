package io.microconfig;

import io.microconfig.commands.ComponentsToProcess;
import io.microconfig.commands.ConfigCommand;
import io.microconfig.factory.BuildConfigCommandFactory;
import io.microconfig.utils.CommandLineParamParser;

import java.io.File;
import java.util.List;

import static io.microconfig.factory.configtypes.CompositeConfigTypeProvider.compositeProvider;
import static io.microconfig.utils.Logger.announce;
import static java.lang.System.currentTimeMillis;

/**
 * VM speedup params:
 * -Xverify:none -XX:TieredStopAtLevel=1
 * <p>
 * Command line params example: *
 * root=C:\Projects\config\repo dest=C:\Projects\configs env=cr-dev6
 */
public class BuildConfigMain {
    private static final String ROOT = "r";
    private static final String DEST = "d";
    private static final String ENV = "e";
    private static final String GROUPS = "g";
    private static final String SERVICES = "s";

    public static void main(String[] args) {
        CommandLineParamParser clp = CommandLineParamParser.parse(args);

        String root = clp.requiredValue(ROOT, "set root=  param (folder with 'components' and 'envs' directories)");
        String destination = clp.requiredValue(DEST, "set dest= param (folder for config build output)");
        String env = clp.requiredValue(ENV, "set env=");
        List<String> groups = clp.listValue(GROUPS);
        List<String> components = clp.listValue(SERVICES);

        long t = currentTimeMillis();
        ConfigCommand configCommand = new BuildConfigCommandFactory(compositeProvider()).newCommand(new File(root), new File(destination));
        execute(configCommand, env, groups, components);
        announce("Generated configs in " + ((currentTimeMillis() - t) + " ms"));

    }

    public static void execute(ConfigCommand configCommand, String env, List<String> groups, List<String> components) {
        if (groups.isEmpty()) {
            configCommand.execute(new ComponentsToProcess(env, components));
        } else {
            groups.forEach(group -> configCommand.execute(new ComponentsToProcess(env, group, components)));
        }
    }
}