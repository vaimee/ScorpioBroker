package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.query;

import eu.neclab.ngsildbroker.commons.constants.DBConstants;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLConstant;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLGenerator;
public class SPARQLGeneratorQuery extends SPARQLGenerator {

	/*
	 * anytime we are using OFFSET and/or LIMIT, it's best practice to also use an ORDER BY
	 * BUT the original SCORPIO-SQL implementation do not use ORDER BY
	 */
	private static boolean useOrderBy=false;
	
	private int offset=-1;
	private int limit=-1;
	
	public SPARQLGeneratorQuery(String table){
		super(table);
	}
	
	public SPARQLGeneratorQuery(String table,int offset,int limit){
		super(table);
		this.offset=offset;
		this.limit=limit;
	}
	
	/*
	 * Main SPARQL Query, the others generation function are specialization of that one
	 * 
	 * jsonb_params are about the json-ld attributes, we are filtering ngsi-ld entities
	 * by their json attribute
	 * 
	 * ngsi_param are about the ngsi-ld georel attributes and temporal attributes
	 * 
	 */
	public String generateSparqlQuery(IParam jsonb_params,String jsonbColumn, IParam ngsi_params, boolean needPiggyType) {
		
		/*
		   SELECT ?e ?s ?p ?o ?type { 
			  		GRAPH ?e { ?s ?p ?o}
			    {
			    	
				      SELECT DISTINCT ?e { 
				      	  ####jsonb_params#####
					      GRAPH ?e { ?s1 ?p1 ?o1. ?s2 ?p2 ?o2 }
					      FILTER( ... )
					      
					      
					     ####ngsi_param#####
						 GRAPH ?table { 
							 ?s_x ?collumn ?e . 
							 ?s_x ?collumnType ?type . 
							 ?s_x ?condPred ?condObj
						 }
						 FILTER ( ?table ...)
				      } 
					  LIMIT 1 OFFSET 0
				}
				
			}

		 */
		//--------------
		String piggyTypeVar ="";
		String piggyTypeTriple ="";
		if(needPiggyType) {
			piggyTypeVar=" ?type ";
			piggyTypeTriple=" ?subject <"+SPARQLConstant.NGSI_GRAPH_PREFIX+DBConstants.DBCOLUMN_TYPE+"> ?type.\n";
		}
		//--------------

		String regexTable = SPARQLGenerator.generateURIRegex("?g", ".*", super._table, null);
		String ngsi_part = "";//before fix: "?s \n";
		if(ngsi_params!=null) {
			String json_b_link = "?subject <"+SPARQLConstant.NGSI_GRAPH_PREFIX+jsonbColumn+"> ?e.\n";
			ngsi_part="GRAPH ?g {\n"+
					json_b_link +
					piggyTypeTriple + //void or not void, following needPiggyType false or true
					ngsi_params.getVars("subject")
				+" }";
			if(ngsi_params.needFilter()) {
				ngsi_part+="FILTER("+regexTable+" && ("+ngsi_params.getClause()+"))";
			}else {
				ngsi_part+="FILTER("+regexTable+")";
			}
		}else if(needPiggyType) {

			String jsonbColumnLink = SPARQLGenerator.generateScorpioUri(jsonbColumn);
			ngsi_part="GRAPH ?g {\n"+
					"?subject "+jsonbColumnLink+" ?e.\n"
					+piggyTypeTriple
					+" }"
					+"FILTER("+regexTable+")";
		}

		String paramVars = "GRAPH ?e {\n"+ jsonb_params.getVars() +"}";
		String filter = "";
		if(jsonb_params.needFilter()) {
			filter = "FILTER(\n";
			filter += jsonb_params.getClause()+")";
		}
		String sparql = "SELECT ?s ?p ?o ?e "+ piggyTypeVar +"{\n"
				+ "GRAPH ?e { ?s ?p ?o}\n"
				+ "{\n"
				+ "SELECT DISTINCT ?e ?type {\n"
				+ paramVars		+"\n"
				+ filter 		+"\n"
				+ ngsi_part		+"\n"
				+"}\n";
		
				//----------------------------LIMIT AND OFFSET
				/*OFFSET and LIMIT need to be on "SELECT DISTINCT ?e"
				 * to limit the number of the entities, for each ?e 
				 * we will have an entity
				 * "ORDER BY ?e" is not necessary in that case
				 */
				if(useOrderBy && (offset>=0 || limit>0)) {
					//for future needs?
					sparql+=" ORDER BY ?e";
				}
				if(limit>0) {
					sparql+=" LIMIT "+limit;
				}
				if(offset>=0) {
					sparql+=" OFFSET "+offset;
				}

				//----------------------------ngsi_part
				sparql+="\n}}";

		return sparql;
	}
	
	/*---------------------------NOT IMPLEMENTED
	 * in case of columns data that need to be aggregate with the json-ld
	 * (seams no need)
	 */
	public String generateSparqlQuery(IParam jsonb_params,String jsonbColumns,String[] agregateCollumns, IParam ngsi_params, boolean needPiggyType) {
		
		if(agregateCollumns.length<1) {
			return generateSparqlQuery(jsonb_params,jsonbColumns,ngsi_params,needPiggyType);
		}
		/*
		 * 
			SELECT ?e ?s ?p ?o WHERE{
				{
			      SELECT ?s ?p ?o ?e {
			      GRAPH ?e { ?s ?p ?o}
			      {
			      SELECT DISTINCT ?e {
			      GRAPH ?e {
			      ?s3_0 <https://uri.etsi.org/ngsi-ld/name> ?o3_0.
			      }}
			      }
			      GRAPH ?g {
			      ?subject <http://localhost:3000/ngsi/kvdata> ?e.
			      ?subject <http://localhost:3000/ngsi/type> ?o2_0.
			       }FILTER(regex(str(?g),"^http://localhost:3000/ngsi/entity/.+") && (str(?o2_0)="http://xmlns.com/foaf/0.1/Person" ))}
				}UNION{
			      SELECT ?e ?s ?p ?o {
			          VALUES (?p) {
			              (<https://uri.etsi.org/ngsi-ld/createdAt>)
			              (<https://uri.etsi.org/ngsi-ld/modifiedAt>)
			          } 
			          {
			              SELECT DISTINCT ?e {
			              GRAPH ?e {
			              ?s3_0 <https://uri.etsi.org/ngsi-ld/name> ?o3_0.
			              }}
			          }
			          GRAPH ?g {
			          ?subject <http://localhost:3000/ngsi/kvdata> ?e.
			          ?subject <http://localhost:3000/ngsi/type> ?o2_0.
			          ?subject ?p ?o .
			          ?subject  <http://localhost:3000/ngsi/id> ?s.
			           }FILTER(regex(str(?g),"^http://localhost:3000/ngsi/entity/.+") && (str(?o2_0)="http://xmlns.com/foaf/0.1/Person" ))}
			      }
			}
		   
		 */
		return "";
	}
	
	public String generateSparqlQuery(IParam jsonb_params,String jsonbColumn, boolean needPiggyType) {
		return generateSparqlQuery(jsonb_params,jsonbColumn,null, needPiggyType);
	}

	
	
	
}
