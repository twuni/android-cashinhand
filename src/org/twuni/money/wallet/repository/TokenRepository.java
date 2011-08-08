package org.twuni.money.wallet.repository;

import java.util.ArrayList;
import java.util.List;

import org.twuni.common.Adapter;
import org.twuni.common.crypto.rsa.PrivateKey;
import org.twuni.common.persistence.Migration;
import org.twuni.common.persistence.Parameterized;
import org.twuni.common.persistence.Parameters;
import org.twuni.common.persistence.Record;
import org.twuni.common.persistence.Session;
import org.twuni.common.persistence.Transaction;
import org.twuni.common.persistence.android.Connection;
import org.twuni.common.persistence.android.Database;
import org.twuni.common.persistence.exception.RollbackException;
import org.twuni.money.common.Repository;
import org.twuni.money.common.SimpleToken;
import org.twuni.money.common.Token;

import android.content.Context;

public class TokenRepository implements Repository<String, Token> {

	private static final Migration [] HISTORY = {
		new Migration( 1, "CREATE TABLE token ( action_key TEXT NOT NULL, owner_key TEXT NOT NULL, value INT NOT NULL, PRIMARY KEY ( action_key ) );", "DROP TABLE token;" )
	};

	private static final String FIND_ALL = "SELECT action_key, owner_key, value FROM token ORDER BY value DESC;";
	private static final String FIND_BY_ID = "SELECT action_key, owner_key, value FROM token WHERE action_key = ?;";
	private static final String INSERT = "INSERT INTO token ( action_key, owner_key, value ) VALUES ( ?, ?, ? );";
	private static final String DELETE = "DELETE FROM token WHERE action_key = ?;";

	private final Adapter<Record, Token> adapter = new Adapter<Record, Token>() {

		@Override
		public Token adapt( Record record ) {
			return new SimpleToken( treasuryUrl, PrivateKey.deserialize( record.getString( "action_key" ) ), PrivateKey.deserialize( record.getString( "owner_key" ) ), record.getInt( "value" ) );
		}

	};

	private final String treasuryUrl;
	private final Database database;
	private final Context context;

	public TokenRepository( Context context, String treasuryUrl ) {
		this.context = context;
		this.treasuryUrl = treasuryUrl;
		this.database = new Database( context, String.format( "wallet_%s", Integer.valueOf( treasuryUrl.hashCode() ) ), HISTORY[HISTORY.length - 1].getSequence(), HISTORY );
	}

	public void deleteAll() {

		Connection connection = database.getWritableConnection();

		connection.run( new Transaction() {

			@Override
			public void perform( Session session ) {
				session.query( "DELETE FROM token;" );
			}

		} );

	}

	@Override
	public void delete( final Token token ) {

		executeWritable( DELETE, new Parameters() {

			@Override
			public void apply( Parameterized target ) {
				target.setParameter( 1, token.getActionKey().serialize() );
			}

		} );

	}

	@Override
	public Token findById( final String serializedActionKey ) {

		final List<Token> result = new ArrayList<Token>( 1 );

		Connection connection = database.getReadableConnection();

		connection.run( new Transaction() {

			@Override
			public void perform( Session session ) {

				result.add( session.unique( FIND_BY_ID, new Parameters() {

					@Override
					public void apply( Parameterized target ) {
						target.setParameter( 1, serializedActionKey );
					}

				}, adapter ) );

			}

		} );

		return result.get( 0 );

	}

	@Override
	public List<Token> list() {
		return list( Integer.MAX_VALUE );
	}

	@Override
	public List<Token> list( final int limit ) {

		final List<Token> result = new ArrayList<Token>();

		Connection connection = database.getReadableConnection();

		connection.run( new Transaction() {

			@Override
			public void perform( Session session ) {
				result.addAll( session.query( FIND_ALL, Parameters.NONE, adapter, limit ) );
			}

		} );

		return result;

	}

	@Override
	public void save( final Token token ) {

		executeWritable( INSERT, new Parameters() {

			@Override
			public void apply( Parameterized target ) {
				target.setParameter( 1, token.getActionKey().serialize() );
				target.setParameter( 2, token.getOwnerKey().serialize() );
				target.setParameter( 3, token.getValue() );
			}

		} );

		save( token.getTreasury() );

	}

	private void save( String treasuryUrl ) {
		TreasuryRepository treasuryRepository = new TreasuryRepository( context );
		try {
			treasuryRepository.findById( treasuryUrl );
		} catch( RollbackException exception ) {
			treasuryRepository.save( treasuryUrl );
		}
	}

	protected void executeWritable( final String sql, final Parameters parameters ) {

		Connection connection = database.getWritableConnection();

		connection.run( new Transaction() {

			@Override
			public void perform( Session session ) {
				session.query( sql, parameters );
			}

		} );

	}

}
