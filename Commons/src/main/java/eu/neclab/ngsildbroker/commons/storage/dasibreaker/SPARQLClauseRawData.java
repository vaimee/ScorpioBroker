package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

public class SPARQLClauseRawData extends SPARQLClause{

	public SPARQLClauseRawData(String column, String value, String name) {
		super(column, value, name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getClauseTriple(int index) {
		return "<"+SPARQLConstant.FOR_INTERNAL_USE+"><"+SPARQLConstant.HAS_RAW_DATA+">'"+super.value
				+"'.\n?s"+index+" ?p"+index+" ?o"+index+".\n";
	}
}
