package org.twuni.money.wallet.util;

import java.util.List;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class JsonUtils {

	public static <T> String serialize( T object ) {
		return new JSONSerializer().exclude( "*.class" ).serialize( object );
	}

	public static <T> T deserialize( String serial, Class<T> type ) {
		return new JSONDeserializer<T>().deserialize( serial, type );
	}

	public static <T> List<T> deserializeList( String serial, Class<T> type ) {
		return new JSONDeserializer<List<T>>().use( "values", type ).deserialize( serial );
	}

}
