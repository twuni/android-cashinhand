package org.twuni.money.wallet.repository;

import java.util.ArrayList;
import java.util.List;

import org.twuni.common.crypto.rsa.PrivateKey;
import org.twuni.money.common.Repository;
import org.twuni.money.common.SimpleToken;
import org.twuni.money.common.Token;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TokenRepository extends SQLiteOpenHelper implements Repository<String, Token> {

	private static final String DATABASE_NAME = "tokens";
	private static final int VERSION = 1;

	private static enum Table {

		TOKEN( "CREATE TABLE %s ( treasury TEXT NOT NULL, id TEXT NOT NULL, secret TEXT NOT NULL, value INTEGER, PRIMARY KEY (treasury,id) );", "treasury", "id", "secret", "value" );

		final String sql;
		final String [] fields;

		private Table( String sql, String... fields ) {
			this.sql = String.format( sql, name() );
			this.fields = fields;
		}

	}

	public TokenRepository( Context context ) {
		super( context, DATABASE_NAME, null, VERSION );
	}

	@Override
	public void delete( final Token token ) {

		SQLiteDatabase database = getWritableDatabase();

		database.beginTransaction();
		database.delete( Table.TOKEN.name(), "treasury = ? AND id = ?", new String [] {
		    token.getTreasury(),
		    token.getActionKey().serialize()
		} );
		database.setTransactionSuccessful();
		database.endTransaction();

	}

	@Override
	public Token findById( String id ) {
		SQLiteDatabase database = getReadableDatabase();
		Cursor cursor = database.query( Table.TOKEN.name(), Table.TOKEN.fields, "id = ?", new String [] {
			id
		}, null, null, null );
		Token token = null;
		if( cursor.moveToNext() ) {
			token = getTokenFromCurrentRow( cursor );
		}
		return token;
	}

	@Override
	public List<Token> list() {
		return list( Integer.MAX_VALUE );
	}

	@Override
	public List<Token> list( int limit ) {
		SQLiteDatabase database = getReadableDatabase();
		Cursor cursor = database.query( Table.TOKEN.name(), Table.TOKEN.fields, null, null, null, null, "treasury ASC, value ASC" );
		List<Token> tokens = new ArrayList<Token>();
		while( cursor.moveToNext() ) {
			tokens.add( getTokenFromCurrentRow( cursor ) );
		}
		return limit < tokens.size() ? tokens.subList( 0, limit ) : tokens;
	}

	@Override
	public void save( final Token token ) {

		SQLiteDatabase database = getWritableDatabase();

		database.beginTransaction();
		database.insert( Table.TOKEN.name(), null, toContentValues( token ) );
		database.setTransactionSuccessful();
		database.endTransaction();

	}

	@Override
	public void onCreate( SQLiteDatabase database ) {
		for( Table table : Table.values() ) {
			database.execSQL( table.sql );
		}
	}

	@Override
	public void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion ) {
	}

	private SimpleToken getTokenFromCurrentRow( Cursor cursor ) {
		return new SimpleToken( cursor.getString( 0 ), PrivateKey.deserialize( cursor.getString( 1 ) ), PrivateKey.deserialize( cursor.getString( 2 ) ), cursor.getInt( 3 ) );
	}

	private ContentValues toContentValues( Token token ) {

		ContentValues values = new ContentValues();

		values.put( "treasury", token.getTreasury() );
		values.put( "id", token.getActionKey().serialize() );
		values.put( "secret", token.getOwnerKey().serialize() );
		values.put( "value", Integer.valueOf( token.getValue() ) );

		return values;

	}
}
