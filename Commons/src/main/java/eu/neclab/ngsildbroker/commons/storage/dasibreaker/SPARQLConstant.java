package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import eu.neclab.ngsildbroker.commons.constants.DBConstants;

public class SPARQLConstant {
	
	public static final String FOR_INTERNAL_USE = "internaluse";
//	public static final String EXISTS_ID = "existsid";
	public static final String ENTITY_TYPE = "entityType";
	public static final String ENTITY_CREATED_AT = "entityCreatedAt";
	public static final String ENTITY_MODIFIED_AT = "entityModifiedAt";
	public static final String HAS_TEMPORAL_INSTANCE = "hasTemporalInstance";
	public static final String HAS_RAW_DATA = "hasRawData";
	public static final String IS_JSON_LD = "isJsonLD";
	
	public static final String SPARQL_COLUMN = "ngsitosparql";

	public static final String HAS_DATA = "hasData";
	public static final String NGSI_GRAPH_PREFIX = "http://localhost:3000/ngsi/";//"http://ngsi/";
	public static final String BLANK_NODE =NGSI_GRAPH_PREFIX+"blanknode/";
	
	public static final String RDF_PREFIX ="PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";
	public static final String RDF_PREFIX_START ="http://www.w3.org/2000/01/rdf-schema#";
	public static final String RDF_PREFIX_SUB ="rdfs:";
	
	public static final String XSD_PREFIX_SUB ="xsd:";
	public static final String XSD_PREFIX_START ="http://www.w3.org/2001/XMLSchema#";
	public static final String XSD_PREFIX ="PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	

	public static final String NGSI_PREFIX_SUB ="ngsi:";
	public static final String NGSI_PREFIX_START ="https://uri.etsi.org/ngsi-ld/";
	public static final String NGSI_PREFIX ="PREFIX ngsi: <https://uri.etsi.org/ngsi-ld/>\n";
	
	
	public static final String[] XSD_ALLOWED ={"int","integer","float",
			"double","boolean","decimal","long"};

	
	public static final String isJsonObject=SPARQLConstant.NGSI_GRAPH_PREFIX +"isJsonObject";
	public static final String isJsonArray=SPARQLConstant.NGSI_GRAPH_PREFIX +"isJsonArray";
	public static final String balnkNode="_:b";//SPARQLConstant.NGSI_GRAPH_PREFIX +"bn";
	public static final String arrayPos="rdfs:_";//https://ontola.io/blog/ordered-data-in-rdf/
	public static final String root=SPARQLConstant.NGSI_GRAPH_PREFIX +"root";
	public static final String rdfType="rdfs:type";//@type and type
	public static final String rdfId="rdfs:Resource"; //@id and id
	//rdf:ID is discouraged: https://stackoverflow.com/questions/7118326/differences-between-rdfresource-rdfabout-and-rdfid
	public static final String context="rdfs:domain";// @context
	public static final String rdfValue="rdfs:value";// @value	
	//"_:";<- this is the standard blank node but blazegrph will not return it on sparql query
	public static final String rdfTypeWithPrefix="http://www.w3.org/2000/01/rdf-schema#type";
	public static final String rdfIdWithPrefix="http://www.w3.org/2000/01/rdf-schema#Resource";
	public static final String arrayPosWithPrefix="http://www.w3.org/2000/01/rdf-schema#_";
	public static final String contextWithPrefix="http://www.w3.org/2000/01/rdf-schema#domain";
	public static final String rdfValuetWithPrefix="http://www.w3.org/2000/01/rdf-schema#value";
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
