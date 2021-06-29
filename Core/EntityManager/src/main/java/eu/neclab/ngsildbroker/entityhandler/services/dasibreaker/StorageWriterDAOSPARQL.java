package eu.neclab.ngsildbroker.entityhandler.services.dasibreaker;

import java.io.Reader;
import java.io.StringReader;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.RdfNQuad;

import eu.neclab.ngsildbroker.commons.constants.AppConstants;
import eu.neclab.ngsildbroker.commons.constants.DBConstants;
import eu.neclab.ngsildbroker.commons.datatypes.TemporalEntityStorageKey;
import eu.neclab.ngsildbroker.commons.serialization.DataSerializer;
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
			String resolved = value;
			Reader targetReader = new StringReader(resolved);
			Document document = JsonDocument.of(targetReader);
			RdfDataset rdf = JsonLd.toRdf(document).get();
			
//			logger.info("\n---------------------------------------\ncreateEntity.JSON-LD: \n" + resolved + "\n");
			String turtle = "";
			for ( RdfNQuad iterable_element : rdf.toList()) {
				//iterable_element.getSubject().isBlankNode()
				//hashmap per sostituirli uguali 
				turtle += "<"+ iterable_element.getSubject().getValue() + "><"+iterable_element.getPredicate().getValue() + "><"+ iterable_element.getObject().getValue() +"> .\n";
			}
//			logger.info("\n---------------------------------------\ncreateEntity.RDF: \n" + turtle + "\n");
			String sparql = "INSERT DATA\n"
					+ "{ \n"
					+ "  graph <http://dasi.breaker.project/ngsi> {\n"
					+turtle 
					+ "} }" ;
			boolean success = !sepa.executeUpdate(sparql).isError();
			logger.info("\nNGSI-LD to SPARQL on sepa success: " + success + "\n");
			return true;
		} catch (Exception e) {
			logger.error("Exception ::", e);
			e.printStackTrace();
		}
		return false;
	}

	public boolean storeEntity(String key, String value, String valueWithoutSysAttrs, String kvValue)
			throws SQLTransientConnectionException {
		String sparql;

		logger.info("\n---------------------------------------\n storeEntity: \n key: " + key + "\n");
		logger.info("\n---------------------------------------\n storeEntity: \n valueWithoutSysAttrs: " + valueWithoutSysAttrs + "\n");
		logger.info("\n---------------------------------------\n storeEntity: \n kvValue: " + kvValue + "\n");
		logger.info("\n---------------------------------------\n storeEntity: \n value: " + value + "\n");
		return true; 
	}

	public boolean storeTemporalEntity(String key, String value) throws SQLException {

		logger.info("\n---------------------------------------\n storeTemporalEntity: \n key: " + key + "\n");
		logger.info("\n---------------------------------------\n storeTemporalEntity: \n value: " + value + "\n");
		try {

			return true;
		} catch (Exception e) {
			logger.error("Exception ::", e);
			e.printStackTrace();
		}
		return false;
	}

}
