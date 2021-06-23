package eu.neclab.ngsildbroker.entityhandler.controller.dasibreaker;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.neclab.ngsildbroker.commons.constants.AppConstants;
import eu.neclab.ngsildbroker.commons.datatypes.AppendResult;
import eu.neclab.ngsildbroker.commons.datatypes.UpdateResult;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;
import eu.neclab.ngsildbroker.commons.ngsiqueries.ParamsResolver;
import eu.neclab.ngsildbroker.commons.tools.HttpUtils;
import eu.neclab.ngsildbroker.entityhandler.services.EntityService;
import eu.neclab.ngsildbroker.entityhandler.validationutil.Validator;

/**
 * 
 * @version 1.0
 * @date 10-Jul-2018
 */

public class EntityControllerNGSI implements IEntityHandler {

	private final static Logger logger = LoggerFactory.getLogger(EntityControllerNGSI.class);

	
	private EntityService entityService;
	private ObjectMapper objectMapper;
	private ParamsResolver paramsResolver;
	private HttpUtils httpUtils;
	
	public EntityControllerNGSI(EntityService entityService, ObjectMapper objectMapper, ParamsResolver paramsResolver,
			HttpUtils httpUtils) {
		super();
		this.entityService = entityService;
		this.objectMapper = objectMapper;
		this.paramsResolver = paramsResolver;
		this.httpUtils = httpUtils;
	}


	LocalDateTime start;
	LocalDateTime end;

	

	/**
	 * Method(POST) for "/ngsi-ld/v1/entities/" rest endpoint.
	 * 
	 * @param payload jsonld message
	 * @return ResponseEntity object
	 * @throws ResponseException 
	 * @throws UnsupportedEncodingException 
	 * @throws MalformedURLException 
	 */
	//@PostMapping
	public ResponseEntity<byte[]> createEntity(HttpServletRequest request,
			@RequestBody(required = false) String payload) 
					throws ResponseException, MalformedURLException, UnsupportedEncodingException , Exception {
		
			String result = null;
			HttpUtils.doPreflightCheck(request, payload);
			logger.trace("create entity :: started");
			String resolved = httpUtils.expandPayload(request, payload, AppConstants.ENTITIES_URL_ID);
			// entityService.validateEntity(resolved, request);

			result = entityService.createMessage(resolved);
			logger.trace("create entity :: completed");
			return ResponseEntity.status(HttpStatus.CREATED).header("location", AppConstants.ENTITES_URL + result).build();
		
	}

	/**
	 * Method(PATCH) for "/ngsi-ld/v1/entities/{entityId}/attrs" rest endpoint.
	 * 
	 * @param entityId
	 * @param payload  json ld message
	 * @return ResponseEntity object
	 * @throws ResponseException 
	 * @throws UnsupportedEncodingException 
	 * @throws MalformedURLException 
	 */
	//@PatchMapping("/**/attrs")
	public ResponseEntity<byte[]> updateEntity(HttpServletRequest request, @RequestBody String payload) 
			throws ResponseException, MalformedURLException, UnsupportedEncodingException, Exception {
		// String resolved = contextResolver.resolveContext(payload);
			HttpUtils.doPreflightCheck(request, payload);
			String[] split = request.getServletPath().replace("/ngsi-ld/v1/entities/", "").split("/attrs");
			String entityId = HttpUtils.denormalize(split[0]);
			logger.trace("update entity :: started");
			String resolved = httpUtils.expandPayload(request, payload, AppConstants.ENTITIES_URL_ID);

			UpdateResult update = entityService.updateMessage(entityId, resolved);
			logger.trace("update entity :: completed");
			if (update.getUpdateResult()) {
				return ResponseEntity.noContent().build();
			} else {
				return ResponseEntity.status(HttpStatus.MULTI_STATUS)
						.body(objectMapper.writeValueAsBytes(update.getAppendedJsonFields()));
			}
		
	}

	/**
	 * Method(POST) for "/ngsi-ld/v1/entities/{entityId}/attrs" rest endpoint.
	 * 
	 * @param entityId
	 * @param payload  jsonld message
	 * @return ResponseEntity object
	 */
	//@PostMapping("/**/attrs")
	public ResponseEntity<byte[]> appendEntity(HttpServletRequest request, @RequestBody String payload,
			@RequestParam(required = false, name = "options") String options) 
					throws ResponseException, MalformedURLException, UnsupportedEncodingException, Exception {
		// String resolved = contextResolver.resolveContext(payload);
			HttpUtils.doPreflightCheck(request, payload);
			String[] split = request.getServletPath().replace("/ngsi-ld/v1/entities/", "").split("/attrs");
			String entityId = HttpUtils.denormalize(split[0]);

			logger.trace("append entity :: started");
			String resolved = httpUtils.expandPayload(request, payload, AppConstants.ENTITIES_URL_ID);

			AppendResult append = entityService.appendMessage(entityId, resolved, options);
			logger.trace("append entity :: completed");
			if (append.getAppendResult()) {
				return ResponseEntity.noContent().build();
			} else {
				return ResponseEntity.status(HttpStatus.MULTI_STATUS)
						.body(objectMapper.writeValueAsBytes(append.getAppendedJsonFields()));
			}
		
	}

	/**
	 * Method(PATCH) for "/ngsi-ld/v1/entities/{entityId}/attrs/{attrId}" rest
	 * endpoint.
	 * 
	 * @param entityId
	 * @param attrId
	 * @param payload
	 * @return
	 */
	//@PatchMapping("/**/attrs/**")
	public ResponseEntity<byte[]> partialUpdateEntity(HttpServletRequest request, @RequestBody String payload) 
			throws ResponseException, MalformedURLException, UnsupportedEncodingException, Exception {
			String[] split = request.getServletPath().replace("/ngsi-ld/v1/entities/", "").split("/attrs/");
			String attrId = HttpUtils.denormalize(split[1]);
			String entityId = HttpUtils.denormalize(split[0]);

			HttpUtils.doPreflightCheck(request, payload);
			logger.trace("partial-update entity :: started");
			String expandedPayload = httpUtils.expandPayload(request, payload, AppConstants.ENTITIES_URL_ID);

			String expandedAttrib = paramsResolver.expandAttribute(attrId, payload, request);

			UpdateResult update = entityService.partialUpdateEntity(entityId, expandedAttrib, expandedPayload);
			logger.trace("partial-update entity :: completed");
			if (update.getStatus()) {
				return ResponseEntity.noContent().build();
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			/*
			 * There is no 207 multi status response in the Partial Attribute Update
			 * operation. Section 6.7.3.1 else { return
			 * ResponseEntity.status(HttpStatus.MULTI_STATUS).body(update.
			 * getAppendedJsonFields()); }
			 */
		
	}

	/**
	 * Method(DELETE) for "/ngsi-ld/v1/entities/{entityId}/attrs/{attrId}" rest
	 * endpoint.
	 * 
	 * @param entityId
	 * @param attrId
	 * @return
	 */
	//@DeleteMapping("/**")
	public ResponseEntity<byte[]> deleteAttribute(HttpServletRequest request,
			@RequestParam(value = "datasetId", required = false) String datasetId,
			@RequestParam(value = "deleteAll", required = false) String deleteAll) 
					throws ResponseException, MalformedURLException, UnsupportedEncodingException, Exception {
			String path = request.getServletPath().replace("/ngsi-ld/v1/entities/", "");
			if (path.contains("/attrs/")) {
				String[] split = path.split("/attrs/");
				String attrId = HttpUtils.denormalize(split[1]);
				String entityId = HttpUtils.denormalize(split[0]);
				logger.trace("delete attribute :: started");
				Validator.validate(request.getParameterMap());
				String expandedAttrib = paramsResolver.expandAttribute(attrId, HttpUtils.getAtContext(request));
				entityService.deleteAttribute(entityId, expandedAttrib, datasetId, deleteAll);
				logger.trace("delete attribute :: completed");
				return ResponseEntity.noContent().build();
			} else {
				return deleteEntity(request);
			}
	
	}

	/**
	 * Method(DELETE) for "/ngsi-ld/v1/entities/{entityId}" rest endpoint.
	 * 
	 * @param entityId
	 * @return
	 */
	public ResponseEntity<byte[]> deleteEntity(HttpServletRequest request) 
			throws ResponseException, MalformedURLException, UnsupportedEncodingException, Exception {
			String entityId = HttpUtils.denormalize(request.getServletPath().replace("/ngsi-ld/v1/entities/", ""));
			logger.trace("delete entity :: started");
			entityService.deleteEntity(entityId);
			logger.trace("delete entity :: completed");
			return ResponseEntity.noContent().build();
		
	}
}
