package com.rivaledge.cloudyrec;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

public class CloudyRecFstore extends CloudyRecResource{
	
	/**
	 * Default constructor
	 */
	public CloudyRecFstore(){}
	
	/**
	 * Upload given file to FStore
	 * @param data Array of bytes of file to upload.
	 * @param fileName Name of file to save for Fstore record.
	 * @param contentType Content type of uploaded file. Set null for default.
	 * @param id Optional Id of existing Fstore record to replace with uploaded file.
	 * @return String representing Id of saved/updated Fstore record.
	 */
	public String upload(byte[] data,String fileName, String contentType,String id)
			throws ClientProtocolException, IOException, URISyntaxException,
			Exception {
		SimpleHttpResponse resp;
		conx = new CloudyRecConnector();
		contentType = contentType == null ? "application/octet-stream" : contentType;
		
		if(id == null){
			resp = checkAndRaiseError(
					conx.httpPostMultipart("/" + version + "/" + appKey + "/fstore",
							data,fileName,contentType));
		}else{
			resp = checkAndRaiseError(
					conx.httpPutMultipart("/" + version + "/" + appKey + "/fstore/" + id,
							data,fileName,contentType));
		}
		
		return resp.getBody();
	}
	
	/**
	 * Get first page of all Fstore items with limit 10.
	 * @return
	 */
	public List<Hashtable<String,Object>> list()
			throws ClientProtocolException, IOException, URISyntaxException,
			Exception {		
		return list(null,10,1);
	}
	
	/**
	 * List existing Fstore records
	 * @param query Record filter conditions.
	 * @param limit Maximum number of records to retrieve.
	 * @param page Page number to retrieve for pagination.
	 * @return List of Hashes.
	 */
	public List<Hashtable<String, Object>> list(String query, int limit, int page)
			throws ClientProtocolException, IOException, URISyntaxException,
			Exception {
		
		List<Hashtable<String,Object>> records = new ArrayList<Hashtable<String,Object>>();

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
					+ "fstore/list", data));
		
		JSONArray ja = new JSONArray(resp.getBody()); // Expecting array
		for(int j = 0; j < ja.length(); j++){
			JSONObject obj = ja.getJSONObject(j);
			Hashtable<String,Object> ht = new Hashtable<String,Object>();
			ht.put("id", obj.get("id").toString());
			ht.put("contentType", obj.get("content_type").toString());
			ht.put("fileName", obj.get("file_name").toString());
			ht.put("tags", obj.get("tags"));
			records.add(ht);
		}
		
		return records;
	}
	
	/**
	 * Set tags for given Fstore record id.
	 * @param id Id of fstore record for tag setting.
	 * @param tags Array of strings.
	 * @return Id of updated record.
	 */
	public String setTags(String id, ArrayList<String> tags)
			throws ClientProtocolException, IOException, URISyntaxException,
			Exception {
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		data.put("tags", combine(tags,","));
		conx = new CloudyRecConnector();
		return checkAndRaiseError(
				conx.httpPut("/" + version + "/" + appKey + "/fstore/" + id + "/tags",
						data)).getBody();
	}
	
	/**
	 * Get tags for given Fstore record id.
	 * @param id Id of fstore record for tag retrieval.
	 * @return Array of strings as tags.
	 */
	public ArrayList<String> getTags(String id)
			throws ClientProtocolException, IOException, URISyntaxException,
			Exception {
		conx = new CloudyRecConnector();
		SimpleHttpResponse resp;

		resp = checkAndRaiseError(conx.httpGet("/" + version + "/" + appKey + "/"
				+ "fstore/" + id + "/tags", null));
		JSONArray ja = new JSONObject(resp.getBody()).getJSONArray("tags");
		ArrayList<String> tags = new ArrayList<String>();
		for(int j = 0; j < ja.length(); j++){
			tags.add(ja.getString(j));
		}
		return tags;
	}
	
	/**
	 * Download file of given FStore record.
	 * @param id Id of fstore record for download.
	 * @return Array of byte data downloaded.
	 * @throws Exception 
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public byte[] download(String id) 
			throws ClientProtocolException, IOException, URISyntaxException, 
			Exception{
		conx = new CloudyRecConnector();
		SimpleHttpResponse resp;

		resp = checkAndRaiseError(conx.httpGet("/" + version + "/" + appKey + "/fstore"
				+ "/" + id, null,true));
		return resp.getBodyRaw();
	}
	
	/**
	 * Delete Fstore record of given id.
	 * @param id Id of fstore record to delete.
	 * @return True if successful.
	 */
	public boolean delete(String id)throws ClientProtocolException,
		IOException, URISyntaxException, Exception {
		conx = new CloudyRecConnector();
		return (checkAndRaiseError(
				conx.httpDelete("/" + version + "/" + appKey + "/fstore/"
						+ id)).getBody().length() == 0);
	}
	
}
