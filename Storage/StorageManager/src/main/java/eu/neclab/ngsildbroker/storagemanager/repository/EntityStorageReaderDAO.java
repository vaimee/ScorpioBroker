package eu.neclab.ngsildbroker.storagemanager.repository;

import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import eu.neclab.ngsildbroker.commons.storage.StorageReaderDAO;

@Repository
@ConditionalOnProperty(value="reader.enabled", havingValue = "true", matchIfMissing = false)
public class EntityStorageReaderDAO extends StorageReaderDAO {
	
	public Long getLocalEntitiesCount() {
		System.out.println("\n\n POS-->STORAGE.STORAGEMANGER.eu.neclab.ngsildbroker.storagemanager.repository.EntityStorageReaderDAO.getLocalEntitiesCount\n\n");
		List<Map<String, Object>> list = readerJdbcTemplate.queryForList(
				"SELECT count(id) FROM entity;");
		if(list == null ||list.isEmpty()) {
			return null;
		}
		return (Long) list.get(0).get("count");

	}
	public Long getLocalTypesCount() {
		System.out.println("\n\n POS-->STORAGE.STORAGEMANGER.eu.neclab.ngsildbroker.storagemanager.repository.EntityStorageReaderDAO.getLocalTypesCount\n\n");
		List<Map<String, Object>> list = readerJdbcTemplate.queryForList(
				"SELECT count(distinct(type)) FROM entity;");
		if(list == null ||list.isEmpty()) {
			return null;
		}
		return (Long) list.get(0).get("count");

	}
}
