package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql;


import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import eu.neclab.ngsildbroker.commons.storage.dasibreaker.ISubscriptionInfoDAO;

@Repository
public class SubscriptionInfoDAOSPARQL extends StorageReaderDAOSPARQL  implements ISubscriptionInfoDAO{
	public Set<String> getAllIds() {
		return null;
	}
	public Map<String, String> getIds2Type() {
		return null;
	}
	public String getEntity(String entityId) {
		return null;
	}
}
