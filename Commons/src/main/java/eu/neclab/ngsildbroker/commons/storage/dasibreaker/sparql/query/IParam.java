package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.query;


public interface IParam {

	
	void addParam(String predicate, String value);
	void addParam(IParam param);
	String getClause();
	String getVars();
	String getVars(String s);
	int getSeed();
	boolean needBrackets();
	boolean needFilter();
	
}
