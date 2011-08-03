package org.twuni.money.wallet.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.twuni.common.Adapter;
import org.twuni.common.persistence.Migration;
import org.twuni.common.persistence.Parameterized;
import org.twuni.common.persistence.Parameters;
import org.twuni.common.persistence.Record;
import org.twuni.common.persistence.Session;
import org.twuni.common.persistence.Transaction;
import org.twuni.common.persistence.android.Connection;
import org.twuni.common.persistence.android.Database;
import org.twuni.money.common.Repository;

import android.content.Context;

public class TreasuryRepository implements Repository<String, String> {

	private static final Migration [] HISTORY = {
		new Migration( 1, "CREATE TABLE treasury ( url TEXT NOT NULL, PRIMARY KEY ( url ) );", "DROP TABLE treasury;" )
	};

	private static final String FIND_ALL = "SELECT url FROM treasury;";
	private static final String FIND_BY_ID = "SELECT url FROM treasury WHERE url = ?;";
	private static final String INSERT = "INSERT INTO treasury VALUES ( ? );";
	private static final String DELETE = "DELETE FROM treasury WHERE url = ?;";

	private final Adapter<Record, String> adapter = new Adapter<Record, String>() {

		@Override
		public String adapt( Record record ) {
			return record.getString( "url" );
		}

	};

	private final Context context;
	private final Database database;
	private final Map<String, TokenRepository> tokenRepositories = new HashMap<String, TokenRepository>();

	public TreasuryRepository( Context context ) {
		this.context = context;
		this.database = new Database( context, "treasuries", HISTORY[HISTORY.length - 1].getSequence(), HISTORY );
	}

	public TokenRepository getTokenRepository( String treasury ) {
		TokenRepository tokenRepository = tokenRepositories.get( treasury );
		if( tokenRepository == null ) {
			tokenRepository = new TokenRepository( context, treasury );
			tokenRepositories.put( treasury, tokenRepository );
		}
		return tokenRepository;
	}

	@Override
	public void delete( final String treasury ) {

		Connection connection = database.getWritableConnection();

		connection.run( new Transaction() {

			@Override
			public void perform( Session session ) {

				session.query( DELETE, new Parameters() {

					@Override
					public void apply( Parameterized target ) {
						target.setParameter( 1, treasury );
					}

				} );

				getTokenRepository( treasury ).deleteAll();

			}

		} );

	}

	@Override
	public String findById( final String url ) {

		final List<String> result = new ArrayList<String>( 1 );

		Connection connection = database.getReadableConnection();

		connection.run( new Transaction() {

			@Override
			public void perform( Session session ) {

				result.add( session.unique( FIND_BY_ID, new Parameters() {

					@Override
					public void apply( Parameterized target ) {
						target.setParameter( 1, url );
					}

				}, adapter ) );

			}

		} );

		return result.get( 0 );

	}

	@Override
	public List<String> list( final int limit ) {

		final List<String> result = new ArrayList<String>();

		Connection connection = database.getReadableConnection();

		connection.run( new Transaction() {

			@Override
			public void perform( Session session ) {
				result.addAll( session.query( FIND_ALL, adapter, limit ) );
			}

		} );

		return result;

	}

	@Override
	public List<String> list() {
		return list( Integer.MAX_VALUE );
	}

	@Override
	public void save( final String treasury ) {

		executeWritable( INSERT, new Parameters() {

			@Override
			public void apply( Parameterized target ) {
				target.setParameter( 1, treasury );
			}

		} );

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
