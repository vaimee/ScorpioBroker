package eu.neclab.ngsildbroker.queryhandler.repository;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import eu.neclab.ngsildbroker.commons.datatypes.GeoqueryRel;
import eu.neclab.ngsildbroker.commons.datatypes.QueryParams;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;
import eu.neclab.ngsildbroker.commons.storage.StorageReaderDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.ICSourceDAO;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.QueryLanguageFactory;

@Repository("qmcsourcedao")
public class CSourceDAO extends StorageReaderDAO {
	
	@Autowired
	QueryLanguageFactory factory;
	
	private ICSourceDAO csDao;
	public CSourceDAO() {
//		csDao= factory.getCSourceDAO();
		//-------------------------------------------------------------THIS NEED TO BE FIX
		//-----> Autowired don't work so i take istance manually, that's no good
		csDao= QueryLanguageFactory.getInstance().getCSourceDAO();
	}
	
	@Override
	public List<String> query(QueryParams qp) {
		return csDao.query(qp);
	}
	
	public List<String> queryExternalCsources(QueryParams qp) throws SQLException {
		return csDao.query(qp);
	}

	@Override
	public String translateNgsildQueryToSql(QueryParams qp) throws ResponseException {
		return csDao.translateNgsildQueryToSql(qp);
	}

	
	
	// TODO: SQL input sanitization
	// TODO: property of property
	// TODO: [SPEC] spec is not clear on how to define a "property of property" in
	// the geoproperty field. (probably using dots, but...)
	@Override
	public String translateNgsildGeoqueryToPostgisQuery(GeoqueryRel georel, String geometry, String coordinates,
			String geoproperty) throws ResponseException {
		return csDao.translateNgsildGeoqueryToPostgisQuery(georel,geometry,coordinates,geoproperty);
	}

}
