package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import eu.neclab.ngsildbroker.commons.constants.DBConstants;
import eu.neclab.ngsildbroker.commons.constants.NGSIConstants;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IConverterJRDF;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.ISubscriptionInfoDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.QueryLanguageFactory;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLGenerator;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SepaGateway;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.response.ErrorResponse;
import it.unibo.arces.wot.sepa.commons.response.QueryResponse;
import it.unibo.arces.wot.sepa.commons.response.Response;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;


@Repository
public class SubscriptionInfoDAOSPARQL extends StorageReaderDAOSPARQL  implements ISubscriptionInfoDAO{
	

	private final static Logger logger = LogManager.getLogger(SubscriptionInfoDAOSPARQL.class);
	
//	public String getEntity(String entityId) {
//		logger.info("\ncall on DAO ====> SubscriptionInfoDAOSQL.getEntity <====\n");
//		List<String> tempList = readerJdbcTemplate.queryForList("SELECT data FROM entity WHERE id='" + entityId + "'", String.class);
//		return tempList.get(0);
//	}
//	
	
	public SubscriptionInfoDAOSPARQL() {
		super();
		super.init();
	}
	
	public Set<String> getAllIds() {
		//SELECT id FROM entity
		String termVarName = "id";
		String idPredicate = NGSIConstants.NGSI_LD_DEFAULT_PREFIX+ DBConstants.DBCOLUMN_ID;
		String filter = "FILTER(regex(str(?g),\"^/"+DBConstants.DBTABLE_ENTITY+".\"))";
		String sparql = "SELECT ?"+termVarName+
					" WHERE {\nGRAPH ?g\n{?s <"+idPredicate+"> ?"+termVarName+"}\n "+
					filter+"}";

		logger.debug("SubscriptionInfoDAOSPARQL.getAllIds.SPARLQ:\n"+sparql+ "\n");
		try {
			Response res= SepaGateway.getInstance().executeQuery(sparql);
			if(res.isError()) {
				System.err.print(((ErrorResponse)res).getError());
			}else {
				HashSet<String> entityList = new HashSet<String>();
				for (Bindings bind : ((QueryResponse)res).getBindingsResults().getBindings()) {
					entityList.add(bind.getRDFTerm(termVarName).getValue());
				}
				return entityList;
			}
		} catch (SEPASecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SEPABindingsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//in case of error
		return new HashSet<String>();
	}
	
	public Map<String, String> getIds2Type() {
		//SELECT id, type FROM entity
		String idPredicate = NGSIConstants.NGSI_LD_DEFAULT_PREFIX+ DBConstants.DBCOLUMN_ID;
		String typePredicate = NGSIConstants.NGSI_LD_DEFAULT_PREFIX+ DBConstants.DBCOLUMN_TYPE;
		String filter = "FILTER(regex(str(?g),\"^/"+DBConstants.DBTABLE_ENTITY+".\"))";
		String sparql = "SELECT ?id ?type"+
					" WHERE {\nGRAPH ?g\n{?s <"+idPredicate+"> ?id.\n "+
					" ?s <"+typePredicate+"> ?type.}\n"+
					filter+"}";

		logger.debug("SubscriptionInfoDAOSPARQL.getIds2Type.SPARLQ:\n"+sparql+ "\n");
		Map<String, String> result =  new HashMap<String, String>();
		
		try {
			Response res= SepaGateway.getInstance().executeQuery(sparql);
			if(res.isError()) {
				System.err.print(((ErrorResponse)res).getError());
			}else {
				for (Bindings bind : ((QueryResponse)res).getBindingsResults().getBindings()) {
					result.put(bind.getRDFTerm("id").getValue(),bind.getRDFTerm("type").getValue());
				}
			}
		} catch (SEPASecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SEPABindingsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public String getEntity(String entityId) {
		//SELECT data FROM entity WHERE id='" + entityId + "'

		String graph = SPARQLGenerator.generateJsonGraphUri(DBConstants.DBTABLE_ENTITY,DBConstants.DBCOLUMN_DATA, entityId);
//		String sparql = "SELECT ?s ?p ?o ?e WHERE {\n"+
//					"GRAPH ?e { ?s ?p ?o}\n"+
//					"VALUES(?e){(<"+graph+">)}"+
//					"}";
		String sparql="SELECT ?s ?p ?o ?e ?type WHERE {\n"+
			"GRAPH ?e { ?s ?p ?o}\n"+
			"GRAPH ?e { <"+entityId+"> rdf:type ?type}\n"+
			"VALUES(?e){(<"+graph+">)}}\n";
		logger.debug("SubscriptionInfoDAOSPARQL.getEntity.SPARLQ:\n"+sparql+ "\n");
	
		try {
			Response res= SepaGateway.getInstance().executeQuery(sparql);
			if(res.isError()) {
				System.err.print(((ErrorResponse)res).getError());
			}else {
				IConverterJRDF converter =QueryLanguageFactory.getConverterJRDF();
				List<String> etities=converter.RDFtoJson(((QueryResponse)res).getBindingsResults().getBindings());
				return etities.get(0);
			}
		} catch (SEPASecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SEPABindingsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
