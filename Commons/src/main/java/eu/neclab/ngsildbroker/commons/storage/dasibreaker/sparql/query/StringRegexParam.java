package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.query;


public class StringRegexParam extends StringEQParam {

	
	public StringRegexParam(boolean and,int seed){
		super(and,seed);
	}

	
	@Override
	protected String generateClauseAt(int x) {
		return 	"regex(str(?o"+_seed+"_"+x+"),\""+values.get(x)+"\") ";
	}
	

}
