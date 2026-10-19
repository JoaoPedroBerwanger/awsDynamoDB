package br.edu.utfpr.to.tsi.projetoTeste;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
 
public class App 
{
    public static void main( String[] args )
    {
    	
        System.out.println( "Hello World!" );
    }
    
    public static void gavaRegistroTabela(DynamoDbClient ddb,
            String tabela,
            String id,
            String idVal,
            String timestramp,
            String timestrampValue,
            String temperatura,
            String temperaturaValue,
            String umidade,
            String umidadeVal,
            String localizacao,
            String localizacaoValue){

		HashMap<String,AttributeValue> itemValues = new HashMap<String,AttributeValue>();
		
		itemValues.put(id, AttributeValue.builder().s(idVal).build());
		itemValues.put(timestramp, AttributeValue.builder().s(timestrampValue).build());
		itemValues.put(temperatura, AttributeValue.builder().s(temperaturaValue).build());
		itemValues.put(umidade, AttributeValue.builder().s(umidadeVal).build());
		itemValues.put(localizacao, AttributeValue.builder().s(localizacaoValue).build());
		
		PutItemRequest request = PutItemRequest.builder().tableName(tabela).item(itemValues).build();
		
		try {
			ddb.putItem(request);
			System.out.println(tabela +" was successfully updated");
		
		} catch (ResourceNotFoundException e) {
			System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tabela);
			System.err.println("Be sure that it exists and that you've typed its name correctly!");
			System.exit(1);
		} catch (DynamoDbException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
    }
    
    public static void buscarDadoBanco(DynamoDbClient ddb,String tableName,String key,String keyVal ) {

        HashMap<String,AttributeValue> keyToGet = new HashMap<String,AttributeValue>();

        keyToGet.put(key, AttributeValue.builder().s(keyVal).build());

        GetItemRequest request = GetItemRequest.builder().key(keyToGet).tableName(tableName).build();

        try {
            Map<String,AttributeValue> returnedItem = ddb.getItem(request).item();

            if (returnedItem != null) {
                Set<String> keys = returnedItem.keySet();
                System.out.println("Amazon DynamoDB table attributes: \n");

                for (String key1 : keys) {
                    System.out.format("%s: %s\n", key1, returnedItem.get(key1).toString());
                }
            } else {
                System.out.format("No item found with the key %s!\n", key);
            }
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    
    public static void updateTableItem(DynamoDbClient ddb,
            String tableName,
            String key,
            String keyVal,
            String name,
		    String updateVal)
    {
		
		HashMap<String,AttributeValue> itemKey = new HashMap<String,AttributeValue>();
		
		itemKey.put(key, AttributeValue.builder().s(keyVal).build());
		
		HashMap<String,AttributeValueUpdate> updatedValues =
		new HashMap<String,AttributeValueUpdate>();
		
		// Update the column specified by name with updatedVal
		updatedValues.put(name, AttributeValueUpdate.builder()
		.value(AttributeValue.builder().s(updateVal).build())
		.action(AttributeAction.PUT)
		.build());
		
		UpdateItemRequest request = UpdateItemRequest.builder()
		.tableName(tableName)
		.key(itemKey)
		.attributeUpdates(updatedValues)
		.build();
		
		try {
		ddb.updateItem(request);
		} catch (ResourceNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (DynamoDbException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		
			System.out.println("Done!");
		}
}
