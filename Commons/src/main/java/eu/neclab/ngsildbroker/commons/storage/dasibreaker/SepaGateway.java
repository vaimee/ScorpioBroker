package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.protocol.SPARQL11Properties.QueryHTTPMethod;
import it.unibo.arces.wot.sepa.commons.protocol.SPARQL11Properties.UpdateHTTPMethod;
import it.unibo.arces.wot.sepa.commons.protocol.SPARQL11Protocol;
import it.unibo.arces.wot.sepa.commons.request.QueryRequest;
import it.unibo.arces.wot.sepa.commons.request.UpdateRequest;
import it.unibo.arces.wot.sepa.commons.response.Response;

public class SepaGateway {

	
	
	protected SPARQL11Protocol client=null;
	protected UpdateHTTPMethod httpMethod_u = UpdateHTTPMethod.POST;
	protected QueryHTTPMethod httpMethod_q = QueryHTTPMethod.POST;
	protected String host="localhost"; //default--> sepa and then take it from environment vars
	protected int port=8000;
//	private String authorization;//not implemented yet
	protected String scheme = "http";
	protected int timeOut =60000;
	
	protected String protocol="http";
	protected String graph ="http://dasi.breaker.project/"; 
	protected String ontology ="http://dasi.breaker.ngsi.ontology/"; 

	public SepaGateway() throws SEPASecurityException {
	    Map<String, String> env = System.getenv();
	  
    	if(env.get("sepaHost")!=null) {
    		host= env.get("sepaHost");
    	}
    	if(env.get("sepaPort")!=null) {
    		port= Integer.parseInt(env.get("sepaPort"));
    	}
    	if(env.get("sepaScheme")!=null) {
    		scheme= env.get("sepaScheme");
    	}
    	
		client= new SPARQL11Protocol();
	}
	
	public String getGraph() {
		return graph;
	}
	public String getGraph(String path) {
		return graph+path;
	}
	
	public Response executeUpdate(String sparql) {	
		
			String prefix_sparql=SPARQLConstant.XSD_PREFIX
								+SPARQLConstant.RDF_PREFIX
								+SPARQLConstant.NGSI_PREFIX
								+"\n" + sparql;
			
			UpdateRequest req = new UpdateRequest(
					httpMethod_u,
					scheme,
					host,
					port,
					"/update",
					prefix_sparql,
					new HashSet<String>(),
					new HashSet<String>(),
					null,//---------------
					timeOut,
					1);
			
			return client.update(req);
		
	}
	

	public Response executeQuery(String sparql) {	

			String prefix_sparql=SPARQLConstant.XSD_PREFIX
								+SPARQLConstant.RDF_PREFIX
								+SPARQLConstant.NGSI_PREFIX
								+"\n" + sparql;
			
			QueryRequest req = new QueryRequest(
					httpMethod_q,
					scheme,
					host,
					port,
					"/query",
					prefix_sparql,
					new HashSet<String>(),
					new HashSet<String>(),
					null,
					timeOut,
					1);
			
			return client.query(req);
		
	}
}
