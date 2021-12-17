package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import com.apicatalog.jsonld.JsonLdError;


public class SPARQLGeneratorUpdate extends SPARQLGenerator{

	public SPARQLGeneratorUpdate(String table) {
		super(table);
		// TODO Auto-generated constructor stub
	}



	
	//-------------------------------------------------------------------------UPDATE
	protected String genereteDeleteWhereOf(int index,String key) {
			String sparql_2= "";
			InternalTriple it = super._triples.get(index);
			if(it.needDataGraph()) {
				sparql_2="GRAPH <"+it.getO()+"> {?s1 ?p1 ?o1}\n";
			}
			String sparql= "DELETE WHERE {\n"+"GRAPH <"+getGraph(key)+"> {\n"+
					it.getTriple(true)+
					"}"+sparql_2+"};\n";
			return sparql;
	}
	
	//---------------------DELETE
	public String generateDeleteAllByKey(String key){
		String regex = generateURIRegex("?g", key, _table, null); //maybe ".*" instead null
		String sparql = "DELETE {GRAPH ?g {?s ?p ?o}}\n"+
							"WHERE{ GRAPH ?g {?s ?p ?o}\n "+
//OLD						"FILTER(regex(str(?g),\"^"+SPARQLConstant.NGSI_GRAPH_PREFIX+_table+".\"))\n"
							"FILTER("+regex+")\n"
							+"}\n";
		return sparql;
	}
	
	public String generateDeleteAllWhere(String key){//<-------------------------NEED TEST IT
		//testing it
//		DELETE WHERE {
//		  GRAPH ?g {?s ?p ?o}
//		  GRAPH <g> { <s><p><o>}
//		  GRAPH <g> {<sg><pg>?g2}
//		  GRAPH ?g2{?s1 ?s2 ?s3}
//		}
		String sparql = "DELETE WHERE{ GRAPH ?g {?s ?p ?o}\n";
		String sparql_2 = "";
		sparql+="GRAPH <"+getGraph(key)+"> {\n";
		int x=0;
		for (InternalTriple internalTriple : _triples) {
			sparql+=internalTriple.getTriple(false);
			if(internalTriple.needDataGraph()) {
				sparql_2+="GRAPH <"+internalTriple.getO()+"> { ?s"+x+" ?p"+x+" ?o"+x+" }\n";
				x++;
			}
		}
		sparql+="}\n"+sparql_2+"};\n";
		return sparql;
	}
	

	//----------------CREATED
	public String generateCreate(String key,boolean onConflict) throws JsonLdError{
		String sparql= "";
		String insertData  = "INSERT DATA {\n"
								+"GRAPH <"+getGraph(key)+"> {\n";
		String deleteWhere ="";
		String sparql_data_graph = "";
		boolean needDelete = false;
		for(int x = 0;x<_triples.size();x++) {
			InternalTriple triple = _triples.get(x);
			insertData+=triple.getTriple(false);
			if(triple.needDataGraph()) {
				sparql_data_graph+="GRAPH <"+triple.getO()+">{\n"
						+triple.getRdfGraphTriples()+"}\n";
			}
			if(onConflict) {
				if(triple.needDelete()) {
					needDelete=true;
					deleteWhere+=genereteDeleteWhereOf(x,key);
				}
			}
		}
		insertData+="} "+sparql_data_graph+"};\n";
		if(onConflict && needDelete) {
			sparql=deleteWhere;
		}
		sparql+=insertData;
		return sparql;
	}
	public String getTable() {
		return _table;
	}


	
	
	
}
