package org.twuni.money.wallet.model;

import java.util.List;

public interface Vault {

	public void save( List<Dollar> dollars );

	public List<Dollar> load();

}
