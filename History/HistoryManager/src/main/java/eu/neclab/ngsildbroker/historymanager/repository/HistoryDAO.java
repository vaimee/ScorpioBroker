package eu.neclab.ngsildbroker.historymanager.repository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import eu.neclab.ngsildbroker.commons.datatypes.QueryParams;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;
import eu.neclab.ngsildbroker.commons.storage.StorageReaderDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IHistoryDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.QueryLanguageFactory;

@Repository
public class HistoryDAO extends StorageReaderDAO {

	protected final static Logger logger = LoggerFactory.getLogger(HistoryDAO.class);
	@Autowired
	QueryLanguageFactory factory;
	private IHistoryDAO hDao;
	public HistoryDAO() {
//		hDao= factory.getHistoryDAO();
		//-------------------------------------------------------------THIS NEED TO BE FIX
		//-----> Autowired don't work so i take istance manually, that's no good
		hDao= QueryLanguageFactory.getInstance().getHistoryDAO();
	}
	@Override
	public String translateNgsildQueryToSql(QueryParams qp) throws ResponseException {
		return hDao.translateNgsildQueryToSql(qp);
	}

	

	protected String translateNgsildTimequeryToSql(String timerel, String time, String timeproperty, String endTime,
			String dbPrefix) throws ResponseException {
		return hDao.translateNgsildTimequeryToSql(timerel, time, timeproperty, endTime,
				dbPrefix);
	}

	public boolean entityExists(String entityId) {
		return hDao.entityExists(entityId);
	}

}
