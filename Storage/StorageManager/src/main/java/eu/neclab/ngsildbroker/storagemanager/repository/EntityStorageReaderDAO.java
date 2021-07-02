package eu.neclab.ngsildbroker.storagemanager.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import eu.neclab.ngsildbroker.commons.storage.StorageReaderDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IEntityStorageReaderDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.QueryLanguageFactory;

@Repository
@ConditionalOnProperty(value="reader.enabled", havingValue = "true", matchIfMissing = false)
public class EntityStorageReaderDAO extends StorageReaderDAO {
	private IEntityStorageReaderDAO esrDAO;
	@Autowired
	QueryLanguageFactory factory;
	public EntityStorageReaderDAO() {
//		esrDAO= factory.getEntityStorageReaderDAO();

		//-------------------------------------------------------------THIS NEED TO BE FIX
		//-----> Autowired don't work so i take istance manually, that's no good
		esrDAO= QueryLanguageFactory.getInstance().getEntityStorageReaderDAO();
	}
	
	public Long getLocalEntitiesCount() {
		return esrDAO.getLocalEntitiesCount();

	}
	public Long getLocalTypesCount() {
		return esrDAO.getLocalTypesCount();
	}


}
