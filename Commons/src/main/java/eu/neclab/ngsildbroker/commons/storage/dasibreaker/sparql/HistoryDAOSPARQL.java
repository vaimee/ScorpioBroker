package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eu.neclab.ngsildbroker.commons.constants.DBConstants;
import eu.neclab.ngsildbroker.commons.constants.NGSIConstants;
import eu.neclab.ngsildbroker.commons.datatypes.GeoqueryRel;
import eu.neclab.ngsildbroker.commons.datatypes.QueryParams;
import eu.neclab.ngsildbroker.commons.enums.ErrorType;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;
import eu.neclab.ngsildbroker.commons.storage.StorageReaderDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IHistoryDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLConstant;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SepaGateway;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.response.ErrorResponse;
import it.unibo.arces.wot.sepa.commons.response.QueryResponse;
import it.unibo.arces.wot.sepa.commons.response.Response;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;

@Repository
public class HistoryDAOSPARQL extends StorageReaderDAOSPARQL  implements IHistoryDAO{

	protected final static Logger logger = LoggerFactory.getLogger(HistoryDAOSPARQL.class);

	protected final static String DBCOLUMN_HISTORY_ENTITY_ID = "id";
	protected final static String DBCOLUMN_HISTORY_ENTITY_TYPE = "type";
	protected final static String DBCOLUMN_HISTORY_ATTRIBUTE_ID = "attributeid";
	protected final static String DBCOLUMN_HISTORY_INSTANCE_ID = "instanceid";

	protected final static Map<String, String> NGSILD_TO_SQL_RESERVED_PROPERTIES_MAPPING_TIME = initNgsildToSqlReservedPropertiesMappingTime();

	protected static Map<String, String> initNgsildToSqlReservedPropertiesMappingTime() {
		Map<String, String> map = new HashMap<>();
		map.put(NGSIConstants.NGSI_LD_CREATED_AT, DBConstants.DBCOLUMN_CREATED_AT);
		map.put(NGSIConstants.NGSI_LD_MODIFIED_AT, DBConstants.DBCOLUMN_MODIFIED_AT);
		map.put(NGSIConstants.NGSI_LD_OBSERVED_AT, DBConstants.DBCOLUMN_OBSERVED_AT);
		return Collections.unmodifiableMap(map);
	}

	@Override
	public String translateNgsildQueryToSql(QueryParams qp) throws ResponseException {
				System.out.print("MIAO");
		return null;
	}

	private String getSqlWhereForField(String dbColumn, String value) {
		System.out.print("MIAO2");
		return null;
	}

	public String translateNgsildTimequeryToSql(String timerel, String time, String timeproperty, String endTime,
			String dbPrefix) throws ResponseException {
		System.out.print("MIAO3");
		return null;
	}

	public boolean entityExists(String entityId) {
		//that query can just look for the entityId

		HashSet<String> entityList = new HashSet<String>();
		//SELECT id FROM entity
		String sparql = "SELECT ?e WHERE "+
					"{\nGRAPH ?e\n{?s ?p ?o}\n"+
					"\nGRAPH <"+entityId+"/"+DBConstants.DBTABLE_TEMPORALENTITY+">\n{?s ?p ?o}\n}\n";
				

		System.out.println("EntityInfoDAOSPARQL.getAllIds.SPARLQ:\n"+sparql+ "\n");
		try {
			Response res= SepaGateway.getInstance().executeQuery(sparql);
			if(res.isError()) {
				System.err.print(((ErrorResponse)res).getError());
			}else {
				for (Bindings bind : ((QueryResponse)res).getBindingsResults().getBindings()) {
					entityList.add(bind.getRDFTerm("e").getValue());
				}
			}
		} catch (SEPASecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SEPABindingsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(entityList.size()>0) {
			return true;
		}else {
			return false;
		}
	}

}
