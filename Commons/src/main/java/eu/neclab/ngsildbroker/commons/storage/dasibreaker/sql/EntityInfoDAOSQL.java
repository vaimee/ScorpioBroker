package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sql;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IEntityInfoDAO;

@Repository
public class EntityInfoDAOSQL extends StorageReaderDAOSQL implements IEntityInfoDAO{

	private final static Logger logger = LogManager.getLogger(EntityInfoDAOSQL.class);
	
	public Set<String> getAllIds() {
		logger.info("\ncall on DAO ====> EntityInfoDAOSQL.getAllIds <====\n");
		List<String> tempList = readerJdbcTemplate.queryForList("SELECT id FROM entity", String.class);
		return new HashSet<String>(tempList);
	}

	public String getEntity(String entityId) {
		logger.info("\ncall on DAO ====> EntityInfoDAOSQL.getEntity <====\n");
		List<String> tempList = readerJdbcTemplate.queryForList("SELECT data FROM entity WHERE id='" + entityId + "'", String.class);
		return tempList.get(0);
	}
}
