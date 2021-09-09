package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.util.List;

import it.unibo.arces.wot.sepa.commons.sparql.Bindings;

public interface IConverterJRDF {

	String JSONtoRDF(String json) throws Exception ;
	List<String> RDFtoJson(List<Bindings> binings) throws Exception ;
	List<String> RDFtoJson(List<Bindings> binings,String filterBy) throws Exception ;
}
