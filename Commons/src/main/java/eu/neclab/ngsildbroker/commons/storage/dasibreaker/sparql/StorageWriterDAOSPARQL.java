package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql;

import java.io.Reader;
import java.io.StringReader;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.RdfNQuad;

import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IStorageWriterDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SepaGateway;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;

//@Repository("emstorage")
//@ConditionalOnProperty(value = "writer.enabled", havingValue = "true", matchIfMissing = false)
public class StorageWriterDAOSPARQL implements IStorageWriterDAO {

	private final static Logger logger = LogManager.getLogger(StorageWriterDAOSPARQL.class);
//	public static final Gson GSON = DataSerializer.GSON;
	
	
	private SepaGateway sepa;
	public StorageWriterDAOSPARQL() {
		try {
			this.sepa= new SepaGateway();
		} catch (SEPASecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public boolean store(String tableName, String columnName, String key, String value) {
		logger.info("\n---------------------------------------\nstore: \n tableName: " + tableName + "\n");
		logger.info("\n---------------------------------------\nstore: \n columnName: " + columnName + "\n");
		logger.info("\n---------------------------------------\nstore: \n key: " + key + "\n");
		logger.info("\n---------------------------------------\nstore: \n value: " + value + "\n");
		try {
			boolean success =generalStorEntity(key,value);
			logger.info("\nNGSI-LD to SPARQL on sepa success: " + success + "\n");
			return true;
		} catch (Exception e) {
			logger.error("Exception ::", e);
			e.printStackTrace();
		}
		return false;
	}

	public boolean storeTemporalEntity(String key, String value) throws SQLException {

		logger.info("\n---------------------------------------\n storeTemporalEntity: \n key: " + key + "\n");
		logger.info("\n---------------------------------------\n storeTemporalEntity: \n value: " + value + "\n");
		try {
			boolean success =generalStorEntity(key,value);
			logger.info("\nNGSI-LD to SPARQL on sepa success: " + success + "\n");
			return true;
		} catch (Exception e) {
			logger.error("Exception ::", e);
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean storeEntity(String key, String value, String valueWithoutSysAttrs, String kvValue)
			throws SQLTransientConnectionException {
		// TODO Auto-generated method stub
		return false;
	}
	

	
	private boolean generalStorEntity(String key,String jsonld) throws JsonLdError {
		return generalStorEntity(key,jsonld,"<http://dasi.breaker.project/ngsi>");
	}
	
	private boolean generalStorEntity(String key,String jsonld, String graph) throws JsonLdError {
		Reader targetReader = new StringReader(jsonld);
		Document document = JsonDocument.of(targetReader);
		RdfDataset rdf = JsonLd.toRdf(document).get();
		
//		logger.info("\n---------------------------------------\ncreateEntity.JSON-LD: \n" + resolved + "\n");
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
//		logger.info("\n---------------------------------------\ncreateEntity.RDF: \n" + turtle + "\n");
		String sparql = "INSERT DATA\n"
				+ "{ \n"
				+ "  graph <http://dasi.breaker.project/ngsi> {\n"
				+turtle 
				+ "} }" ;
		return !sepa.executeUpdate(sparql).isError();
	}




}
