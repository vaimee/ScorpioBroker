package eu.neclab.ngsildbroker.commons.storage.dasibreaker.test;

import static org.junit.Assert.assertTrue;



import org.junit.Test;

import com.apicatalog.jsonld.JsonLdError;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.ConverterJRDF;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SepaGateway;
import it.unibo.arces.wot.sepa.commons.response.QueryResponse;

public class JSONfromToRDFTest{


	

	@Test
	public void testJSONLDtoRDFtoJSONLD() throws JsonLdError{
		System.out.println("---------------------------------TEST: testJSONLDtoRDFtoJSONLD");
		String jsonld = "{\n"
				+ "    \"id\": \"urn:ngsi-ld:Building:storeProva94\",\n"
				+ "    \"type\": \"Building\",\n"
				+ "    \"category\": {\n"
				+ "    	\"type\": \"Property\",\n"
				+ "        \"value\": [\"commercial\"]\n"
				+ "    },\n"
				+ "    \"address\": {\n"
				+ "        \"type\": \"Property\",\n"
				+ "        \"value\": {\n"
				+ "            \"streetAddress\": \"Friedrichstraße 44\",\n"
				+ "            \"addressRegion\": \"Berlin\",\n"
				+ "            \"addressLocality\": \"Kreuzberg\",\n"
				+ "            \"postalCode\": \"10969\"\n"
				+ "        },\n"
				+ "        \"verified\": {\n"
				+ "			\"type\": \"Property\",\n"
				+ "			\"value\": true\n"
				+ "		}\n"
				+ "    },\n"
				+ "     \"location\": {\n"
				+ "        \"type\": \"GeoProperty\",\n"
				+ "        \"value\": {\n"
				+ "             \"type\": \"Point\",\n"
				+ "              \"coordinates\": [13.3903, 52.5075]\n"
				+ "        }\n"
				+ "    },\n"
				+ "    \"name\": {\n"
				+ "        \"type\": \"Property\",\n"
				+ "        \"value\": \"Checkpoint Markt\"\n"
				+ "    },\n"
				+ "    \"@context\": [\n"
				+ "        \"https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld\"\n"
				+ "    ]\n"
				+ "}";
	
		try {
			System.out.println("rdf test result-->"+new ConverterJRDF().JSONtoRDF(jsonld));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


	@Test
	public void fullChain() throws JsonLdError{
		System.out.println("---------------------------------TEST: fullChain");
		String jsonld = "{\n"
				+ "    \"id\": \"urn:ngsi-ld:Building:storeProva94\",\n"
				+ "    \"type\": \"Building\",\n"
				+ "    \"category\": {\n"
				+ "    	\"type\": \"Property\",\n"
				+ "        \"value\": [\"commercial\"]\n"
				+ "    },\n"
				+ "    \"address\": {\n"
				+ "        \"type\": \"Property\",\n"
				+ "        \"value\": {\n"
				+ "            \"streetAddress\": \"Friedrichstraße 44\",\n"
				+ "            \"addressRegion\": \"Berlin\",\n"
				+ "            \"addressLocality\": \"Kreuzberg\",\n"
				+ "            \"postalCode\": \"10969\"\n"
				+ "        },\n"
				+ "        \"verified\": {\n"
				+ "			\"type\": \"Property\",\n"
				+ "			\"value\": true\n"
				+ "		}\n"
				+ "    },\n"
				+ "     \"location\": {\n"
				+ "        \"type\": \"GeoProperty\",\n"
				+ "        \"value\": {\n"
				+ "             \"type\": \"Point\",\n"
				+ "              \"coordinates\": [13.3903, 52.5075]\n"
				+ "        }\n"
				+ "    },\n"
				+ "    \"name\": {\n"
				+ "        \"type\": \"Property\",\n"
				+ "        \"value\": \"Checkpoint Markt\"\n"
				+ "    },\n"
				+ "    \"@context\": [\n"
				+ "        \"https://fiware.github.io/data-models/context.jsonld\",\n"
				+ "        \"https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld\"\n"
				+ "    ]\n"
				+ "}";
	
		try {
			String turtle = new ConverterJRDF().JSONtoRDF(jsonld);
			String sparql = "DELETE WHERE {GRAPH <g> {?s ?p ?o}};INSERT DATA { GRAPH <g> {"+turtle+"}}";
			SepaGateway sg = new SepaGateway();
			assertTrue("INSERT DATA",!sg.executeUpdate(sparql).isError());
			sparql="SELECT ?e ?s ?p ?o {GRAPH <g> { ?s ?p ?o} GRAPH ?e { ?s ?p ?o}}";
			QueryResponse qr = (QueryResponse)sg.executeQuery(sparql);
			System.out.println("fullChain test result-->"+new ConverterJRDF().RDFtoJson(qr.getBindingsResults().getBindings()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void fullChain2() throws Exception{
		System.out.println("---------------------------------TEST: fullChain2");
		String jsonld = "{\"https://schema.org/address\":[{\"@type\":[\"https://uri.etsi.org/ngsi-ld/Property\"],\"https://uri.etsi.org/ngsi-ld/hasValue\":[{\"https://uri.etsi.org/ngsi-ld/default-context/addressLocality\":[{\"@value\":\"Kreuzberg\"}],\"https://uri.etsi.org/ngsi-ld/default-context/addressRegion\":[{\"@value\":\"Berlin\"}],\"https://uri.etsi.org/ngsi-ld/default-context/postalCode\":[{\"@value\":\"10969\"}],\"https://uri.etsi.org/ngsi-ld/default-context/streetAddress\":[{\"@value\":\"Friedrichstraße 44\"}]}],\"https://uri.etsi.org/ngsi-ld/default-context/verified\":[{\"@type\":[\"https://uri.etsi.org/ngsi-ld/Property\"],\"https://uri.etsi.org/ngsi-ld/hasValue\":[{\"@value\":true}],\"https://uri.etsi.org/ngsi-ld/createdAt\":[{\"@type\":\"https://uri.etsi.org/ngsi-ld/DateTime\",\"@value\":\"2021-07-26T09:56:23.236062Z\"}],\"https://uri.etsi.org/ngsi-ld/modifiedAt\":[{\"@type\":\"https://uri.etsi.org/ngsi-ld/DateTime\",\"@value\":\"2021-07-26T09:56:23.236062Z\"}]}],\"https://uri.etsi.org/ngsi-ld/createdAt\":[{\"@type\":\"https://uri.etsi.org/ngsi-ld/DateTime\",\"@value\":\"2021-07-26T09:56:23.236062Z\"}],\"https://uri.etsi.org/ngsi-ld/modifiedAt\":[{\"@type\":\"https://uri.etsi.org/ngsi-ld/DateTime\",\"@value\":\"2021-07-26T09:56:23.236062Z\"}]}],\"https://uri.fiware.org/ns/data-models#category\":[{\"@type\":[\"https://uri.etsi.org/ngsi-ld/Property\"],\"https://uri.etsi.org/ngsi-ld/hasValue\":[{\"@value\":\"commercial\"}],\"https://uri.etsi.org/ngsi-ld/createdAt\":[{\"@type\":\"https://uri.etsi.org/ngsi-ld/DateTime\",\"@value\":\"2021-07-26T09:56:23.236062Z\"}],\"https://uri.etsi.org/ngsi-ld/modifiedAt\":[{\"@type\":\"https://uri.etsi.org/ngsi-ld/DateTime\",\"@value\":\"2021-07-26T09:56:23.236062Z\"}]}],\"@id\":\"urn:ngsi-ld:Building:storeProva7799\",\"https://uri.etsi.org/ngsi-ld/location\":[{\"@type\":[\"https://uri.etsi.org/ngsi-ld/GeoProperty\"],\"https://uri.etsi.org/ngsi-ld/hasValue\":[{\"@value\":\"{\\\"type\\\":\\\"Point\\\",\\\"coordinates\\\":[13.3903,52.5075]}\"}],\"https://uri.etsi.org/ngsi-ld/createdAt\":[{\"@type\":\"https://uri.etsi.org/ngsi-ld/DateTime\",\"@value\":\"2021-07-26T09:56:23.236062Z\"}],\"https://uri.etsi.org/ngsi-ld/modifiedAt\":[{\"@type\":\"https://uri.etsi.org/ngsi-ld/DateTime\",\"@value\":\"2021-07-26T09:56:23.236062Z\"}]}],\"https://uri.etsi.org/ngsi-ld/name\":[{\"@type\":[\"https://uri.etsi.org/ngsi-ld/Property\"],\"https://uri.etsi.org/ngsi-ld/hasValue\":[{\"@value\":\"Checkpoint Markt\"}],\"https://uri.etsi.org/ngsi-ld/createdAt\":[{\"@type\":\"https://uri.etsi.org/ngsi-ld/DateTime\",\"@value\":\"2021-07-26T09:56:23.236062Z\"}],\"https://uri.etsi.org/ngsi-ld/modifiedAt\":[{\"@type\":\"https://uri.etsi.org/ngsi-ld/DateTime\",\"@value\":\"2021-07-26T09:56:23.236062Z\"}]}],\"@type\":[\"https://uri.fiware.org/ns/data-models#Building\"],\"https://uri.etsi.org/ngsi-ld/createdAt\":[{\"@type\":\"https://uri.etsi.org/ngsi-ld/DateTime\",\"@value\":\"2021-07-26T09:56:23.236062Z\"}],\"https://uri.etsi.org/ngsi-ld/modifiedAt\":[{\"@type\":\"https://uri.etsi.org/ngsi-ld/DateTime\",\"@value\":\"2021-07-26T09:56:23.236062Z\"}]}\n";
		String turtle = new ConverterJRDF().JSONtoRDF(jsonld);
//		System.out.println("-------------turtle:\n"+turtle); //ok
		try {
			String sparql = "DELETE WHERE {GRAPH <g2> {?s ?p ?o}};INSERT DATA { GRAPH <g2> {"+turtle+"}}";
			SepaGateway sg = new SepaGateway();
			assertTrue("INSERT DATA",!sg.executeUpdate(sparql).isError());
			sparql="SELECT ?e ?s ?p ?o {GRAPH <g2> { ?s ?p ?o} GRAPH ?e { ?s ?p ?o}}";
			QueryResponse qr = (QueryResponse)sg.executeQuery(sparql);
			System.out.println("fullChain test result-->"+new ConverterJRDF().RDFtoJson(qr.getBindingsResults().getBindings()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
}
