package net;

import java.io.Serializable;

public class Request implements Serializable {

	private static final long serialVersionUID = -1715424764982519859L;
	
	public String query;
	
	public void setQuery(String query) {
		this.query = query;
	}

}
