package com.productmanagement.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.errors.SchemaProblem;

@Configuration
public class GraphQLConfig {

	@Bean
	public GraphQL graphQL() {
		return GraphQL.newGraphQL(newSchema()).build();
	}

	private GraphQLSchema newSchema() {
//		try {
//			File file = new File("./src/main/resources/GraphQl/schema.graphqls");
//			byte[] fileBytes = Files.readAllBytes(file.toPath());
//			String fileContent = new String(fileBytes, StandardCharsets.UTF_8);
//			System.err.println("!!!!!!!!!!!!!!!!" + fileContent);
//		} catch (Exception e) {
//			// TODO: handle exception
//			System.err.println(e);
//		}

		Resource schemaResource = new ClassPathResource("GraphQl/schema.graphqls");
		TypeDefinitionRegistry typeRegistry;
		try {
			typeRegistry = new SchemaParser().parse(schemaResource.getInputStream());
			RuntimeWiring wiring = buildWiring();
			SchemaGenerator schemaGenerator = new SchemaGenerator();
			return schemaGenerator.makeExecutableSchema(typeRegistry, wiring);
		} catch (SchemaProblem e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private RuntimeWiring buildWiring() {
		// Define your data fetchers and resolver mappings here
		return RuntimeWiring.newRuntimeWiring().build();
	}
}
