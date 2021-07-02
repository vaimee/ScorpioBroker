package eu.neclab.ngsildbroker.entityhandler.services;

import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import eu.neclab.ngsildbroker.commons.constants.DBConstants;
import eu.neclab.ngsildbroker.commons.datatypes.TemporalEntityStorageKey;
import eu.neclab.ngsildbroker.commons.serialization.DataSerializer;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IStorageWriterDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.QueryLanguageFactory;

@Repository("emstorage")
@ConditionalOnProperty(value = "writer.enabled", havingValue = "true", matchIfMissing = false)
public class StorageWriterDAO implements IStorageWriterDAO {

	private final static Logger logger = LogManager.getLogger(StorageWriterDAO.class);
//	public static final Gson GSON = DataSerializer.GSON;

//	@Autowired
//	private JdbcTemplate writerJdbcTemplate;
//
//	@Autowired
//	private DataSource writerDataSource;
//
//	private TransactionTemplate writerTransactionTemplate;
//	private JdbcTemplate writerJdbcTemplateWithTransaction;
	
	@Autowired
	QueryLanguageFactory factory;
	
	private IStorageWriterDAO swdao;
	
	@PostConstruct
	public void init() {
//		writerJdbcTemplate.execute("SELECT 1"); // create connection pool and connect to database
//		// https://gist.github.com/mdellabitta/1444003
//		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(writerDataSource);
//		writerJdbcTemplateWithTransaction = new JdbcTemplate(transactionManager.getDataSource());
//		writerTransactionTemplate = new TransactionTemplate(transactionManager);
		swdao = factory.getStorageWriterDao();
	}

	public boolean store(String tableName, String columnName, String key, String value) {
		return swdao.store(tableName, columnName, key, value);
	}

	public boolean storeEntity(String key, String value, String valueWithoutSysAttrs, String kvValue)
			throws SQLTransientConnectionException {
		return swdao.storeEntity( key, value, valueWithoutSysAttrs, kvValue);
	}

	public boolean storeTemporalEntity(String key, String value) throws SQLException {
		return swdao.storeTemporalEntity(key, value);
	}

}
