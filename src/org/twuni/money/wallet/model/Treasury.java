package org.twuni.money.wallet.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.twuni.money.wallet.exception.NetworkError;
import org.twuni.money.wallet.util.JsonUtils;

public class Treasury {

	public static Treasury getTreasury( Dollar dollar ) {
		return new Treasury();
	}

	private final String mergeUrl;
	private final String splitUrl;
	private final String valueUrl;
	private final DefaultHttpClient client;

	public Treasury() {
		this( "http://home.twuni.org:8080/treasury" );
	}

	public Treasury( String baseUrl ) {
		client = new DefaultHttpClient();
		mergeUrl = baseUrl + "/merge";
		splitUrl = baseUrl + "/split";
		valueUrl = baseUrl + "/value";
	}

	public List<Dollar> split( Dollar dollar, int amount ) {

		HttpPost post = new HttpPost( splitUrl );

		List<NameValuePair> parameters = new ArrayList<NameValuePair>( 3 );

		parameters.add( new BasicNameValuePair( "id", dollar.getId().toString() ) );
		parameters.add( new BasicNameValuePair( "secret", dollar.getSecret().toString() ) );
		parameters.add( new BasicNameValuePair( "value", Integer.toString( amount ) ) );

		try {
			post.setEntity( new UrlEncodedFormEntity( parameters ) );
			return JsonUtils.deserializeList( client.execute( post, new BasicResponseHandler() ), Dollar.class );
		} catch( IOException exception ) {
			throw new NetworkError( exception );
		}

	}

	public Dollar merge( Dollar a, Dollar b ) {

		HttpPost post = new HttpPost( mergeUrl );

		List<NameValuePair> parameters = new ArrayList<NameValuePair>( 4 );

		parameters.add( new BasicNameValuePair( "id1", a.getId().toString() ) );
		parameters.add( new BasicNameValuePair( "secret1", a.getSecret().toString() ) );

		parameters.add( new BasicNameValuePair( "id2", b.getId().toString() ) );
		parameters.add( new BasicNameValuePair( "secret2", b.getSecret().toString() ) );

		try {
			post.setEntity( new UrlEncodedFormEntity( parameters ) );
			return JsonUtils.deserialize( client.execute( post, new BasicResponseHandler() ), Dollar.class );
		} catch( IOException exception ) {
			throw new NetworkError( exception );
		}

	}

	public int evaluate( Dollar dollar ) {

		HttpGet get = new HttpGet( String.format( "%s?id=%s", valueUrl, encodeUrlComponent( dollar.getId() ) ) );

		try {
			return JsonUtils.deserialize( client.execute( get, new BasicResponseHandler() ), Integer.class ).intValue();
		} catch( IOException exception ) {
			throw new NetworkError( exception );
		}

	}

	private String encodeUrlComponent( String id ) {
		if( id == null || "".equals( id ) ) {
			return "";
		}
		try {
			return URLEncoder.encode( id, "UTF-8" );
		} catch( UnsupportedEncodingException exception ) {
		}
		return id;
	}

}
