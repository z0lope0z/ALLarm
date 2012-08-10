package com.rivaledge.cloudyrec;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.http.client.ClientProtocolException;

import org.json.*;

public class CloudyRecResource {

	protected String appKey = "ZBzNYrRUZP";
	protected int version = 0;
	protected String id = null;
	protected CloudyRecConnector conx;
	private boolean debug = true;

	public CloudyRecResource() {
	}

	public String getAppKey() {
		return appKey;
	}

	public int getVersion() {
		return version;
	}

	public String getId() {
		return id;
	}

	public String getResourceKey() {
		// TODO: to be overwritten by Sub Class
		return null;
	}

	protected JSONArray listFromCloud(String query)
			throws ClientProtocolException, IOException, URISyntaxException,
			Exception {
		return listFromCloud(query,10,1);
	}

	protected JSONArray listFromCloud(String query, int limit, int page)
			throws ClientProtocolException, IOException, URISyntaxException,
			Exception {

		if (limit < 0) {
			limit = 10;
		}
		if (page < 0) {
			page = 1;
		}

		Hashtable<String, Object> data = new Hashtable<String, Object>();
		data.put("limit", limit);
		data.put("page", page);

		conx = new CloudyRecConnector();
		SimpleHttpResponse resp;

		if (query != null) {
			data.put("query", query);
		} 
		
		resp = checkAndRaiseError(conx.httpGet("/" + version + "/" + appKey + "/"
					+ getResourceKey(), data));
		
		return new JSONArray(resp.getBody()); // Expecting array
	}

	protected JSONArray listChildren(
			Class<? extends CloudyRecResource> resourceClass)
			throws ClientProtocolException, IOException, URISyntaxException,
			Exception {
		return listChildren(resourceClass, 10, 1);
	}

	protected JSONArray listChildren(
			Class<? extends CloudyRecResource> resourceClass, int limit,
			int page) throws ClientProtocolException, IOException,
			URISyntaxException, Exception {
		if (limit < 0) {
			limit = 10;
		}
		if (page < 0) {
			page = 0;
		}

		Hashtable<String, Object> data = new Hashtable<String, Object>();
		data.put("limit", limit);
		data.put("page", page);

		conx = new CloudyRecConnector();
		SimpleHttpResponse resp;
		resp = checkAndRaiseError(conx.httpGet("/" + version + "/" + appKey + "/"
				+ resourceClass.newInstance().getResourceKey() + "/cof/" + id, data));
		return new JSONArray(resp.getBody());
	}

	protected JSONObject loadFromCloud(String id)
			throws ClientProtocolException, IOException, URISyntaxException,
			Exception {
		conx = new CloudyRecConnector();
		SimpleHttpResponse resp;

		resp = checkAndRaiseError(conx.httpGet("/" + version + "/" + appKey + "/"
				+ getResourceKey() + "/" + id, null));
		return new JSONObject(resp.getBody());
	}

	protected JSONObject authenticateToCloud(String uname, String passwd)
			throws ClientProtocolException, IOException, URISyntaxException,
			Exception {
		conx = new CloudyRecConnector();
		SimpleHttpResponse resp;
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		data.put("_uname", uname);
		data.put("_passwd", passwd);

		resp = checkAndRaiseError(conx.httpPost("/" + version + "/" + appKey + "/"
				+ getResourceKey() + "/auth", data));
		return new JSONObject(resp.getBody());
	}

	protected String insertToCloud(Hashtable data)
			throws ClientProtocolException, IOException, URISyntaxException,
			Exception {
		conx = new CloudyRecConnector();
		return checkAndRaiseError(
				conx.httpPost("/" + version + "/" + appKey + "/" + getResourceKey(), data))
				.getBody();
	}

	protected String updateToCloud(Hashtable data)
			throws ClientProtocolException, IOException, URISyntaxException,
			Exception {
		conx = new CloudyRecConnector();
		return checkAndRaiseError(
				conx.httpPut("/" + version + "/" + appKey + "/" + getResourceKey() + "/" + id,
						data)).getBody();
	}

	protected boolean deleteFromCloud() throws ClientProtocolException,
			IOException, URISyntaxException, Exception {
		conx = new CloudyRecConnector();
		return (checkAndRaiseError(
				conx.httpDelete("/" + version + "/" + appKey + "/" + getResourceKey() + "/"
						+ id)).getBody().length() == 0);
	}

	protected SimpleHttpResponse checkAndRaiseError(SimpleHttpResponse resp)
			throws Exception {
		JSONObject job;

		switch (resp.getStatus()) {
		case 401:
			job = new JSONObject(resp.getBody());
			throw new InvalidAppOrResourceException(job.get("message")
					.toString());
		case 403:
			job = new JSONObject(resp.getBody());
			throw new ForbiddenException(job.get("message").toString());
		case 404:
			job = new JSONObject(resp.getBody());
			throw new ResourceNotFoundException(job.get("message").toString());
		case 406:
			job = new JSONObject(resp.getBody());
			throw new UnmetConditionException(job.get("message").toString());
		case 408:
			job = new JSONObject(resp.getBody());
			throw new InternalTimeoutException(job.get("message").toString());
		case 500:
			job = new JSONObject(resp.getBody());
			throw new InternalServerException(job.get("message").toString());
		}
		return resp;
	}

	protected void debug(String message) {
		if (this.debug == true)
			System.out.println(message);
	}

	protected String combine(ArrayList<String> s, String glue)
	{
		int k=s.size();
		if (k==0)
			return null;

		StringBuilder out=new StringBuilder();
		out.append(s.get(0).toString());
		for (int x=1;x<k;++x)
			out.append(glue).append(s.get(x).toString());

		return out.toString();
	}

}
