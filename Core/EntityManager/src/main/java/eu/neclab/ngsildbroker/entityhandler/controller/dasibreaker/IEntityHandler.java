package eu.neclab.ngsildbroker.entityhandler.controller.dasibreaker;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;

public interface IEntityHandler {
	
	public ResponseEntity<byte[]> createEntity(HttpServletRequest request,
			@RequestBody(required = false) String payload) 
				throws ResponseException, MalformedURLException, UnsupportedEncodingException ,Exception;
	
	public ResponseEntity<byte[]> updateEntity(HttpServletRequest request, @RequestBody String payload)
			throws ResponseException, MalformedURLException, UnsupportedEncodingException ,Exception;

	public ResponseEntity<byte[]> appendEntity(HttpServletRequest request, @RequestBody String payload,
			@RequestParam(required = false, name = "options") String options)
					throws ResponseException, MalformedURLException, UnsupportedEncodingException ,Exception;
	
	public ResponseEntity<byte[]> partialUpdateEntity(HttpServletRequest request, @RequestBody String payload)
			throws ResponseException, MalformedURLException, UnsupportedEncodingException ,Exception;
	
	
	public ResponseEntity<byte[]> deleteAttribute(HttpServletRequest request,
			@RequestParam(value = "datasetId", required = false) String datasetId,
			@RequestParam(value = "deleteAll", required = false) String deleteAll) 
					throws ResponseException, MalformedURLException, UnsupportedEncodingException ,Exception;
	
	public ResponseEntity<byte[]> deleteEntity(HttpServletRequest request)
			throws ResponseException, MalformedURLException, UnsupportedEncodingException ,Exception;
	
	
}