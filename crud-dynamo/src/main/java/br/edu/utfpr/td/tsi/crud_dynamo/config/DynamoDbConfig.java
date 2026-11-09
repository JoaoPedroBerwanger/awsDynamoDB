package br.edu.utfpr.td.tsi.crud_dynamo.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.edu.utfpr.td.tsi.crud_dynamo.model.LeituraSensor;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbConfig {

	@Value("${aws.region}")
	private String regiao;

	@Value("${aws.dynamodb.endpoint}")
	private String endpoint;

	@Value("${aws.dynamodb.tabela}")
	private String nomeTabela;

	@Bean
	public DynamoDbClient dynamoDbClient() {
		return DynamoDbClient.builder()
				.region(Region.of(regiao))
				.endpointOverride(URI.create(endpoint))
				.credentialsProvider(DefaultCredentialsProvider.builder().build())
				.build();
	}

	@Bean
	public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
		return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
	}

	@Bean
	public DynamoDbTable<LeituraSensor> tabelaSensores(DynamoDbEnhancedClient enhancedClient) {
		return enhancedClient.table(nomeTabela, TableSchema.fromBean(LeituraSensor.class));
	}
}
