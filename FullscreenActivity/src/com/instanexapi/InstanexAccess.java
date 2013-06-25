package com.instanexapi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Base64;

@SuppressLint("SimpleDateFormat")
public class InstanexAccess {

	private String AppAccessKey = "";
	private String AppSecretKey = "";
	private String ApiToken = "";
	private String BaseURL = "";
	
	public InstanexAccess() {
		super();
	}

	public InstanexAccess(String appAccessKey, String appSecretKey, String apiToken, String baseURL) {
		super();
		AppAccessKey = appAccessKey;
		AppSecretKey = appSecretKey;
		ApiToken = apiToken;
		BaseURL = baseURL;
	}
	
	public String getHostFromURL() {
		return BaseURL.split("//")[1];
	}
	
	public String hashed_string(String input, String secretKey) {
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
	        byte[] key = Base64.decode(secretKey, 0);
	        SecretKeySpec secret_key = new SecretKeySpec(key, "HmacSHA256");
	        sha256_HMAC.init(secret_key);
	
	        String encoded = Base64.encodeToString(sha256_HMAC.doFinal(input.getBytes("UTF-8")), Base64.NO_WRAP);
	        return encoded;
		}
		catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
	}
	
	public String createCurrentDateString() {
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
		Calendar originalDate = Calendar.getInstance(); 
		Date date = originalDate.getTime();
		TimeZone tz = originalDate.getTimeZone();
		long msFromEpochGmt = date.getTime();

		//gives you the current offset in ms from GMT at the current date
		int offsetFromUTC = tz.getOffset(msFromEpochGmt);
		
		Calendar gmtCal = Calendar.getInstance(); 
		gmtCal.setTime(date);
		gmtCal.add(Calendar.MILLISECOND, -offsetFromUTC);
		gmtCal.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		String dateString = format.format(gmtCal.getTime());
		dateString += " GMT";
		return dateString;
	}
	
	public String compute_signature(String accessKey, String currDateString, String httpMethod, String url) {
		StringBuilder sb = new StringBuilder();
		
	    sb.append(accessKey);
	    sb.append("\r\n");
	    sb.append(httpMethod + "\r\n");
	    String shortUrl = url; 
	    sb.append(shortUrl);
	    sb.append("\r\n");
	    sb.append(currDateString);
	    sb.append("\r\n");
	    
	    String unencrypted_signature = sb.toString();
		return unencrypted_signature;
	}
	
	public String getAuthorizationWithUsername(String username, String password) {
		String apiToken = "";
		String userAuth = ""; 
        String jsonmessage = "{\"Username\": \"" + username + "\", \"Password\": \"" + password + "\"}";
        String dateString = this.createCurrentDateString();
        try {
	        String unencoded_signature = this.compute_signature(AppAccessKey, dateString, "POST", "/api/account/token");
	        String encoded = this.hashed_string(unencoded_signature, AppSecretKey);

	        String fullURL = this.BaseURL + "/api/account/token";
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost(fullURL);
	        
	        httppost.addHeader(HTTP.TARGET_HOST, getHostFromURL());
	        httppost.addHeader("Accept", "application/json");
	        httppost.addHeader("Instanext-AccessKey", AppAccessKey);
	        httppost.addHeader("Date", dateString);
	        httppost.addHeader("Instanext-Signature", encoded);
	        httppost.addHeader("Accept-Language", "en-us");
	        httppost.addHeader("Accept-Encoding", "gzip, deflate");
	
	        // Create the POST object and add the parameters
	        StringEntity body = new StringEntity(jsonmessage, HTTP.UTF_8);
	        body.setContentType("application/json; charset=utf-8");
	        
	        httppost.setEntity(body);
	
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
	        userAuth = EntityUtils.toString(response.getEntity());
	        
	        JSONObject resObj = new JSONObject(userAuth);
	        apiToken = resObj.getString("ApiToken");
        } catch (Exception e) {
        	e.printStackTrace();
        	return "";
        }
		return apiToken;
	}
	
	public String[] getCompanies() {
		String[] list = null;
		String dateString = this.createCurrentDateString();
		try {
			String unencoded_signature = this.compute_signature(AppAccessKey, dateString, "GET", "/api/companies");
	        String encoded = this.hashed_string(unencoded_signature, AppSecretKey);
	
	        String fullURL = this.BaseURL + "/api/companies";
			HttpClient httpclient = new DefaultHttpClient();
	        HttpGet httpget = new HttpGet(fullURL);
	        
	        httpget.addHeader(HTTP.TARGET_HOST, "api.instanexdev.com");
	        httpget.addHeader("Accept", "application/json");
	        httpget.addHeader("Instanext-AccessKey", AppAccessKey);
	        httpget.addHeader("Date", dateString);
	        httpget.addHeader("Instanext-Signature", encoded);
	        httpget.addHeader("Instanext-AuthToken", ApiToken);
	        httpget.addHeader("Accept-Language", "en-us");
	        httpget.addHeader("Accept-Encoding", "gzip, deflate");
	        
	        HttpResponse response = httpclient.execute(httpget);
	        
	        String companys = EntityUtils.toString(response.getEntity());
	        
	        JSONArray companyObj = new JSONArray(companys);
	        list = new String[companyObj.length()];
	        for (int i = 0; i < companyObj.length(); i++) {
	        	list[i] = companyObj.getJSONObject(i).getString("Name");
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
