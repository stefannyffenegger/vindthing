package ch.vindthing;

import ch.vindthing.model.Store;
import ch.vindthing.model.User;
import ch.vindthing.payload.request.LoginRequest;
import ch.vindthing.payload.request.ProfileUpdateRequest;
import ch.vindthing.payload.request.SignupRequest;
import ch.vindthing.payload.request.StoreAddRequest;
import ch.vindthing.payload.response.StoreResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= WebEnvironment.RANDOM_PORT)
public class VindThingBackendTest {
	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	int randomServerPort;

	private String token;
	private String storeId;
	private String itemId;

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
	public void testStoreAdd() throws URISyntaxException {
		final String baseUrl = "http://localhost:"+randomServerPort+"/api/store/add";
		URI uri = new URI(baseUrl);
		StoreAddRequest storeAddRequest = new StoreAddRequest("Test Bookshelf", "For books",
				"Living room");

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);

		HttpEntity<StoreAddRequest> request = new HttpEntity<>(storeAddRequest, headers);
		ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

		System.out.println(result.getBody());

		Assert.assertEquals(201, result.getStatusCodeValue());
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