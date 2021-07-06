package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql;

import java.io.Reader;
import java.io.StringReader;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.HashMap;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.RdfNQuad;

import eu.neclab.ngsildbroker.commons.constants.DBConstants;
import eu.neclab.ngsildbroker.commons.datatypes.TemporalEntityStorageKey;
import eu.neclab.ngsildbroker.commons.serialization.DataSerializer;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IStorageWriterDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SepaGateway;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SepaJSONLDGateway;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;

//@Repository("emstorage")
//@ConditionalOnProperty(value = "writer.enabled", havingValue = "true", matchIfMissing = false)
public class StorageWriterDAOSPARQL implements IStorageWriterDAO {

	private final static Logger logger = LogManager.getLogger(StorageWriterDAOSPARQL.class);
//	public static final Gson GSON = DataSerializer.GSON;
	
	
	private SepaJSONLDGateway sepa;
	public StorageWriterDAOSPARQL() {
		try {
			this.sepa= new SepaJSONLDGateway();
		} catch (SEPASecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	public boolean store(String tableName, String columnName, String key, String value) {
//		logger.info("\n---------------------------------------\nstore: \n tableName: " + tableName + "\n");
//		logger.info("\n---------------------------------------\nstore: \n columnName: " + columnName + "\n");
//		logger.info("\n---------------------------------------\nstore: \n key: " + key + "\n");
//		logger.info("\n---------------------------------------\nstore: \n value: " + value + "\n");
		String path = tableName+"/"+columnName;
		try {
			boolean success =false;
			if (value != null && !value.equals("null")) {
				success =sepa.generalStoreEntity(key,value,path);
				logger.info("\n===> NGSI-LD to SPARQL on sepa (generalStorEntity) success: " + success + "\n");
			}else {
				success =sepa.generalDeleteEntityRecursively(key,path);
				logger.info("\n===> NGSI-LD to SPARQL on sepa (generalDeleteEntityRecursively) success: " + success + "\n");
			}
			return true; // success;
		} catch (Exception e) {
			logger.error("Exception ::", e);
			e.printStackTrace();
		}
		return false;
	}

	public boolean storeTemporalEntity(String key, String value) throws SQLException {

		
//		logger.info("\n---------------------------------------\n storeTemporalEntity: \n key: " + key + "\n");
//		logger.info("\n---------------------------------------\n storeTemporalEntity: \n value: " + value + "\n");
		try {

			TemporalEntityStorageKey tesk = DataSerializer.getTemporalEntityStorageKey(key);

			String entityId = tesk.getEntityId();
			String entityType = tesk.getEntityType();
			String entityCreatedAt = tesk.getEntityCreatedAt();
			String entityModifiedAt = tesk.getEntityModifiedAt();

			String attributeId = tesk.getAttributeId();
			String instanceId = tesk.getInstanceId(); // WARING--------------------------_>IGNORED
			Boolean overwriteOp = tesk.getOverwriteOp();

			Integer n = 0;

			if (!value.equals("null")) {
				//-------------------------------------TRANZACTION (T1 start)
						String sparql="";
						boolean needUpdateModData = true;
						if (entityId != null && entityType != null && entityCreatedAt != null
								&& entityModifiedAt != null) {
//							writerJdbcTemplateWithTransaction.update(sql, entityId, entityType, entityCreatedAt,entityModifiedAt);
							sparql= "INSERT DATA { GRAPH "+sepa.getGraph(DBConstants.DBTABLE_TEMPORALENTITY)+"{\n"
									+ "<"+entityId+"><"+SepaJSONLDGateway.ENTITY_TYPE+"><"+entityType+">.\n"
									+ "<"+entityId+"><"+SepaJSONLDGateway.ENTITY_CREATED_AT+"><"+entityCreatedAt+">.\n"
									+ "<"+entityId+"><"+SepaJSONLDGateway.ENTITY_MODIFIED_AT+"><"+entityModifiedAt+">.\n"
									+ "}}\n";
							needUpdateModData=false;
						}

						if (entityId != null && attributeId != null) {
							String composedKey= entityId+"@"+attributeId;
							if (overwriteOp != null && overwriteOp) {
//								sql = "DELETE FROM " + DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE
//										+ " WHERE temporalentity_id = ? AND attributeid = ?";
//								tn += writerJdbcTemplateWithTransaction.update(sql, entityId, attributeId);
								sparql+=sepa.getSparqlDeleteEntityRecursively(composedKey, DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE)+"\n";
//								sparql+="DELETE WHERE { GRAPH "+sepa.getGraph(DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE)+"{"
//										+ "<"+entityId+"@"+attributeId+"> ?p ?o.\n"
//										+ "}}";
							}
//							sql = "INSERT INTO " + DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE
//									+ " (temporalentity_id, attributeid, data) VALUES (?, ?, ?::jsonb) ON CONFLICT(temporalentity_id, attributeid, instanceid) DO UPDATE SET data = EXCLUDED.data";
//							tn += writerJdbcTemplateWithTransaction.update(sql, entityId, attributeId, value);
							sparql+=sepa.getSparqlStoreTemporalEntity(key,composedKey,value, DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE)+"\n";
						
							// update modifiedat field in temporalentity  //<<<<<------------IS THIS DUPLICATE!?
//							sql = "UPDATE " + DBConstants.DBTABLE_TEMPORALENTITY
//									+ " SET modifiedat = ?::timestamp WHERE id = ?";
//							tn += writerJdbcTemplateWithTransaction.update(sql, entityModifiedAt, entityId);
							if(needUpdateModData) {
								sparql+= "INSERT DATA { GRAPH "+sepa.getGraph(DBConstants.DBTABLE_TEMPORALENTITY)+"{\n"
										+ "<"+entityId+"><"+SepaJSONLDGateway.ENTITY_MODIFIED_AT+"><"+entityModifiedAt+">.\n"
										+ "}}\n";
								needUpdateModData=false;
							}
						}
						if(sparql.length()>0) {
							boolean success = !sepa.executeUpdate(sparql).isError();
							return true; // success
						}else {
							return false;
						}

						//-------------------------------------TRANZACTION (T1 end)

			} else {
				boolean success = false;
				//WARNING-------> instanceId ignored
//				if (entityId != null && attributeId != null && instanceId != null) {
////					sql = "DELETE FROM " + DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE
////							+ " WHERE temporalentity_id = ? AND attributeid = ? AND instanceid = ?";
////					n = writerJdbcTemplate.update(sql, entityId, attributeId, instanceId);
//					String composedKey= entityId+"@"+attributeId;
//					boolean success = sepa.generalDeleteEntityRecursively(composedKey,DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE);
//					
//				} else 
					if (entityId != null && attributeId != null) {
//					sql = "DELETE FROM " + DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE
//							+ " WHERE temporalentity_id = ? AND attributeid = ?";
//					n = writerJdbcTemplate.update(sql, entityId, attributeId);
					String composedKey= entityId+"@"+attributeId;
					success = sepa.generalDeleteEntityRecursively(composedKey,DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE);
					
				} else if (entityId != null) {
//					sql = "DELETE FROM " + DBConstants.DBTABLE_TEMPORALENTITY + " WHERE id = ?";
//					n = writerJdbcTemplate.update(sql, entityId);
					success= sepa.generalDeleteEntityRecursively(entityId,DBConstants.DBTABLE_TEMPORALENTITY);
				}
					return true; // success
			}

		} catch (Exception e) {
			logger.error("Exception ::", e);
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean storeEntity(String key, String value, String valueWithoutSysAttrs, String kvValue)
			throws SQLTransientConnectionException {
		
//		logger.info("\ncall on DAO ====> StorageWriterDAOSQL.storeEntity <====\n");
//		String sql;
//		int n = 0;
//		if (value != null && !value.equals("null")) {
//			sql = "INSERT INTO " + DBConstants.DBTABLE_ENTITY + " (id, " + DBConstants.DBCOLUMN_DATA + ", "
//					+ DBConstants.DBCOLUMN_DATA_WITHOUT_SYSATTRS + ",  " + DBConstants.DBCOLUMN_KVDATA
//					+ ") VALUES (?, ?::jsonb, ?::jsonb, ?::jsonb) ON CONFLICT(id) DO UPDATE SET ("
//					+ DBConstants.DBCOLUMN_DATA + ", " + DBConstants.DBCOLUMN_DATA_WITHOUT_SYSATTRS + ",  "
//					+ DBConstants.DBCOLUMN_KVDATA + ") = (EXCLUDED." + DBConstants.DBCOLUMN_DATA + ", EXCLUDED."
//					+ DBConstants.DBCOLUMN_DATA_WITHOUT_SYSATTRS + ",  EXCLUDED." + DBConstants.DBCOLUMN_KVDATA + ")";
//			n = writerJdbcTemplate.update(sql, key, value, valueWithoutSysAttrs, kvValue);
//		} else {
//			sql = "DELETE FROM " + DBConstants.DBTABLE_ENTITY + " WHERE id = ?";
//			n = writerJdbcTemplate.update(sql, key);
//		}
//		logger.trace("Rows affected: " + Integer.toString(n));
//		return true; // (n>0);

		try {
			boolean success =false;
			String path =  DBConstants.DBTABLE_ENTITY +"/"+ DBConstants.DBCOLUMN_DATA ;
			if (value != null && !value.equals("null")) {
				success =sepa.generalStoreEntity(key,value,path);
				
				path=  DBConstants.DBTABLE_ENTITY +"/"+ DBConstants.DBCOLUMN_DATA_WITHOUT_SYSATTRS ;
				success =sepa.generalStoreEntity(key,valueWithoutSysAttrs,path);
				
				path=  DBConstants.DBTABLE_ENTITY +"/"+ DBConstants.DBCOLUMN_KVDATA ;
				success =sepa.generalStoreEntity(key,kvValue,path);
				
				logger.info("\n===> NGSI-LD to SPARQL on sepa (generalStorEntity) success: " + success + "\n");
			}else {
				success =sepa.generalDeleteEntityRecursively(key,path);
				logger.info("\n===> NGSI-LD to SPARQL on sepa (generalDeleteEntityRecursively) success: " + success + "\n");
			}
			return true; // success;
		} catch (Exception e) {
			logger.error("Exception ::", e);
			e.printStackTrace();
		}
		return false;
	}
	





}
