package br.edu.utfpr.td.tsi.crud_dynamo.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.edu.utfpr.td.tsi.crud_dynamo.model.LeituraSensor;
import br.edu.utfpr.td.tsi.crud_dynamo.repository.SensorRepository;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

@RestController
public class SensorController {

	private final SensorRepository repository;

	public SensorController(SensorRepository repository) {
		this.repository = repository;
	}

	@PostMapping("/sensores")
	public ResponseEntity<LeituraSensor> inserir(@RequestBody LeituraSensor leitura) {
		
		if (leitura.getSensorId() == null || leitura.getSensorId().isBlank() || leitura.getTimestamp() == null) {
			return ResponseEntity.badRequest().build();
		}
		
		repository.inserir(leitura);
		URI local = URI.create("/sensores/" + leitura.getSensorId() + "/" + leitura.getTimestamp());
		
		return ResponseEntity.created(local).body(leitura);
	}

	@PutMapping("/sensores/{sensorId}/{timestamp}")
	public LeituraSensor atualizar(@PathVariable String sensorId, @PathVariable Long timestamp, @RequestBody LeituraSensor dados) {
		
		dados.setSensorId(sensorId);
		dados.setTimestamp(timestamp);
		
		return repository.atualizar(dados);
	}

	@DeleteMapping("/sensores/{sensorId}/{timestamp}")
	public ResponseEntity<Void> remover(@PathVariable String sensorId, @PathVariable Long timestamp) {
		
		return repository.remover(sensorId, timestamp)
				.map(removido -> ResponseEntity.noContent().<Void>build())
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/sensores/{sensorId}")
	public List<LeituraSensor> buscarPorSensor(@PathVariable String sensorId) {
		return repository.buscarPorSensor(sensorId);
	}

	@GetMapping("/sensores/{sensorId}/{timestamp}")
	public ResponseEntity<LeituraSensor> buscarPorSensorETimestamp(@PathVariable String sensorId, @PathVariable Long timestamp) {
		return ResponseEntity.of(repository.buscarPorSensorETimestamp(sensorId, timestamp));
	}

	@GetMapping("/localizacoes/{localizacao}/sensores")
	public List<LeituraSensor> buscarPorLocalizacao(@PathVariable String localizacao) {
		return repository.buscarPorLocalizacao(localizacao);
	}

	@GetMapping("/localizacoes/{localizacao}/sensores/{sensorId}")
	public List<LeituraSensor> buscarPorSensorELocalizacao(@PathVariable String localizacao, @PathVariable String sensorId) {
		return repository.buscarPorSensorELocalizacao(sensorId, localizacao);
	}

	@GetMapping("/sensores")
	public List<LeituraSensor> listarTodos() {
		return repository.listarTodos();
	}

	@ExceptionHandler(ConditionalCheckFailedException.class)
	public ResponseEntity<String> condicaoFalhou(ConditionalCheckFailedException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body("Operação recusada: o item já existe (POST) ou não existe (PUT).");
	}
}
