package eu.neclab.ngsildbroker.storagemanager.repository;

import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IStorageWriterDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.QueryLanguageFactory;

@Repository
@ConditionalOnProperty(value="writer.enabled", havingValue = "true", matchIfMissing = false)
public class StorageWriterDAO implements IStorageWriterDAO {
	
	private final static Logger logger = LogManager.getLogger(StorageWriterDAO.class);
//	public static final Gson GSON = DataSerializer.GSON;

//    @Autowired
//	private JdbcTemplate writerJdbcTemplate;
//    
//    @Autowired
//	private DataSource writerDataSource;
//    
//	private TransactionTemplate writerTransactionTemplate;
//	private JdbcTemplate writerJdbcTemplateWithTransaction; 
	@Autowired
	QueryLanguageFactory factory;
	
	private IStorageWriterDAO swdao;
	@PostConstruct
	public void init() {		
//        writerJdbcTemplate.execute("SELECT 1"); // create connection pool and connect to database     
//		// https://gist.github.com/mdellabitta/1444003
//		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(writerDataSource);
//        writerJdbcTemplateWithTransaction = new JdbcTemplate(transactionManager.getDataSource());
//        writerTransactionTemplate = new TransactionTemplate(transactionManager);
        swdao =factory.getStorageWriterDao();
	}
	
	public boolean store(String tableName, String columnName, String key, String value) {
		return swdao.store(tableName, columnName, key, value);
	}
	
	public boolean storeTemporalEntity(String key, String value) throws SQLException {
		return swdao.storeTemporalEntity(key, value);
	}

	@Override
	public boolean storeEntity(String key, String value, String valueWithoutSysAttrs, String kvValue)
			throws SQLTransientConnectionException {
		// TODO Auto-generated method stub
		return swdao.storeEntity(key, value,valueWithoutSysAttrs,kvValue);
	}

	
}
