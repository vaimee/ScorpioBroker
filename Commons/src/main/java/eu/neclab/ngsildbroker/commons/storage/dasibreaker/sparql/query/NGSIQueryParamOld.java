package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.query;

import java.util.ArrayList;
import java.util.List;

import eu.neclab.ngsildbroker.commons.constants.NGSIConstants;
import eu.neclab.ngsildbroker.commons.datatypes.QueryTerm;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLConstant;

public class NGSIQueryParamOld extends StringEQParam {

	private final static boolean useHasValueToo=true; 
	
	private String numberOP;
	private boolean needStrOnObj= false;
	
	protected List<String> hv_predicates; 
	protected List<String> hv_piggyValues; 
	protected List<String> piggyValues;

	String piggyValue = ""; 
	String hv_predicate="";
	String hv_piggyValue="";
	
	/*
	 * Important note
	 * From the standard page 90: 
	 * 		If a JSON-LD context is not provided then all the query terms shall be resolved 
	 * 		against the default JSON-LD @context
	 * From the standard page 106: 
	 * 		The type in get query need to be: Comma separated list of entity type names
	 */
	/*
	 * Example, used for generate part of the SPARQL query for the test 042 of TestSuit:
	 * Look up to the lines marked with "-->"
	 * 
			SELECT ?s ?p ?o ?e {
				GRAPH ?e { ?s ?p ?o}
				{
				SELECT DISTINCT ?e {
					GRAPH ?e {
-->						?s3_0 <https://uri.etsi.org/ngsi-ld/default-context/P100> ?bn_3_0.
-->						?bn_3_0 ?p_3_0 ?o3_0.
-->						?s3_0 <https://uri.etsi.org/ngsi-ld/default-context/P100> ?hv_bn_3_0.
-->						?hv_bn_3_0 ?hv_p_3_0 ?shv3_0.
-->						?shv3_0 ?phv3_0 ?ohv3_0.
					}
					FILTER(
-->						(str(?o3_0)>"2018-12-03T12:00:00.000Z" && regex(str(?p_3_0),".+observedAt$"))
-->						||
-->						(str(?ohv3_0)>"2018-12-03T12:00:00.000Z" && regex(str(?hv_p_3_0),".+observedAt$"))
					)
				GRAPH ?g {
				?subject <http://localhost:3000/ngsi/data_without_sysattrs> ?e.
				?subject <http://localhost:3000/ngsi/type> ?o2_0.
				 }FILTER(regex(str(?g),"^.*entity") && (str(?o2_0)="https://uri.etsi.org/ngsi-ld/default-context/T_Query" ))
				}
				 LIMIT 50 OFFSET 0
			}}
	 */
	public NGSIQueryParamOld(int seed,QueryTerm ngsiQuery){
		super(ngsiQuery.isNextAnd(),seed);
//		public final static String QUERY_EQUAL = "=="; 			//SPARQL---> "="
//		public final static String QUERY_UNEQUAL = "!=";
//		public final static String QUERY_GREATEREQ = ">=";
//		public final static String QUERY_GREATER = ">";
//		public final static String QUERY_LESSEQ = "<=";
//		public final static String QUERY_LESS = "<";
//		public final static String QUERY_PATTERNOP = "~=";		//SPARQL---> ??????? WIP
//		public final static String QUERY_NOTPATTERNOP = "!~=";	//SPARQL---> ??????? WIP
		
		hv_predicates=new ArrayList<String>();
		hv_piggyValues=new ArrayList<String>(); 
		piggyValues=new ArrayList<String>(); 
		
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
		String attribute = ngsiQuery.getAttribute();
		String expandAttribute="";
		String expandAttributePrefix=null;
		try {
			expandAttribute = ngsiQuery.getExpandAttribute();
			expandAttributePrefix=expandAttribute.substring(0, 
					expandAttribute.length()-attribute.length());
		} catch (ResponseException e1) {
			//we need concat the default context prefix at the "attribute"
			expandAttributePrefix=NGSIConstants.NGSI_LD_DEFAULT_PREFIX;
			expandAttribute=expandAttributePrefix+attribute;
			System.out.print("WARNING: NGSIQueryParam.costructor :"+e1.getMessage());
		}
		
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
		predicate="<"+expandAttributePrefix+attrParts[0]+">";
		if(useHasValueToo) {
			hv_predicate = "<"+expandAttributePrefix+attrParts[0]+">";
		}
		for(int x =1;x<attrParts.length;x++) {
			predicate+=" ?bn_"+seed +"_"+seed2+".\n?bn_"+seed +"_"+seed2+" ?p_"+seed +"_"+seed2;
			piggyValue+=" && regex(str(?p_"+seed +"_"+seed2+"),\".+"+attrParts[x]+"$\")";
			if(useHasValueToo) {
				hv_predicate +=" ?hv_bn_"+seed +"_"+seed2+".\n?hv_bn_"+seed +"_"+seed2+" ?hv_p_"+seed +"_"+seed2;
				hv_piggyValue+=" && regex(str(?hv_p_"+seed +"_"+seed2+"),\".+"+attrParts[x]+"$\")";
			}
			seed2++;
			if(x<attrParts.length-1) {
				String bn_as_link = " ?bn_"+seed + "_"+seed2;
				predicate+=bn_as_link +".\n" +bn_as_link +" ";
				if(useHasValueToo) {
					String hv_bn_as_link = " ?hv_bn_"+seed + "_"+seed2;
					hv_predicate += " ?hv_bn_"+seed + "_"+seed2;
					hv_predicate+=hv_bn_as_link +".\n" +hv_bn_as_link +" ";
				}
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
		values.add(opertant);	
	}
	
	
	
	@Override
	protected String generateClauseAt(int x) {
		String value = values.get(x);
		if(useHasValueToo) {
			if(needStrOnObj) {
				return "(str(?o"+_seed+"_"+x+")"+numberOP+value + piggyValue +")\n"+
							"||\n"+
						"(str(?ohv"+_seed+"_"+x+")"+numberOP+value + hv_piggyValue+")\n";
			}else {
				return "(?o"+_seed+"_"+x+" "+numberOP+value + piggyValue +")\n"+
							"||\n"+
						"(?ohv"+_seed+"_"+x+" "+numberOP+value + hv_piggyValue+")\n";
			}
		}else {
			if(needStrOnObj) {
				return "str(?o"+_seed+"_"+x+")"+numberOP+value+" "+ piggyValue;
			}else {
				return "?o"+_seed+"_"+x+numberOP+value+" "+ piggyValue;
			}
		}
	}
	
	@Override
	public String getVars() {
		if(useHasValueToo) {
			String vars ="";
			//that for is not needed (predicates.size() is always 1)
			for (int x =0 ;x<predicates.size();x++) {
				String varID = _seed+"_"+x;
				vars+="?s"+varID+" " +  predicates.get(x) + " ?o"+varID+".\n";
				
				vars+="?s"+varID+" " +hv_predicates+ " ?shv"+varID+".\n";
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
