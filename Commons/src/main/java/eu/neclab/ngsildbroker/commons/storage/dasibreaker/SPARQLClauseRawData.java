package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

public class SPARQLClauseRawData extends SPARQLClause{

	//for interal use
	public SPARQLClauseRawData(String column, String value) {
		super(column, value);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getClause(String table, String key,String varName,int index) {
		
		String triple ="<"+SPARQLConstant.FOR_INTERNAL_USE+
				"><"+SPARQLConstant.HAS_RAW_DATA+">'"+super.value+"'";
		
		String okVar ="?"+varName+index;
		
		String myGraph  =key==null? "?g"+index:"<"+ SPARQLConstant.NGSI_GRAPH_PREFIX+table+"/"+column+"/"+key+">";
		
		return "BIND(EXISTS{GRAPH "+myGraph+" {"+triple+"}} AS "+okVar+")";
	}
}
