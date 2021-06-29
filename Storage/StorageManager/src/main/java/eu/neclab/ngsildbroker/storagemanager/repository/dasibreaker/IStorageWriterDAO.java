package eu.neclab.ngsildbroker.storagemanager.repository.dasibreaker;

import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;

public interface IStorageWriterDAO {
	boolean store(String tableName, String columnName, String key, String value);
	

	boolean storeTemporalEntity(String key, String value) throws SQLException;
}
