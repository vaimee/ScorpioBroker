package eu.neclab.ngsildbroker.commons.storage.dasibreaker;



public class SPARQLGenerator {

	protected String _table;
	protected ConverterJSONLDSPARQL _converter;
	
	
	public SPARQLGenerator(String table){
		_table=table;
		_converter= new ConverterJSONLDSPARQL();
	}
	
	public void resetConverter() {
		_converter= new ConverterJSONLDSPARQL();
	}
	
	//----------------------------------SETTERS and GETTERS
	public String getTable() {
		return _table;
	}

	public void setTable(String _table) {
		this._table = _table;
	}


	public ConverterJSONLDSPARQL get_converter() {
		return _converter;
	}


	public void set_converter(ConverterJSONLDSPARQL _converter) {
		this._converter = _converter;
	}
	
	
}
