package eu.neclab.ngsildbroker.subscriptionmanager.service;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import eu.neclab.ngsildbroker.commons.storage.StorageReaderDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.ISubscriptionInfoDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.QueryLanguageFactory;

@Repository
public class SubscriptionInfoDAO extends StorageReaderDAO {

	@Autowired
	QueryLanguageFactory factory;
	private ISubscriptionInfoDAO siDao;
	public SubscriptionInfoDAO() {
//		siDao= factory.getSubscriptionInfoDAO();

		//-------------------------------------------------------------THIS NEED TO BE FIX
		//-----> Autowired don't work so i take istance manually, that's no good
		siDao= QueryLanguageFactory.getInstance().getSubscriptionInfoDAO();
	}
	public Set<String> getAllIds() {
		return siDao.getAllIds();
	}
	public Map<String, String> getIds2Type() {
		return siDao.getIds2Type();
	}
	public String getEntity(String entityId) {
		return siDao.getEntity(entityId);
	}
}
