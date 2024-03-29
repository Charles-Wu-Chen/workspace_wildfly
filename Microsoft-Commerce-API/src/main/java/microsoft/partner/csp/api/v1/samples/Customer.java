/*********************************************************
*                                                        * 
*   Copyright (C) Microsoft. All rights reserved.        * 
*                                                        * 
*********************************************************/ 

package microsoft.partner.csp.api.v1.samples;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unchecked")
public class Customer {

	private String saToken;
	private String customerId;
	private String tid;
	private String eTid;
	private String customerToken;

	public Customer(String saToken)
	{
		this.saToken = saToken;
	}
	
    // summary
    // This method is used to get customer info in the Microsoft reseller ecosystem by the reseller
    // summary
    // param name="customerCid", cid of the customer whose information we are trying to retrieve
    // returns: the created customer information: customer cid, customer microsoft id 
	public void getCustomerbyCustomerId(String customerCid)
	{
		String requestUrl = String.format("%s%s%s", PartnerAPiCredentialsProvider.getPropertyValue("ApiEndpoint"), "/", customerCid);
		System.out.println("Request Url = " + requestUrl);
		
		HttpResponse response = null;
		CloseableHttpClient client = HttpClientBuilder.create().build();
		String responseBody;
		HttpGet getRequest = new HttpGet(requestUrl);

		try
		{
			getRequest.addHeader("Accept", "application/json");
			getRequest.addHeader("Authorization", String.format("%s%s", "Bearer ",saToken));
			getRequest.addHeader("x-ms-correlation-id", UUID.randomUUID().toString());
			getRequest.addHeader("x-ms-tracking-id", UUID.randomUUID().toString());
			getRequest.addHeader("api-version", "2015-03-31");
			
			response = client.execute(getRequest);
			responseBody = CrestApiUtilities.parseResponse(response);
			System.out.println("Response for getCustomerbyCustomerId = " + responseBody);
			client.close();
		}
		catch (ClientProtocolException e)
		{
			System.out.println("Client Protocol Exception Occured - " + e.getMessage());
		}
		catch (UnsupportedEncodingException ue)
		{
			System.out.println("Exception occured while creating the request body - " + ue.getMessage());
		} 
		catch (IOException e) 
		{
			System.out.println("IO Exception occured while getting the response - " + e.getMessage());
		} 
	}

	// summary
	// Given the Customer Microsoft ID, and the Reseller Microsoft ID, this method will retrieve the customer cid that can be used to perform transactions on behalf of the customer using the partner APIs
	// summary
	// param name="customerMicrosoftId", Microsoft ID of the customer, this is expected to be available to the reseller, tid
	// param name="resellerMicrosoftId", Microsoft ID of the reseller, etid
	// returns: customer cid that can be used to perform transactions on behalf of the customer by the reseller
	public void getCustomerbyIdentity(String customerMicrosoftId, String resellerMicrosoftId)
	{
		String requestUrl = String.format("%s%s%s%s%s", PartnerAPiCredentialsProvider.getPropertyValue("ApiEndpoint"), "/customers/get-by-identity?provider=AAD&type=external_group&tid=", customerMicrosoftId, "&etid=",resellerMicrosoftId );
		System.out.println("Request Url = " + requestUrl);
		
		HttpResponse response = null;
		CloseableHttpClient client = HttpClientBuilder.create().build();
		String responseBody;

		HttpGet getRequest = new HttpGet(requestUrl);

		try
		{
			getRequest.addHeader("Accept", "application/json");
			getRequest.addHeader("Authorization", String.format("%s%s", "Bearer ",saToken));
			getRequest.addHeader("x-ms-correlation-id", UUID.randomUUID().toString());
			getRequest.addHeader("x-ms-tracking-id", UUID.randomUUID().toString());
			getRequest.addHeader("api-version", "2015-03-31");
			
			response = client.execute(getRequest);
			responseBody = CrestApiUtilities.parseResponse(response);
			System.out.println("Response for getCustomerbyIdentity = " + responseBody);
			client.close();
		}
		catch (ClientProtocolException e)
		{
			System.out.println("Client Protocol Exception Occured - " + e.getMessage());
		}
		catch (UnsupportedEncodingException ue)
		{
			System.out.println("Exception occured while creating the request body - " + ue.getMessage());
		} 
		catch (IOException e) 
		{
			System.out.println("IO Exception occured while getting the response - " + e.getMessage());
		} 
	}
	
    // summary
    // This method is used to get the customer token given a customer cid and the ad token
    // summary
    // param name="customerCid", cid of the customer
    // param name="adAuthorizationToken", active directory authorization token
    // returns : customer authorization token
	public String getCustomerToken(String customerCid, String aadToken)
	{
		String requestUrl = String.format("%s%s%s%s", PartnerAPiCredentialsProvider.getPropertyValue("ApiEndpoint"), "/", customerCid, "/tokens");
		System.out.println("Request Url = " + requestUrl);
		
		HttpResponse response = null;
		CloseableHttpClient client = HttpClientBuilder.create().build();
		String responseBody;

		//Request type is POST
		HttpPost postRequest = new HttpPost(requestUrl);
		try
		{
			postRequest.addHeader("Accept", "application/json");
			postRequest.addHeader("Authorization", String.format("%s%s", "Bearer ",aadToken));
			postRequest.addHeader("x-ms-correlation-id", UUID.randomUUID().toString());
			postRequest.addHeader("x-ms-tracking-id", UUID.randomUUID().toString());
			postRequest.addHeader("api-version", "2015-03-31");
			postRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
			
			List<NameValuePair>requestBody = new ArrayList<NameValuePair>();
			requestBody.add(new BasicNameValuePair("grant_type","client_credentials"));
			postRequest.setEntity(new UrlEncodedFormEntity(requestBody));
			
			response = client.execute(postRequest);
			responseBody = CrestApiUtilities.parseResponse(response);
			client.close();
			
			JSONParser parser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) parser.parse(responseBody);
			customerToken = (String)jsonResponse.get("access_token");
			System.out.println("Customer Token = " + customerToken);
		}
		catch (ClientProtocolException e)
		{
			System.out.println("Client Protocol Exception Occured - " + e.getMessage());
		}
		catch (UnsupportedEncodingException ue)
		{
			System.out.println("Exception occured while creating the request body - " + ue.getMessage());
		} 
		catch (IOException e) 
		{
			System.out.println("IO Exception occured while getting the response - " + e.getMessage());
		} 
		catch (ParseException e) 
		{
			System.out.println("JSON Parse exception occured - " + e.getMessage());
		}
		return customerToken;
	}
	
    // summary
    // This method is used to create a customer in the Microsoft reseller ecosystem by the reseller
    // summary
    // <Internal parameter ="customerJson">customer information: domain, admin credentials for the new tenant, address, primary contact info</param>
    // param name="resellerCid", reseller cid
    // returns: the newly created customer information: all of the above from customer, customer cid, customer microsoft id
	public void createCustomer(String resellerCid)
	{
		String requestUrl = String.format("%s%s%s%s", PartnerAPiCredentialsProvider.getPropertyValue("ApiEndpoint"), "/", resellerCid, "/customers/create-reseller-customer");
		System.out.println("Request Url = " + requestUrl);
		
		HttpResponse response = null;
		CloseableHttpClient client = HttpClientBuilder.create().build();
		String responseBody;
		HttpPost postRequest = new HttpPost(requestUrl);
		
		try
		{
			postRequest.addHeader("Authorization", String.format("%s%s", "Bearer ",saToken));
			postRequest.addHeader("x-ms-correlation-id", UUID.randomUUID().toString());
			postRequest.addHeader("x-ms-tracking-id", UUID.randomUUID().toString());
			postRequest.addHeader("api-version", "2015-03-31");
			postRequest.addHeader("Content-Type", "application/json");

			JSONObject customerJson = createCutomerProfile();
			StringEntity se = new StringEntity(customerJson.toString());
			postRequest.setEntity(se);

			response = client.execute(postRequest);
			responseBody = CrestApiUtilities.parseResponse(response);
			client.close();
			
			System.out.println("Response Body = " + responseBody);

			parseJSONResponse(responseBody);
		}
		catch (ClientProtocolException e)
		{
			System.out.println("Client Protocol Exception Occured - " + e.getMessage());
		}
		catch (UnsupportedEncodingException ue)
		{
			System.out.println("Exception occured while creating the request body - " + ue.getMessage());
		} 
		catch (IOException e) 
		{
			System.out.println("IO Exception occured while getting the response - " + e.getMessage());
		} 
		catch (ParseException e) 
		{
			System.out.println("JSON Parse exception occured - " + e.getMessage());
		}
	}
	private JSONObject createCutomerProfile()
	{
		JSONObject customerAddress = new JSONObject();
		
		System.out.println(" ====================================");
		System.out.println(" Create a new customer");
		System.out.println(" ====================================");
		
		System.out.println("Enter domain prefix to be created : ");
		Scanner scanner = new Scanner(System.in);
		String domainPrefix = scanner.nextLine();
		
		System.out.println("Company Name\t: ");
		String company = scanner.nextLine();
		
		System.out.println("First Name\t: ");
		String fName = scanner.nextLine();

		System.out.println("Last Name\t: ");
		String lName = scanner.nextLine();

		System.out.println("Email\t\t: ");
		String email = scanner.nextLine();

		System.out.println("Address Line1\t:");
		String addrLine1 = scanner.nextLine();

		System.out.println("Address Line2\t:");
		String addrLine2 = scanner.nextLine();
		
		System.out.println("City\t\t:");
		String city = scanner.nextLine();
		
		System.out.println("State (example :WA, TX...)\t\t:");
		String state = scanner.nextLine();
		
		System.out.println("ZipCode\t\t:");
		String zip = scanner.nextLine();
		
		customerAddress.put("first_name", fName);
		customerAddress.put("last_name", lName);
		customerAddress.put("address_line1", addrLine1);
		customerAddress.put("address_line2", addrLine2);
		customerAddress.put("city", city);
		customerAddress.put("region", state);
		customerAddress.put("postal_code", zip);
		customerAddress.put("country", "AU");
		//customerAddress.put("country", "US");

		
		JSONObject customerProfile = new JSONObject();
		customerProfile.put("email", email);
		customerProfile.put("type", "organization");
		customerProfile.put("company_name", company);
		customerProfile.put("culture", "en-US");
		customerProfile.put("language", "en");
		customerProfile.put("default_address", customerAddress);

		JSONObject customerDetails = new JSONObject();
		customerDetails.put("domain_prefix", domainPrefix);
		customerDetails.put("user_name", "admin");
		customerDetails.put("password", "Password!1");
		customerDetails.put("profile", customerProfile);
		
		System.out.println("Request Body = " + customerDetails );
		
		scanner.close();

		return customerDetails;
	}
	
	/*
	 * 
	 * Response Body = {"domain_prefix":"TestApiDomain1","user_name":"admin","password":"Password!1",
	 * "customer":{"id":"ceb40434-61e1-4d4b-9189-e61daca86d93",
	 * "identity":{"provider":"AAD","type":"external_group",
	 * "data":{"tid":"95217330-4530-4614-b3f7-0fc2832f8e23",
	 * "etid":"85bbe446-e7fa-4814-99b8-cd49210b4bfe",
	 * "eoid":"e09f2651-d25f-4c2a-a5f4-2e10a4763b9d"}},
	 * "is_test":false,
	 * "links":{"self":{"href":"customers/ceb40434-61e1-4d4b-9189-e61daca86d93","method":"GET"},
	 * "profiles":{"href":"ceb40434-61e1-4d4b-9189-e61daca86d93/profiles","method":"GET"},
	 * "addresses":{"href":"ceb40434-61e1-4d4b-9189-e61daca86d93/addresses","method":"GET"}},
	 * "object_type":"Customer"},"profile":{"email":"charles.wu@melbourneit.com.au",
	 * "company_name":"MIT","language":"en","culture":"en-US",
	 * "customer_id":"ceb40434-61e1-4d4b-9189-e61daca86d93",
	 * "id":"70122ab3-6614-4524-acc4-ce320d891171",
	 * "snapshot_id":"70122ab3-6614-4524-acc4-ce320d891171/1","type":"organization",
	 * "default_address":{"id":"fe87946d-d935-4d74-9b7b-c5094fa5e0d8","country":"AU","region":"Victoria","city":"Melbourne","address_line1":"level 3","address_line2":"469 Latrob","address_line3":"","postal_code":"3000","first_name":"Charles","last_name":"Wu",
	 * "links":{"self":{"href":"ceb40434-61e1-4d4b-9189-e61daca86d93/addresses/fe87946d-d935-4d74-9b7b-c5094fa5e0d8","method":"GET"}},"object_type":"Address"},
	 * "etag":"2693096514209040356","links":{"self":{"href":"ceb40434-61e1-4d4b-9189-e61daca86d93/profiles/70122ab3-6614-4524-acc4-ce320d891171","method":"GET"},"snapshot":{"href":"ceb40434-61e1-4d4b-9189-e61daca86d93/profiles/70122ab3-6614-4524-acc4-ce320d891171/1","method":"GET"},"update":{"href":"ceb40434-61e1-4d4b-9189-e61daca86d93/profiles/70122ab3-6614-4524-acc4-ce320d891171","method":"PUT"}},
	 * "object_type":"OrganizationProfile"}}
etid = 85bbe446-e7fa-4814-99b8-cd49210b4bfe
Customer Id = ceb40434-61e1-4d4b-9189-e61daca86d93
	 */
	private void parseJSONResponse(String jsonResponse) throws ParseException
	{
		JSONParser parser = new JSONParser();
		JSONObject response = (JSONObject) parser.parse(jsonResponse);
		JSONObject customer = (JSONObject)response.get("customer");

		customerId = (String)customer.get("id"); //persist these values for later use

		JSONObject identity = (JSONObject)customer.get("identity");
		JSONObject data = (JSONObject)identity.get("data");

		tid = (String)data.get("tid"); //persist these values for later use
		eTid = (String)data.get("etid"); //persist these values for later use
	}
	
	public String getCustomerId()
	{
		System.out.println("Customer Id = " + customerId);
		return customerId;
	}
	public String getTid()
	{
		System.out.println("tid = " + tid);
		return tid;
	}
	public String getEtid()
	{
		System.out.println("etid = " + eTid);
		return eTid;
	}

}
