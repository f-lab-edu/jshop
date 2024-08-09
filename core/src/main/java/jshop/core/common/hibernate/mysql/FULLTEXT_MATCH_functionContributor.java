package jshop.core.common.hibernate.mysql;

import static org.hibernate.type.StandardBasicTypes.BOOLEAN;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;

public class FULLTEXT_MATCH_functionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions
            .getFunctionRegistry()
            .registerPattern("fulltext_match", "match(?1) against(?2)",
                functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(BOOLEAN));
    }
}