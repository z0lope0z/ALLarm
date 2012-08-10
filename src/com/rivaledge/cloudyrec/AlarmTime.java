package com.rivaledge.cloudyrec;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.text.ParseException;
import java.util.List;
import java.util.Date;
import org.apache.http.client.ClientProtocolException;
import org.json.*;

/**
 * AlarmTime class represents alarm_time resource type
 * of Allarm in CloudyRec.
 *
 * @author CloudyRecJavaCodegen 
 */
public class AlarmTime extends CloudyRecResource {

	private String alarm_timeResKey = "GByNprSNft";
	private Date alarm_time;

	public AlarmTime(Date alarm_time) {
		this.alarm_time = alarm_time;
	}

	/**
	 * Default empty constructor
	 */
	public AlarmTime() {}

	public Date getAlarm_time(){
		return this.alarm_time;
	}

	public void setAlarm_time(Date alarm_time){
		this.alarm_time = alarm_time;
	}

	public String getId(){ return this.id;}

	public String getResourceKey() {
		return alarm_timeResKey;
	}

	/**
	 * Load AlarmTime data from server for given Id.
	 */
	public boolean load(String id) throws ClientProtocolException, IOException,
			URISyntaxException, Exception {
		setData(loadFromCloud(id));
		return true;
	}

	/**
	 * Get a list of AlarmTime resources from server for given filter.
	 * Default page number 1 with maximum 10 records count.
	 */
	public List<AlarmTime> list(String query) throws ClientProtocolException,
			IOException, URISyntaxException, Exception {
		return list(query, 10, 1); //page number default 1
	}


	/**
	 * Get a list of AlarmTime resources from server for given filter,
	 * with given page number and number of records.
	 */
	public List<AlarmTime> list(String query, int limit, int page)
			throws ClientProtocolException, IOException, URISyntaxException,
			Exception {
		List<AlarmTime> resources = new ArrayList<AlarmTime>();

		JSONArray ja = listFromCloud(query, limit, page);
		for(int j = 0; j < ja.length(); j++){
			JSONObject obj = ja.getJSONObject(j);
			AlarmTime res = new AlarmTime();
			res.setData(obj);
			resources.add(res);
		}

		return resources;
	}



	/**
	 * Save current AlarmTime data to Server.
	 */
	public boolean save() throws ClientProtocolException, IOException,
			URISyntaxException, Exception {
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		data.put("alarm_time",ISO8601DateParser.toString(this.alarm_time));
		if (this.id == null) {
			id = insertToCloud(data);
		} else {
			id = updateToCloud(data);
		}
		return (!id.equals(null));
	}

	/**
	 * Delete current AlarmTime data from Server.
	 * After successful deletion, the object's Id will be set to null.
	 */
	public boolean delete() throws ClientProtocolException, IOException,
			URISyntaxException, Exception {
		if (id == null)
			return false;

		if (deleteFromCloud())
			this.id = null;

		return true;
	}

	/**
	 * Set given JSONObject data to respective properties of AlarmTime object.
	 */
	public void setData(JSONObject data) throws JSONException, ParseException{
		this.id = data.get("id").toString();
		this.alarm_time = ISO8601DateParser.parse(data.get("alarm_time").toString());
	}
}
