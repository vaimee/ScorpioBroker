package eu.neclab.ngsildbroker.commons.storage.dasibreaker;



public class SPARQLGenerator {

	protected SPARQLConverter _converter;
	
	
	public SPARQLGenerator(String table){
		_converter= new SPARQLConverter(table);
	}
	
	
	//----------------------------------SETTERS and GETTERS
	public String getTable() {
		return _converter.getTable();
	}

	public SPARQLConverter getConverter() {
		return _converter;
	}

	public void setConverter(SPARQLConverter converter) {
		this._converter =  converter;
	}
	
	
}
