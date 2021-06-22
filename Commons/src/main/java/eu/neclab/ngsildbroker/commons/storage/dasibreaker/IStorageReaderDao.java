package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.util.List;

import eu.neclab.ngsildbroker.commons.datatypes.GeoqueryRel;
import eu.neclab.ngsildbroker.commons.datatypes.QueryParams;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;

public interface IStorageReaderDao {
	
	 void init();
	 List<String> query(QueryParams qp);
	 String getListAsJsonArray(List<String> s);
	 List<String> getLocalTypes();
	 List<String> getAllTypes();
	 String typesAndAttributeQuery(QueryParams qp) throws ResponseException;
	 String translateNgsildQueryToSql(QueryParams qp) throws ResponseException;
	 String translateNgsildGeoqueryToPostgisQuery(GeoqueryRel georel, String geometry, String coordinates,
			String geoproperty, String dbColumn) throws ResponseException;
	 String translateNgsildGeoqueryToPostgisQuery(GeoqueryRel georel, String geometry, String coordinates,
			String geoproperty) throws ResponseException;
	

}
