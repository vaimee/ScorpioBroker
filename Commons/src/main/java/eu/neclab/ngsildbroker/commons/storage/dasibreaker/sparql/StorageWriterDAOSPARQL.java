package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql;

import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.apicatalog.jsonld.JsonLdError;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.neclab.ngsildbroker.commons.constants.DBConstants;
import eu.neclab.ngsildbroker.commons.datatypes.TemporalEntityStorageKey;
import eu.neclab.ngsildbroker.commons.serialization.DataSerializer;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLConverter;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IStorageWriterDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLClause;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLClauseRawData;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLConstant;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLGeneratorUpdate;
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
//		logger.info("\n---------------------------------------\nstore: \n tableName: " + tableName + "\n");
//		logger.info("\n---------------------------------------\nstore: \n columnName: " + columnName + "\n");
//		logger.info("\n---------------------------------------\nstore: \n key: " + key + "\n");
//		logger.info("\n---------------------------------------\nstore: \n value: " + value + "\n");
//		String path = tableName+"/"+columnName;
//		try {
//			boolean success =false;
//			if (value != null && !value.equals("null")) {
//				success =sepa.generalStoreEntity(key,value,path);
//				logger.info("\n===> NGSI-LD to SPARQL on sepa (generalStorEntity) success: " + success + "\n");
//			}else {
//				success =sepa.generalDeleteEntityRecursively(key,path);
//				logger.info("\n===> NGSI-LD to SPARQL on sepa (generalDeleteEntityRecursively) success: " + success + "\n");
//			}
//			return true; // success;
//		} catch (Exception e) {
//			logger.error("Exception ::", e);
//			e.printStackTrace();
//		}

//		boolean success =false;
//		SPARQLGeneratorUpdate gen = new SPARQLGeneratorUpdate(tableName,key,true);
//		
//		String sparql = "";
//	
//		if (value != null && !value.equals("null")) {
//			if(Arrays.asList(SPARQLConstant.JSON_COLUMNS).contains(columnName)) {
//				try {
//					gen.insertJsonColumn(value,columnName);
//				} catch (JsonLdError e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}else {
//				gen.insertRawDataColumn(value,columnName);
//			}
//			sparql=gen.generateCreateEntity();
//		}else{
//			sparql=gen.generateDeleteWhere(true);
//		}
		
		boolean success =false;
		SPARQLConverter jrs = new SPARQLConverter(tableName);
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
		logger.info("\n store--> sparql:\n" + sparql);
		success= !sepa.executeUpdate(sparql).isError();
		logger.info("\n store--> success:\n" + success);
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
			Integer n = 0;
			
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
					
					//------------DEPRECATE
//					SPARQLGeneratorUpdate gen = new SPARQLGeneratorUpdate(DBConstants.DBTABLE_TEMPORALENTITY,entityId,true);
//					gen.insertRawDataColumn(entityType,DBConstants.DBCOLUMN_TYPE);
//					gen.insertRawDataColumn(entityCreatedAt,DBConstants.DBCOLUMN_CREATED_AT);
//					gen.insertRawDataColumn(entityModifiedAt,DBConstants.DBCOLUMN_MODIFIED_AT);
//					sparql+=gen.generateCreateEntity()+";\n";
					//------------DEPRECATE
					SPARQLConverter jrs = new SPARQLConverter(DBConstants.DBTABLE_TEMPORALENTITY);
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

						//------------DEPRECATE
//						SPARQLGeneratorUpdate gen = new SPARQLGeneratorUpdate(DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE,entityId,true);
//						gen.insertRawDataColumn(attributeId,DBConstants.DBCOLUMN_ATTRIBUTE_ID);
//						sparql+=gen.generateDeleteWhere(true)+";\n";
						//------------DEPRECATE
						SPARQLConverter jrs = new SPARQLConverter(DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE);
						jrs.addTriple(entityId, DBConstants.DBCOLUMN_ID, entityId);
						jrs.addTriple(entityId, DBConstants.DBCOLUMN_ATTRIBUTE_ID, attributeId);
						sparql+=jrs.generateDeleteAllWhere(entityId);
					}
					
//					sql = "INSERT INTO " + DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE
//							+ " (temporalentity_id, attributeid, data) VALUES (?, ?, ?::jsonb) ON CONFLICT(temporalentity_id, attributeid, instanceid) DO UPDATE SET data = EXCLUDED.data";
//					tn += writerJdbcTemplateWithTransaction.update(sql, entityId, attributeId, value);
					
					//------------DEPRECATE
//					SPARQLGeneratorUpdate gen = new SPARQLGeneratorUpdate(DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE,entityId,true);
//					gen.insertRawDataColumn(attributeId,DBConstants.DBCOLUMN_ATTRIBUTE_ID);
//					gen.insertRawDataColumn(value,DBConstants.DBCOLUMN_DATA);
//					sparql+=gen.generateCreateEntity()+";\n";
					//------------DEPRECATE
					
					SPARQLConverter jrs = new SPARQLConverter(DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_ID, entityId);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_ATTRIBUTE_ID, attributeId);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_DATA, value);
					sparql+=jrs.generateCreate(entityId,true);
					
					// update modifiedat field in temporalentity
//					sql = "UPDATE " + DBConstants.DBTABLE_TEMPORALENTITY
//							+ " SET modifiedat = ?::timestamp WHERE id = ?";
//					tn += writerJdbcTemplateWithTransaction.update(sql, entityModifiedAt, entityId);
					
					//------------DEPRECATE
//					gen = new SPARQLGeneratorUpdate(DBConstants.DBTABLE_TEMPORALENTITY,entityId,true);
//					gen.insertRawDataColumn(entityModifiedAt,DBConstants.DBCOLUMN_MODIFIED_AT);
//					sparql+=gen.generateCreateEntity()+";\n";
					//------------DEPRECATE
					
					jrs = new SPARQLConverter(DBConstants.DBTABLE_TEMPORALENTITY);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_ID, entityId);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_MODIFIED_AT, entityModifiedAt);
					sparql+=jrs.generateCreate(entityId,true);
					
				}
			} else {

				if (entityId != null && attributeId != null && instanceId != null) {
//					sql = "DELETE FROM " + DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE
//							+ " WHERE temporalentity_id = ? AND attributeid = ? AND instanceid = ?";
//					n = writerJdbcTemplate.update(sql, entityId, attributeId, instanceId);
					
					//------------DEPRECATE
//					SPARQLGeneratorUpdate gen = new SPARQLGeneratorUpdate(DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE,entityId,true);
//					gen.insertRawDataColumn(entityType,DBConstants.DBCOLUMN_TYPE);
//					gen.insertRawDataColumn(entityCreatedAt,DBConstants.DBCOLUMN_CREATED_AT);
//					gen.insertRawDataColumn(entityModifiedAt,DBConstants.DBCOLUMN_MODIFIED_AT);
//					sparql+=gen.generateCreateEntity()+";\n";
					//------------DEPRECATE
					
					SPARQLConverter jrs = new SPARQLConverter(DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_ID, entityId);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_TYPE, entityType);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_CREATED_AT, entityCreatedAt);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_MODIFIED_AT, entityModifiedAt);
					sparql+=jrs.generateCreate(entityId,true);
					
				} else if (entityId != null && attributeId != null) {
//					sql = "DELETE FROM " + DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE
//							+ " WHERE temporalentity_id = ? AND attributeid = ?";
//					n = writerJdbcTemplate.update(sql, entityId, attributeId);

					//------------DEPRECATE
//					SPARQLGeneratorUpdate gen = new SPARQLGeneratorUpdate(DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE,entityId,true);
////					gen.insertRawDataColumn(attributeId,DBConstants.DBCOLUMN_ATTRIBUTE_ID);
//					ArrayList<SPARQLClause> clauses =new ArrayList<SPARQLClause>();
//					clauses.add(new SPARQLClauseRawData(DBConstants.DBCOLUMN_ATTRIBUTE_ID,attributeId));
//					sparql+=gen.generateDeleteAllWhere(clauses)+";\n";
//					//------------------------------------------------------___WIP
//					//	need use SPARQLClause concept and SPARQLGenerator.generateDeleteWhere(ArrayList<SPARQLClause>)
//					//------------------------------------------------------___WIP
//					
//					//------------------------------------------------------------WARNING
//					//------------------------------------------------------------WARNING
//					//------------------------------------------------------------WARNING
//					//need check if Scorpio always use 'entityId' as identifier in SQL query
//					//for example here, the SQL WHERE is on temporalentity_id = ? AND attributeid = ?" so we have
//					//'entityId', but if there is a SQL WHERE that didn't use 'entityId' we will be in trouble
//					//------------------------------------------------------------WARNING
//					//------------------------------------------------------------WARNING
//					//------------------------------------------------------------WARNING
					//------------DEPRECATE
					
					SPARQLConverter jrs = new SPARQLConverter(DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_ID, entityId);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_ATTRIBUTE_ID, attributeId);
					sparql+=jrs.generateDeleteAllWhere(entityId);
				} else if (entityId != null) {
//					sql = "DELETE FROM " + DBConstants.DBTABLE_TEMPORALENTITY + " WHERE id = ?";
//					n = writerJdbcTemplate.update(sql, entityId);
					
					//------------DEPRECATE
//					SPARQLGeneratorUpdate gen = new SPARQLGeneratorUpdate(DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE,entityId,true);
//					sparql+=gen.generateDeleteWhere(true)+";\n";
					//------------DEPRECATE
					
					SPARQLConverter jrs = new SPARQLConverter(DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE);
					jrs.addTriple(entityId, DBConstants.DBCOLUMN_ID, entityId);
					sparql+=jrs.generateDeleteAllWhere(entityId);
				}
			}
			
			logger.info("\nstoreTemporalEntity--> sparql:\n" + sparql);
			boolean success= !sepa.executeUpdate(sparql).isError();
//			logger.debug("Rows affected: " + Integer.toString(n));
			logger.info("\nstoreTemporalEntity--> success:\n" + success);
			
			return success;
		} catch (Exception e) {
			logger.error("Exception ::", e);
			e.printStackTrace();
		}
		return false;
	}
	
//	public boolean storeTemporalEntity(String key, String value) throws SQLException {
//
//		
////		logger.info("\n---------------------------------------\n storeTemporalEntity: \n key: " + key + "\n");
////		logger.info("\n---------------------------------------\n storeTemporalEntity: \n value: " + value + "\n");
//		try {
//
//			TemporalEntityStorageKey tesk = DataSerializer.getTemporalEntityStorageKey(key);
//
//			String entityId = tesk.getEntityId();
//			String entityType = tesk.getEntityType();
//			String entityCreatedAt = tesk.getEntityCreatedAt();
//			String entityModifiedAt = tesk.getEntityModifiedAt();
//
//			String attributeId = tesk.getAttributeId();
//			String instanceId = tesk.getInstanceId(); // WARING--------------------------_>IGNORED
//			Boolean overwriteOp = tesk.getOverwriteOp();
//			Integer n = 0;
//			
//
//			if (!value.equals("null")) {
//				//-------------------------------------TRANZACTION (T1 start)
//						String sparql="";
//						boolean needUpdateModData = true;
//						if (entityId != null && entityType != null && entityCreatedAt != null
//								&& entityModifiedAt != null) {
////							writerJdbcTemplateWithTransaction.update(sql, entityId, entityType, entityCreatedAt,entityModifiedAt);
//							sparql= "INSERT DATA { GRAPH "+sepa.getGraph(DBConstants.DBTABLE_TEMPORALENTITY)+"{\n"
//									+ "<"+entityId+"><"+SepaJSONLDGateway.ENTITY_TYPE+"><"+entityType+">.\n"
//									+ "<"+entityId+"><"+SepaJSONLDGateway.ENTITY_CREATED_AT+"><"+entityCreatedAt+">.\n"
//									+ "<"+entityId+"><"+SepaJSONLDGateway.ENTITY_MODIFIED_AT+"><"+entityModifiedAt+">.\n"
//									+ "}}\n";
//							needUpdateModData=false;
//						}
//
//						if (entityId != null && attributeId != null) {
//							String composedKey= entityId+"@"+attributeId;
//							if (overwriteOp != null && overwriteOp) {
////								sql = "DELETE FROM " + DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE
////										+ " WHERE temporalentity_id = ? AND attributeid = ?";
////								tn += writerJdbcTemplateWithTransaction.update(sql, entityId, attributeId);
//								sparql+=sepa.getSparqlDeleteEntityRecursively(composedKey, DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE)+"\n";
////								sparql+="DELETE WHERE { GRAPH "+sepa.getGraph(DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE)+"{"
////										+ "<"+entityId+"@"+attributeId+"> ?p ?o.\n"
////										+ "}}";
//							}
////							sql = "INSERT INTO " + DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE
////									+ " (temporalentity_id, attributeid, data) VALUES (?, ?, ?::jsonb) ON CONFLICT(temporalentity_id, attributeid, instanceid) DO UPDATE SET data = EXCLUDED.data";
////							tn += writerJdbcTemplateWithTransaction.update(sql, entityId, attributeId, value);
//							sparql+=sepa.getSparqlStoreTemporalEntity(key,composedKey,value, DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE)+"\n";
//						
//							// update modifiedat field in temporalentity  //<<<<<------------IS THIS DUPLICATE!?
////							sql = "UPDATE " + DBConstants.DBTABLE_TEMPORALENTITY
////									+ " SET modifiedat = ?::timestamp WHERE id = ?";
////							tn += writerJdbcTemplateWithTransaction.update(sql, entityModifiedAt, entityId);
//							if(needUpdateModData) {
//								sparql+= "INSERT DATA { GRAPH "+sepa.getGraph(DBConstants.DBTABLE_TEMPORALENTITY)+"{\n"
//										+ "<"+entityId+"><"+SepaJSONLDGateway.ENTITY_MODIFIED_AT+"><"+entityModifiedAt+">.\n"
//										+ "}}\n";
//								needUpdateModData=false;
//							}
//						}
//						if(sparql.length()>0) {
//							boolean success = !sepa.executeUpdate(sparql).isError();
//							return true; // success
//						}else {
//							return false;
//						}
//
//						//-------------------------------------TRANZACTION (T1 end)
//
//			} else {
//				boolean success = false;
//				//WARNING-------> instanceId ignored
////				if (entityId != null && attributeId != null && instanceId != null) {
//////					sql = "DELETE FROM " + DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE
//////							+ " WHERE temporalentity_id = ? AND attributeid = ? AND instanceid = ?";
//////					n = writerJdbcTemplate.update(sql, entityId, attributeId, instanceId);
////					String composedKey= entityId+"@"+attributeId;
////					boolean success = sepa.generalDeleteEntityRecursively(composedKey,DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE);
////					
////				} else 
//					if (entityId != null && attributeId != null) {
////					sql = "DELETE FROM " + DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE
////							+ " WHERE temporalentity_id = ? AND attributeid = ?";
////					n = writerJdbcTemplate.update(sql, entityId, attributeId);
//					String composedKey= entityId+"@"+attributeId;
//					success = sepa.generalDeleteEntityRecursively(composedKey,DBConstants.DBTABLE_TEMPORALENTITY_ATTRIBUTEINSTANCE);
//					
//				} else if (entityId != null) {
////					sql = "DELETE FROM " + DBConstants.DBTABLE_TEMPORALENTITY + " WHERE id = ?";
////					n = writerJdbcTemplate.update(sql, entityId);
//					success= sepa.generalDeleteEntityRecursively(entityId,DBConstants.DBTABLE_TEMPORALENTITY);
//				}
//					return true; // success
//			}
//
//		} catch (Exception e) {
//			logger.error("Exception ::", e);
//			e.printStackTrace();
//		}
//		return false;
//	}
	
	@Override
	public boolean storeEntity(String key, String value, String valueWithoutSysAttrs, String kvValue)
			throws SQLTransientConnectionException {
		
		boolean success =false;
		int n = 0;//not used yet
		

//		SPARQLGeneratorUpdate gen = new SPARQLGeneratorUpdate(DBConstants.DBTABLE_ENTITY,key,true);
		SPARQLConverter jrs = new SPARQLConverter(DBConstants.DBTABLE_ENTITY);
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
			
			//------------DEPRECATE
//			try {
//				gen.insertJsonColumn(value,  DBConstants.DBCOLUMN_DATA);
//				gen.insertJsonColumn(valueWithoutSysAttrs,  DBConstants.DBCOLUMN_DATA_WITHOUT_SYSATTRS);
//				gen.insertJsonColumn(kvValue,  DBConstants.DBCOLUMN_KVDATA);
//				sparql=gen.generateCreateEntity();
//			} catch (JsonLdError e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				return false;
//			}
			//------------DEPRECATE
			
			
			jrs.addTriple(key, DBConstants.DBCOLUMN_DATA, value);
			jrs.addTriple(key, DBConstants.DBCOLUMN_DATA_WITHOUT_SYSATTRS, valueWithoutSysAttrs);
			jrs.addTriple(key, DBConstants.DBCOLUMN_KVDATA, kvValue);
			
			//--------------WIP (this part need to be managed directly from "value" json-ld)
			JsonObject jsonObject = new JsonParser().parse(value).getAsJsonObject();
			String type = jsonObject.get("@type").isJsonNull()?"noTyep": jsonObject.get("@type").getAsString();
			jrs.addTriple(key, DBConstants.DBCOLUMN_TYPE, type);//this will be inferred from the json-ld
			//so from the respective triple when we will found a RDF-JSONLD converter (titanium is not good)
			try {
				sparql=jrs.generateCreate(key,true);
			} catch (JsonLdError e) {
				logger.error("Exception ::", e);
				return false;
			}
		}else {
			sparql=jrs.generateDeleteAllWhere(key);
		}
		
		
		success= !sepa.executeUpdate(sparql).isError();
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
