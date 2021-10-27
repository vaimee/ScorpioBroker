package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.query;

import eu.neclab.ngsildbroker.commons.constants.NGSIConstants;
import eu.neclab.ngsildbroker.commons.datatypes.QueryTerm;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLConstant;

public class NGSIQueryParam extends StringEQParam {

	private final static boolean useHasValueToo=true; 
	
	private String numberOP;
	

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
		//JSON-LD fields
		try {
			predicate="<"+ngsiQuery.getExpandAttribute()+">";
		} catch (ResponseException e) {
			//maybee here need print something or handle it with SPARQL REGEX
			//as regex(str(?p),"*/"+ngsiQuery.getAttribute())
			predicate="<"+ngsiQuery.getAttribute()+">";
		} 	
		predicates.add(predicate);
		//JSON-LD value
		values.add(ngsiQuery.getOperant());	
	}
	
	
	
	@Override
	protected String generateClauseAt(int x) {
		if(useHasValueToo) {
			return "(?o"+_seed+"_"+x+numberOP+values.get(x)+" || "+
					"?ohv"+_seed+"_"+x+numberOP+values.get(x) +")";
		}else {
			return "?o"+_seed+"_"+x+numberOP+values.get(x)+" ";
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
				vars+="?s"+varID+" " + predicates.get(x)+ "?shv"+varID+".\n";
				vars+="?shv"+varID+" <" + NGSIConstants.NGSI_LD_HAS_VALUE+ "> ?ohv"+varID+".\n";
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
	

}
