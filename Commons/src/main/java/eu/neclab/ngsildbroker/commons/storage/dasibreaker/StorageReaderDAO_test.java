package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ReflectionUtils;
import eu.neclab.ngsildbroker.commons.constants.DBConstants;
import eu.neclab.ngsildbroker.commons.constants.NGSIConstants;
import eu.neclab.ngsildbroker.commons.datatypes.GeoqueryRel;
import eu.neclab.ngsildbroker.commons.datatypes.QueryParams;
import eu.neclab.ngsildbroker.commons.enums.ErrorType;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;
import eu.neclab.ngsildbroker.commons.storage.StorageReaderDAO;

/*
 * This class is a wrapper for the real dao objs
 */
abstract public class StorageReaderDAO_test {

	private final static Logger logger = LogManager.getLogger(StorageReaderDAO.class);
	
	//this boolean is temporary, just for switch between SQL-only and SQL+SPARQL behaviour
	private final static boolean enableSparql=true;
	//need refactoring for these DAO obj... need to be like an real DAO pattern
	private StorageReaderDAOsql sqlDAO;
	private StorageReaderDAOsparql sparqlDAO;
	
//	@Autowired
//	protected JdbcTemplate readerJdbcTemplate;
//
//	public Random random=new Random();
//
//	public static int countHeader = 0;
	
	@PostConstruct
	public void init() {
		sqlDAO=new StorageReaderDAOsql();
		sqlDAO.init();
		if(enableSparql) {
			sparqlDAO= new StorageReaderDAOsparql();
		}
	}

	
	public List<String> query(QueryParams qp) {
		return sqlDAO.query(qp);
	}

	public String getListAsJsonArray(List<String> s) {
		return sqlDAO.getListAsJsonArray(s);
	}

	public List<String> getLocalTypes() {
		return sqlDAO.getLocalTypes();
	}
	
	public List<String> getAllTypes() {
		return sqlDAO.getAllTypes();
	}
	
	protected String typesAndAttributeQuery(QueryParams qp) throws ResponseException {
		return sqlDAO.typesAndAttributeQuery(qp);
	}


	protected String translateNgsildQueryToSql(QueryParams qp) throws ResponseException {
		return sqlDAO.translateNgsildQueryToSql(qp);
	}

	protected String translateNgsildGeoqueryToPostgisQuery(GeoqueryRel georel, String geometry, String coordinates,
			String geoproperty, String dbColumn) throws ResponseException {
		return sqlDAO.translateNgsildGeoqueryToPostgisQuery(georel,geometry,coordinates,geoproperty,dbColumn);
	}

	protected String translateNgsildGeoqueryToPostgisQuery(GeoqueryRel georel, String geometry, String coordinates,
			String geoproperty) throws ResponseException {
		return sqlDAO.translateNgsildGeoqueryToPostgisQuery(georel,geometry,coordinates,geoproperty);
	}

}
