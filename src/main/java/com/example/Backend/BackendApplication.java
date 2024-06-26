package com.example.Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.endpoints.internal.Value;
import software.amazon.awssdk.services.dynamodb.model.*;

import static org.springframework.security.config.Customizer.withDefaults;

@RestController
@SpringBootApplication
public class BackendApplication implements WebMvcConfigurer {

	public static void main(String[] args) {

		/*
		The below code was run when i first started the server. The table has been created, so we can either use table.exists({tableName})
		to check if the table we want to create exists first.

		DynamoDbClient client = DynamoDbClient.builder().region(Region.US_EAST_2).build();

		CreateTableRequest tableRequest = CreateTableRequest.builder().tableName("Users").attributeDefinitions(
				AttributeDefinition.builder().attributeName("Email").attributeType(ScalarAttributeType.S).build()
		).keySchema(KeySchemaElement.builder().attributeName("Email").keyType(KeyType.HASH).build())
				.provisionedThroughput(ProvisionedThroughput.builder().writeCapacityUnits(1l).readCapacityUnits(1l).build()).build();
		try {
			CreateTableResponse tableResponse = client.createTable(tableRequest);
			System.out.println("Table created successfully" + tableResponse.tableDescription() + tableResponse);
		}
		catch(DynamoDbException excp){
			System.out.println("DynamoDBException status code " + excp.statusCode());
		}
		*/

		SpringApplication.run(BackendApplication.class, args);
	}
	@GetMapping("/")
	public String home() {
		return "home";
	}

//	@GetMapping("/test")
//		public String heyThere(){
//			return "Hey there";
		//}
}
