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
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.util.ReflectionUtils;
import eu.neclab.ngsildbroker.commons.constants.DBConstants;
import eu.neclab.ngsildbroker.commons.constants.NGSIConstants;
import eu.neclab.ngsildbroker.commons.datatypes.GeoqueryRel;
import eu.neclab.ngsildbroker.commons.datatypes.QueryParams;
import eu.neclab.ngsildbroker.commons.enums.ErrorType;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;
import eu.neclab.ngsildbroker.commons.storage.StorageReaderDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.ConverterJSONLDSPARQL;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IStorageReaderDao;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLClause;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLClauseRawData;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLGeneratorQuery;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SepaGateway;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.response.QueryResponse;

 public class StorageReaderDAOSPARQL implements IStorageReaderDao {

	private final static Logger logger = LogManager.getLogger(StorageReaderDAOSPARQL.class);
	
	private SepaGateway sepa;
	public void init() {
		try {
			sepa= new SepaGateway();
		} catch (SEPASecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	public List<String> query(QueryParams qp) {
		ArrayList<String> ris = new ArrayList<String>();
		
		//---------------------------------WIP
		//---------------------------------WIP
		//---------------------------------WIP
		//---------------------------------WIP
		//---------------------------------WIP
		try {
			if(qp.getCheck()!=null) {
				//---------------------------------WIP
//				String sqlQuery=typesAndAttributeQuery(qp);
//				ris= new ArrayList<String>(readerJdbcTemplate.queryForList(sqlQuery,String.class));
			}else {
				String sparql = translateNgsildQueryToSql(qp);
				logger.info("NGSI-LD to SPARQL: " + sparql);
				//SqlRowSet result = readerJdbcTemplate.queryForRowSet(sqlQuery);
				if(qp.getLimit() == 0 &&  qp.getCountResult() == true) {
					//---------------------------------WIP
//					List<String> list = readerJdbcTemplate.queryForList(sqlQuery,String.class);
//					StorageReaderDAO.countHeader = StorageReaderDAO.countHeader+list.size();	
//					return new ArrayList<String>();
				}else {
//					List<String> list = readerJdbcTemplate.queryForList(sqlQuery,String.class);
//					StorageReaderDAO.countHeader = StorageReaderDAO.countHeader+list.size();
//					ris=new ArrayList<String>(list);
					QueryResponse resp=(QueryResponse)sepa.executeQuery(sparql);
					List<String> list = new ArrayList<String>();
					list.add(ConverterJSONLDSPARQL.tempRDFtoJSONLD(resp.getBindingsResults()));
					return list;
				}
			}
			
		} catch(DataIntegrityViolationException e) {
			//Empty result don't worry
			logger.warn("SQL Result Exception::", e);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		return ris;	
		
		//Example of origin SQL output
		/*
		 * {"@id": "urn:ngsi-ld:Building:storeProva9999", "@type": ["https://uri.fiware.org/ns/data-models#Building"], "https://schema.org/address": [{"@type": ["https://uri.etsi.org/ngsi-ld/Property"], "https://uri.etsi.org/ngsi-ld/hasValue": [{"https://uri.etsi.org/ngsi-ld/default-context/postalCode": [{"@value": "10969"}], "https://uri.etsi.org/ngsi-ld/default-context/addressRegion": [{"@value": "Berlin"}], "https://uri.etsi.org/ngsi-ld/default-context/streetAddress": [{"@value": "Friedrichstraße 44"}], "https://uri.etsi.org/ngsi-ld/default-context/addressLocality": [{"@value": "Kreuzberg"}]}], "https://uri.etsi.org/ngsi-ld/default-context/verified": [{"@type": ["https://uri.etsi.org/ngsi-ld/Property"], "https://uri.etsi.org/ngsi-ld/hasValue": [{"@value": true}]}]}], "https://uri.etsi.org/ngsi-ld/name": [{"@type": ["https://uri.etsi.org/ngsi-ld/Property"], "https://uri.etsi.org/ngsi-ld/hasValue": [{"@value": "Checkpoint Markt"}]}], "https://uri.etsi.org/ngsi-ld/location": [{"@type": ["https://uri.etsi.org/ngsi-ld/GeoProperty"], "https://uri.etsi.org/ngsi-ld/hasValue": [{"@value": "{\"type\":\"Point\",\"coordinates\":[13.3903,52.5075]}"}]}], "https://uri.fiware.org/ns/data-models#category": [{"@type": ["https://uri.etsi.org/ngsi-ld/Property"], "https://uri.etsi.org/ngsi-ld/hasValue": [{"@value": "commercial"}]}]}
		 */
		
		/*
		 * [{"@id": "urn:ngsi-ld:Building:storeProva9999", "@type": ["https://uri.fiware.org/ns/data-models#Building"], "https://schema.org/address": [{"@type": ["https://uri.etsi.org/ngsi-ld/Property"], "https://uri.etsi.org/ngsi-ld/hasValue": [{"https://uri.etsi.org/ngsi-ld/default-context/postalCode": [{"@value": "10969"}], "https://uri.etsi.org/ngsi-ld/default-context/addressRegion": [{"@value": "Berlin"}], "https://uri.etsi.org/ngsi-ld/default-context/streetAddress": [{"@value": "Friedrichstraße 44"}], "https://uri.etsi.org/ngsi-ld/default-context/addressLocality": [{"@value": "Kreuzberg"}]}], "https://uri.etsi.org/ngsi-ld/default-context/verified": [{"@type": ["https://uri.etsi.org/ngsi-ld/Property"], "https://uri.etsi.org/ngsi-ld/hasValue": [{"@value": true}]}]}], "https://uri.etsi.org/ngsi-ld/name": [{"@type": ["https://uri.etsi.org/ngsi-ld/Property"], "https://uri.etsi.org/ngsi-ld/hasValue": [{"@value": "Checkpoint Markt"}]}], "https://uri.etsi.org/ngsi-ld/location": [{"@type": ["https://uri.etsi.org/ngsi-ld/GeoProperty"], "https://uri.etsi.org/ngsi-ld/hasValue": [{"@value": "{\"type\":\"Point\",\"coordinates\":[13.3903,52.5075]}"}]}], "https://uri.fiware.org/ns/data-models#category": [{"@type": ["https://uri.etsi.org/ngsi-ld/Property"], "https://uri.etsi.org/ngsi-ld/hasValue": [{"@value": "commercial"}]}]}]
		 */

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
		logger.info("\ncall on DAO ====> StorageReaderDAOSPARQL.typesAndAttributeQuery <====\n");
		String query="";
		if(qp.getCheck()=="NonDeatilsType" && qp.getAttrs()==null) {
//			int number = random.nextInt(999999);
//			query="select jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"','urn:ngsi-ld:EntityTypeList:"+number+"','"+NGSIConstants.JSON_LD_TYPE+"', jsonb_build_array('"+NGSIConstants.NGSI_LD_ENTITY_LIST+"'), '"+NGSIConstants.NGSI_LD_TYPE_LIST+"',json_agg(distinct jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"', type)::jsonb)) from entity;";

			//select jsonb_build_object('@id','urn:ngsi-ld:EntityTypeList:01234','@type', jsonb_build_array('https://uri.etsi.org/ngsi-ld/EntityTypeList'), 'https://uri.etsi.org/ngsi-ld/typeList',json_agg(distinct jsonb_build_object('@id', type)::jsonb)) from entity;
			//EXAMPLE RIS:
			/*
			{
				    "@id": "urn:ngsi-ld:EntityTypeList:01234",
				    "@type": [
				        "https://uri.etsi.org/ngsi-ld/EntityTypeList"
				    ],
				    "https://uri.etsi.org/ngsi-ld/typeList": [
				        {
				            "@id": "https://uri.fiware.org/ns/data-models#Building"
				        }
				    ]
			}
			 */
			
			return query;
		}
//		else if(qp.getCheck()=="deatilsType" && qp.getAttrs()==null) {
//			query="select distinct jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"',type,'"+NGSIConstants.JSON_LD_TYPE+"', jsonb_build_array('"+NGSIConstants.NGSI_LD_ENTITY_TYPE+"'), '"+NGSIConstants.NGSI_LD_TYPE_NAME+"', jsonb_build_array(jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"', type)), '"+NGSIConstants.NGSI_LD_ATTRIBUTE_NAMES+"', jsonb_agg(jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"', attribute.key))) from entity, jsonb_each(data_without_sysattrs - '"+NGSIConstants.JSON_LD_ID+"' - '"+NGSIConstants.JSON_LD_TYPE+"') attribute group by id;";
//		    return query;
//		}
//		else if(qp.getCheck()=="type" && qp.getAttrs()!=null) {
//			String type=qp.getAttrs();
//			query="with r as (select distinct attribute.key as mykey, jsonb_agg(distinct jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"', attribute.value#>>'{0,@type,0}')) as mytype from entity, jsonb_each(data_without_sysattrs - '"+NGSIConstants.JSON_LD_ID+"' - '"+NGSIConstants.JSON_LD_TYPE+"') attribute where type='"+type+"'"+" group by attribute.key)select jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"',type,'"+NGSIConstants.JSON_LD_TYPE+"', jsonb_build_array('"+NGSIConstants.NGSI_LD_ENTITY_TYPE_INFO+"'), '"+NGSIConstants.NGSI_LD_TYPE_NAME+"', jsonb_build_array(jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"', type)),'"+NGSIConstants.NGSI_LD_ENTITY_COUNT+"', jsonb_build_array(jsonb_build_object('"+NGSIConstants.JSON_LD_VALUE+"', count(Distinct id))), '"+NGSIConstants.NGSI_LD_ATTRIBUTE_DETAILS+"', jsonb_agg(distinct jsonb_build_object('"+NGSIConstants.NGSI_LD_ATTRIBUTE_NAME+"',jsonb_build_array(jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"', mykey)), '"+NGSIConstants.NGSI_LD_ATTRIBUTE_TYPES+"', mytype, '"+NGSIConstants.JSON_LD_ID+"', mykey, '"+NGSIConstants.JSON_LD_TYPE+"',jsonb_build_array('"+NGSIConstants.NGSI_LD_ATTRIBUTE+"')))) from entity, r attribute where type='"+type+"' group by type;";
//			return query;
//		}
//		else if(qp.getCheck()=="NonDeatilsAttributes" && qp.getAttrs()==null) {
//			int number = random.nextInt(999999);
//			query="select jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"','urn:ngsi-ld:AttributeList:"+number+"','"+NGSIConstants.JSON_LD_TYPE+"', jsonb_build_array('"+NGSIConstants.NGSI_LD_ATTRIBUTE_LIST_1+"'), '"+NGSIConstants.NGSI_LD_ATTRIBUTE_LIST_2+"',json_agg(distinct jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"', attribute.key)::jsonb)) from entity,jsonb_each(data_without_sysattrs-'"+NGSIConstants.JSON_LD_ID+"'-'"+NGSIConstants.JSON_LD_TYPE+"') attribute;";
//			return query;
//			
//		}
//		else if(qp.getCheck()=="deatilsAttributes" && qp.getAttrs()==null) {
//			query="select jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"', attribute.key,'"+NGSIConstants.JSON_LD_TYPE+"','"+NGSIConstants.NGSI_LD_ATTRIBUTE+"','"+NGSIConstants.NGSI_LD_ATTRIBUTE_NAME+"',jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"', attribute.key),'"+NGSIConstants.NGSI_LD_TYPE_NAMES+"',jsonb_agg(distinct jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"', type))) from entity,jsonb_each(data_without_sysattrs-'"+NGSIConstants.JSON_LD_ID+"'-'"+NGSIConstants.JSON_LD_TYPE+"') attribute group by attribute.key;";
//			return query;
//			
//		}
//		else if(qp.getCheck()=="Attribute" && qp.getAttrs()!=null) {
//			String type=qp.getAttrs();
//			query="with r as(select count(data_without_sysattrs->'"+type+"') as mycount  from entity), y as(select  jsonb_agg(distinct jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"',type)) as mytype,jsonb_build_array(jsonb_build_object('"+NGSIConstants.JSON_LD_VALUE+"',mycount)) as finalcount, jsonb_agg(distinct jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"',attribute.value#>>'{0,@type,0}')) as mydata from r,entity,jsonb_each(data_without_sysattrs-'"+NGSIConstants.JSON_LD_ID+"'-'"+NGSIConstants.JSON_LD_TYPE+"') attribute where attribute.key='"+type+"' group by mycount) select jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"','"+type+"','"+NGSIConstants.JSON_LD_TYPE+"','"+NGSIConstants.NGSI_LD_ATTRIBUTE+"','"+NGSIConstants.NGSI_LD_ATTRIBUTE_NAME+"',jsonb_build_object('"+NGSIConstants.JSON_LD_ID+"','"+type+"'),'"+NGSIConstants.NGSI_LD_TYPE_NAMES+"',mytype,'"+NGSIConstants.NGSI_LD_ATTRIBUTE_COUNT+"',finalcount,'"+NGSIConstants.NGSI_LD_ATTRIBUTE_TYPES+"',mydata) from y,r;";
//			return query;
//			
//		}
		return null;
	}


	/*
	 * TODO: optimize sql queries by using prepared statements (if possible)
	 * translateNgsildQueryToSql
	 */
	public String translateNgsildQueryToSql(QueryParams qp) throws ResponseException {
		SPARQLGeneratorQuery gen = new SPARQLGeneratorQuery(DBConstants.DBTABLE_ENTITY);
		boolean getById = false;
		boolean getByType = qp.getType()!=null && qp.getType()!="";
		
		if(getByType) {
			return gen.generateSparqlGetByType(qp.getType(),DBConstants.DBCOLUMN_DATA);
		}else if(getById) {
			throw new ResponseException("NOT IMPLEMENTED YET");
		}else {
			throw new ResponseException("NOT IMPLEMENTED YET");
		}
		
		//---------------------------------------
//		ArrayList<SPARQLClause> clauses = new ArrayList<SPARQLClause> ();
//		logger.info("\ncall on DAO ====> StorageReaderDAOSQL.translateNgsildQueryToSql <====\n");
//		StringBuilder fullSqlWhereProperty = new StringBuilder(70);
//		
//		ReflectionUtils.doWithFields(qp.getClass(), field -> {
//			String dbColumn, sqlOperator;
//			String sqlWhereProperty = "";
//
//			field.setAccessible(true);
//			String queryParameter = field.getName();
//			Object fieldValue = field.get(qp);
//			if (fieldValue != null) {
//
//				logger.trace("Query parameter:" + queryParameter);
//				logger.info("Query parameter:" + queryParameter+"; fieldValue: "+fieldValue);
//
//				String queryValue = "";
//				if (fieldValue instanceof String) {
//					queryValue = fieldValue.toString();
//					logger.trace("Query value: " + queryValue);
//				}
//
//				switch (queryParameter) {
//				case NGSIConstants.QUERY_PARAMETER_IDPATTERN:
//					dbColumn = DBConstants.DBCOLUMN_ID;
//					sqlOperator = "~";
//					sqlWhereProperty = dbColumn + " " + sqlOperator + " '" + queryValue + "'";
//					break;
//				case NGSIConstants.QUERY_PARAMETER_TYPE:
//				case NGSIConstants.QUERY_PARAMETER_ID:
//
//					//-----------------------------------------------------------DONE
////					dbColumn = queryParameter;
//					if (queryValue.indexOf(",") == -1) {
////						sqlOperator = "=";
////						sqlWhereProperty = dbColumn + " " + sqlOperator + " '" + queryValue + "'";
//						clauses.add(new SPARQLClauseRawData(queryParameter, queryValue));
//					} else {
////						sqlOperator = "IN";
////						sqlWhereProperty = dbColumn + " " + sqlOperator + " ('" + queryValue.replace(",", "','") + "')";
//						for (String value : queryValue.split(",")) {
//							SPARQLClauseRawData c = new SPARQLClauseRawData(queryParameter, value);
//							c.setAnd(false);
//							clauses.add(c);
//						}
//					}
//					break;
//				case NGSIConstants.QUERY_PARAMETER_ATTRS:
//					dbColumn = "data";
//					sqlOperator = "?";
//					if (queryValue.indexOf(",") == -1) {
//						sqlWhereProperty = dbColumn + " " + sqlOperator + "'" + queryValue + "'";
//					} else {
//						sqlWhereProperty = "("+dbColumn + " " + sqlOperator + " '"
//								+ queryValue.replace(",", "' OR " + dbColumn + " " + sqlOperator + "'") + "')";
//					}
//					break;
//				case NGSIConstants.QUERY_PARAMETER_GEOREL:
//					if (fieldValue instanceof GeoqueryRel) {
//						GeoqueryRel gqr = (GeoqueryRel) fieldValue;
//						logger.trace("Georel value " + gqr.getGeorelOp());
//						try {
//							sqlWhereProperty = translateNgsildGeoqueryToPostgisQuery(gqr, qp.getGeometry(),
//									qp.getCoordinates(), qp.getGeoproperty());
//						} catch (ResponseException e) {
//							e.printStackTrace();
//						}
//					}
//					break;
//				case NGSIConstants.QUERY_PARAMETER_QUERY:
//					sqlWhereProperty = queryValue;
//					break;
//				}
//				fullSqlWhereProperty.append(sqlWhereProperty);
//				if (!sqlWhereProperty.isEmpty())
//					fullSqlWhereProperty.append(" AND ");
//			}
//		});
//
//		String tableDataColumn;
//		if (qp.getKeyValues()) {
//			if (qp.getIncludeSysAttrs()) {
//				tableDataColumn = DBConstants.DBCOLUMN_KVDATA;
//			} else { // without sysattrs at root level (entity createdat/modifiedat)
//				tableDataColumn = DBConstants.DBCOLUMN_KVDATA + " - '" + NGSIConstants.NGSI_LD_CREATED_AT + "' - '"
//						+ NGSIConstants.NGSI_LD_MODIFIED_AT + "'";
//			}
//		} else {
//			if (qp.getIncludeSysAttrs()) {
//				tableDataColumn = DBConstants.DBCOLUMN_DATA;
//			} else {
//				tableDataColumn = DBConstants.DBCOLUMN_DATA_WITHOUT_SYSATTRS; // default request
//			}
//		}
//
//		String dataColumn = tableDataColumn;
//		if (qp.getAttrs() != null) {
//			String expandedAttributeList = "'" + NGSIConstants.JSON_LD_ID + "','" + NGSIConstants.JSON_LD_TYPE + "','"
//					+ qp.getAttrs().replace(",", "','") + "'";
//			if (qp.getIncludeSysAttrs()) {
//				expandedAttributeList += "," + NGSIConstants.NGSI_LD_CREATED_AT + ","
//						+ NGSIConstants.NGSI_LD_MODIFIED_AT;
//			}
//			dataColumn = "(SELECT jsonb_object_agg(key, value) FROM jsonb_each(" + tableDataColumn + ") WHERE key IN ( "
//					+ expandedAttributeList + "))";
//		}
//		String sqlQuery = "SELECT " + dataColumn + " as data FROM " + DBConstants.DBTABLE_ENTITY + " ";
//		if (fullSqlWhereProperty.length() > 0) {
//			sqlQuery += "WHERE " + fullSqlWhereProperty.toString() + " 1=1 ";
//		}
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
//		// order by ?
//		String sparql = gen.generateSelect(clauses); 
//		return sparql;
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
