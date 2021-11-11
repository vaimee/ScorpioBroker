package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import com.apicatalog.jsonld.JsonLdError;

public class InternalTriple {
	private String _s;
	private String _p;
	private String _o;
	private boolean _needDelete = false;
	private String _needDataGraph = null;
	public InternalTriple(String s, String p, String o) {
		super();
		this._s = s;
		this._p = p;
		this._o = o;
		this._needDataGraph=null;
	}
	public InternalTriple(String s, String p, String o,String needDataGraph) {
		super();
		this._s = s;
		this._p = p;
		this._o = o;
		this._needDataGraph=needDataGraph;
	}
	public String getTriple(boolean objectAsVar) {
		if(objectAsVar) {
			return _s+_p+"?o .\n";
		}else {
			if(_needDataGraph==null) {
				return _s+_p+_o+".\n";
			}else {
				return _s+_p+"<"+_o+">.\n";
			}
		}
	}
	public String getO() {
		return _o;
	}
	public boolean needDelete() {
		return _needDelete;
	}
	public void setNeedDelete(boolean needDelete) {
		this._needDelete = needDelete;
	}

	public boolean needDataGraph() {
		return _needDataGraph!=null;
	}
	public String getRdfGraphTriples() throws JsonLdError {
		IConverterJRDF converter =QueryLanguageFactory.getConverterJRDF();
		//--------------------WIP
		//--------------------WIP
		//here we need context and type of the entity,
		//taken from the ngsi-ld query
		//--------------------WIP
		//--------------------WIP
		try {
			return converter.JSONtoRDF(_needDataGraph);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
}
