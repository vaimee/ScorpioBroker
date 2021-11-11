package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.query;

import eu.neclab.ngsildbroker.commons.constants.NGSIConstants;
import eu.neclab.ngsildbroker.commons.datatypes.QueryTerm;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLConstant;

public class NGSIQueryParam extends StringEQParam {

	private final static boolean useHasValueToo=true; 
	
	private String numberOP;
	private boolean needStrOnObj= false;
	

	public NGSIQueryParam(int seed,QueryTerm ngsiQuery){
		super(ngsiQuery.isNextAnd(),seed);
//		public final static String QUERY_EQUAL = "=="; 			//SPARQL---> "="
//		public final static String QUERY_UNEQUAL = "!=";
//		public final static String QUERY_GREATEREQ = ">=";
//		public final static String QUERY_GREATER = ">";
//		public final static String QUERY_LESSEQ = "<=";
//		public final static String QUERY_LESS = "<";
//		public final static String QUERY_PATTERNOP = "~=";		//SPARQL---> ??????? WIP
//		public final static String QUERY_NOTPATTERNOP = "!~=";	//SPARQL---> ??????? WIP
		switch (ngsiQuery.getOperator()){
			case NGSIConstants.QUERY_EQUAL:
				this.numberOP="=";
				break;
			case NGSIConstants.QUERY_PATTERNOP: 	//------------_NOT IMPLEMENTED
//					Match pattern (production rule named patternOp). A matching entity shall contain the target element and the
//					target value shall be in the L(R) of the regular pattern specified by the Query Term:
				this.numberOP="=";
				break;
			case NGSIConstants.QUERY_NOTPATTERNOP:	//------------_NOT IMPLEMENTED
//					If the target value data type is different than String then it shall be considered as not matching.
//					Do not match pattern (production rule named notPatternOp). A matching entity shall contain the target
//					element and the target value shall not be in the L(R) of the regular pattern specified by the Query Term:
				this.numberOP="=";
				break;
			default: 
				this.numberOP=ngsiQuery.getOperator();
				break;
		}
		
		String predicate;
		//
		/*the Attribute can be a composed json as: http://context-uri/P100.P1_P1
		 * 
		 *CASE 1) 	-->http://example.org/P100.P1_P1
		 * 
		 *CASE 2) 	-->P100.P1_P1
		 */
		

		String attribute = ngsiQuery.getAttribute();
		String expandAttribute="";
		try {
			expandAttribute = ngsiQuery.getExpandAttribute();
		} catch (ResponseException e1) {
			/*
			 * this can be a problem.. we are not sure if will be a CASE1 or CASE2
			 * or a not composed json 
			 * ex: http://context.uri.contains.points/P100
			 * that uri cotains "." but these are not about json fields separator
			 */
			
		}
		

		String piggyValue = ""; //in case of CASE2
		
		//checking CASE1 or CASE2
		if(attribute.compareTo(expandAttribute)==0) { //CASE1
			//that case is "safe" because we have full-uri 
			//(we don't need SPARQLregex)
			
			//checking if the Attribute is composed
			if(attribute.contains(".")) {//composed
				//building the json path of the composed attribute following RDF
				String temp[] = expandAttribute.split("\\/");
				attribute = temp[temp.length-1];
				String expandAttributePrefix=expandAttribute.substring(0, 
						expandAttribute.length()-attribute.length());
				predicate="";
				String attrParts[] = attribute.split("\\.");
				for(int seed3 =0;seed3<attrParts.length;seed3++) {
					predicate+="<"+expandAttributePrefix+attrParts[seed3] + "> ";
					if(seed3<attrParts.length-1) {
						String bn_as_link = "?bn_"+seed + "_"+seed3;
						predicate+=bn_as_link +".\n" +bn_as_link +" ";
					}
				}
				/*Exemple:http://context-uri/P100.P1_P1
				 * 
				 * expandAttributePrefix="http://context-uri/"
				 * 
				 * attrParts=["P100","P1_P1"]
				 * 
				 * ####at seed3=0)
				 * 		predicate="<http://context-uri/P100> ?bn_0_0_0 .\n ?bn_0_0_0 "
				 * ####at seed3=1)
				 * 		predicate="<http://context-uri/P100> ?bn_0_0_0 .\n ?bn_0_0_0 <http://context-uri/P1_P1>"
				 */
			}else {//not composed
				predicate="<"+attribute+">";
			}
		}else {// CASE2
			//that case is "unsafe", we realy don't know the real uri
			//so we need SPARAL rexeg 
			//expandAttribute is not alweys correct as full-uri 
			//default full uri would be added as follow
			//EX: <https://uri.etsi.org/ngsi-ld/P1> instead of <http://example.org/P1>
			
			//checking if the Attribute is composed
			if(attribute.contains(".")) {//composed

				String expandAttributePrefix="";
				try {
					expandAttributePrefix=ngsiQuery.getExpandAttribute();
					expandAttributePrefix=expandAttributePrefix.substring(0, 
							expandAttributePrefix.length()-attribute.length());
				} catch (ResponseException e) {
					//maybee here need print something or handle it with SPARQL REGEX
					//as regex(str(?p),"*/"+ngsiQuery.getAttribute())
					//as in the following try-catch
				}
				predicate="";
				String attrParts[] = attribute.split("\\.");
				for(int seed3 =0;seed3<attrParts.length;seed3++) {
					predicate+="?pv_"+seed +"_"+seed3;
					piggyValue+=" && regex(str(?pv_"+seed +"_"+seed3+"),\".+"+attrParts[seed3]+"$\")";
					if(seed3<attrParts.length-1) {
						String bn_as_link = "?bn_"+seed + "_"+seed3;
						predicate+=bn_as_link +".\n" +bn_as_link +" ";
					}
				}
				/*Exemple:http://context-uri/P100.P1_P1
				 * 
				 * expandAttributePrefix="http://context-uri/"
				 * 
				 * attrParts=["P100","P1_P1"]
				 * 
				 * ####at seed3=0)
				 * 		predicate="?pv_0_0_0 ?bn_0_0_0 .\n ?bn_0_0_0 "
				 * ####at seed3=1)
				 * 		predicate="?pv_0_0_0 ?bn_0_0_0 .\n ?bn_0_0_0 ?pv_0_0_1"
				 * 
				 * piggyValue=" && regex(str(?pv_0_0_0),".+P100$") && regex(str(?pv_0_0_1),".+P1_P1$")"
				 */
				
			}else {//attribute is not composed
					predicate="?pv_"+seed;
					piggyValue+=" && regex(str(?pv_"+seed+"),\".+"+attribute+"$\")";
			}
		}
		

		//JSON-LD fields
		predicates.add(predicate);
		//JSON-LD value (and if needs piggyValue)
		String opertant = ngsiQuery.getOperant();
		if(!isNumeric(opertant)) {
			needStrOnObj=true;
			//that sanitize is not necessary
			if(!opertant.startsWith("\"") || !opertant.endsWith("\"") ) {
				opertant="\""+opertant+"\"";
			}
			
		}else {
			needStrOnObj=false;
		}
		values.add(opertant+piggyValue+" ");	
	}
	
	
	
	@Override
	protected String generateClauseAt(int x) {
		String value = values.get(x);
		if(useHasValueToo) {
			if(needStrOnObj) {
				return "(str(?o"+_seed+"_"+x+")"+numberOP+value+" || "+
						"str(?ohv"+_seed+"_"+x+")"+numberOP+value+")";
			}else {
				return "(?o"+_seed+"_"+x+numberOP+value+" || "+
						"?ohv"+_seed+"_"+x+numberOP+value+")";
			}
		}else {
			if(needStrOnObj) {
				return "str(?o"+_seed+"_"+x+")"+numberOP+value+" ";
			}else {
				return "?o"+_seed+"_"+x+numberOP+value+" ";
			}
		}
	}
	
	@Override
	public String getVars() {
		if(useHasValueToo) {
			String vars ="";
			for (int x =0 ;x<predicates.size();x++) {
				String varID = _seed+"_"+x;
				vars+="?s"+varID+" " + predicates.get(x)+ " ?o"+varID+".\n";
				/*
				 * <urn:ngsi-ld:T:I123k468:Context>	<http://example.org/P100>	t5942
				 * t5942	<https://uri.etsi.org/ngsi-ld/hasValue>	12
				 * 
				 */
				vars+="?s"+varID+" " + predicates.get(x)+ " ?shv"+varID+".\n";
				if(needStrOnObj) {
					/*
					 * if value is not NUMERIC, the ?phv will not < NGSIConstants.NGSI_LD_HAS_VALUE>
					 * but can be  <https://uri.etsi.org/ngsi-ld/hasObject> or something else
					 * in future that can be managed in a better way
					 */
					vars+="?shv"+varID+" ?phv"+varID+" ?ohv"+varID+".\n";
					
				}else {
					vars+="?shv"+varID+" <" + NGSIConstants.NGSI_LD_HAS_VALUE+ "> ?ohv"+varID+".\n";
				}
			}
			for (IParam param : params) {
				vars+=param.getVars();
			}
			if(vars.length()==0) {
				return "?sANY ?pANY ?oANY";
			}
			return vars;
		}else {
			return super.getVars();
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

}
