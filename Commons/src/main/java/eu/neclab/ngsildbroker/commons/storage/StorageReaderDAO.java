package eu.neclab.ngsildbroker.commons.storage;

import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import eu.neclab.ngsildbroker.commons.datatypes.GeoqueryRel;
import eu.neclab.ngsildbroker.commons.datatypes.QueryParams;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.IStorageReaderDao;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.QueryLanguageFactory;

public class StorageReaderDAO implements IStorageReaderDao {

	private final static Logger logger = LogManager.getLogger(StorageReaderDAO.class);

	public static int countHeader = 0;
	
	@Autowired
	QueryLanguageFactory factory;
	
	private IStorageReaderDao srDao;
	public StorageReaderDAO() {
		//-------------------------------------------------------------THIS NEED TO BE FIX
		//-----> Autowired don't work so i take istance manually, that's no good
		srDao= QueryLanguageFactory.getInstance().getStorageReaderDao();
	}
	
	public List<String> getLocalTypes() {
		return srDao.getLocalTypes();
	}
	
	public List<String> getAllTypes() {
		return srDao.getAllTypes();
	}
	
	/*
	 * TODO: optimize sql queries for types and Attributes by using prepared statements (if possible)
	 */
	public String typesAndAttributeQuery(QueryParams qp) throws ResponseException {
		return srDao.typesAndAttributeQuery(qp);
	}


	/*
	 * TODO: optimize sql queries by using prepared statements (if possible)
	 */
	public String translateNgsildQueryToSql(QueryParams qp) throws ResponseException {
		return srDao.translateNgsildQueryToSql(qp);
	}

	// TODO: SQL input sanitization
	// TODO: property of property
	// [SPEC] spec is not clear on how to define a "property of property" in
	// the geoproperty field. (probably using dots)
	public String translateNgsildGeoqueryToPostgisQuery(GeoqueryRel georel, String geometry, String coordinates,
			String geoproperty, String dbColumn) throws ResponseException {
		return srDao.translateNgsildGeoqueryToPostgisQuery(georel, geometry, coordinates, geoproperty);
	}

	public String translateNgsildGeoqueryToPostgisQuery(GeoqueryRel georel, String geometry, String coordinates,
			String geoproperty) throws ResponseException {
		return srDao.translateNgsildGeoqueryToPostgisQuery(georel,geometry,coordinates,geoproperty);
	}

	@Override
	public void init() {
		srDao.init();
	}

	@Override
	public List<String> query(QueryParams qp) {
		// TODO Auto-generated method stub
		return	srDao.query(qp);
	}

	@Override
	public String getListAsJsonArray(List<String> s) {
		// TODO Auto-generated method stub
		return	srDao.getListAsJsonArray(s);
	}

}
