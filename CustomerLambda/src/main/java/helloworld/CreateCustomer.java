package helloworld;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import helloworld.dto.Customer;

/**
 * Handler for requests to Lambda function.
 */
public class CreateCustomer {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());
	private static final Table table = dynamoDB.getTable(System.getenv("CUSTOMERS_TABLE"));

	public APIGatewayProxyResponseEvent handleCreateCustomerRequest(APIGatewayProxyRequestEvent request)
			throws JsonMappingException, JsonProcessingException {
		String body = request.getBody();
		if (StringUtils.isNullOrEmpty(body)) {
			throw new IllegalArgumentException("Invalid payload to the lambda");
		}
		Customer customer = OBJECT_MAPPER.readValue(body, Customer.class);
		Item item = new Item().withPrimaryKey("id", customer.getId()).withString("firstName", customer.getFirstName())
				.withString("lastName", customer.getLastName()).withString("email", customer.getEmail());
		table.putItem(item);
		return new APIGatewayProxyResponseEvent()
				.withBody("Customer with id " + customer.getId() + " has been created successfully")
				.withStatusCode(200);
	}
}