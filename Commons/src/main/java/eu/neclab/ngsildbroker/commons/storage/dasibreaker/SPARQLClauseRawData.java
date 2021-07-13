package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

public class SPARQLClauseRawData extends SPARQLClause{

	public SPARQLClauseRawData(String column, String value, String name) {
		super(column, value, name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getClauseTriple(String table, String key,int index) {
		
		String triple ="<"+SPARQLConstant.FOR_INTERNAL_USE+
				"><"+SPARQLConstant.HAS_RAW_DATA+">'"+super.value;
		
		String myX ="?x"+index;
		String myGraph  = SPARQLConstant.NGSI_GRAPH_PREFIX+table+"/"+key;
		
		return "BIND(EXISTS{GRAPH <"+myGraph+"> {"+triple+"}} AS "+myX+")";
	}
}
