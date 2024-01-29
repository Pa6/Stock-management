package com.productmanagement.controller;



import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;

@RestController
public class GraphQLController {
	private final GraphQL graphQL;

    public GraphQLController(GraphQL graphQL) {
        this.graphQL = graphQL;
    }

    
    @PostMapping("/graphql")
    @PreAuthorize("hasAuthority('USER')")
    public Object graphql(@RequestBody String query) {
        try {
        	ExecutionResult executionResult = graphQL.execute(ExecutionInput.newExecutionInput().query(query).build());
            return executionResult.toSpecification();
        }catch (AccessDeniedException e) {
			return null;
		}
    }
}
