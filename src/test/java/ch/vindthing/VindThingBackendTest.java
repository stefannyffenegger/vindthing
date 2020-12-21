package ch.vindthing;

import ch.vindthing.model.Item;
import ch.vindthing.model.Store;
import ch.vindthing.payload.request.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import java.net.URI;
import java.net.URISyntaxException;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= WebEnvironment.RANDOM_PORT)
public class VindThingBackendTest {
	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	int randomServerPort;

	private String token;
	static String storeId;
	static String itemId;

	@Before
	@Test
	public void testLoginSuccess() throws URISyntaxException {
		final String baseUrl = "http://localhost:"+randomServerPort+"/api/auth/signin";
		URI uri = new URI(baseUrl);
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("test@vt.ch");
		loginRequest.setPassword("test1234");

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);
		ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);

		//Save jwt for later
		token = result.getBody().subSequence(10, 190).toString();

		Assert.assertEquals(200, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody().contains("token"));
		Assert.assertNotNull(token);
	}

	@Test
	public void testLoginWrongCredentials() throws URISyntaxException {
		final String baseUrl = "http://localhost:"+randomServerPort+"/api/auth/signin";
		URI uri = new URI(baseUrl);
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("fail@vt.ch");
		loginRequest.setPassword("blah1234");

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);
		ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);

		Assert.assertEquals(401, result.getStatusCodeValue());
	}

	@Test
	public void testProfileUpdate() throws URISyntaxException {
		final String baseUrl = "http://localhost:"+randomServerPort+"/api/auth/profile/update";
		URI uri = new URI(baseUrl);
		ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest(null, "jUnit",
				"test1234");

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+token);

		HttpEntity<ProfileUpdateRequest> request = new HttpEntity<>(profileUpdateRequest, headers);
		ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);

		Assert.assertEquals(200, result.getStatusCodeValue());
	}

	@Test
	public void testProfileGet() throws URISyntaxException {
		final String baseUrl = "http://localhost:"+randomServerPort+"/api/auth/profile/get";
		URI uri = new URI(baseUrl);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);

		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

		Assert.assertEquals(200, result.getStatusCodeValue());
	}

	@Test
	public void test1StoreAdd() throws URISyntaxException {
		final String baseUrl = "http://localhost:"+randomServerPort+"/api/store/add";
		URI uri = new URI(baseUrl);
		StoreAddRequest storeAddRequest = new StoreAddRequest("Test Bookshelf", "For books",
				"Living room");

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);

		HttpEntity<StoreAddRequest> request = new HttpEntity<>(storeAddRequest, headers);
		ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

		ObjectMapper mapper = new ObjectMapper();
		try {
			Store store = mapper.readValue(result.getBody(), Store.class);
			storeId = store.getId();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		Assert.assertEquals(201, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody().contains("Test Bookshelf"));
		Assert.assertNotNull(storeId);
	}

	@Test
	public void test2StoreUpdate() throws URISyntaxException {
		final String baseUrl = "http://localhost:"+randomServerPort+"/api/store/update";
		URI uri = new URI(baseUrl);

		StoreUpdateRequest storeUpdateRequest = new StoreUpdateRequest(storeId,"Test Bookshelf Updated",
				"For books updated", "Living room updated");

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);

		HttpEntity<StoreUpdateRequest> request = new HttpEntity<>(storeUpdateRequest, headers);
		ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);

		Assert.assertEquals(200, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody().contains("Test Bookshelf Updated"));
	}

	@Test
	public void test2StoreCommentAdd() throws URISyntaxException {
		final String baseUrl = "http://localhost:"+randomServerPort+"/api/store/comment/add";
		URI uri = new URI(baseUrl);
		CommentAddRequest commentAddRequest = new CommentAddRequest("This is a test comment", storeId);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);

		HttpEntity<CommentAddRequest> request = new HttpEntity<>(commentAddRequest, headers);
		ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

		Assert.assertEquals(200, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody().contains("This is a test comment"));
	}

	@Test
	public void test2ItemAdd() throws URISyntaxException {
		final String baseUrl = "http://localhost:"+randomServerPort+"/api/item/add";
		URI uri = new URI(baseUrl);
		ItemAddRequest itemAddRequest = new ItemAddRequest("Java for dummies", "Test Item", 2,
				storeId);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);

		HttpEntity<ItemAddRequest> request = new HttpEntity<>(itemAddRequest, headers);
		ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

		ObjectMapper mapper = new ObjectMapper();
		try {
			Item item = mapper.readValue(result.getBody(), Item.class);
			itemId = item.getId();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		Assert.assertEquals(201, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody().contains("Java for dummies"));
	}

	@Test
	public void test3ItemUpdate() throws URISyntaxException {
		final String baseUrl = "http://localhost:"+randomServerPort+"/api/item/update";
		URI uri = new URI(baseUrl);
		ItemUpdateRequest itemUpdateRequest = new ItemUpdateRequest(itemId, "Java for dummies updated",
				"Test Item update", 3, false);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);

		HttpEntity<ItemUpdateRequest> request = new HttpEntity<>(itemUpdateRequest, headers);
		ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

		Assert.assertEquals(200, result.getStatusCodeValue());
		Assert.assertTrue(result.getBody().contains("Java for dummies updated"));
	}

	@Test
	public void test3ItemUpdateWrongParameters() throws URISyntaxException {
		final String baseUrl = "http://localhost:"+randomServerPort+"/api/item/update";
		URI uri = new URI(baseUrl);
		ItemUpdateRequest itemUpdateRequest = new ItemUpdateRequest("12344klsj3", "Java for dummies updated",
				"Test Item update", 3, false);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);

		HttpEntity<ItemUpdateRequest> request = new HttpEntity<>(itemUpdateRequest, headers);
		ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

		Assert.assertEquals(400, result.getStatusCodeValue());
	}

	@Test
	public void testGetAll() throws URISyntaxException {
		final String baseUrl = "http://localhost:"+randomServerPort+"/api/store/get-all";
		URI uri = new URI(baseUrl);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);

		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

		Assert.assertEquals(200, result.getStatusCodeValue());
	}
}