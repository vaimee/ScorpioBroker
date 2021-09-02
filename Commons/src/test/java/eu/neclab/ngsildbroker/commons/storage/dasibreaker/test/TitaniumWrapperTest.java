package eu.neclab.ngsildbroker.commons.storage.dasibreaker.test;


import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdOptions;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.document.RdfDocument;
import com.apicatalog.jsonld.loader.DocumentLoader;
import com.apicatalog.rdf.RdfDataset;



public class TitaniumWrapperTest  {


	

	private static final String in="{\n"
			+ "    \"id\": \"http://people/People1\",\n"
			+ "    \"type\": \"Person\",\n"
			+ "   	\"name\": \"Pluto\",\n"
			+ "    \"nickname\": \"PlutoIsTheBest\",\n"
			+ " 	\"telephone\":33333333333,\n"
			+ "    \"@context\": [\n"
			+ "       \"https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld\",\n"
			+ "      \"https://json-ld.org/contexts/person.jsonld\"\n"
			+ "    ]\n"
			+ "}";
	
	private static final String frame = "{\"@context\":[\"https://json-ld.org/contexts/person.jsonld\"], \"@type\": \"Person\"}";
	
	
	@Test
	public void test() throws Exception{

		System.out.println(in);
		Reader targetReader = new StringReader(in);
		Document document = JsonDocument.of(targetReader);
		RdfDataset rdfdataset = JsonLd.toRdf(document).get();
		

		JsonLdOptions options = new JsonLdOptions();
		options.setUseNativeTypes(true);
		
		Document document2 = RdfDocument.of(rdfdataset);
		Reader targetReaderContext = new StringReader(frame);
		Document context = JsonDocument.of(targetReaderContext);
		Document jsondocument = JsonDocument.of(JsonLd.fromRdf(document2).options(options).get());
		System.out.println(JsonLd.frame(jsondocument,context).get().toString());
		
		
		
	}
	
	
//	
//	
//	@Test
//	public void test() throws Exception{
//		TitaniumWrapper tw = new TitaniumWrapper();
//		String rdf = tw.JSONtoRDF(in);
//		
//		System.out.println("##########################RDF#########################");
//		System.out.println(rdf);
//		
//		tw.setFrame(frame);
//		
//		String jsonld = tw.nQuadsToJson(rdf);
//		
//		System.out.println("##########################JSON-LD#########################");
//		System.out.println(jsonld);
//	
//		Reader targetReader = new StringReader(jsonld);
//		Document document = JsonDocument.of(targetReader);
//		
//		Reader targetReaderContext = new StringReader(frame);
//		Document context = JsonDocument.of(targetReaderContext);
//		String compacted_out = JsonLd.compact(document,context).compactToRelative(false)
//			      .get().toString();
//		System.out.println("Compact_out:\n\n\n"+compacted_out);
//
//		String framed_out = JsonLd.frame(document,context).get().toString();
//		System.out.println("Framed_out:\n\n\n"+framed_out);
//		
//	}
//	
	
	
	
	
}
