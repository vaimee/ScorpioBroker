package eu.neclab.ngsildbroker.entityhandler.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import eu.neclab.ngsildbroker.commons.storage.StorageReaderDAO;

@Repository
public class EntityInfoDAO extends StorageReaderDAO {
	public Set<String> getAllIds() {
		System.out.println("\n\n POS-->CORE.ENTITYMANAGER.eu.neclab.ngsildbroker.entityhandler.services.EntityInfoDAO\n\n");
		List<String> tempList = readerJdbcTemplate.queryForList("SELECT id FROM entity", String.class);
		return new HashSet<String>(tempList);
	}

	public String getEntity(String entityId) {
		System.out.println("\n\n POS-->CORE.ENTITYMANAGER.eu.neclab.ngsildbroker.entityhandler.services.getEntity\n\n");
		List<String> tempList = readerJdbcTemplate.queryForList("SELECT data FROM entity WHERE id='" + entityId + "'", String.class);
		return tempList.get(0);
	}
}
