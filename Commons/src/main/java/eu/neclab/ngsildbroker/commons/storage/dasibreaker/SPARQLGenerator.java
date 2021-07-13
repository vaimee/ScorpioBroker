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
	
	public String generateDeleteWhere(boolean deleteAll) {
		String sparql ="DELETE WHERE {\n";
		//deleteAll-->false--> is for ON CONFLICT DO UPDATE
		if(deleteAll) {
			sparql+="GRAPH ?g {\n";
			sparql+="?s ?p ?o.\n";
			sparql+="}\n";
			sparql+=" FILTER regex(?g,"+gnererateRegexColumn()+") \n";
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
	
	public String generateDeleteWhere(ArrayList<SPARQLClause> column_value) {
		//WIP----------------------------------WIP
//		DELETE { 
//			  ?uri our:name ?literal .
//			}
//			WHERE { 
//			  { SELECT ?uri ?literal 
//			    WHERE { ?uri a our:thing; 
//			                 our:name ?literal . 
//			    }
//			  }
//			}
		String sparql ="DELETE WHERE {\n";
		int index =0;
		for (SPARQLClause sparqlClause : column_value) {
			String g = "?g"+index;
			sparql+="GRAPH "+g+ " {\n";
			sparql+=sparqlClause.getClauseTriple(index);
			sparql+="}\n";
			if(sparqlClause.ifFilter()) {
				sparql+=sparqlClause.getFilter(index);
			}
		}
		sparql +="}";
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
		return "^http:\\/\\/"+_table+"\\/.+\\/"+_key+"$";
	}
	protected String getGraph(String column) {
		return "http://"+_table+"/"+column+"/"+_key;
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
