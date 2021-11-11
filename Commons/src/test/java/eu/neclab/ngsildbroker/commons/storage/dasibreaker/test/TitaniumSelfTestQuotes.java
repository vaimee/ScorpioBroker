package eu.neclab.ngsildbroker.commons.storage.dasibreaker.test;


import static org.junit.Assert.assertTrue;

import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;

public class TitaniumSelfTestQuotes {


	private static final String withQuote="{\n"
			+"\"@id\":\"prova\", \"type\":\"http://example.org/vocab\","
				+"\"field\":\"{\"prova\":\"1\"}\""
			+ "}";

	private static final String noQuote="{\n"
			+"\"@id\":\"prova\", \"type\":\"http://example.org/vocab\","
				+"\"field\":\"{prova:1}\""
			+ "}";
	
	private static final String context = "{\"@context\":"
			+ "\"https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld\","
			+ "\"@type\":\"http://example.org/vocab\""
			+ "}";
	
	@Test
	public void testTitaniumNoRDF() throws Exception{
	

		
		Reader targetReader = new StringReader(noQuote);
		Document document = JsonDocument.of(targetReader);


		Reader targetReaderContext = new StringReader(context);
		Document context = JsonDocument.of(targetReaderContext);
		
		String framed_out = JsonLd.frame(document,context).get().toString();
		System.out.println("Framed noQuote:\n\n\n"+framed_out);
	
		targetReader = new StringReader(withQuote); //go into error
		document = JsonDocument.of(targetReader);

		framed_out = JsonLd.frame(document,context).get().toString();
		System.out.println("Framed withQuote:\n\n\n"+framed_out);
	}
	
	
	
}
