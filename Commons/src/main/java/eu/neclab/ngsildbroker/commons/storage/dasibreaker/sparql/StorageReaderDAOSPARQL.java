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
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IStorageReaderDao;

 public class StorageReaderDAOSPARQL implements IStorageReaderDao {

	private final static Logger logger = LogManager.getLogger(StorageReaderDAOSPARQL.class);
	
	public void init() {
		
	}
	

	
	public List<String> query(QueryParams qp) {
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
		StringBuilder fullSqlWhereProperty = new StringBuilder(70);

		// https://stackoverflow.com/questions/3333974/how-to-loop-over-a-class-attributes-in-java
		ReflectionUtils.doWithFields(qp.getClass(), field -> {
			String dbColumn, sqlOperator;
			String sqlWhereProperty = "";

			field.setAccessible(true);
			String queryParameter = field.getName();
			Object fieldValue = field.get(qp);
			if (fieldValue != null) {

				logger.trace("Query parameter:" + queryParameter);

				String queryValue = "";
				if (fieldValue instanceof String) {
					queryValue = fieldValue.toString();
					logger.trace("Query value: " + queryValue);
				}

				switch (queryParameter) {
				case NGSIConstants.QUERY_PARAMETER_IDPATTERN:
					dbColumn = DBConstants.DBCOLUMN_ID;
					sqlOperator = "~";
					sqlWhereProperty = dbColumn + " " + sqlOperator + " '" + queryValue + "'";
					break;
				case NGSIConstants.QUERY_PARAMETER_TYPE:
				case NGSIConstants.QUERY_PARAMETER_ID:
					dbColumn = queryParameter;
					if (queryValue.indexOf(",") == -1) {
						sqlOperator = "=";
						sqlWhereProperty = dbColumn + " " + sqlOperator + " '" + queryValue + "'";
					} else {
						sqlOperator = "IN";
						sqlWhereProperty = dbColumn + " " + sqlOperator + " ('" + queryValue.replace(",", "','") + "')";
					}
					break;
				case NGSIConstants.QUERY_PARAMETER_ATTRS:
					dbColumn = "data";
					sqlOperator = "?";
					if (queryValue.indexOf(",") == -1) {
						sqlWhereProperty = dbColumn + " " + sqlOperator + "'" + queryValue + "'";
					} else {
						sqlWhereProperty = "("+dbColumn + " " + sqlOperator + " '"
								+ queryValue.replace(",", "' OR " + dbColumn + " " + sqlOperator + "'") + "')";
					}
					break;
				case NGSIConstants.QUERY_PARAMETER_GEOREL:
					if (fieldValue instanceof GeoqueryRel) {
						GeoqueryRel gqr = (GeoqueryRel) fieldValue;
						logger.trace("Georel value " + gqr.getGeorelOp());
						try {
							sqlWhereProperty = translateNgsildGeoqueryToPostgisQuery(gqr, qp.getGeometry(),
									qp.getCoordinates(), qp.getGeoproperty());
						} catch (ResponseException e) {
							e.printStackTrace();
						}
					}
					break;
				case NGSIConstants.QUERY_PARAMETER_QUERY:
					sqlWhereProperty = queryValue;
					break;
				}
				fullSqlWhereProperty.append(sqlWhereProperty);
				if (!sqlWhereProperty.isEmpty())
					fullSqlWhereProperty.append(" AND ");
			}
		});

		String tableDataColumn;
		if (qp.getKeyValues()) {
			if (qp.getIncludeSysAttrs()) {
				tableDataColumn = DBConstants.DBCOLUMN_KVDATA;
			} else { // without sysattrs at root level (entity createdat/modifiedat)
				tableDataColumn = DBConstants.DBCOLUMN_KVDATA + " - '" + NGSIConstants.NGSI_LD_CREATED_AT + "' - '"
						+ NGSIConstants.NGSI_LD_MODIFIED_AT + "'";
			}
		} else {
			if (qp.getIncludeSysAttrs()) {
				tableDataColumn = DBConstants.DBCOLUMN_DATA;
			} else {
				tableDataColumn = DBConstants.DBCOLUMN_DATA_WITHOUT_SYSATTRS; // default request
			}
		}

		String dataColumn = tableDataColumn;
		if (qp.getAttrs() != null) {
			String expandedAttributeList = "'" + NGSIConstants.JSON_LD_ID + "','" + NGSIConstants.JSON_LD_TYPE + "','"
					+ qp.getAttrs().replace(",", "','") + "'";
			if (qp.getIncludeSysAttrs()) {
				expandedAttributeList += "," + NGSIConstants.NGSI_LD_CREATED_AT + ","
						+ NGSIConstants.NGSI_LD_MODIFIED_AT;
			}
			dataColumn = "(SELECT jsonb_object_agg(key, value) FROM jsonb_each(" + tableDataColumn + ") WHERE key IN ( "
					+ expandedAttributeList + "))";
		}
		String sqlQuery = "SELECT " + dataColumn + " as data FROM " + DBConstants.DBTABLE_ENTITY + " ";
		if (fullSqlWhereProperty.length() > 0) {
			sqlQuery += "WHERE " + fullSqlWhereProperty.toString() + " 1=1 ";
		}
		int limit = qp.getLimit();
		int offSet = qp.getOffSet();
				
		if(limit == 0) {
            sqlQuery += "";           
        }
        else {
        sqlQuery += "LIMIT " + limit + " ";
        }
		if(offSet != -1) {
			sqlQuery += "OFFSET " + offSet + " "; 
		}
		// order by ?

		return sqlQuery;
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
