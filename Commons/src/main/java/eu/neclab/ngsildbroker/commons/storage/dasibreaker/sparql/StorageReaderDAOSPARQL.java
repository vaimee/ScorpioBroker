package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ReflectionUtils;
import eu.neclab.ngsildbroker.commons.constants.DBConstants;
import eu.neclab.ngsildbroker.commons.constants.NGSIConstants;
import eu.neclab.ngsildbroker.commons.datatypes.GeoqueryRel;
import eu.neclab.ngsildbroker.commons.datatypes.QueryParams;
import eu.neclab.ngsildbroker.commons.enums.ErrorType;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;
import eu.neclab.ngsildbroker.commons.storage.StorageReaderDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IStorageReaderDao;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SepaJSONLDGateway;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;

 public class StorageReaderDAOSPARQL implements IStorageReaderDao {

	private final static Logger logger = LogManager.getLogger(StorageReaderDAOSPARQL.class);
	
	private SepaJSONLDGateway sepa;
	public void init() {
		try {
			sepa= new SepaJSONLDGateway();
		} catch (SEPASecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	public List<String> query(QueryParams qp) {
		try {
			if(qp.getCheck()!=null) {
				logger.info("query{qp.getCheck()!=null} NOT IMPLEMENTED");
				return null;//----------------------------------------------------NOT IMPLEMENTED
//				String sqlQuery=typesAndAttributeQuery(qp);
//				return readerJdbcTemplate.queryForList(sqlQuery,String.class);
			}
			String sparqlQuery = translateNgsildQueryToSql(qp);
			logger.info("NGSI-LD to SPARQL: " + sparqlQuery);
			logger.info("Query JSON-LD result: \n" + sepa.getJsonLdResOfQuery(sparqlQuery));
			//SqlRowSet result = readerJdbcTemplate.queryForRowSet(sqlQuery);
			//------------------------------------------------------------------------------------------INGORED
//			if(qp.getLimit() == 0 &&  qp.getCountResult() == true) {
//				List<String> list = readerJdbcTemplate.queryForList(sqlQuery,String.class);
//				StorageReaderDAO.countHeader = StorageReaderDAO.countHeader+list.size();	
//				return new ArrayList<String>();
//			} 
//			List<String> list = readerJdbcTemplate.queryForList(sqlQuery,String.class);
//			StorageReaderDAO.countHeader = StorageReaderDAO.countHeader+list.size();
			
			return new ArrayList<String>();
		} catch(DataIntegrityViolationException e) {
			//Empty result don't worry
			logger.warn("SQL Result Exception::", e);
			return new ArrayList<String>();
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		return new ArrayList<String>();

	}

	public String getListAsJsonArray(List<String> s) {
		return "[" + String.join(",", s) + "]";
	}

	public List<String> getLocalTypes() {
		return null;
	}
	
	public List<String> getAllTypes() {
		return null;
	}
	
	/*
	 * TODO: optimize sql queries for types and Attributes by using prepared statements (if possible)
	 */
	public String typesAndAttributeQuery(QueryParams qp) throws ResponseException {
		return null;
	}


	/*
	 * TODO: optimize sql queries by using prepared statements (if possible)
	 */
	public String translateNgsildQueryToSql(QueryParams qp) throws ResponseException {
		logger.info("\ncall on DAO ====> StorageReaderDAOSQL.translateNgsildQueryToSql <====\n");
//		StringBuilder fullSqlWhereProperty = new StringBuilder(70);
		
		//--------------------SPARQL
		StringBuilder fullInnerGraphClause = new StringBuilder(200);
		
		// https://stackoverflow.com/questions/3333974/how-to-loop-over-a-class-attributes-in-java
		ReflectionUtils.doWithFields(qp.getClass(), field -> {
			String dbColumn, sqlOperator;
			String sqlWhereProperty = "";
			field.setAccessible(true);
			String queryParameter = field.getName();
			Object fieldValue = field.get(qp);
			
			//--------------------SPARQL
			String innerGraphClause = "";
			
			if (fieldValue != null) {

				logger.trace("Query parameter:" + queryParameter);

				String queryValue = "";
				if (fieldValue instanceof String) {
					queryValue = fieldValue.toString();
					logger.trace("Query value: " + queryValue);
				}

				switch (queryParameter) {
				case NGSIConstants.QUERY_PARAMETER_IDPATTERN: 
					throw new IllegalArgumentException("QUERY_PARAMETER_IDPATTERN Not implemented yet!");
//					dbColumn = DBConstants.DBCOLUMN_ID;
//					sqlOperator = "~";
//					sqlWhereProperty = dbColumn + " " + sqlOperator + " '" + queryValue + "'";
//					break;
				case NGSIConstants.QUERY_PARAMETER_TYPE://-------------------------------------------------------------DONE
					if (queryValue.indexOf(",") == -1) {
						innerGraphClause+="?temp rdf:type <" + queryValue+">.\n";
					}else {
						innerGraphClause+="VALUES (?o){\n";
						for (String qV : queryValue.split(",")) {
							innerGraphClause+="("+qV+")\n";
						}
						innerGraphClause+="}\n";
						innerGraphClause+="?temp rdf:type ?o.\n";
					}
					innerGraphClause+="?temp (<>|!<>)* ?s .\n";
					break;
				case NGSIConstants.QUERY_PARAMETER_ID://---------------------------------------------------------------DONE
//					dbColumn = queryParameter;
//					if (queryValue.indexOf(",") == -1) {
//						sqlOperator = "=";
//						sqlWhereProperty = dbColumn + " " + sqlOperator + " '" + queryValue + "'";
//					} else {
//						sqlOperator = "IN";
//						sqlWhereProperty = dbColumn + " " + sqlOperator + " ('" + queryValue.replace(",", "','") + "')";
//					}
					if (queryValue.indexOf(",") == -1) {
						innerGraphClause+="<"+SepaJSONLDGateway.FOR_INTERNAL_USE+"><"+SepaJSONLDGateway.EXISTS_ID+"><" + queryValue+">.\n";
					}else {
						innerGraphClause+="VALUES (?temp){\n";
						for (String qV : queryValue.split(",")) {
							innerGraphClause+="("+qV+")\n";
						}
						innerGraphClause+="}\n";
						innerGraphClause+="<"+SepaJSONLDGateway.FOR_INTERNAL_USE+"><"+SepaJSONLDGateway.EXISTS_ID+"> ?temp.\n";
					}
					innerGraphClause+="?temp (<>|!<>)* ?s .\n";
					break;
				case NGSIConstants.QUERY_PARAMETER_ATTRS:
					throw new IllegalArgumentException("QUERY_PARAMETER_ATTRS Not implemented yet!");
//					dbColumn = "data";
//					sqlOperator = "?";
//					if (queryValue.indexOf(",") == -1) {
//						sqlWhereProperty = dbColumn + " " + sqlOperator + "'" + queryValue + "'";
//					} else {
//						sqlWhereProperty = "("+dbColumn + " " + sqlOperator + " '"
//								+ queryValue.replace(",", "' OR " + dbColumn + " " + sqlOperator + "'") + "')";
//					}
//					break;
				case NGSIConstants.QUERY_PARAMETER_GEOREL:
					if (fieldValue instanceof GeoqueryRel) {
						throw new IllegalArgumentException("QUERY_PARAMETER_GEOREL Not implemented yet!");
//						GeoqueryRel gqr = (GeoqueryRel) fieldValue;
//						logger.trace("Georel value " + gqr.getGeorelOp());
//						try {
//							sqlWhereProperty = translateNgsildGeoqueryToPostgisQuery(gqr, qp.getGeometry(),
//									qp.getCoordinates(), qp.getGeoproperty());
//						} catch (ResponseException e) {
//							e.printStackTrace();
//						}
					}
					break;
				case NGSIConstants.QUERY_PARAMETER_QUERY:
					throw new IllegalArgumentException("QUERY_PARAMETER_QUERY Not implemented yet!");
//					sqlWhereProperty = queryValue;
//					break;
				}
				if(!innerGraphClause.isEmpty()) {
					fullInnerGraphClause.append(innerGraphClause+"\n");
				}
				
//				fullSqlWhereProperty.append(sqlWhereProperty);
//				if (!sqlWhereProperty.isEmpty()) {
//					fullSqlWhereProperty.append(" AND ");
//				}
			}
		});

		String graph;//tableDataColumn;
		if (qp.getKeyValues()) {
			if (qp.getIncludeSysAttrs()) {
				graph = DBConstants.DBCOLUMN_KVDATA;
			} else { // without sysattrs at root level (entity createdat/modifiedat)
				graph = DBConstants.DBCOLUMN_KVDATA + " - '" + NGSIConstants.NGSI_LD_CREATED_AT + "' - '"
						+ NGSIConstants.NGSI_LD_MODIFIED_AT + "'";
			}
		} else {
			if (qp.getIncludeSysAttrs()) {
				graph = DBConstants.DBCOLUMN_DATA;
			} else {
				graph = DBConstants.DBCOLUMN_DATA_WITHOUT_SYSATTRS; // default request
			}
		}

//		String dataColumn = tableDataColumn;
		if (qp.getAttrs() != null) {
			throw new IllegalArgumentException("qp.getAttrs() Not implemented yet!");
//			String expandedAttributeList = "'" + NGSIConstants.JSON_LD_ID + "','" + NGSIConstants.JSON_LD_TYPE + "','"
//					+ qp.getAttrs().replace(",", "','") + "'";
//			if (qp.getIncludeSysAttrs()) {
//				expandedAttributeList += "," + NGSIConstants.NGSI_LD_CREATED_AT + ","
//						+ NGSIConstants.NGSI_LD_MODIFIED_AT;
//			}
//			dataColumn = "(SELECT jsonb_object_agg(key, value) FROM jsonb_each(" + tableDataColumn + ") WHERE key IN ( "
//					+ expandedAttributeList + "))";
		}
		//--------------
//		String sqlQuery = "SELECT " + dataColumn + " as data FROM " + DBConstants.DBTABLE_ENTITY + " ";
		graph=DBConstants.DBTABLE_ENTITY +"/"+graph;
		String sparqlQuery = "SELECT ?s ?p ?o WHERE {\n";
		//---------------
		if (fullInnerGraphClause.length() > 0) {
//			sqlQuery += "WHERE " + fullSqlWhereProperty.toString() + " 1=1 ";
			sparqlQuery+=fullInnerGraphClause;
		}else {
			logger.trace("WARNING: sparql query for get all RDF-DB");
//			sparqlQuery+="?s ?p ?o.\n";
		}
		sparqlQuery+="?s ?p ?o.\n";
		sparqlQuery+="}";
		
		
		///-----------------------------------------------------_NOT IMPLEMENTED
//		int limit = qp.getLimit();
//		int offSet = qp.getOffSet();
//				
//		if(limit == 0) {
//            sqlQuery += "";           
//        }
//        else {
//        sqlQuery += "LIMIT " + limit + " ";
//        }
//		if(offSet != -1) {
//			sqlQuery += "OFFSET " + offSet + " "; 
//		}
		// order by ?
		
		return sparqlQuery; //sqlQuery;
	}

	// TODO: SQL input sanitization
	// TODO: property of property
	// [SPEC] spec is not clear on how to define a "property of property" in
	// the geoproperty field. (probably using dots)
	public String translateNgsildGeoqueryToPostgisQuery(GeoqueryRel georel, String geometry, String coordinates,
			String geoproperty, String dbColumn) throws ResponseException {
		return null;
	}

	public String translateNgsildGeoqueryToPostgisQuery(GeoqueryRel georel, String geometry, String coordinates,
			String geoproperty) throws ResponseException {
		return this.translateNgsildGeoqueryToPostgisQuery(georel, geometry, coordinates, geoproperty, null);
	}

	

}
