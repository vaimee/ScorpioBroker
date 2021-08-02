package eu.neclab.ngsildbroker.commons.storage.dasibreaker.test;


import static org.junit.Assert.assertTrue;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.document.RdfDocument;
import com.apicatalog.rdf.RdfDataset;

import eu.neclab.ngsildbroker.commons.storage.dasibreaker.JRSConverter;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.JfromToRDF;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SepaGateway;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.TitaniumWrapper;
import it.unibo.arces.wot.sepa.commons.response.QueryResponse;
import jakarta.json.JsonArray;
import jakarta.json.JsonValue;

public class TitaniumTest extends JRSConverter {


	public TitaniumTest() {
		super("TitaniumTest");
		// TODO Auto-generated constructor stub
	}
	
	
	@Test
	public void testTitanium() throws Exception{
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
//		Reader targetReader = new StringReader(jsonld);
//		Document document = JsonDocument.of(targetReader);
//		RdfDataset rdf = JsonLd.toRdf(document).get();
//		String rdf_triples = super.rdfDatasetToTripleString(rdf,"KEY");
//		System.out.println("RDF-->"+rdf_triples);
//		RdfDataset rdf2 = super.triplesStringToDataSet(rdf_triples);
//		Document document2 = RdfDocument.of(rdf2);
//		JsonArray ris = JsonLd.fromRdf(document2).get();
//		String rdf_triples2 = super.rdfDatasetToTripleString(rdf2,"KEY");
//		System.out.println("rdf_triples2-->"+rdf_triples2);
//		String jsonld_str= super.resolveJsonBlankNode(ris);
//		System.out.println("test jsonld-->"+jsonld);
//		System.out.println("test result-->"+jsonld_str);
		TitaniumWrapper tw = new TitaniumWrapper();
		String turtle = tw.JSONtoRDF(jsonld);
		System.out.println("---------------------------------To rdf:\n"+turtle);
		try {
			String sparql = "DELETE WHERE {GRAPH <g2> {?s ?p ?o}};INSERT DATA { GRAPH <g2> {"+turtle+"}}";
			SepaGateway sg = new SepaGateway();
			assertTrue("INSERT DATA",!sg.executeUpdate(sparql).isError());
			sparql="SELECT ?e ?s ?p ?o {GRAPH <g2> { ?s ?p ?o} GRAPH ?e { ?s ?p ?o}}";
			QueryResponse qr = (QueryResponse)sg.executeQuery(sparql);
			List<String> jsonLDexpanded= tw.RDFtoJson(qr.getBindingsResults().getBindings());
			System.out.println("---------------------------------To json-ld:\\n"+jsonLDexpanded);
			List<String> contexts= new ArrayList<String>();
			contexts.add("https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld");
			List<String>  compacts = tw.compact(jsonLDexpanded, contexts);

			System.out.println("---------------------------------To json-ld COMPACTS:\\n"+compacts);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	@Test
//	public void testJSONLDtoRDFtoJSONLD_direct() throws JsonLdError{
//		System.out.println("---------------------------------TEST: testJSONLDtoRDFtoJSONLD_direct");
//		String jsonld = "{\n"
//				+ "    \"id\": \"urn:ngsi-ld:Building:storeProva94\",\n"
//				+ "    \"type\": \"Building\",\n"
//				+ "    \"category\": {\n"
//				+ "    	\"type\": \"Property\",\n"
//				+ "        \"value\": [\"commercial\"]\n"
//				+ "    },\n"
//				+ "    \"address\": {\n"
//				+ "        \"type\": \"Property\",\n"
//				+ "        \"value\": {\n"
//				+ "            \"streetAddress\": \"Friedrichstraße 44\",\n"
//				+ "            \"addressRegion\": \"Berlin\",\n"
//				+ "            \"addressLocality\": \"Kreuzberg\",\n"
//				+ "            \"postalCode\": \"10969\"\n"
//				+ "        },\n"
//				+ "        \"verified\": {\n"
//				+ "			\"type\": \"Property\",\n"
//				+ "			\"value\": true\n"
//				+ "		}\n"
//				+ "    },\n"
//				+ "     \"location\": {\n"
//				+ "        \"type\": \"GeoProperty\",\n"
//				+ "        \"value\": {\n"
//				+ "             \"type\": \"Point\",\n"
//				+ "              \"coordinates\": [13.3903, 52.5075]\n"
//				+ "        }\n"
//				+ "    },\n"
//				+ "    \"name\": {\n"
//				+ "        \"type\": \"Property\",\n"
//				+ "        \"value\": \"Checkpoint Markt\"\n"
//				+ "    },\n"
//				+ "    \"@context\": [\n"
//				+ "        \"https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld\"\n"
//				+ "    ]\n"
//				+ "}";
//		Reader targetReader = new StringReader(jsonld);
//		Document document = JsonDocument.of(targetReader);
//		RdfDataset rdf = JsonLd.toRdf(document).get();
//		Document document2 = RdfDocument.of(rdf);
//		JsonArray ris = JsonLd.fromRdf(document2).get();
//		String jsonld_str= super.resolveJsonBlankNode(ris);
//		System.out.println("jsonld_str-->"+jsonld_str);
////		Consumer<JsonValue> print = x -> System.out.println(x.toString());
////		ris.forEach(print);
//				
//	}
//	
	
	
	
	
}
