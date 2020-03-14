package io.microconfig.domain.impl.properties.resolvers;

import io.microconfig.domain.Property;
import io.microconfig.domain.impl.properties.resolvers.expression.Expression;
import io.microconfig.domain.impl.properties.resolvers.placeholder.RootComponent;

import static io.microconfig.io.FileUtils.LINES_SEPARATOR;
import static java.lang.String.format;

public class PropertyResolveException extends RuntimeException {
    public PropertyResolveException(String message) {
        super(message);
    }

    public PropertyResolveException(String unresolvedPlaceholder, Property sourceOfPlaceholder, RootComponent root,
                                    Throwable cause) {
        super(resolveExceptionMessage(unresolvedPlaceholder, sourceOfPlaceholder, root), cause);
    }

    public PropertyResolveException(String unresolvedPlaceholder, Property sourceOfPlaceholder, RootComponent root) {
        super(resolveExceptionMessage(unresolvedPlaceholder, sourceOfPlaceholder, root));
    }

    public PropertyResolveException(Expression expression, RootComponent root, Throwable cause) {
        super(format("Can't evaluate EL '%s'. Root component -> %s[%s]. " +
                        "All string must be escaped with single quote '. " +
                        "Example of correct EL: #{'${component1@ip}' + ':' + ${ports@port1}}",
                expression, root.getComponentName(), root.getEnvironment()), cause
        );
    }

    private static String resolveExceptionMessage(String unresolvedPlaceholder, Property sourceOfPlaceholder, RootComponent root) {
        return format("Can't resolve placeholder '%s' defined in " + LINES_SEPARATOR + "'%s', that property is a transitive dependency of '%s'.",
                unresolvedPlaceholder,
                sourceOfPlaceholder.getSource().sourceInfo(),
                root);
    }

    public static PropertyResolveException badPlaceholderFormat(String value) {
        return new PropertyResolveException("Can't parse placeholder '" + value + "'"
                + ". Supported format: ${componentName[optionalEnvName]@propertyPlaceholder:optionalDefaultValue}");
    }

    public static PropertyResolveException badSpellFormat(String value) {
        throw new PropertyResolveException("'" + value + "' is not an expression. Supported format is: #{expression}");
    }
}