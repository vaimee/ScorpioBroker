package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.RdfDocument;
import jakarta.json.JsonArray;

//JSONLD-RDF-SPARQL-Converter
//with key into graph
//DEPRECATED
public class SPARQLGenerator {


	
	protected String _table;
	protected ArrayList<InternalTriple> _triples;
	
	public SPARQLGenerator(String table){
		_table = table;
		_triples= new ArrayList<InternalTriple>();
	}
	
	public void addTriple(String key,String column,String value) {
			addTriple(key,column,value,false);
	}
	
	public void addTriple(String key,String column,String value,boolean justCreate) {
		String v = value;
		boolean needDataGraph=Arrays.asList(SPARQLConstant.JSON_COLUMNS).contains(column);
		InternalTriple it ;
		if(needDataGraph) {
//			String filteredKey = key;
//			if(key.startsWith("http://")) {
//				filteredKey=key.substring(7);
//			}
			v=generateJsonGraphUri(column,key);
			it= new InternalTriple(
					"<"+key+">",
					generateScorpioUri(column),
					v,
					value);
		}else {
			it = new InternalTriple(
					"<"+key+">",
					generateScorpioUri(column),
					"'"+value+"'");
		}
		if(!justCreate) {
			it.setNeedDelete(true);
		}
		_triples.add(it);
	}
	

	//---------------------------------------------------------------------UTILS
	protected JsonArray rdfToJsonLd(String rdf) throws JsonLdError {
		Reader targetReader = new StringReader(rdf);
		Document document = RdfDocument.of(targetReader);
		return JsonLd.fromRdf(document).get();
	}


	/*
	 * This method generate the regex clause for filter a "var" as SCORPIO URI GRAPH
	 * MAY BE NEED add "\" before "/" like "\/"
	 */
	public static String generateURIRegex(String var,String key,String table,String column) {
		String regexStart = "regex(str("+var+"),\"^";
		String regexEnd="\")";
		if(key!=null) {
			regexStart+=key;
		}
		
		if(table!=null) {
			regexStart+="/"+table;
		}
		
		if(column!=null) {
			regexStart+="/"+column;
		}
		return  regexStart+regexEnd;
	}
	
	/*
	 * This method generate URI for the SOCPRIO contents
	 */
	public static String generateScorpioUri(String scorpio_content) {
		//filter for avoid double http:// inside uri
//		if(scorpio_content.startsWith("http://")){
//			return "<"+SPARQLConstant.NGSI_GRAPH_PREFIX+scorpio_content.substring(7)+">";
//		}else {
//			return "<"+SPARQLConstant.NGSI_GRAPH_PREFIX+scorpio_content+">";
//		}
		return "<"+SPARQLConstant.NGSI_GRAPH_PREFIX+scorpio_content+">";
	}

	/*
	 * This method generate the graph URI which contain the SCORPIO data of the entity "key"
	 */
	protected String getGraph(String key) {
//		String filteredKey = key;
//		if(key.startsWith("http://")) {
//			filteredKey=key.substring(7);
//		}
		//OLD
		//return SPARQLConstant.NGSI_GRAPH_PREFIX+_table+"/"+filteredKey;
		String correspondingGraph=key;
		if(!key.endsWith("/")) {
			correspondingGraph+="/";
		}
		correspondingGraph+=_table;
		return correspondingGraph;
	}

	/*
	 * This method generate the graph URI which contain the JSON_LD triples of the "column"
	 */
	public String generateJsonGraphUri(String column,String key) {
		//old
		//return SPARQLConstant.NGSI_GRAPH_PREFIX+table+"/"+column+"/"+key;
		return this.getGraph(key)+"/"+column;
	}
	/*
	 * This method generate the graph URI which contain the JSON_LD triples of the "column"
	 */
	public static String generateJsonGraphUri(String table,String column,String key){
		String correspondingGraph=key;
		if(!key.endsWith("/")) {
			correspondingGraph+="/";
		}
		correspondingGraph+=table+"/"+column;
		return correspondingGraph;
	}
	
	

}
