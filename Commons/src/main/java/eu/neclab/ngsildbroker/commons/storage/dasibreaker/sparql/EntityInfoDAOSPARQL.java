package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import eu.neclab.ngsildbroker.commons.constants.DBConstants;
import eu.neclab.ngsildbroker.commons.constants.NGSIConstants;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IConverterJRDF;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IEntityInfoDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.QueryLanguageFactory;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLConstant;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLGenerator;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SepaGateway;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.TitaniumWrapper;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.response.ErrorResponse;
import it.unibo.arces.wot.sepa.commons.response.QueryResponse;
import it.unibo.arces.wot.sepa.commons.response.Response;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;

@Repository
public class EntityInfoDAOSPARQL extends StorageReaderDAOSPARQL implements IEntityInfoDAO {
	

	
	public Set<String> getAllIds() {
		//SELECT id FROM entity
		String termVarName = "id";
		String idPredicate = NGSIConstants.NGSI_LD_DEFAULT_PREFIX + DBConstants.DBCOLUMN_ID;
		String filter = "FILTER(regex(str(?g),\"^/"+DBConstants.DBTABLE_ENTITY+".\"))";
		String sparql = "SELECT ?"+termVarName+
					" WHERE {\nGRAPH ?g\n{?s <"+idPredicate+"> ?"+termVarName+"}\n "+
					filter+"}";

		System.out.println("EntityInfoDAOSPARQL.getAllIds.SPARLQ:\n"+sparql+ "\n");
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

	public String getEntity(String entityId) {
		//"SELECT data FROM entity WHERE id='" + entityId + "'"

		
		String graph = SPARQLGenerator.generateJsonGraphUri(DBConstants.DBTABLE_ENTITY,DBConstants.DBCOLUMN_DATA, entityId);
//		String sparql = "SELECT ?s ?p ?o ?e WHERE {\n"+
//					"GRAPH ?e { ?s ?p ?o}\n"+
//					"VALUES(?e){(<"+graph+">)}"+
//					"}";
		String sparql="SELECT ?s ?p ?o ?e ?type WHERE {\n"+
			"GRAPH ?e { ?s ?p ?o}\n"+
			"GRAPH ?e { <"+entityId+"> rdf:type ?type}\n"+
			"VALUES(?e){(<"+graph+">)}}\n";
		System.out.println("EntityInfoDAOSPARQL.getEntity.SPARLQ:\n"+sparql+ "\n");
	
		try {
			Response res= SepaGateway.getInstance().executeQuery(sparql);
			if(res.isError()) {
				System.err.print(((ErrorResponse)res).getError());
			}else {
				IConverterJRDF converter =QueryLanguageFactory.getConverterJRDF();
//				if(converter instanceof TitaniumWrapper) {
//					((TitaniumWrapper)converter).setResolveBlankNodesNoFraming(true);
//				}
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
