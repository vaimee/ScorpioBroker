package eu.neclab.ngsildbroker.entityhandler.services.dasibreaker;

import java.util.HashSet;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.protocol.SPARQL11Properties.UpdateHTTPMethod;
import it.unibo.arces.wot.sepa.commons.protocol.SPARQL11Protocol;
import it.unibo.arces.wot.sepa.commons.request.UpdateRequest;
import it.unibo.arces.wot.sepa.commons.response.Response;

public class SepaGateway {

	private SPARQL11Protocol client=null;
	private UpdateHTTPMethod httpMethod = UpdateHTTPMethod.POST;
	private String host="localhost";
	private int port=8000;
	private String path="/update";
	//private String authorization;//not implemented yet
	private String scheme = "http";
	private int timeOut =60000;
	
	public String protocol="http";
	public String graph ="<http://dasi.breaker.project/>"; 

	public SepaGateway() throws SEPASecurityException {
			client= new SPARQL11Protocol();
	}
	
	public Response executeUpdate(String sparql) {	
		
			UpdateRequest req = new UpdateRequest(
					httpMethod,
					scheme,
					host,
					port,
					path,
					sparql,
					new HashSet<String>(),
					new HashSet<String>(),
					null,
					timeOut,
					1);
			
			return client.update(req);
		
		}
}
