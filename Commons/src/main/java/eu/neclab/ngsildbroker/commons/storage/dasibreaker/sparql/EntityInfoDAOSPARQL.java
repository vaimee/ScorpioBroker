package eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import eu.neclab.ngsildbroker.commons.storage.StorageReaderDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IEntityInfoDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.QueryLanguageFactory;

@Repository
public class EntityInfoDAOSPARQL extends StorageReaderDAOSPARQL implements IEntityInfoDAO {
	public Set<String> getAllIds() {
		return new HashSet<String>();
	}

	public String getEntity(String entityId) {
		return null;
	}
}
