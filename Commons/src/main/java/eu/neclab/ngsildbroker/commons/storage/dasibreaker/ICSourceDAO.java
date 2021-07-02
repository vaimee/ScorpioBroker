package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.sql.SQLException;
import java.util.List;

import eu.neclab.ngsildbroker.commons.datatypes.GeoqueryRel;
import eu.neclab.ngsildbroker.commons.datatypes.QueryParams;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;

public interface ICSourceDAO {

	
	public List<String> query(QueryParams qp);
	public List<String> queryExternalCsources(QueryParams qp) throws SQLException;
	public String translateNgsildQueryToSql(QueryParams qp) throws ResponseException;
	public String translateNgsildGeoqueryToPostgisQuery(GeoqueryRel georel, String geometry, String coordinates,
			String geoproperty) throws ResponseException;
	
}
