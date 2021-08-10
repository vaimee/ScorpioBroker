package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.query;

import java.util.ArrayList;
import java.util.List;

public class StringEQParam implements IParam {

	
	protected List<String> predicates;
	protected List<String> values;

	protected List<IParam> params; 
	protected String operator;
	
	protected int _seed=0;
	
	public StringEQParam(boolean and,int seed){
		predicates= new ArrayList<String>();
		values = new ArrayList<String>();
		params = new ArrayList<IParam>();
		if(and) {
			operator = " && ";
		}else {
			operator = " || ";
		}
		_seed=seed;
	}
	
	public void addParam(String predicate, String value) {
		predicates.add(predicate);
		values.add(value);
	}
	
	@Override
	public void addParam(IParam param) {
		// TODO Auto-generated method stub
		params.add(param);
	}

	@Override
	public String getClause() {
		String clause ="";
		for (int x =0 ;x<predicates.size();x++) {
			clause+=generateClauseAt(x);
			if(x>0 && x<predicates.size()-1) {
				clause+=operator;
			}
		}
		for (int x =0; x<params.size();x++) {
			if(params.get(x).needBrackets()) {
				clause+="("+params.get(x).getClause()+")";
			}else {
				clause+=params.get(x).getClause();
			}
			if(x>0 && x<params.size()-1) {
				clause+=operator;
			}
		}
		return clause;
	}

	protected String generateClauseAt(int x) {
		return 	"str(?o"+_seed+"_"+x+")=\""+values.get(x)+"\" ";
	}
	
	@Override
	public boolean needBrackets() {
		// TODO Auto-generated method stub
		return predicates.size()>1;
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
		return vars;
	}

	@Override
	public int getSeed() {
		// TODO Auto-generated method stub
		return _seed;
	}

}
