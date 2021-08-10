package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.query;

import java.util.ArrayList;

import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLClause;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLConstant;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLGenerator;
public class SPARQLGeneratorQuery extends SPARQLGenerator {


	
	public SPARQLGeneratorQuery(String table){
		super(table);
	}

	public String generateSelect(ArrayList<SPARQLClause> column_value) {
		//-----------------------------------------------DEPRECATE
//		SELECT ?s ?p ?o { GRAPH ?g {?s ?p ?o}
//		  { 
//		    SELECT ?g WHERE {
//		      {
//		        SELECT ?ok1 {
//
//		        BIND( EXISTS{
//		            GRAPH ?g1 {
//		              ?s1 ?p1 <o2>.
//		              ?s1 ?p1 ?o1}}AS ?ok1)
//
//
//		        BIND( EXISTS{
//		            GRAPH ?g2 {
//		              ?s2 ?p2 <o>.
//		              ?s2 ?p2 ?o2}}AS ?ok2)
//		      }
//		      HAVING(?ok1 =true && ?ok2=true)
//		    }
//		      
//		    	GRAPH ?g{?s ?p ?o}
//		        FILTER(regex(str(?g),"^http://localhost:9999/blazegraph/namespace/kb/g.$") && ?ok1)
//		              
//		   }
//		           
//		 }
//		 }
		String varName="ok";
		String having = "HAVING(";
		String bindings = "";
		String regex = "\"^"+SPARQLConstant.NGSI_GRAPH_PREFIX+super.getTable()+"/.+\"";
		
		int index =0;	
		for (SPARQLClause sparqlClause : column_value) {
//	        BIND( EXISTS{
//	            GRAPH ?g2 {
//	              ?s2 ?p2 <o>.
//	              ?s2 ?p2 ?o2}}AS ?ok2)
			bindings+=sparqlClause.getClause(super.getTable(), null, varName,index)+"\n";
			having+="?"+varName+index+ "=true ";
			if(index>0) {
				having+=sparqlClause.getCongiunction()+" ";
			}
			index++;
		}
		having+=")";
		
		String sparql="SELECT ?s ?p ?o { GRAPH ?g {?s ?p ?o} {";
		sparql+="  SELECT ?g {";
		sparql+="  SELECT ?"+varName+"1 {";
		sparql+=bindings;
		sparql+="  }";
		sparql+=having;
		sparql+="  }}";//GRAPH ?g{?s ?p ?o}
		sparql+="  FILTER(regex(str(?g),"+regex+") && ?"+varName+"1)";
		sparql+="}";
		return sparql;
	}

	public String generateSparqlGetByType(String type, String column) {
//		SELECT ?s ?p ?o {
//		    GRAPH ?g { ?s ?p ?o}
//		    { 
//		        SELECT ?g {
//		            GRAPH ?g { 
//		                ?s1 rdf:type <https://uri.fiware.org/ns/data-models#Building>
//		            }
//		        }
//		    }
//		    FILTER(regex(str(?g),"^http://ngsi/entity/data/.+"))
//		}

		String regex = "^"+SPARQLConstant.NGSI_GRAPH_PREFIX+super.getTable()+"/"+column+"/.+";
		String sparql ="PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
		sparql+="SELECT ?s ?p ?o {";
		sparql+="GRAPH ?g { ?s ?p ?o}{";
			sparql+="SELECT ?g {";
				sparql+="GRAPH ?g { ";
		sparql+="?s1 rdf:type <"+type+">";
		sparql+="}}}";
		sparql+="FILTER(regex(str(?g),\""+regex+"\"))";
		sparql+="}";
		return sparql;
	}
	
	/*
	 * jsonb_params are about the json-ld attributes, we are filtering ngsi-ld entities
	 * by their json attribute
	 * 
	 * ngsi_param are abount the ngsi-ld georel attributes and temporal attributes
	 */
	public String generateSparqlGetByAttr(IParam jsonb_params, IParam ngsi_param) {
		//------------------------------NOT TESTED YET
		//------------------------------NOT TESTED YET
		//------------------------------NOT TESTED YET
		//------------------------------NOT TESTED YET
		/*
		   SELECT ?e ?s ?p ?o{
			  		GRAPH ?e { ?s ?p ?o}
			    {
			      SELECT DISTINCT ?e { GRAPH ?e { ?s1 ?p1 ?o1. ?s2 ?p2 ?o2 }
			
			      FILTER( ... )
			      } 
			
				}
				 GRAPH ?table { ?s_x ?collumn ?e . ?s_x ?condPred ?condObj}
				 FILTER ( ?table ...)
			}
		 */
		String paramVars = "GRAPH ?e {\n"+ jsonb_params.getVars() +"}";
		String filter = jsonb_params.getClause();
		String ngsi_part = "";
		if(ngsi_param!=null) {
			ngsi_part="GRAPH ?g {\n"+ngsi_param.getVars()+" }";
			filter+=" && ( "+ngsi_param.getClause()+" )";
			String regex = "\"^"+SPARQLConstant.NGSI_GRAPH_PREFIX+super.getTable()+"/.+\"";
			ngsi_part+="FILTER(regex(str(?g),\""+regex+"\"))";
		}
		String sparql = "SELECT ?s ?p ?o ?e {\n"
				+ "GRAPH ?e { ?s ?p ?o}\n"
				+ "{\n"
				+ "SELECT DISTINCT ?e {\n"
				+ paramVars
				+"}\n"
				+ "FILTER(\n"
				+ filter
				+")}\n"+ngsi_part+"}}";
		return sparql;
	}
	
	public String generateSparqlGetByAttr(IParam jsonb_params) {
		return generateSparqlGetByAttr(jsonb_params,null);
	}

	
	
	
}
