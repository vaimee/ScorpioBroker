package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import eu.neclab.ngsildbroker.commons.datatypes.QueryParams;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;

public interface IHistoryDAO {
	public String translateNgsildQueryToSql(QueryParams qp) throws ResponseException ;

	public String translateNgsildTimequeryToSql(String timerel, String time, String timeproperty, String endTime,
			String dbPrefix) throws ResponseException;
	public boolean entityExists(String entityId);
}
