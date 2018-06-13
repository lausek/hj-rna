package net;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
	
	private static final long serialVersionUID = 1989302747270755505L;
	
	public List<String> results;

	public void setResults(List<String> results) {
		this.results = results;
	}
	
}