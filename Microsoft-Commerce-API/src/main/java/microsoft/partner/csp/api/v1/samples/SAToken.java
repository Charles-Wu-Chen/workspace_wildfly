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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SAToken {

	private String aadToken;
	private String saToken;
	public SAToken(String aadToken)
	{
		this.aadToken = aadToken;
	}
	public String getSAToken()
	{
		List<NameValuePair> requestBody = new ArrayList<NameValuePair>();
		String requestUrl = String.format("%s%s", PartnerAPiCredentialsProvider.getPropertyValue("ApiEndpoint"), "/my-org/tokens");
		System.out.println("Request Url = " + requestUrl);
		
		HttpResponse response = null;
		CloseableHttpClient client = HttpClientBuilder.create().build();
		String responseBody;
		HttpPost postRequest = new HttpPost(requestUrl);

		try
		{
			requestBody.add(new BasicNameValuePair("grant_type", "client_credentials"));
			postRequest.setEntity(new UrlEncodedFormEntity(requestBody));

			StringBuffer authorization = new StringBuffer("Bearer ");
			authorization.append(aadToken);
			postRequest.addHeader("Accept", "application/json");
			postRequest.addHeader("Authorization", authorization.toString());
			
			response = client.execute(postRequest);
			responseBody = CrestApiUtilities.parseResponse(response);
			System.out.println("SAToken Response Body = " + responseBody);
			
			JSONParser parser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) parser.parse(responseBody);
			saToken = (String)jsonResponse.get("access_token");
			
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
		catch (ParseException e) 
		{
			System.out.println("JSON Parse exception occured - " + e.getMessage());
		}
		System.out.println("SAToken = " + saToken);
		return saToken;
	}
}
