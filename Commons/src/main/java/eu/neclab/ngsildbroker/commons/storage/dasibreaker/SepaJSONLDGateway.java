package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.RdfNQuad;

import eu.neclab.ngsildbroker.commons.constants.DBConstants;
import it.unibo.arces.wot.sepa.commons.response.QueryResponse;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;
import it.unibo.arces.wot.sepa.commons.sparql.BindingsResults;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;

public class SepaJSONLDGateway extends SepaGateway{

	public static final String FOR_INTERNAL_USE = "internaluse";
	public static final String EXISTS_ID = "existsid";
	public static final String ENTITY_TYPE = "entityType";
	public static final String ENTITY_CREATED_AT = "entityCreatedAt";
	public static final String ENTITY_MODIFIED_AT = "entityModifiedAt";
	public static final String HAS_TEMPORAL_INSTANCE = "hasTemporalInstance";
	
	public SepaJSONLDGateway() throws SEPASecurityException {
		super();
	}
	
	/*
	 * SPARQL DELETE WHERE
	 * this update will delete all triples linked to "key" subject
	 * maybe is not safe: will be delete triple linked by others "key" with same object
	 * example:
	 * 
	 *T1) <key1> <p1> <o1>
	 *T2) <o1> <p2> <o2>
	 * 
	 *T3) <key2> <p1> <o1>
	 *T4) <o1> <p2> <o4>
	 * 
	 * generalDeleteEntityRecursively(key1); will delete T1,T2,T4 so T3 is not consistent
	 */
	public boolean generalDeleteEntityRecursively(String key,String db) {
//		delete { ?s ?p ?o }
//		where {
//		  <urn:ngsi-ld:testunit:4915> (<>|!<>)* ?s . 
//		  ?s ?p ?o .
//		}
		String sparql = getSparqlDeleteEntityRecursively(key,db);
		return !super.executeUpdate(sparql).isError() && !deleteInternalEntityKey(key,db);
		
	}
	
	public String getSparqlDeleteEntityRecursively(String key,String db) {
//		delete { ?s ?p ?o }
//		where {
//		  <urn:ngsi-ld:testunit:4915> (<>|!<>)* ?s . 
//		  ?s ?p ?o .
//		}
		return "DELETE {?s ?p ?o}\n"
				+ "where { \n"
				+ "  graph <"+super.graph+"/"+db+"> {\n"
				+ "<"+key+"> (<>|!<>)* ?s .\n"  
				+ "?s ?p ?o .\n"
				+ "} }" ;
		
	}
	/*
	 * SPARQL SELECT
	 */
	public String generalGetEntityRecursivelyByKey(String key,String db) throws JsonLdError, SEPABindingsException {
//		SELECT ?s ?p ?o 
//		where {
//	          GRAPH <prova>{
//			  <urn:ngsi-ld:testunit:4915> (<>|!<>)* ?s . 
//			  ?s ?p ?o .
//	          }
//			}
		String sparql = "SELECT ?s ?p ?o\n"
				+ "where { \n"
				+ "  graph  <"+super.graph+"/"+db+"> {\n"
				+ "<"+key+"> (<>|!<>)* ?s .\n"  
				+ "?s ?p ?o .\n"
				+ "} }" ;
		QueryResponse res = (QueryResponse)super.executeQuery(sparql);
		return bindingsResultsToJsonld(res.getBindingsResults());
	}

	/*
	 * SPARQL INSERT DATA
	 */
	public boolean generalStoreEntity(String key,String jsonld,String db) throws JsonLdError {
		
		String sparql = getSparqlGeneralStoreEntity(key,jsonld,db);
		return !super.executeUpdate(sparql).isError();
	}
	
	public String getSparqlGeneralStoreEntity(String key,String jsonld,String db) throws JsonLdError {
		
//		logger.info("\n---------------------------------------\ncreateEntity.JSON-LD: \n" + resolved + "\n");
		String turtle = jsonldToTriple(jsonld,key);
		
		//for faster and easy getAllIDs method:
		turtle += "<"+ super.ontology+"#"+FOR_INTERNAL_USE+"><"+super.ontology+"#"+EXISTS_ID+"><"+key+"> .\n";
		
//		logger.info("\n---------------------------------------\ncreateEntity.RDF: \n" + turtle + "\n");
		return "INSERT DATA\n"
				+ "{ \n"
				+ "  graph <"+super.graph+"/"+db+"> {\n"
				+turtle 
				+ "} }" ;
	}
	public String getSparqlStoreTemporalEntity(String key,String temporalentity_id,String jsonld,String db) throws JsonLdError {
		
//		logger.info("\n---------------------------------------\ncreateEntity.JSON-LD: \n" + resolved + "\n");
		String composedkey = key+"@"+temporalentity_id;
		String turtle = jsonldToTriple(jsonld,key,composedkey);
		
		//for faster and easy getAllIDs method:
		turtle += "<"+ super.ontology+"#"+FOR_INTERNAL_USE+"><"+super.ontology+"#"+EXISTS_ID+"><"+key+"> .\n";
		//Linking key with composedkey
		turtle += "<"+key+"><"+super.ontology+"#"+HAS_TEMPORAL_INSTANCE+"><"+composedkey+"> .\n";
		
//		logger.info("\n---------------------------------------\ncreateEntity.RDF: \n" + turtle + "\n");
		return "INSERT DATA\n"
				+ "{ \n"
				+ "  graph <"+super.graph+"/"+db+"> {\n"
				+turtle 
				+ "} }" ;
	}
	public Set<String> getAllKeys(){
		return getAllKeys(DBConstants.DBTABLE_ENTITY);
	}
	
	public Set<String> getAllKeys(String db){
		String sparql = "SELECT ?o\n"
				+ "where { \n"
				+ "  graph  <"+super.graph+"/"+db+"> {\n" 
				+ "<"+ super.ontology+"#"+FOR_INTERNAL_USE+"><"+super.ontology+"#"+EXISTS_ID+"> ?o.\n"
				+ "} }" ;
		QueryResponse res = (QueryResponse)super.executeQuery(sparql);
		Set<String> ids = new HashSet<String>();
		for (Bindings binding : res.getBindingsResults().getBindings()) {
			ids.add(binding.getValue("o"));
		} 
		return ids;
	}
	
	public boolean deleteInternalEntityKey(String key,String db) {
		String sparql = "DELETE DATA\n"
				+ "where { \n"
				+ "  graph <"+super.graph+"/"+db+"> {\n"
				+"<"+ super.ontology+"#"+FOR_INTERNAL_USE+"><"+super.ontology+"#"+EXISTS_ID+"> <"+key+">.\n"
				+ "} }" ;
		return !super.executeUpdate(sparql).isError();
		
	}
	
	//-------------------------------------------------------------------------------
	//---------------------------------------------------------------CONVERT UTILS---
	//-------------------------------------------------------------------------------
	
	private String bindingsResultsToJsonld(BindingsResults bindings) throws JsonLdError, SEPABindingsException {
		String triples = "";
		for (Bindings binding : bindings.getBindings()) {
			triples+="<" +binding.getValue("s") + ">";
			triples+=" <" +binding.getValue("p") + ">";
			if(binding.isLiteral("o")) {
				triples+=" \"" +binding.getValue("o") + "\"";
			}else {
				triples+=" <" +binding.getValue("o") + ">";
			}
			triples+=".\n";
		} 

		Reader targetReader = new StringReader(triples);
		Document document = JsonDocument.of(targetReader);
		return JsonLd.fromRdf(document).get().toString();
	}
	
	private String rdfDatasetToTripleString(RdfDataset rdf, String key) {
		String turtle = "";
		HashMap<String,String> blankNodeHasMap = new HashMap<String,String>();
		for ( RdfNQuad iterable_element : rdf.toList()) {
			if(iterable_element.getSubject().isBlankNode()) {
				String genericBlankNode = iterable_element.getSubject().getValue();
				String uniqueBlankNode;
				if(blankNodeHasMap.containsKey(genericBlankNode)){
					uniqueBlankNode = blankNodeHasMap.get(genericBlankNode);
				}else {
					uniqueBlankNode = "_:"+key+"_"+UUID.randomUUID().toString();
					blankNodeHasMap.put(genericBlankNode,uniqueBlankNode);
				}
				turtle += "<"+ uniqueBlankNode + "><"+iterable_element.getPredicate().getValue() + "><"+ iterable_element.getObject().getValue() +"> .\n";
			}else if(iterable_element.getObject().isBlankNode()){
				String genericBlankNode = iterable_element.getObject().getValue();
				String uniqueBlankNode;
				if(blankNodeHasMap.containsKey(genericBlankNode)){
					uniqueBlankNode = blankNodeHasMap.get(genericBlankNode);
				}else {
					uniqueBlankNode = "_:"+key+"_"+UUID.randomUUID().toString();
					blankNodeHasMap.put(genericBlankNode,uniqueBlankNode);
				}
				turtle += "<"+ iterable_element.getSubject().getValue()  + "><"+iterable_element.getPredicate().getValue() + "><"+ uniqueBlankNode +"> .\n";
			}else{
				turtle += "<"+ iterable_element.getSubject().getValue() + "><"+iterable_element.getPredicate().getValue() + "><"+ iterable_element.getObject().getValue() +"> .\n";
			}
		}
		return turtle;
	}

	private String jsonldToTriple(String jsonld,String key) throws JsonLdError {
		Reader targetReader = new StringReader(jsonld);
		Document document = JsonDocument.of(targetReader);
		RdfDataset rdf = JsonLd.toRdf(document).get();
		return rdfDatasetToTripleString(rdf,key);
	}
	
	private String rdfDatasetToTripleString(RdfDataset rdf, String key,String replaceKey) {
		String turtle = "";
		HashMap<String,String> blankNodeHasMap = new HashMap<String,String>();
		for ( RdfNQuad iterable_element : rdf.toList()) {
			if(iterable_element.getSubject().isBlankNode()) {
				String genericBlankNode = iterable_element.getSubject().getValue();
				String uniqueBlankNode;
				if(blankNodeHasMap.containsKey(genericBlankNode)){
					uniqueBlankNode = blankNodeHasMap.get(genericBlankNode);
				}else {
					uniqueBlankNode = "_:"+replaceKey+"_"+UUID.randomUUID().toString();
					blankNodeHasMap.put(genericBlankNode,uniqueBlankNode);
				}
				turtle += "<"+ uniqueBlankNode + "><"+iterable_element.getPredicate().getValue() + "><"+ iterable_element.getObject().getValue() +"> .\n";
			}else if(iterable_element.getObject().isBlankNode()){
				String genericBlankNode = iterable_element.getObject().getValue();
				String uniqueBlankNode;
				if(blankNodeHasMap.containsKey(genericBlankNode)){
					uniqueBlankNode = blankNodeHasMap.get(genericBlankNode);
				}else {
					uniqueBlankNode = "_:"+replaceKey+"_"+UUID.randomUUID().toString();
					blankNodeHasMap.put(genericBlankNode,uniqueBlankNode);
				}
				String s = iterable_element.getSubject().getValue();
				if(s.compareTo(key)==0) {
					s= replaceKey;
				}
				turtle += "<"+ s + "><"+iterable_element.getPredicate().getValue() + "><"+ uniqueBlankNode +"> .\n";
			}else{
				String s = iterable_element.getSubject().getValue();
				if(s.compareTo(key)==0) {
					s= replaceKey;
				}
				String o = iterable_element.getObject().getValue() ;
				if(o.compareTo(key)==0) {
					o= replaceKey;
				}
				turtle += "<"+ s + "><"+iterable_element.getPredicate().getValue() + "><"+ o+"> .\n";
			}
		}
		return turtle;
	}
	private String jsonldToTriple(String jsonld,String key,String replaceKey) throws JsonLdError {
		Reader targetReader = new StringReader(jsonld);
		Document document = JsonDocument.of(targetReader);
		RdfDataset rdf = JsonLd.toRdf(document).get();
		return rdfDatasetToTripleString(rdf,key,replaceKey);
	}


}
