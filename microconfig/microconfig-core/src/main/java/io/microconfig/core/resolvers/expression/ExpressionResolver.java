package io.microconfig.core.resolvers.expression;

import io.microconfig.core.properties.ComponentWithEnv;
import io.microconfig.core.resolvers.RecursiveResolver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ParseException;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.microconfig.core.resolvers.expression.ExpressionEvaluator.withFunctionsFrom;
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
        public String resolveFor(ComponentWithEnv _1, ComponentWithEnv _2) {
            try {
                return evaluator.evaluate(value);
            } catch (EvaluationException | ParseException e) {
                throw new RuntimeException(e);//todo
            }
        }

        @Override
        public String toString() {
            return "#{" + value + "}";
        }
    }
}