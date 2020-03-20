package io.microconfig.core.resolvers;

import io.microconfig.core.properties.ComponentWithEnv;
import io.microconfig.core.properties.Resolver;

import java.util.Optional;

public interface RecursiveResolver extends Resolver {
    @Override
    default String resolve(CharSequence value,
                           ComponentWithEnv sourceOfValue,
                           ComponentWithEnv root,
                           String configType) {
        StringBuilder result = new StringBuilder(value);
        while (true) {
            Optional<Statement> optionalStatement = findStatementIn(result);
            if (!optionalStatement.isPresent()) break;

            Statement statement = optionalStatement.get();
            String resolved = statement.resolveFor(sourceOfValue, root, configType);
            result.replace(statement.getStartIndex(), statement.getEndIndex(), resolved);
        }
        return result.toString();
    }

    Optional<Statement> findStatementIn(CharSequence line);

    interface Statement {
        String resolveFor(ComponentWithEnv sourceOfValue, ComponentWithEnv root, String configType);

        int getStartIndex();

        int getEndIndex();
    }
}