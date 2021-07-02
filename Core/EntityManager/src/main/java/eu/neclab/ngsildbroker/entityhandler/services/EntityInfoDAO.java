package eu.neclab.ngsildbroker.entityhandler.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import eu.neclab.ngsildbroker.commons.storage.StorageReaderDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IEntityInfoDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.QueryLanguageFactory;

@Repository
public class EntityInfoDAO extends StorageReaderDAO {
	
	@Autowired
	QueryLanguageFactory factory;
	
	private IEntityInfoDAO eiDao;
	public EntityInfoDAO() {
//		eiDao= factory.getEntityInfoDAO();
		//-------------------------------------------------------------THIS NEED TO BE FIX
		//-----> Autowired don't work so i take istance manually, that's no good
		eiDao= QueryLanguageFactory.getInstance().getEntityInfoDAO();
	}
	public Set<String> getAllIds() {return eiDao.getAllIds();
	}

	public String getEntity(String entityId) {
		return eiDao.getEntity(entityId);
	}
}
