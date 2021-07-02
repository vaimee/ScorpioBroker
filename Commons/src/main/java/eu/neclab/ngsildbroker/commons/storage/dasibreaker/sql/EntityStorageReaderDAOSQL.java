package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sql;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IEntityStorageReaderDAO;

//@Repository
//@ConditionalOnProperty(value="reader.enabled", havingValue = "true", matchIfMissing = false)
public class EntityStorageReaderDAOSQL extends StorageReaderDAOSQL  implements IEntityStorageReaderDAO {

	private final static Logger logger = LogManager.getLogger(EntityStorageReaderDAOSQL.class);
	
	public Long getLocalEntitiesCount() {
		logger.info("\ncall on DAO ====> EntityStorageReaderDAOSQL.getLocalEntitiesCount <====\n");
		List<Map<String, Object>> list = readerJdbcTemplate.queryForList(
				"SELECT count(id) FROM entity;");
		if(list == null ||list.isEmpty()) {
			return null;
		}
		return (Long) list.get(0).get("count");

	}
	public Long getLocalTypesCount() {
		logger.info("\ncall on DAO ====> EntityStorageReaderDAOSQL.getLocalTypesCount <====\n");
		List<Map<String, Object>> list = readerJdbcTemplate.queryForList(
				"SELECT count(distinct(type)) FROM entity;");
		if(list == null ||list.isEmpty()) {
			return null;
		}
		return (Long) list.get(0).get("count");

	}
}
