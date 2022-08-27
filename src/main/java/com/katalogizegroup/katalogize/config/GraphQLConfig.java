package com.katalogizegroup.katalogize.config;

import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLException;
import graphql.GraphqlErrorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;

@Configuration
public class GraphQLConfig {

    @Bean
    public DataFetcherExceptionResolver exceptionResolver() {
        return DataFetcherExceptionResolver.forSingleError((ex, env) -> {
            if (ex instanceof GraphQLException) {
                return GraphqlErrorBuilder.newError(env).message(ex.getMessage()).errorType(ErrorType.ValidationError).build();
                //return GraphqlErrorBuilder.newError().message(ex.getMessage()).build();
            }else{
                return null;
            }
        });
    }
}
