package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.query;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.neclab.ngsildbroker.commons.constants.NGSIConstants;
import eu.neclab.ngsildbroker.commons.datatypes.QueryTerm;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;


/*
 * This class menage asingle leaft of the NGSI-QUERY
 * we will create normal "var" that represent the triples for the SPARQL Query
 * and "cluases" that represent the FILTER terms for the SPARQL Query correlated to "var"
 * 
 * the "HasValue" version is needed to cover the possibility about the "hasValue" triple added by Titanium framing
 * so we create "hv_vars" and "hv_clauses" too
 * 
 * we menage the NGSIConstants.NGSI_LD_DEFAULT_PREFIX or the prefix provided by the @context
 * only for the first Attribute part in case of a composed Attribute
 * For example the composed attribute "P100.P1_1" will have the full uri only for "P100" part as:
 * "http://context-uri/P100"
 * and "P1_1" will be covered by a FILTER regex as: regex(?p1_1, ".*P1_1")
 */
public class NGSIQueryTerm {

	private final static Logger logger = LogManager.getLogger(NGSIQueryTerm.class);
	
	
	private String vars;
	private String hv_vars;
	private String clauses;
	private String hv_clauses;

	//the "value_clauses" and "value_hv_clauses" can be overridden using "overwrite" method
	//for example in case the NGSI-Query has more tha one clause for the same Attribute
	//example: P100.P1_1>0 OR P100.P1_1<5 the clauses that define the P100.P1_1 are the same
	//but the clause for the >0 and <5 are not the same
	//so we will overwrite only "value_clauses" and "value_hv_clauses"
	private String value_clauses ="";
	private String value_hv_clauses ="";
	
	private boolean needStrOnObj; 
	private String _seed="";
	
	public NGSIQueryTerm(QueryTerm ngsiQuery,String seed){
			this._seed=seed;
			String attribute = ngsiQuery.getAttribute();
			String expandAttribute="";
			String expandAttributePrefix=null;
			//this part can be implemented in a better way maybe
			//but more expansive, for example using context for resolve Attributes
			//the real problem is that SCORPIO do not divide the json by comma it-self
			
			//IMPORTANT:
			//SCORPIO do not remove prefix in the "ngsiQuery.getAttribute()", this is the main problem
			
			try {
				expandAttribute = ngsiQuery.getExpandAttribute();
				if(attribute.contains(":")) {//ex->schema:name
					attribute=attribute.substring((attribute.indexOf(":")+1), attribute.length());
				}
				expandAttributePrefix=expandAttribute.substring(0, 
						expandAttribute.length()-attribute.length());
			} catch (ResponseException e1) {
				//we need concat the default context prefix at the "attribute"
				
				//if attr is for example "schema:name" this will be a problem 
				//-->https://uri.etsi.org/ngsi-ld/default-context/schema:name
				
				expandAttributePrefix=NGSIConstants.NGSI_LD_DEFAULT_PREFIX;
				expandAttribute=expandAttributePrefix+attribute;
				logger.warn("WARNING: NGSIQueryTerm.costructor :"+e1.getMessage());
			}
			
			hv_vars="";
			vars="";
			clauses="";
			hv_clauses="";
			overwrite(ngsiQuery);
			
			int seed2 = 0;
			/*Exemple:http://context-uri/P100.P1_P1
			 * 
			 * expandAttributePrefix="http://context-uri/"
			 * 
			 * attrParts=["P100","P1_P1"]
			 */
			String attrParts[];
			//checking if the attribute is composed or not
			if(attribute.contains(".")) {//composed
				//if the attribute is composed, we can establish only 
				//the first uri as absolute with the context
				attrParts = attribute.split("\\.");
			}else {//attribute is not composed
				attrParts = new String[]{attribute};
			}
			//first attribute 
			vars+="<"+expandAttributePrefix+attrParts[0]+">";
			hv_vars+= "<"+expandAttributePrefix+attrParts[0]+">";
			for(int x =1;x<attrParts.length;x++) {
				vars+=" ?bn_"+seed +"_"+seed2+".\n?bn_"+seed +"_"+seed2+" ?p_"+seed +"_"+seed2;
				clauses+=" && regex(str(?p_"+seed +"_"+seed2+"),\".+"+attrParts[x]+"$\")";
				

				hv_vars +=" ?hv_bn_"+seed +"_"+seed2+".\n?hv_bn_"+seed +"_"+seed2+" ?hv_p_"+seed +"_"+seed2;
				hv_clauses+=" && regex(str(?hv_p_"+seed +"_"+seed2+"),\".+"+attrParts[x]+"$\")";
				
			
				seed2++;
				if(x<attrParts.length-1) {
					String bn_as_link = " ?bn"+seed + "_"+seed2;
					vars+=bn_as_link +".\n" +bn_as_link +" ";
					
					String hv_bn_as_link = " ?hvbn"+seed + "_"+seed2;
					hv_vars += " ?hv_p"+seed + "_"+seed2 + " "+hv_bn_as_link +".\n" +hv_bn_as_link +" ";
					seed2++;
				}
			}
			vars+=" ?o"+_seed+".\n";
			String shv = " ?shv_"+seed + "_"+seed2;
			if(needStrOnObj) {
				/*
				 * if value is not NUMERIC, the ?phv will not < NGSIConstants.NGSI_LD_HAS_VALUE>
				 * but can be  <https://uri.etsi.org/ngsi-ld/hasObject> or something else
				 * in future that can be managed in a better way
				 */
				hv_vars+=shv+".\n"+shv+" ?phv"+seed + "_"+seed2+" ?ohv"+_seed+".\n";
				
			}else {
				hv_vars+=shv+".\n"+shv+" <" + NGSIConstants.NGSI_LD_HAS_VALUE+ "> ?ohv"+_seed+".\n";
			}
			
		
	}
	
	protected void overwrite(QueryTerm ngsiQuery) {
		String numberOP = this.convertOperator(ngsiQuery.getOperator());
		//JSON-LD value (and if needs piggyValue)
		String value = ngsiQuery.getOperant();
		if(!isNumeric(value)) {
			needStrOnObj=true;
			//that sanitize is not necessary
			if(!value.startsWith("\"") || !value.endsWith("\"") ) {
				value="\""+value+"\"";
			}
		}else {
			needStrOnObj=false;
		}
		if(needStrOnObj) {
			value_clauses= "str(?o"+_seed+") "+numberOP+" "+value;
					
			value_hv_clauses="str(?ohv"+_seed+") "+numberOP+" "+value;
		}else {
			value_clauses= "?o"+_seed+" "+numberOP+" "+value;
					
			value_hv_clauses="?ohv"+_seed+" "+numberOP+" "+value;
		}
		
	}
	
	
	protected String convertOperator(String op) {
		//		public final static String QUERY_EQUAL = "=="; 			//SPARQL---> "="
		//		public final static String QUERY_UNEQUAL = "!=";
		//		public final static String QUERY_GREATEREQ = ">=";
		//		public final static String QUERY_GREATER = ">";
		//		public final static String QUERY_LESSEQ = "<=";
		//		public final static String QUERY_LESS = "<";
		//		public final static String QUERY_PATTERNOP = "~=";		//SPARQL---> ??????? WIP
		//		public final static String QUERY_NOTPATTERNOP = "!~=";	//SPARQL---> ??????? WIP
		switch (op){
			case NGSIConstants.QUERY_EQUAL:
				return"=";
			case NGSIConstants.QUERY_PATTERNOP: 	//------------_NOT IMPLEMENTED
	//				Match pattern (production rule named patternOp). A matching entity shall contain the target element and the
	//				target value shall be in the L(R) of the regular pattern specified by the Query Term:
				return"=";
			case NGSIConstants.QUERY_NOTPATTERNOP:	//------------_NOT IMPLEMENTED
	//				If the target value data type is different than String then it shall be considered as not matching.
	//				Do not match pattern (production rule named notPatternOp). A matching entity shall contain the target
	//				element and the target value shall not be in the L(R) of the regular pattern specified by the Query Term:
				return"=";
			default: 
				return op;
		}
	}
	
	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        double d = Double.parseDouble(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	
	public String getVars() {
		return vars;
	}


	public String getClauses() {
		return "("+value_clauses+clauses+")";
	}


	public String getHvVars() {
		return hv_vars;
	}


	public String getHvClauses() {
		return "("+value_hv_clauses+hv_clauses+")";
	}
	
	public String getAllClauses(boolean useHasValueToo) {
		if(useHasValueToo) {
			return "("+getClauses()+"||"+getHvClauses()+")";
		}else {
			return getClauses();
		}
	}



}
