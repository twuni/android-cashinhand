package org.twuni.money.wallet.repository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.twuni.money.common.SimpleRepository;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PreferencesRepository<T> extends SimpleRepository<T> {

	private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private final Type listType;

	private final SharedPreferences preferences;
	private final String key;

	public PreferencesRepository( SharedPreferences preferences, String key, Type listType ) {
		this.preferences = preferences;
		this.key = key;
		this.listType = listType;
	}

	@Override
	public List<T> list( int limit ) {
		map.clear();
		for( T token : load() ) {
			save( token );
		}
		return super.list( limit );
	}

	@Override
	public void save( T value ) {
		super.save( value );
		save();
	}

	@Override
	public void delete( T value ) {
		super.delete( value );
		save();
	}

	private List<T> load() {
		return gson.fromJson( preferences.getString( key, "[]" ), listType );
	}

	private void save() {
		List<T> list = new ArrayList<T>();
		for( T item : map.values() ) {
			if( item != null ) {
				list.add( item );
			}
		}
		preferences.edit().putString( key, gson.toJson( list ) ).commit();
	}

}
