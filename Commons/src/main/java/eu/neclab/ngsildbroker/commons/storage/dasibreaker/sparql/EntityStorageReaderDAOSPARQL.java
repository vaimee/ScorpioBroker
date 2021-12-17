package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql;


import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IEntityStorageReaderDAO;

//@Repository
//@ConditionalOnProperty(value="reader.enabled", havingValue = "true", matchIfMissing = false)
public class EntityStorageReaderDAOSPARQL extends StorageReaderDAOSPARQL  implements IEntityStorageReaderDAO {
	
	public Long getLocalEntitiesCount() {
//		System.out.println("\n\n POS-->STORAGE.STORAGEMANGER.eu.neclab.ngsildbroker.storagemanager.repository.EntityStorageReaderDAO.getLocalEntitiesCount\n\n");
//		List<Map<String, Object>> list = readerJdbcTemplate.queryForList(
//				"SELECT count(id) FROM entity;");
//		if(list == null ||list.isEmpty()) {
//			return null;
//		}
//		return (Long) list.get(0).get("count");
		return null;

	}
	public Long getLocalTypesCount() {
//		System.out.println("\n\n POS-->STORAGE.STORAGEMANGER.eu.neclab.ngsildbroker.storagemanager.repository.EntityStorageReaderDAO.getLocalTypesCount\n\n");
//		List<Map<String, Object>> list = readerJdbcTemplate.queryForList(
//				"SELECT count(distinct(type)) FROM entity;");
//		if(list == null ||list.isEmpty()) {
//			return null;
//		}
//		return (Long) list.get(0).get("count");
		return null;

	}
}
