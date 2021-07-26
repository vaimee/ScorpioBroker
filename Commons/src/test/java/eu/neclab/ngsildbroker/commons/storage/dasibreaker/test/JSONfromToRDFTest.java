package eu.neclab.ngsildbroker.commons.storage.dasibreaker.test;

import static org.junit.Assert.assertTrue;



import org.junit.Test;

import com.apicatalog.jsonld.JsonLdError;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.JSONfromToRDF;
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
			System.out.println("rdf test result-->"+new JSONfromToRDF().JSONtoRDF(jsonld));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


	@Test
	public void fullChain() throws JsonLdError{
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
			String turtle = new JSONfromToRDF().JSONtoRDF(jsonld);
			String sparql = "DELETE WHERE {GRAPH <g> {?s ?p ?o}};INSERT DATA { GRAPH <g> {"+turtle+"}}";
			SepaGateway sg = new SepaGateway();
			assertTrue("INSERT DATA",!sg.executeUpdate(sparql).isError());
			sparql="SELECT ?s ?p ?o {GRAPH <g> { ?s ?p ?o}}";
			QueryResponse qr = (QueryResponse)sg.executeQuery(sparql);
			System.out.println("fullChain test result-->"+new JSONfromToRDF().RDFtoJson(qr.getBindingsResults().getBindings()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
}
