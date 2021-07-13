package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import eu.neclab.ngsildbroker.commons.constants.DBConstants;

public class SPARQLConstant {
	
	public static final String FOR_INTERNAL_USE = "internaluse";
	public static final String EXISTS_ID = "existsid";
	public static final String ENTITY_TYPE = "entityType";
	public static final String ENTITY_CREATED_AT = "entityCreatedAt";
	public static final String ENTITY_MODIFIED_AT = "entityModifiedAt";
	public static final String HAS_TEMPORAL_INSTANCE = "hasTemporalInstance";
	public static final String HAS_RAW_DATA = "hasRawData";
	
	public static final String SPARQL_COLUMN = "ngsitosparql";
	
//	public static final String SPARQL_CHECK_CONFLICT = "http://"+SPARQL_COLUMN+"/checkconflict";
	
	public static final String[] JSON_COLUMNS = new String[]{
			DBConstants.DBCOLUMN_DATA,
			DBConstants.DBCOLUMN_KVDATA,
			DBConstants.DBCOLUMN_DATA_WITHOUT_SYSATTRS};
			//	DBCOLUMN_ID = "id";
			//	DBCOLUMN_TYPE = "type";
			//	DBCOLUMN_CREATED_AT = "createdat";
			//	DBCOLUMN_MODIFIED_AT = "modifiedat";
			//	DBCOLUMN_OBSERVED_AT = "observedat";
			//	DBCOLUMN_LOCATION = "location";
			//	DBCOLUMN_OBSERVATION_SPACE = "observationspace";
			//	DBCOLUMN_OPERATION_SPACE = "operationspace";

}
