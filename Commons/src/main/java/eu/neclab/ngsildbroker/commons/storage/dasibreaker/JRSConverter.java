package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.RdfDocument;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.sparql.BindingsResults;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;

//JSONLD-RDF-SPARQL-Converter
public class JRSConverter {

	
	
	
	private HashMap<String,String> _blankNodeHasMap;
	protected String _table;
	private ArrayList<InternalTriple> _triples;
	public JRSConverter(String table){
		_table = table;
		_blankNodeHasMap = new HashMap<String,String>();
		_triples= new ArrayList<InternalTriple>();
	}
	
	public void addTriple(String key,String column,String value) {
			addTriple(key,column,value,true);
	}
	
	public void addTriple(String key,String column,String value,boolean justCreate) {
		String v = value;
		boolean needDataGraph=Arrays.asList(SPARQLConstant.JSON_COLUMNS).contains(column);
		InternalTriple it ;
		if(needDataGraph) {
			v=SPARQLConstant.NGSI_GRAPH_PREFIX+_table+"/"+column+"/"+key;
			it= new InternalTriple(
					"<"+SPARQLConstant.NGSI_GRAPH_PREFIX+key+">",
					"<"+SPARQLConstant.NGSI_GRAPH_PREFIX+column+">",
					v,
					value);
		}else {
			it = new InternalTriple(
					"<"+SPARQLConstant.NGSI_GRAPH_PREFIX+key+">",
					"<"+SPARQLConstant.NGSI_GRAPH_PREFIX+column+">",
					"'"+value+"'");
		}
		if(!justCreate) {
			it.setNeedDelete(true);
		}
		_triples.add(it);
	}
	
	protected String getGraph(String key) {
		return SPARQLConstant.NGSI_GRAPH_PREFIX+_table+"/"+key;
	}
	//-------------------------------------------------------------------------UPDATE
	protected String genereteDeleteWhereOf(int index,String key) {
			String sparql_2= "";
			InternalTriple it = _triples.get(index);
			if(it.needDataGraph()) {
				sparql_2="GRAPH <"+it.getO()+"> {?s1 ?p1 ?o1}\n";
			}
			String sparql= "DELETE WHERE {\n"+"GRAPH <"+getGraph(key)+"> {\n"+
					it.getTriple(true)+
					"}"+sparql_2+"};\n";
			return sparql;
	}
	
	public String generateDeleteAllWhere(String key){
		//testing it
//		DELETE WHERE {
//		  GRAPH ?g {?s ?p ?o}
//		  GRAPH <g> { <s><p><o>}
//		  GRAPH <g> {<sg><pg>?g2}
//		  GRAPH ?g2{?s1 ?s2 ?s3}
//		}
		String sparql = "DELETE WHERE{ GRAPH ?g {?s ?p ?o}\n";
		String sparql_2 = "";
		sparql+="GRAPH <"+getGraph(key)+"> {\n";
		int x=0;
		for (InternalTriple internalTriple : _triples) {
			sparql+=internalTriple.getTriple(false);
			if(internalTriple.needDataGraph()) {
				sparql_2+="GRAPH <"+internalTriple.getO()+"> { ?s"+x+" ?p"+x+" ?o"+x+" }\n";
				x++;
			}
		}
		sparql+="}\n"+sparql_2+"};\n";
		return sparql;
	}
//	public String generateDeleteAllWhere(String key,int basedOnIndex){
//		//DELETE WHERE{ GRAPH ?g {?s ?p ?o} GRAPH <g> {<s5><p5><o5>}}
//		String sparql = "DELETE WHERE{ GRAPH ?g {?s ?p ?o}\n";
//		sparql+="GRAPH <"+getGraph(key)+"> {\n";
//		sparql+=_triples.get(basedOnIndex).getTriple(false);
//		sparql+="}};\n";
//		return sparql;
//	}
	public String resolveJsonBlankNode(JsonArray json) {
		JsonBlankNodeResolver jsnr = new JsonBlankNodeResolver();
		for (JsonValue x : json) {
			 JsonObject jo = x.asJsonObject();
				if(jo.containsKey("@id")) {
					String b_name = jo.get("@id").toString();
//					System.out.println(b_name); //ok
					if(b_name.matches("^\"_:b[0-9]+\"$")) {
						jsnr.put(b_name, jo);
					}else {
						if(!jsnr.hasRoot()) {
							jsnr.putRoot(jo);
						}else {
							throw new RuntimeException("Invalid JsonArray for resolveJsonBlankNode: there is more than one root element.");
						}
					}
				}else {
					throw new RuntimeException("Invalid JsonArray for resolveJsonBlankNode: a root key is not @id.");
				}
		}
		if(jsnr.hasRoot()) {
			return jsnr.iterateOnBNodes();
		}else {
			throw new RuntimeException("Invalid JsonArray for resolveJsonBlankNode: no root element found.");
		}
	}
	public String generateCreate(String key,boolean onConflict) throws JsonLdError{
		String sparql= "";
		String insertData  = "INSERT DATA {\n"
								+"GRAPH <"+getGraph(key)+"> {\n";
		String deleteWhere ="";
		String sparql_data_graph = "";
		boolean needDelete = false;
		for(int x = 0;x<_triples.size();x++) {
			InternalTriple triple = _triples.get(x);
			insertData+=triple.getTriple(false);
			if(triple.needDataGraph()) {
				sparql_data_graph+="GRAPH <"+triple.getO()+">{\n"
						+triple.getRdfGraphTriples()+"}\n";
			}
			if(onConflict) {
				if(triple.needDelete()) {
					needDelete=true;
					deleteWhere+=genereteDeleteWhereOf(x,key);
				}
			}
		}
		insertData+="} "+sparql_data_graph+"};\n";
		if(onConflict && needDelete) {
			sparql=deleteWhere;
		}
		sparql+=insertData;
		return sparql;
	}
	public String getTable() {
		return _table;
	}

	//-------------------------------------------------------------------------QUERY
	public String generateGetEntity(String searchColumn, String searchValue, String targetColumn) {//WIP

		//SELECT ?s ?p ?o  WHERE{
		//  
		//   GRAPH ?o1 {?s ?p ?o}
		//         
		//  {
		//  	SELECT ?o1 WHERE{
		//			
		//  		GRAPH ?g {
		//          ?s1 <http://ngsi/data> ?o1.
		//        }
		//        {
		//          SELECT ?g ?s1 WHERE { 
		//            GRAPH ?g { ?s1 <http://ngsi/type> 'https://uri.fiware.org/ns/data-models#Building'} 
		//                  FILTER(regex(str(?g),"^http://ngsi/entity/."))
		//          }
		//        }
		//  
		//  	
		//
		//	}
		//  }
		//}

		String sparql = "SELECT ?e WHERE{\n";
		sparql+="GRAPH ?g1 {\n";
		sparql+=	"?s1 <"+SPARQLConstant.NGSI_GRAPH_PREFIX+ targetColumn+"> ?e.\n";
		sparql+="}{\n";
		sparql+=	"SELECT ?g1 ?s1 WHERE { \n";
		sparql+=		"GRAPH ?g1 {\n";
		sparql+=			" ?s1 <"+SPARQLConstant.NGSI_GRAPH_PREFIX+searchColumn+"> '"+searchValue+"'.\n";
		sparql+=		"} FILTER(regex(str(?g1),\"^"+SPARQLConstant.NGSI_GRAPH_PREFIX+_table+".\"))\n";
		sparql+="}}}\n";
		
		String sparqlGetEntityTriples = "SELECT ?s ?p ?o ?e WHERE{\n";
		sparqlGetEntityTriples+="GRAPH ?e {?s ?p ?o}\n";
		sparqlGetEntityTriples+="{\n";
		sparqlGetEntityTriples+=sparql;
		sparqlGetEntityTriples+="}}\n";
		return sparqlGetEntityTriples;
	}
	//-------------------------------------------------------------------WIP
//	public String JSONLDtoSparqlTriple(String jsonld,String key) {
//		return "<"+SPARQLConstant.NGSI_GRAPH_PREFIX+key+"><"
//				+SPARQLConstant.NGSI_GRAPH_PREFIX+SPARQLConstant.IS_JSON_LD
//				+">'"+jsonld+"'";
//	}
//	public static String JSONLDtoSparqlTriple(String column,String key,String value) {
//		return "<"+SPARQLConstant.NGSI_GRAPH_PREFIX+key+"><"
//				+SPARQLConstant.NGSI_GRAPH_PREFIX+column
//				+">'"+value+"'";
//	}
//	public static String JSONLDtoSparqlGraph(String table,String key,String triples) {
//		String sparql = "GRAPH <"+getGraph(table,key)+"> {\n";
//		sparql+=triples;
//		sparql +="}\n";
//		return sparql;
//	}
//	protected static String getGraph(String table,String key) {
//		return SPARQLConstant.NGSI_GRAPH_PREFIX+table+"/"+key;
//	}

	//-------------------------------------------------------------------WIP

	
	protected JsonArray rdfToJsonLd(String rdf) throws JsonLdError {
		Reader targetReader = new StringReader(rdf);
		Document document = RdfDocument.of(targetReader);
		return JsonLd.fromRdf(document).get();
	}


	public HashMap<String, String> getBlankNodeHasMap() {
		return _blankNodeHasMap;
	}
	
	//----------------------------------------------------------------------------------------Internal obj for support
	private class JsonBlankNodeResolver{
		private static final int maxIter = 1000;
		private JsonObject _root=null; 
		private HashMap<String,String> _b_node;
//		private HashMap<String,Boolean> _finished;
		public JsonBlankNodeResolver() {
			_b_node= new HashMap<String,String>();
//			_finished= new HashMap<String,Boolean>();
			_root=null; 
		}
		public void put(String b_name, JsonObject b_node) {
//			b_node.remove("@id");//don't work
			JsonObjectBuilder builder =Json.createObjectBuilder();
			for (String key : b_node.keySet()) {
				if(key!="@id") {
					builder.add(key, b_node.get(key));
				}
			}
			String clean = builder.build().toString();
//			System.out.println("-->:"+clean); //ok
			_b_node.put(b_name,clean);
//			_finished.put(b_name,false);
		}
		public void putRoot(JsonObject root) {
			_root=root;
		}
		public boolean hasRoot() {
			return _root!=null;
		}
//		public boolean hasBNode(String b_name) {
//			return _finished.containsKey(b_name);
//		}
		
		public String iterateOnBNodes() {
			String ris ="";
			int count =_b_node.keySet().size();
			HashMap<String,String> finished = new HashMap<String,String>();
			HashMap<String,String> toMod;
			ArrayList<String> toRemove;
			int avoidDeadLoop = 0;
			while(finished.keySet().size()!=count && avoidDeadLoop<maxIter) {
				avoidDeadLoop++;
//				Set<String> keys =  _b_node.keySet();
				toMod = new HashMap<String,String>();
				toRemove = new ArrayList<String>();
				for (String n_name : _b_node.keySet()) {
					String temp = _b_node.get(n_name);
					Boolean thisNodeIsOK=true;
					Boolean thisNodeIsModded=false;
					for (String n_name_finished : finished.keySet()) {
						String match = "{\"@id\":"+n_name_finished+"}";
						if(temp.contains(match)) {
							temp=temp.replace(match, finished.get(n_name_finished));
							thisNodeIsModded=true;
						}
			
					}
					for (String n_name_not_finished : _b_node.keySet()) {
						String match = "{\"@id\":"+n_name_not_finished+"}";
						if(temp.contains(match)) {
							thisNodeIsOK=false;
						}
					}
					if(thisNodeIsOK) {
						finished.put(n_name, temp);
//						_b_node.remove(n_name); //ConcurrentModificationException
						toRemove.add(n_name);
					}else if(thisNodeIsModded){
						toMod.put(n_name, temp);
//						_b_node.put(n_name, temp); //ConcurrentModificationException
					}
				}
				//for ConcurrentModificationException
				//already try with Iterator (don't work) 
				//so need that stuff:
				for (String needRemove : toRemove) {
					_b_node.remove(needRemove);
				}
				for (String needMod : toMod.keySet()) {
					_b_node.put(needMod, toMod.get(needMod));
				}
			}
			if(finished.keySet().size()!=count) {
				throw new RuntimeException("JsonBlankNodeResolver.iterateOnBNodes Avoid dead loop, max iter number reaced.");
			}else {
				ris = _root.asJsonObject().toString();
				for (String n_name_finished : finished.keySet()) {
					String match = "{\"@id\":"+n_name_finished+"}";
					if(ris.contains(match)) {
						ris=ris.replace(match, finished.get(n_name_finished));
					}
				}
			}
			return ris;
		}
		 
	}
	private class InternalTriple{
		private String _s;
		private String _p;
		private String _o;
		private boolean _needDelete = false;
		private String _needDataGraph = null;
		public InternalTriple(String s, String p, String o) {
			super();
			this._s = s;
			this._p = p;
			this._o = o;
			this._needDataGraph=null;
		}
		public InternalTriple(String s, String p, String o,String needDataGraph) {
			super();
			this._s = s;
			this._p = p;
			this._o = o;
			this._needDataGraph=needDataGraph;
		}
		public String getTriple(boolean objectAsVar) {
			if(objectAsVar) {
				return _s+_p+"?o .\n";
			}else {
				if(_needDataGraph==null) {
					return _s+_p+_o+".\n";
				}else {
					return _s+_p+"<"+_o+">.\n";
				}
			}
		}
		public String getO() {
			return _o;
		}
		public boolean needDelete() {
			return _needDelete;
		}
		public void setNeedDelete(boolean needDelete) {
			this._needDelete = needDelete;
		}

		public boolean needDataGraph() {
			return _needDataGraph!=null;
		}
		public String getRdfGraphTriples() throws JsonLdError {
			//Titanium 
//			Reader targetReader = new StringReader(_needDataGraph);
//			Document document = JsonDocument.of(targetReader);
//			RdfDataset rdf = JsonLd.toRdf(document).get();
//			return rdf.toString();
			JfromToRDF converter = new JfromToRDF();
			try {
				return converter.JSONtoRDF(_needDataGraph);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
		}
		
		
		
	}

}
