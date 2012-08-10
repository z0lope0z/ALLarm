package com.rivaledge.cloudyrec;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.*;

public class SimpleHttpResponse {

	private int status = 0;
	private String reason = null;
	private String body = null;
	private byte[] bodyRaw = null;
	
	public SimpleHttpResponse(HttpResponse resp) {
		simplify(resp);
	}
	
	public SimpleHttpResponse(HttpResponse resp,boolean isRaw) {
		if(isRaw)
			simplifyRaw(resp);
		else
			simplify(resp);
	}

	public int getStatus() {
		return this.status;
	}

	public String getReason() {
		return this.reason;
	}

	public String getBody() {
		return this.body;
	}
	
	public byte[] getBodyRaw(){
		return this.bodyRaw;
	}

	private void simplify(HttpResponse resp) {
		HttpEntity entity = resp.getEntity();
		status = resp.getStatusLine().getStatusCode();
		reason = resp.getStatusLine().getReasonPhrase();
		BufferedReader rd;
		
		try {
			rd = new BufferedReader(new InputStreamReader(entity.getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			body = sb.toString();

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (NullPointerException npe) {
			body = "";
		}
	}
	
	private void simplifyRaw(HttpResponse resp) {
		HttpEntity entity = resp.getEntity();
		status = resp.getStatusLine().getStatusCode();
		reason = resp.getStatusLine().getReasonPhrase();
		
		try {
			InputStream in = entity.getContent();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
		    int numRead;
		    while ((numRead = in.read(buffer)) != -1) {
		    	bos.write(buffer, 0, numRead);
		    }
		    bodyRaw = bos.toByteArray();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (NullPointerException npe) {
			body = "";
			bodyRaw = null;
		}
	}
}

