package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;

public interface IStorageWriterDAO {
	boolean store(String tableName, String columnName, String key, String value);
	
	boolean storeEntity(String key, String value, String valueWithoutSysAttrs, String kvValue)
			throws SQLTransientConnectionException ;
	
	boolean storeTemporalEntity(String key, String value) throws SQLException;
}
