package eu.neclab.ngsildbroker.entityhandler.controller.dasibreaker;

import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.RdfNQuad;

import eu.neclab.ngsildbroker.commons.constants.AppConstants;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;
import eu.neclab.ngsildbroker.commons.tools.HttpUtils;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.request.UpdateRequest;


public class EntityControllerSPARQL implements IEntityHandler {

	private final static Logger logger = LoggerFactory.getLogger(EntityControllerSPARQL.class);
	
//	private EntityService entityService;
//	private ObjectMapper objectMapper;
//	private ParamsResolver paramsResolver;
	private HttpUtils httpUtils;
	private SepaGateway sepa=null;
	
	public EntityControllerSPARQL(HttpUtils httpUtils) {
		super();
		this.httpUtils = httpUtils;
	}

	@Override
	public ResponseEntity<byte[]> createEntity(HttpServletRequest request, String payload) throws MalformedURLException, UnsupportedEncodingException, ResponseException, JsonLdError, SEPASecurityException {

		if(this.sepa==null) {
			this.sepa= new SepaGateway();
		}
		String resolved = httpUtils.expandPayload(request, payload, AppConstants.ENTITIES_URL_ID);
		Reader targetReader = new StringReader(resolved);
		Document document = JsonDocument.of(targetReader);
		RdfDataset rdf = JsonLd.toRdf(document).get();
		
//		logger.info("\n---------------------------------------\ncreateEntity.JSON-LD: \n" + resolved + "\n");
		String turtle = "";
		for ( RdfNQuad iterable_element : rdf.toList()) {
			turtle += "<"+ iterable_element.getSubject().getValue() + "><"+iterable_element.getPredicate().getValue() + "><"+ iterable_element.getObject().getValue() +"> .\n";
		}
//		logger.info("\n---------------------------------------\ncreateEntity.RDF: \n" + turtle + "\n");
		
		String sparql = "INSERT DATA\n"
				+ "{ \n"
				+ "  graph <http://dasi.breaker.project/ngsi> {\n"
				+turtle 
				+ "} }" ;
		boolean success = !sepa.executeUpdate(sparql).isError();
		logger.info("\nNGSI-LD to SPARQL on sepa success: " + success + "\n");
		return null;
	}

	@Override
	public ResponseEntity<byte[]> updateEntity(HttpServletRequest request, String payload) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<byte[]> appendEntity(HttpServletRequest request, String payload, String options) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<byte[]> partialUpdateEntity(HttpServletRequest request, String payload) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<byte[]> deleteAttribute(HttpServletRequest request, String datasetId, String deleteAll) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<byte[]> deleteEntity(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
