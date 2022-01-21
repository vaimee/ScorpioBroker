package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.util.HashSet;
import java.util.Map;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.protocol.SPARQL11Properties.QueryHTTPMethod;
import it.unibo.arces.wot.sepa.commons.protocol.SPARQL11Properties.UpdateHTTPMethod;
import it.unibo.arces.wot.sepa.commons.protocol.SPARQL11Protocol;
import it.unibo.arces.wot.sepa.commons.request.QueryRequest;
import it.unibo.arces.wot.sepa.commons.request.UpdateRequest;
import it.unibo.arces.wot.sepa.commons.response.Response;

public class SepaGateway {

	
	private static SepaGateway instance=null;
	public static SepaGateway getInstance() throws SEPASecurityException {
		//System.out.println("\n###############################\n DAO setted on SPARQL\n###############################\n");
		if(instance==null) {
			instance=new SepaGateway();
		}
		return instance;
	}
	
	
	protected SPARQL11Protocol client=null;
	protected UpdateHTTPMethod httpMethod_u = UpdateHTTPMethod.POST;
	protected String path_u="/sparql";
	protected QueryHTTPMethod httpMethod_q = QueryHTTPMethod.POST;
	protected String path_q="/sparql";
	protected String host="localhost"; //default--> sepa and then take it from environment vars
	protected int port=8000;
//	private String authorization;//not implemented yet
	protected String scheme = "http";
	protected int timeOut =60000;
	

	private SepaGateway() throws SEPASecurityException {
	    Map<String, String> env = System.getenv();
	  
    	if(env.get("SEPA_HOST")!=null) {
    		host= env.get("SEPA_HOST");
    	}
    	if(env.get("SEPA_PORT")!=null) {
    		port= Integer.parseInt(env.get("SEPA_PORT"));
    	}
    	if(env.get("SEPA_SCHEME")!=null) {
    		scheme= env.get("SEPA_SCHEME");
    	}
    	if(env.get("SEPA_TIME_OUT")!=null) {
    		timeOut= Integer.parseInt(env.get("SEPA_TIME_OUT"));
    	}

    	if(env.get("SEPA_HTTP_METHOD_QUERY")!=null) {
    		String method =env.get("SEPA_HTTP_METHOD_QUERY");
    		if(method.toLowerCase().compareTo("post")==0) {
    			httpMethod_u = UpdateHTTPMethod.POST;
    		}else if(method.toLowerCase().compareTo("encoded post")==0){
    			httpMethod_u = UpdateHTTPMethod.URL_ENCODED_POST;
    		}
    	}

    	if(env.get("SEPA_HTTP_METHOD_QUERY")!=null) {
    		String method =env.get("SEPA_HTTP_METHOD_QUERY");
    		if(method.toLowerCase().compareTo("post")==0) {
    			httpMethod_q = QueryHTTPMethod.POST;
    		}else if(method.toLowerCase().compareTo("encoded post")==0){
    			httpMethod_q = QueryHTTPMethod.URL_ENCODED_POST;
    		}else if(method.toLowerCase().compareTo("get")==0){
    			httpMethod_q = QueryHTTPMethod.GET;
    		}
    	}

    	if(env.get("SEPA_PATH_QUERY")!=null) {
    		path_q= env.get("SEPA_PATH_QUERY");
    	}

    	if(env.get("SEPA_PATH_UPDATE")!=null) {
    		path_u= env.get("SEPA_PATH_UPDATE");
    	}
    	
		client= new SPARQL11Protocol();
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
					path_u,
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
								+SPARQLConstant.RDFS_PREFIX
								+SPARQLConstant.NGSI_PREFIX
								+SPARQLConstant.RDF_PREFIX
								+"\n" + sparql;
			
			QueryRequest req = new QueryRequest(
					httpMethod_q,
					scheme,
					host,
					port,
					path_q,
					prefix_sparql,
					new HashSet<String>(),
					new HashSet<String>(),
					null,
					timeOut,
					1);
			
			return client.query(req);
		
	}
}
