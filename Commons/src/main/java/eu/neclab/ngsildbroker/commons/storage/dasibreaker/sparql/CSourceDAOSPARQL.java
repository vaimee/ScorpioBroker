package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import eu.neclab.ngsildbroker.commons.constants.DBConstants;
import eu.neclab.ngsildbroker.commons.constants.NGSIConstants;
import eu.neclab.ngsildbroker.commons.datatypes.GeoqueryRel;
import eu.neclab.ngsildbroker.commons.datatypes.QueryParams;
import eu.neclab.ngsildbroker.commons.enums.ErrorType;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;
import eu.neclab.ngsildbroker.commons.storage.StorageReaderDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.ICSourceDAO;

//@Repository("rmcsourcedao")
public class CSourceDAOSPARQL extends StorageReaderDAO implements ICSourceDAO {

	private final static Logger logger = LogManager.getLogger(CSourceDAOSPARQL.class);

	protected final static String DBCOLUMN_CSOURCE_INFO_ENTITY_ID = "entity_id";
	protected final static String DBCOLUMN_CSOURCE_INFO_ENTITY_IDPATTERN = "entity_idpattern";
	protected final static String DBCOLUMN_CSOURCE_INFO_ENTITY_TYPE = "entity_type";
	protected final static String DBCOLUMN_CSOURCE_INFO_PROPERTY_ID = "property_id";
	protected final static String DBCOLUMN_CSOURCE_INFO_RELATIONSHIP_ID = "relationship_id";
	
	protected final static Map<String, String> NGSILD_TO_SQL_RESERVED_PROPERTIES_MAPPING_GEO = initNgsildToSqlReservedPropertiesMappingGeo();

	protected static Map<String, String> initNgsildToSqlReservedPropertiesMappingGeo() {
		Map<String, String> map = new HashMap<>();
		map.put(NGSIConstants.NGSI_LD_LOCATION, DBConstants.DBCOLUMN_LOCATION);
		return Collections.unmodifiableMap(map);
	}

	protected final static Map<String, String> NGSILD_TO_POSTGIS_GEO_OPERATORS_MAPPING = initNgsildToPostgisGeoOperatorsMapping();

	protected static Map<String, String> initNgsildToPostgisGeoOperatorsMapping() {
		Map<String, String> map = new HashMap<>();
		map.put(NGSIConstants.GEO_REL_NEAR, null);
		map.put(NGSIConstants.GEO_REL_WITHIN, DBConstants.POSTGIS_INTERSECTS);
		map.put(NGSIConstants.GEO_REL_CONTAINS, DBConstants.POSTGIS_CONTAINS);
		map.put(NGSIConstants.GEO_REL_OVERLAPS, null);
		map.put(NGSIConstants.GEO_REL_INTERSECTS, DBConstants.POSTGIS_INTERSECTS);
		map.put(NGSIConstants.GEO_REL_EQUALS, DBConstants.POSTGIS_CONTAINS);
		map.put(NGSIConstants.GEO_REL_DISJOINT, null);
		return Collections.unmodifiableMap(map);
	}

	private boolean externalCsourcesOnly = false; 
	
	@Override
	public List<String> query(QueryParams qp) {
		return null;
	}
	
	public List<String> queryExternalCsources(QueryParams qp) throws SQLException {
		return null;
	}

	@Override
	public String translateNgsildQueryToSql(QueryParams qp) throws ResponseException {
		return null;
	}

	private String getCommonSqlWhereForTypeIdIdPattern(String typeValue, String idValue, String idPatternValue) {
		return null;
	}
	
	private String getSqlWhereByType(String typeValue, boolean includeIdAndIdPatternNullTest) {
		return null;
	}
	
	private String getSqlWhereById(String typeValue, String idValue) {
		return null;
	}
	
	private String getSqlWhereByIdPattern(String typeValue, String idPatternValue) {
		return null;
	}	
	
	private String getSqlWhereByAttrsInTypeFiltering(String attrsValue) {
		return null;
	}
	
	// TODO: SQL input sanitization
	// TODO: property of property
	// TODO: [SPEC] spec is not clear on how to define a "property of property" in
	// the geoproperty field. (probably using dots, but...)
	@Override
	public String translateNgsildGeoqueryToPostgisQuery(GeoqueryRel georel, String geometry, String coordinates,
			String geoproperty) throws ResponseException {
		return null;
	}

}
