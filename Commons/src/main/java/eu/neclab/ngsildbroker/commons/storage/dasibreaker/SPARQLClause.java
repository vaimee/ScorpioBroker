package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

public class SPARQLClause {

	public static final String INDEX_VAR = "?index";
	
	protected String column;
	protected String value;
	protected String name;
	protected boolean _isFilter;
	protected String filter;
	
	public SPARQLClause(String column, String value, String name) {
		super();
		this.column = column;
		this.value = value;
		this.name = name;
		this._isFilter=false;
	}
	
	public String getClauseTriple(String table, String key,int index) {
		String triple = "?s"+index+" ?p"+index+" ?o"+index+".\n";
		String myX ="?x"+index;
		String myGraph  = SPARQLConstant.NGSI_GRAPH_PREFIX+table+"/"+key;
		return "BIND(EXISTS{GRAPH <"+myGraph+"> {"+triple+"}} AS "+myX+")";
	}
	public void setFilter(String filter) {
		this.filter=filter;
		this._isFilter=true;
	}
	public boolean ifFilter() {
		return this._isFilter;
	}
	public String getFilter(int index) {
		return filter.replace(" "+INDEX_VAR+" ", " "+index+" ");
	}
	
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
