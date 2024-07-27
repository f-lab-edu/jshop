package jshop.global.hibernate;

import static org.hibernate.type.StandardBasicTypes.BOOLEAN;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;

public class CustomFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions
            .getFunctionRegistry()
            .registerPattern("json_contains", "json_contains(?1, ?2)",
                functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(BOOLEAN));
    }
}