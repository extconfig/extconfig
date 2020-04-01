package io.microconfig.core.properties.resolvers.expression;

import io.microconfig.core.properties.DeclaringComponent;
import io.microconfig.core.properties.PropertyResolveException;
import io.microconfig.core.properties.resolvers.RecursiveResolver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.microconfig.core.properties.resolvers.expression.ExpressionEvaluator.withFunctionsFrom;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.regex.Pattern.compile;

@RequiredArgsConstructor
public class ExpressionResolver implements RecursiveResolver {
    private final Pattern expressionPattern = compile("#\\{(?<value>[^{]+?)}");
    private final ExpressionEvaluator evaluator = withFunctionsFrom(PredefinedFunctions.class);

    @Override
    public Optional<Statement> findStatementIn(CharSequence line) {
        Matcher expressionMatcher = expressionPattern.matcher(line);
        return expressionMatcher.find() ? of(toExpression(expressionMatcher)) : empty();
    }

    private Expression toExpression(Matcher matcher) {
        return new Expression(matcher.group("value"), matcher.start(), matcher.end());
    }

    @RequiredArgsConstructor
    private class Expression implements Statement {
        private final String value;
        @Getter
        private final int startIndex;
        @Getter
        private final int endIndex;

        @Override
        public String resolveFor(DeclaringComponent component, DeclaringComponent root) {
            try {
                return evaluator.evaluate(value);
            } catch (RuntimeException e) {
                throw new PropertyResolveException(
                        format("Can't evaluate expression '%s' declared in '%s', root is %s.", this, component, root) +
                                "\nEvaluation exception: " + e.getMessage()
                );
            }
        }

        @Override
        public String toString() {
            return "#{" + value + "}";
        }
    }
}