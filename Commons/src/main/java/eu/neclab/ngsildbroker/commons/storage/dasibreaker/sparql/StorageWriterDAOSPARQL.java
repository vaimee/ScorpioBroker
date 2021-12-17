package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql;

import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.apicatalog.jsonld.JsonLdError;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.neclab.ngsildbroker.commons.constants.DBConstants;
import eu.neclab.ngsildbroker.commons.datatypes.TemporalEntityStorageKey;
import eu.neclab.ngsildbroker.commons.serialization.DataSerializer;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLGeneratorUpdate;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IStorageWriterDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SepaGateway;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.response.ErrorResponse;
import it.unibo.arces.wot.sepa.commons.response.Response;

//@Repository("emstorage")
//@ConditionalOnProperty(value = "writer.enabled", havingValue = "true", matchIfMissing = false)
public class StorageWriterDAOSPARQL implements IStorageWriterDAO {

	private final static Logger logger = LogManager.getLogger(StorageWriterDAOSPARQL.class);
//	public static final Gson GSON = DataSerializer.GSON;
	
	
	private SepaGateway sepa;
	
	public StorageWriterDAOSPARQL() {
		/*
		 * That constructor is not necessary
		 * We can get the SEPA instance in store method directly
		 */
		try {
			this.sepa=SepaGateway.getInstance();
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
		
		SPARQLGeneratorUpdate jrs = new SPARQLGeneratorUpdate(tableName);
		jrs.addTriple(key, columnName, value);
		String sparql="";
		if (value != null && !value.equals("null")) {
			try {
				sparql = jrs.generateCreate(key, true);
			} catch (JsonLdError e) {
				logger.error("Exception ::", e);
				return false;
			}
		}else {
			sparql = jrs.generateDeleteAllByKey(key);
		}
	
		
		Response res=sepa.executeUpdate(sparql);
		boolean success= !res.isError();
		logger.info("\n STORE--> sparql:\n" + sparql);
		if(!success) {
			System.err.print(((ErrorResponse)res).getError());
		}
		logger.info("\n STORE--> success:" + success);
		
		return success;
	}

	
	public boolean storeTemporalEntity(String key, String value) throws SQLException {
//		logger.info("\ncall on DAO ====> StorageWriterDAOSQL.storeTemporalEntity <====\n");
//		logger.info("\ncall on DAO ====> StorageWriterDAOSQL.storeTemporalEntity.key: "+key+"\n");
//		logger.info("\ncall on DAO ====> StorageWriterDAOSQL.storeTemporalEntity.value: "+value+"\n");
		String sparql = "";
		try {

			TemporalEntityStorageKey tesk = DataSerializer.getTemporalEntityStorageKey(key);

			String entityId = tesk.getEntityId();
			String entityType = tesk.getEntityType();
			String entityCreatedAt = tesk.getEntityCreatedAt();
			String entityModifiedAt = tesk.getEntityModifiedAt();

			String attributeId = tesk.getAttributeId();
			String instanceId = tesk.getInstanceId();
			Boolean overwriteOp = tesk.getOverwriteOp();
			//Integer n = 0;
			
//			logger.info("\ncall on DAO ====> StorageWriterDAOSQL.storeTemporalEntity.entityId: "+entityId+"\n");
//			logger.info("\ncall on DAO ====> StorageWriterDAOSQL.storeTemporalEntity.entityType: "+entityType+"\n");
//			logger.info("\ncall on DAO ====> StorageWriterDAOSQL.storeTemporalEntity.entityCreatedAt: "+entityCreatedAt+"\n");
//			logger.info("\ncall on DAO ====> StorageWriterDAOSQL.storeTemporalEntity.entityModifiedAt: "+entityModifiedAt+"\n");
//			logger.info("\ncall on DAO ====> StorageWriterDAOSQL.storeTemporalEntity.attributeId: "+attributeId+"\n");
//			logger.info("\ncall on DAO ====> StorageWriterDAOSQL.storeTemporalEntity.instanceId: "+instanceId+"\n");
			
			//In the SQL version there is a Transaction, we can just create unique SPARQL request for do that (maybe?)
			

			
		
			if (!value.equals("null")) {
				if (//---------------------DBTABLE_TEMPORALENTITY
						entityId != null 
						&& entityType != null 
						&& entityCreatedAt != null
						&& entityModifiedAt != null
				) {
//					sql = "INSERT INTO " + DBConstants.DBTABLE_TEMPORALENTITY
//							+ " (id, type, createdat, modifiedat) VALUES (?, ?, ?::timestamp, ?::timestamp)
//					ON CONFLICT(id) DO UPDATE SET type = EXCLUDED.type, createdat = EXCLUDED.createdat, modifiedat = EXCLUDED.modifiedat";
//					tn = writerJdbcTemplateWithTransaction.update(sql, entityId, entityType, entityCreatedAt,
//							entityModifiedAt);

					SPARQLGeneratorUpdate jrs = new SPARQLGeneratorUpdate(DBConstants.DBTABLE_TEMPORALENTITY);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_ID, entityId);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_TYPE, entityType);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_CREATED_AT, entityCreatedAt);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_MODIFIED_AT, entityModifiedAt);
					sparql+=jrs.generateCreate(entityId, true);
				}
				if (entityId != null && attributeId != null) {
					if (overwriteOp != null && overwriteOp) {
//						sql = "DELETE FROM " + DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE
//								+ " WHERE temporalentity_id = ? AND attributeid = ?";
//						tn += writerJdbcTemplateWithTransaction.update(sql, entityId, attributeId);

						SPARQLGeneratorUpdate jrs = new SPARQLGeneratorUpdate(DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE);
						jrs.addTriple(entityId, DBConstants.DBCOLUMN_ID, entityId);
						jrs.addTriple(entityId, DBConstants.DBCOLUMN_ATTRIBUTE_ID, attributeId);
						sparql+=jrs.generateDeleteAllWhere(entityId);
					}
					
//					sql = "INSERT INTO " + DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE
//							+ " (temporalentity_id, attributeid, data) VALUES (?, ?, ?::jsonb) ON CONFLICT(temporalentity_id, attributeid, instanceid) DO UPDATE SET data = EXCLUDED.data";
//					tn += writerJdbcTemplateWithTransaction.update(sql, entityId, attributeId, value);

					
					SPARQLGeneratorUpdate jrs = new SPARQLGeneratorUpdate(DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_ID, entityId);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_ATTRIBUTE_ID, attributeId);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_DATA, value);
					sparql+=jrs.generateCreate(entityId,true);
					
					// update modifiedat field in temporalentity
//					sql = "UPDATE " + DBConstants.DBTABLE_TEMPORALENTITY
//							+ " SET modifiedat = ?::timestamp WHERE id = ?";
//					tn += writerJdbcTemplateWithTransaction.update(sql, entityModifiedAt, entityId);
					
						
					jrs = new SPARQLGeneratorUpdate(DBConstants.DBTABLE_TEMPORALENTITY);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_ID, entityId);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_MODIFIED_AT, entityModifiedAt);
					sparql+=jrs.generateCreate(entityId,true);
					
				}
			} else {

				if (entityId != null && attributeId != null && instanceId != null) {
//					sql = "DELETE FROM " + DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE
//							+ " WHERE temporalentity_id = ? AND attributeid = ? AND instanceid = ?";
//					n = writerJdbcTemplate.update(sql, entityId, attributeId, instanceId);
					
					
					SPARQLGeneratorUpdate jrs = new SPARQLGeneratorUpdate(DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_ID, entityId);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_TYPE, entityType);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_CREATED_AT, entityCreatedAt);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_MODIFIED_AT, entityModifiedAt);
					sparql+=jrs.generateCreate(entityId,true);
					
				} else if (entityId != null && attributeId != null) {
//					sql = "DELETE FROM " + DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE
//							+ " WHERE temporalentity_id = ? AND attributeid = ?";
//					n = writerJdbcTemplate.update(sql, entityId, attributeId);

		
					
					SPARQLGeneratorUpdate jrs = new SPARQLGeneratorUpdate(DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_ID, entityId);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_ATTRIBUTE_ID, attributeId);
					sparql+=jrs.generateDeleteAllWhere(entityId);
				} else if (entityId != null) {
//					sql = "DELETE FROM " + DBConstants.DBTABLE_TEMPORALENTITY + " WHERE id = ?";
//					n = writerJdbcTemplate.update(sql, entityId);
											
					SPARQLGeneratorUpdate jrs = new SPARQLGeneratorUpdate(DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_ID, entityId);
					sparql+=jrs.generateDeleteAllWhere(entityId);
				}
			}
			
			logger.info("\nSTORETemporalEntity--> sparql:\n" + sparql);
			Response res=sepa.executeUpdate(sparql);
			boolean success= !res.isError();
			logger.info("\nSTORETemporalEntity--> success:" + success);
			if(!success) {
				System.err.print(((ErrorResponse)res).getError());
			}
			return success;
		} catch (Exception e) {
			logger.error("Exception ::", e);
			e.printStackTrace();
		}
		return false;
	}
	
	
	@Override
	public boolean storeEntity(String key, String value, String valueWithoutSysAttrs, String kvValue)
			throws SQLTransientConnectionException {
	
		//int n = 0;//not used yet
		

//		SPARQLGeneratorUpdate gen = new SPARQLGeneratorUpdate(DBConstants.DBTABLE_ENTITY,key,true);
		SPARQLGeneratorUpdate jrs = new SPARQLGeneratorUpdate(DBConstants.DBTABLE_ENTITY);
		jrs.addTriple(key, DBConstants.DBCOLUMN_ID, key);
		
		String sparql = "";
		if (value != null && !value.equals("null")) {
//			sql = "INSERT INTO " + DBConstants.DBTABLE_ENTITY + " (id, " + DBConstants.DBCOLUMN_DATA + ", "
//					+ DBConstants.DBCOLUMN_DATA_WITHOUT_SYSATTRS + ",  " + DBConstants.DBCOLUMN_KVDATA
//					+ ") VALUES (?, ?::jsonb, ?::jsonb, ?::jsonb) ON CONFLICT(id) DO UPDATE SET ("
//					+ DBConstants.DBCOLUMN_DATA + ", " + DBConstants.DBCOLUMN_DATA_WITHOUT_SYSATTRS + ",  "
//					+ DBConstants.DBCOLUMN_KVDATA + ") = (EXCLUDED." + DBConstants.DBCOLUMN_DATA + ", EXCLUDED."
//					+ DBConstants.DBCOLUMN_DATA_WITHOUT_SYSATTRS + ",  EXCLUDED." + DBConstants.DBCOLUMN_KVDATA + ")";
//			n = writerJdbcTemplate.update(sql, key, value, valueWithoutSysAttrs, kvValue);
			
		
			
			jrs.addTriple(key, DBConstants.DBCOLUMN_DATA, value);
			jrs.addTriple(key, DBConstants.DBCOLUMN_DATA_WITHOUT_SYSATTRS, valueWithoutSysAttrs);
			jrs.addTriple(key, DBConstants.DBCOLUMN_KVDATA, kvValue);
			
			JsonObject jsonObject = new JsonParser().parse(value).getAsJsonObject();
			JsonElement type = jsonObject.get("@type");
			if(type!=null && !type.isJsonNull()) {
				if(type.isJsonArray()) {
					JsonArray typeArr = jsonObject.get("@type").getAsJsonArray();
					for (JsonElement type_element : typeArr) {
						jrs.addTriple(key, DBConstants.DBCOLUMN_TYPE, type_element.getAsString());
					}
				}else {
					jrs.addTriple(key, DBConstants.DBCOLUMN_TYPE, type.getAsString());
				}
			}
			try {
				sparql=jrs.generateCreate(key,true);
			} catch (JsonLdError e) {
				logger.error("Exception ::", e);
				return false;
			}
		}else {
			sparql=jrs.generateDeleteAllWhere(key);
		}
		
		Response res=sepa.executeUpdate(sparql);
		boolean success= !res.isError();
		logger.info("\nSTOREEntity--> sparql:\n" + sparql);
		if(!success) {
			System.err.print(((ErrorResponse)res).getError());
		}
		logger.info("\nSTOREEntity--> success:" + success);
		
		return success;
		
		
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


	}
	





}
