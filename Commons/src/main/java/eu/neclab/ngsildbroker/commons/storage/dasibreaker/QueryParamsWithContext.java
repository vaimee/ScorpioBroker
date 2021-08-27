package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import eu.neclab.ngsildbroker.commons.datatypes.QueryParams;

public class QueryParamsWithContext extends QueryParams{

	private String context;

	
	public QueryParamsWithContext(String context) {
		super();
		this.context = context;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}
	
}
