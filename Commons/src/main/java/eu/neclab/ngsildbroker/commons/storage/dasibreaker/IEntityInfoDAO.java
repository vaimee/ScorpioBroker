package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.util.Set;

public interface IEntityInfoDAO {
	public Set<String> getAllIds() ;

	public String getEntity(String entityId);
	
	
}
