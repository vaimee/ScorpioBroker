package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.query;

import java.util.ArrayList;
import java.util.HashMap;
import eu.neclab.ngsildbroker.commons.datatypes.QueryTerm;



public class NGSIQueryParam extends StringEQParam {

	private final static boolean useHasValueToo=true; 
		
	private HashMap<String,NGSIQueryTerm> terms;
	private Integer iSeed;
	private String _vars;
	private String _clauses;
	
	

	
	public NGSIQueryParam(int seed,QueryTerm ngsiQuery,boolean isAnd){
		super(isAnd,seed);
		//this is necessary for the correct work of the method StringEQParam.getClause
		super.predicates= new ArrayList<String>();
		super.predicates.add("");
		
		this.terms= new HashMap<String,NGSIQueryTerm>();
		this.iSeed=0;
		String temp[]=generateVarsAndClauses(ngsiQuery);
		this._vars=temp[0];
		this._clauses=temp[1];
	}

	
	/*
	 * using:
	 * 
	 * 	FIRST 	as ngsiQuery.getFirstChild()
	 * 	NEXT 	as ngsiQuery.getNext()
	 * 	ACTUAL 	as ngsiQuery.getOperant() ; ngsiQuery.getAttribute() 
	 * 	R()  	as generateVarsAndClauses()
	 * 	OP 		as ngsiQuery.getOperator()
	 * 
	 * CASES:
	 * 
	 * FIRST == NULL &&  NEXT ==NULL -->return the leaf
	 * FIRST != NULL &&  NEXT ==NULL -->return R(FIRST)
	 * FIRST == NULL &&  NEXT !=NULL -->return R(NEXT)
	 * FIRST != NULL &&  NEXT !=NULL -->return (R(FIRST) OP R(NEXT))
	 * 
	 * EXTRA CASE:
	 * 
	 * FIRST == NULL && NEXT !=NULL && ACTUAL!=null -->return (R(ACTUAL) OP R(NEXT))
	 * 
	 * NOTE:
	 * ACTUAL is the same of FIRST for the NGSIQueryParam logic
	 */
	protected String[] generateVarsAndClauses(QueryTerm ngsiQuery) {
		QueryTerm first =ngsiQuery.getFirstChild();
		if(ngsiQuery.hasNext()) {
			QueryTerm next =ngsiQuery.getNext();
			String op;
			if(ngsiQuery.isNextAnd()) {
				op="&&";
			}else {
				op="||";
			}
			if(first==null) {
				if(therIsActual(ngsiQuery)) {
					return assembl(
							generateLeaf(ngsiQuery),
							op,
							generateVarsAndClauses(next)
						);
				}else {
					return generateVarsAndClauses(next);
				}
			}else {
				return assembl(
							generateVarsAndClauses(first),
							op,
							generateVarsAndClauses(next)
						);
			}
		}else {
			if(first==null) {
				//this is a leaf
				return generateLeaf(ngsiQuery);
			}else {
				return generateVarsAndClauses(first);
			}
				 
		}
		
		
		
	}
	
	protected String[] generateLeaf(QueryTerm ngsiQuery) {
		String vars = "";
		String clauses ="";
		
		NGSIQueryTerm term;
		String termName = ngsiQuery.getAttribute();
		if(terms.containsKey(termName)) {
			term= terms.get(termName);
			//we need overwrite the OP and the VALUE of the term
			term.overwrite(ngsiQuery);
		}else {
			term = new NGSIQueryTerm(ngsiQuery,super._seed+"_"+iSeed);
			iSeed++;
			terms.put(termName, term);
			//only if the Term is new i need to add vars (triples that describes the NGSI-LD term)
			//if the "terms" hasmap contains the term i dont need to re-add the vars
			vars+="?s"+super._seed+ " "+term.getVars();
			if(useHasValueToo) {
				vars+="?shv"+this._seed+ " "+term.getHvVars();
			}
		}
		
		clauses+=term.getAllClauses(useHasValueToo);

		return new String[]{vars,clauses};
	}
	
	/*
	 * "ngsiQuery" has the ACTUAL?
	 * 
	 * NOTE: ACTUAL is the same of FIRST for the NGSIQueryParam logic
	 */
	protected boolean  therIsActual(QueryTerm ngsiQuery) {
		return (
				ngsiQuery.getAttribute()!=null && 
				ngsiQuery.getAttribute().length()>0
				);
	}
	
	protected String[] assembl(String[] first, String op,String[] next) {
		String vars = first[0]+ next[0];
		String clauses ="("+first[1]+op+next[1]+")";
		return new String[]{vars,clauses};
	}
	
	@Override
	protected String generateClauseAt(int x) {
		//we can ignore x ( we will have just one predicate)
		return _clauses;
	}
	
	@Override
	public String getVars() {
		String vars = this._vars;
		for (IParam param : params) {
			vars+=param.getVars();
		}
		if(vars.length()==0) {
			vars="?sANY ?pANY ?oANY";
		}
		return vars;
	}
	
	

}
