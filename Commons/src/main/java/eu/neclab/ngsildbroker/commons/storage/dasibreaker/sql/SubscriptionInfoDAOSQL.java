package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import eu.neclab.ngsildbroker.commons.storage.dasibreaker.ISubscriptionInfoDAO;

@Repository
public class SubscriptionInfoDAOSQL extends StorageReaderDAOSQL  implements ISubscriptionInfoDAO{

	private final static Logger logger = LogManager.getLogger(SubscriptionInfoDAOSQL.class);

	public Set<String> getAllIds() {
		logger.info("\ncall on DAO ====> SubscriptionInfoDAOSQL.getAllIds <====\n");
		List<String> tempList = readerJdbcTemplate.queryForList("SELECT id FROM entity", String.class);
		return new HashSet<String>(tempList);
	}
	public Map<String, String> getIds2Type() {
		logger.info("\ncall on DAO ====> SubscriptionInfoDAOSQL.getIds2Type <====\n");
		List<Map<String, Object>> temp = readerJdbcTemplate.queryForList("SELECT id, type FROM entity");
		HashMap<String, String> result = new HashMap<String, String>();
		for(Map<String, Object> entry: temp) {
			result.put(entry.get("id").toString(), entry.get("type").toString());
		}
		return result;
	}
	public String getEntity(String entityId) {
		logger.info("\ncall on DAO ====> SubscriptionInfoDAOSQL.getEntity <====\n");
		List<String> tempList = readerJdbcTemplate.queryForList("SELECT data FROM entity WHERE id='" + entityId + "'", String.class);
		return tempList.get(0);
	}
}
