package net;

import java.io.Serializable;
import java.util.List;

public class Response<T> implements Serializable {
	
	private static final long serialVersionUID = 1989302747270755505L;
	
	public List<T> results;

	public void setResults(List<T> results) {
		this.results = results;
	}
	
}