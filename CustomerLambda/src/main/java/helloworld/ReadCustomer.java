package helloworld;

import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import helloworld.dto.Customer;

public class ReadCustomer {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.defaultClient();
//	private static final Table table = dynamoDB.getTable("CUSTOMER");

	public APIGatewayProxyResponseEvent handleReadCustomer(APIGatewayProxyRequestEvent request)
			throws JsonProcessingException {
		ScanResult scan = dynamoDBClient.scan(new ScanRequest().withTableName(System.getenv("CUSTOMERS_TABLE")));

		List<Customer> customers = scan.getItems().stream()
				.map(item -> new Customer(Integer.parseInt(item.get("id").getN()), item.get("firstName").getS(),
						item.get("lastName").getS(), item.get("email").getS()))
				.toList();
		String json = OBJECT_MAPPER.writeValueAsString(customers);
		return new APIGatewayProxyResponseEvent().withBody(json).withStatusCode(200);
	}
}