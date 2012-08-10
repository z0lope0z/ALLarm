package com.rivaledge.cloudyrec;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.*;
import java.util.Hashtable;

public class CloudyRecConnector {

	private String scheme = "http";
	private String host = "sandbox.cloudyrec.com";
	private int port = 80;
	private OAuthConsumer consumer = 
			new CommonsHttpOAuthConsumer("sWse22VKqL7autMh2lYx2A","Mp8FO4z1tXgZ7ljifHJVw");

	public CloudyRecConnector() {}

	public SimpleHttpResponse httpGet(String url, Hashtable<String, Object> data) 
			throws ClientProtocolException, IOException, URISyntaxException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException{
		return httpGet(url,data,false);
	}

	public SimpleHttpResponse httpGet(String url, Hashtable<String, Object> data,boolean isRaw)
			throws ClientProtocolException, IOException, URISyntaxException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {

		URI uri = URIUtils.createURI(scheme, host, port, url, buildQuery(data),
				null);
		HttpGet get = new HttpGet(uri);
		consumer.sign(get);
		return new SimpleHttpResponse(new DefaultHttpClient().execute(get),isRaw);
	}

	public SimpleHttpResponse httpPost(String url, Hashtable<String, Object> data)
	throws ClientProtocolException, IOException, URISyntaxException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		URI uri = URIUtils.createURI(scheme, host, port, url, buildQuery(data),
				null);
		HttpPost post = new HttpPost(uri);
		consumer.sign(post);
		return new SimpleHttpResponse(new DefaultHttpClient().execute(post));
	}

	public SimpleHttpResponse httpPostMultipart(String url, byte[] data, String fileName, String contentType)
	throws ClientProtocolException, IOException, URISyntaxException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		URI uri = URIUtils.createURI(scheme, host, port, url,null,null);
		HttpPost post = new HttpPost(uri);
		MultipartEntity entity = new MultipartEntity();
		ByteArrayBody body = new ByteArrayBody(
			data,
			contentType,
			fileName);
		entity.addPart("file",body);
		post.setEntity(entity);
		consumer.sign(post);
		return new SimpleHttpResponse(new DefaultHttpClient().execute(post));
	}

	public SimpleHttpResponse httpPut(String url, Hashtable<String, Object> data)
	throws ClientProtocolException, IOException, URISyntaxException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		URI uri = URIUtils.createURI(scheme, host, port, url, buildQuery(data),
				null);
		HttpPut put = new HttpPut(uri);
		consumer.sign(put);
		return new SimpleHttpResponse(new DefaultHttpClient().execute(put));
	}

	public SimpleHttpResponse httpPutMultipart(String url, byte[] data, String fileName, String contentType)
		throws ClientProtocolException, IOException, URISyntaxException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		URI uri = URIUtils.createURI(scheme, host, port, url,null,null);
		HttpPut put = new HttpPut(uri);
		MultipartEntity entity = new MultipartEntity();
		ByteArrayBody body = new ByteArrayBody(
				data,
				contentType,
				fileName);
		entity.addPart("file",body);
		put.setEntity(entity);
		consumer.sign(put);
		return new SimpleHttpResponse(new DefaultHttpClient().execute(put));
	}

	public SimpleHttpResponse httpDelete(String url)
	throws ClientProtocolException, IOException, URISyntaxException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		URI uri = URIUtils.createURI(scheme, host, port, url, null,
				null);
		HttpDelete del = new HttpDelete(uri);
		consumer.sign(del);
		return new SimpleHttpResponse(new DefaultHttpClient().execute(del));
	}

	private String buildQuery(Hashtable<String, Object> data) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (data != null) {
			Enumeration<String> keys = data.keys();
			while (keys.hasMoreElements()) {
				Object name = keys.nextElement();
				Object value = data.get(name);
				params.add(new BasicNameValuePair(name.toString(), value
						.toString()));
			}
			return URLEncodedUtils.format(params, "UTF-8");
		} else {
			return null;
		}
	}
}

