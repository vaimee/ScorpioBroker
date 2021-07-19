package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.util.ArrayList;
import java.util.HashMap;
import com.apicatalog.jsonld.JsonLdError;

public class SPARQLGeneratorUpdate extends SPARQLGenerator{


	private String _key;
//	private String _key_name;
	private boolean _checkConflict;
	private HashMap<String,String> _map;
	
	public SPARQLGeneratorUpdate(String table, String key,boolean checkConflict){
		super(table);
		_map=new HashMap<String,String>();
		_key=key;
		_checkConflict=checkConflict;
	}

	
	public void insertJsonColumn(String json, String column) throws JsonLdError {
			String sparql = "GRAPH <"+getGraph(column)+"> {\n";
			sparql +=super._converter.jsonldToTriple(json,_key);
			sparql +="}\n";
			_map.put(column, sparql);
	}
	
	public void insertTriplesColumn(String triples, String column){
		String sparql = "GRAPH <"+getGraph(column)+"> {\n";
		sparql +=triples;
		sparql +="}\n";
		_map.put(column, sparql);
	}
	
	public void insertRawDataColumn(String rawData, String column){
		String sparql = "GRAPH <"+getGraph(column)+"> {\n";
		sparql +="<"+SPARQLConstant.FOR_INTERNAL_USE+"><"+SPARQLConstant.HAS_RAW_DATA+">'"+rawData+"'.\n";
		sparql +="}\n";
		_map.put(column, sparql);
	}
	
//	public String generateSelect() {
//		//   FILTER regex(?name, "^ali", "i") }
//		
//	}
	
	protected String generateInsertData() {
		String sparql ="INSERT DATA {\n";
		for (String mapKey : _map.keySet()) {
			sparql+=_map.get(mapKey)+"\n";
		}
//		if(_checkConflict) {
//			sparql+="GRAPH <"+SPARQLConstant.SPARQL_CHECK_CONFLICT+"> {\n";
//			for (String mapKey : _map.keySet()) {
//				sparql +="<"+_key+"> <"+SPARQLConstant.+"> <"+mapKey+">.\n";
//			}
//			sparql +="}\n";
//		}
		sparql +="}";
		return sparql;
	}
	protected String generateDeleteAll() {
		String sparql="GRAPH ?g {\n";
		sparql+="?s ?p ?o.\n";
		sparql+="}\n";
		sparql+=" FILTER regex(str(?g),"+gnererateRegexColumn()+") \n";
		return sparql;
	}
	public String generateDeleteWhere(boolean deleteAll) {
		String sparql ="DELETE WHERE {\n";
		//deleteAll-->false--> is for ON CONFLICT DO UPDATE
		if(deleteAll) {
			sparql+=generateDeleteAll();
		}else {
			for (String mapKey : _map.keySet()) {
				if(mapKey.compareTo(SPARQLConstant.SPARQL_COLUMN)==0) {
					//don't NEED (for new skip)
//					sparql+="GRAPH <"+getGraph(mapKey)+">{\n"
//							+ "<> <> <>."
//							+ "?s ?p ?o.}\n";
				}else {
					sparql+="GRAPH <"+getGraph(mapKey)+">{?s ?p ?o}\n";
				}
			}
		}
		sparql +="}";
		return sparql;
	}
	
	public String generateDeleteAllWhere(ArrayList<SPARQLClause> column_value) {
//	DELETE { GRAPH ?g {?s ?p ?o} } 
//		WHERE{
//			  GRAPH ?g {?s ?p ?o}
//			  { 
//			    SELECT ?g WHERE {
//			      {
//			        SELECT ?ok1 {
//
//			        BIND( EXISTS{
//			            GRAPH ?g1 {
//			              ?s1 ?p1 <o2>.
//			              ?s1 ?p1 ?o1}}AS ?ok1)
//
//
//			        BIND( EXISTS{
//			            GRAPH ?g2 {
//			              ?s2 ?p2 <o>.
//			              ?s2 ?p2 ?o2}}AS ?ok2)
//			      }
//			      HAVING(?ok1 =true && ?ok2=true)
//			    }
//			      
//			    	GRAPH ?g{?s ?p ?o}
//			        FILTER(regex(str(?g),"^http://localhost:9999/blazegraph/namespace/kb/g.$") && ?ok1)
//			              
//			   }
//			           
//			 }
//
//			}
//			           
//			  
		String varName = "ok";
		String regex = gnererateRegexColumn();
		String filter ="FILTER(regex(str(?g), "+regex+" && ?"+varName+"1)";
		String having ="HAVING(";// HAVING(?ok1 =true && ?ok2=true)
		String bindings ="";
		
		int index =0;	
		for (SPARQLClause sparqlClause : column_value) {
//	        BIND( EXISTS{
//	            GRAPH ?g2 {
//	              ?s2 ?p2 <o>.
//	              ?s2 ?p2 ?o2}}AS ?ok2)
			bindings+=sparqlClause.getClause(super._table, _key, varName,index)+"\n";
			having+="?"+varName+index+ "=true ";
			if(index>0) {
				having+="&& ";
			}
			index++;
		}
		having+=")";
		String sparql =	"DELETE { GRAPH ?g {?s ?p ?o} } \n";
		sparql+=			"WHERE {\n";
		sparql+=				"GRAPH ?g {?s ?p ?o}\n";
		sparql+=					"{\n";
		sparql+=						"SELECT ?g WHERE {\n";
		sparql+=								"{\n";
		sparql+=									"SELECT ?"+varName+"1 {\n";
		sparql+=										bindings+"\n";
		sparql+=									"}\n";
		sparql+=									having+"\n";
		sparql+=								"}\n";
		sparql+=							"GRAPH ?g{?s ?p ?o}\n";
		sparql+=							filter+"\n";
		sparql+=						"}\n";
		sparql+=					"}\n";
		sparql+=			"}\n";
		
		
		return sparql;
	}
	
	public String generateCreateEntity() {
		String sparql = "";
		if(_checkConflict) {
			sparql= generateDeleteWhere(false)+";\n";
		}
		sparql+= generateInsertData();
		return sparql;
	}
	
	protected String gnererateRegexColumn() {
//		return "^http:\\/\\/parte1\\/.+\\/parte3$";
		return "^"+SPARQLConstant.NGSI_GRAPH_PREFIX+_table+"\\/.+\\/"+_key+"$";
	}
	protected String getGraph(String column) {
		return ""+SPARQLConstant.NGSI_GRAPH_PREFIX+_table+"/"+column+"/"+_key;
	}

	//-----------------------------------------setters and getters
	

	public String getKey() {
		return _key;
	}

	public void setKey(String _key) {
		this._key = _key;
	}


	public HashMap<String, String> getTableMap() {
		return _map;
	}


	

	public boolean isCheckConflict() {
		return _checkConflict;
	}


	
	
	
}
