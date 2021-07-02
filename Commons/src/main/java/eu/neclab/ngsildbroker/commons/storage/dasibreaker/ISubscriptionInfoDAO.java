package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.util.Map;
import java.util.Set;

public interface ISubscriptionInfoDAO {
	public Set<String> getAllIds() ;
	public Map<String, String> getIds2Type();
	public String getEntity(String entityId);
}
