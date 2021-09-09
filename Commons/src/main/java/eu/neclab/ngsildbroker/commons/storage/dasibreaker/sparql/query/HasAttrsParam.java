package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.query;

import java.util.ArrayList;
import java.util.List;

public class HasAttrsParam implements IParam {

	
	protected List<String> predicates;
//	protected List<String> values; // no need

	protected List<IParam> params; 
	protected String operator;
	
	protected int _seed=0;
	
	public HasAttrsParam(boolean and,int seed){
		predicates= new ArrayList<String>();
//		values = new ArrayList<String>();
		params = new ArrayList<IParam>();
		if(and) {
			operator = " && ";
		}else {
			operator = " || ";
		}
		_seed=seed;
	}
	
	public void addParam(String predicate) {
		predicates.add(predicate);
	}
	
	public void addParam(String predicate, String value) {
		predicates.add(predicate);
		//values.add(value); //no need
	}
	
	@Override
	public void addParam(IParam param) {
		// TODO Auto-generated method stub
		params.add(param);
	}

	@Override
	public String getClause() {
		String clause ="";
		for (int x =0; x<params.size();x++) {
			if(params.get(x).needBrackets()) {
				clause+="("+params.get(x).getClause()+")";
			}else {
				clause+=params.get(x).getClause();
			}
			if(x<params.size()-1) {
				clause+=operator;
			}
		}
		return clause;
	}

	public boolean needFilter() {
		return params.size()>0;
	}
	protected String generateClauseAt(int x) {
		return 	"";
	}
	
	@Override
	public boolean needBrackets() {
		return false;
	}
	
	@Override
	public String getVars() {
		String vars ="";
		for (int x =0 ;x<predicates.size();x++) {
			String varID = _seed+"_"+x;
			vars+="?s"+varID+" " + predicates.get(x)+ " ?o"+varID+".\n";
		}
		for (IParam param : params) {
			vars+=param.getVars();
		}
		if(vars.length()==0) {
			return "?sANY ?pANY ?oANY";
		}
		return vars;
	}
	
	@Override
	public String getVars(String s) {
		String vars ="";
		for (int x =0 ;x<predicates.size();x++) {
			String varID = _seed+"_"+x;
			vars+="?s"+varID+" " + predicates.get(x)+ " ?o"+varID+".\n";
		}
		for (IParam param : params) {
			vars+=param.getVars("?"+s);
		}
		if(vars.length()==0) {
			return "?sANY ?pANY ?oANY";
		}
		return vars;
	}
	@Override
	public int getSeed() {
		// TODO Auto-generated method stub
		return _seed;
	}

}
