package net;

import java.io.Serializable;

public class Request implements Serializable {

	public enum Type {
		SEARCH,
		CREATE,
		CHANGE,
		DELETE,
		EXECUTE,
	};
	
	private static final long serialVersionUID = -1715424764982519859L;
	
	public Type type;
	public int entryID;
	public String pool, query;
	
	public Request() {
		this.type = Type.SEARCH;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public void setPool(String pool) {
		this.pool = pool;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
}
