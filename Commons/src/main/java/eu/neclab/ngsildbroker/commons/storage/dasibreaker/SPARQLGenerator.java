package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.RdfNQuad;

public class SPARQLGenerator {

	private HashMap<String,String> _map;
	private HashMap<String,String> _blankNodeHasMap;
	private String _table;
	private String _key;
//	private String _key_name;
	private boolean _checkConflict;
	
	public SPARQLGenerator(String table, String key,boolean checkConflict){
		_map=new HashMap<String,String>();
		_blankNodeHasMap = new HashMap<String,String>();
		_table=table;
		_key=key;
		_checkConflict=checkConflict;
		
	}

	
	public void insertJsonColumn(String json, String column) throws JsonLdError {
		String sparql = "GRAPH <"+getGraph(column)+"> {\n";
		sparql +=jsonldToTriple(json,_key);
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
		sparql+=" FILTER regex(?g,"+gnererateRegexColumn()+") \n";
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
			bindings+=sparqlClause.getClause(_table, _key, varName,index)+"\n";
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
	//-----------------------------------------utils
	
	protected String gnererateRegexColumn() {
//		return "^http:\\/\\/parte1\\/.+\\/parte3$";
		return "^"+SPARQLConstant.NGSI_GRAPH_PREFIX+_table+"\\/.+\\/"+_key+"$";
	}
	protected String getGraph(String column) {
		return SPARQLConstant.NGSI_GRAPH_PREFIX+_table+"/"+column+"/"+_key;
	}
	
	protected String jsonldToTriple(String jsonld,String key) throws JsonLdError {
		Reader targetReader = new StringReader(jsonld);
		Document document = JsonDocument.of(targetReader);
		RdfDataset rdf = JsonLd.toRdf(document).get();
		return rdfDatasetToTripleString(rdf,key);
	}
	protected String rdfDatasetToTripleString(RdfDataset rdf, String key) {
		String turtle = "";
		for ( RdfNQuad iterable_element : rdf.toList()) {
			String s = iterable_element.getSubject().getValue();
			if(iterable_element.getSubject().isBlankNode()) {
				s= "<"+resolveBlankNode(s,key) +">";
			}else {
				s = "<"+s+">";
			}
			String p= "<"+iterable_element.getPredicate().getValue()+">";
			String o =  iterable_element.getObject().getValue();
			if(iterable_element.getObject().isBlankNode()) {
				o= "<"+resolveBlankNode(o,key) +">";
			}else if(iterable_element.getObject().isLiteral()) {
				o = "'"+o+"'";
			}else {
				o = "<"+o+">";
			}
			turtle+=s+" "+ p+ " " + o +".\n";
		}
		return turtle;
	}
	protected String resolveBlankNode(String blankNode,String key) {
		String uniqueBlankNode;
		if(_blankNodeHasMap.containsKey(blankNode)){
			uniqueBlankNode = _blankNodeHasMap.get(blankNode);
		}else {
			uniqueBlankNode = "_:"+key+"_"+UUID.randomUUID().toString();
			_blankNodeHasMap.put(blankNode,uniqueBlankNode);
		}
		return uniqueBlankNode;
	}
	//-----------------------------------------setters and getters
	public String getTable() {
		return _table;
	}

	public void setTable(String _table) {
		this._table = _table;
	}

	public String getKey() {
		return _key;
	}

	public void setKey(String _key) {
		this._key = _key;
	}


	public HashMap<String, String> getTableMap() {
		return _map;
	}


	public HashMap<String, String> getBlankNodeHasMap() {
		return _blankNodeHasMap;
	}


	public boolean isCheckConflict() {
		return _checkConflict;
	}


	
	
	
}
