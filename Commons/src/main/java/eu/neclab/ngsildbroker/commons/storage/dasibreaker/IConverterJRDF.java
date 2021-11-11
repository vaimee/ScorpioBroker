package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.util.List;

import eu.neclab.ngsildbroker.commons.datatypes.QueryParams;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;
import it.unibo.arces.wot.sepa.commons.sparql.BindingsResults;

public interface IConverterJRDF {

	String JSONtoRDF(String json) throws Exception ;
	List<String> RDFtoJson(List<Bindings> binings) throws Exception ;
	List<String> RDFtoJson(List<Bindings> binings,String filterBy) throws Exception ;
	List<String> getJsonLD(QueryParams qp,BindingsResults binds) throws Exception ;
}
