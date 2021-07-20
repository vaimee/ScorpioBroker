package eu.neclab.ngsildbroker.commons.storage.dasibreaker;



public class SPARQLGenerator {

	protected JRSConverter _converter;
	
	
	public SPARQLGenerator(String table){
		_converter= new JRSConverter(table);
	}
	

	
	//----------------------------------SETTERS and GETTERS
	public String getTable() {
		return _converter.getTable();
	}

	public JRSConverter getConverter() {
		return _converter;
	}

	public void setConverter(JRSConverter converter) {
		this._converter =  converter;
	}
	
	
}
