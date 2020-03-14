package io.microconfig.domain;

import java.util.Optional;

public interface Resolver {
    Optional<Statement> findStatementIn(CharSequence line);

    interface Statement {
        int getStartIndex();

        int getEndIndex();

        String resolve();
    }
}