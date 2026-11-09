package br.edu.utfpr.td.tsi.crud_dynamo.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.edu.utfpr.td.tsi.crud_dynamo.model.LeituraSensor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.IgnoreNullsMode;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Repository
public class SensorRepository {

	private final DynamoDbTable<LeituraSensor> tabela;

	public SensorRepository(DynamoDbTable<LeituraSensor> tabela) {
		this.tabela = tabela;
	}

	private Key chave(String sensorId, Long timestamp) {
		return Key.builder().partitionValue(sensorId).sortValue(timestamp).build();
	}

	public void inserir(LeituraSensor leitura) {
		
		Expression naoExiste = Expression.builder().expression("attribute_not_exists(SensorID)").build();
		
		tabela.putItem(PutItemEnhancedRequest.builder(LeituraSensor.class)
				.item(leitura)
				.conditionExpression(naoExiste).build());
	}

	public LeituraSensor atualizar(LeituraSensor leitura) {
		
		Expression existe = Expression.builder().expression("attribute_exists(SensorID)").build();
		
		return tabela.updateItem(UpdateItemEnhancedRequest.builder(LeituraSensor.class)
				.item(leitura)
				.ignoreNullsMode(IgnoreNullsMode.SCALAR_ONLY)
				.conditionExpression(existe).build());
	}

	public Optional<LeituraSensor> remover(String sensorId, Long timestamp) {
		return Optional.ofNullable(tabela.deleteItem(chave(sensorId, timestamp)));
	}

	public List<LeituraSensor> buscarPorSensor(String sensorId) {
		QueryConditional condicao = QueryConditional.keyEqualTo(Key.builder().partitionValue(sensorId).build());
		
		return tabela.query(condicao).items().stream().toList();
	}

	public Optional<LeituraSensor> buscarPorSensorETimestamp(String sensorId, Long timestamp) {
		return Optional.ofNullable(tabela.getItem(chave(sensorId, timestamp)));
	}

	public List<LeituraSensor> buscarPorLocalizacao(String localizacao) {
		QueryConditional condicao = QueryConditional.keyEqualTo(Key.builder().partitionValue(localizacao).build());
		
		return tabela.index(LeituraSensor.INDICE_LOCALIZACAO)
				.query(condicao).stream()
				.flatMap(pagina -> pagina.items().stream())
				.toList();
	}

	public List<LeituraSensor> buscarPorSensorELocalizacao(String sensorId, String localizacao) {
		QueryConditional condicao = QueryConditional.keyEqualTo(Key.builder().partitionValue(sensorId).build());
		
		Expression filtro = Expression.builder()
				.expression("Localizacao = :loc")
				.expressionValues(Map.of(":loc", AttributeValue.fromS(localizacao)))
				.build();
		
		QueryEnhancedRequest request = QueryEnhancedRequest.builder().
				queryConditional(condicao).filterExpression(filtro).build();
		
		return tabela.query(request).items().stream().toList();
	}

	public List<LeituraSensor> listarTodos() {
		return tabela.scan().items().stream().toList();
	}
}
