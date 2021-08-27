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

import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SPARQLConverter;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.ConverterJRDF;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.SepaGateway;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.TitaniumWrapper;
import it.unibo.arces.wot.sepa.commons.response.QueryResponse;
import jakarta.json.JsonArray;
import jakarta.json.JsonValue;

public class TitaniumSelfTest {


	private static final String in="{\n"
			+ "  \"@context\": {\n"
			+ "    \"dcterms\": \"http://purl.org/dc/terms/\",\n"
			+ "    \"ex\": \"http://example.org/vocab#\",\n"
			+ "    \"ex:contains\": {\"@type\": \"@id\"}\n"
			+ "  },\n"
			+ "  \"@graph\": [\n"
			+ "    {\n"
			+ "      \"@id\": \"http://example.org/test/#library\",\n"
			+ "      \"@type\": \"ex:Library\",\n"
			+ "      \"ex:contains\": \"http://example.org/test#book\"\n"
			+ "    },\n"
			+ "    {\n"
			+ "      \"@id\": \"http://example.org/test#book\",\n"
			+ "      \"@type\": \"ex:Book\",\n"
			+ "      \"dcterms:contributor\": \"Writer\",\n"
			+ "      \"dcterms:title\": \"My Book\",\n"
			+ "      \"ex:contains\": \"http://example.org/test#chapter\"\n"
			+ "    },\n"
			+ "    {\n"
			+ "      \"@id\": \"http://example.org/test#chapter\",\n"
			+ "      \"@type\": \"ex:Chapter\",\n"
			+ "      \"dcterms:description\": \"Fun\",\n"
			+ "      \"dcterms:title\": \"Chapter One\",\n"
			+ "      \"ex:act\": \"ex:ActOne\"\n"
			+ "    }\n"
			+ "  ]\n"
			+ "}";
	
	private static final String context = "{\n"
			+ "  \"@context\": {\n"
			+ "    \"dcterms\": \"http://purl.org/dc/terms/\",\n"
			+ "    \"ex\": \"http://example.org/vocab#\"\n"
			+ "  },\n"
			+ "  \"@type\": \"http://example.org/vocab#Library\""
			+ "}";
	
	@Test
	public void testTitaniumNoRDF() throws Exception{
	
		Reader targetReader = new StringReader(in);
		Document document = JsonDocument.of(targetReader);

		Reader targetReaderContext = new StringReader(context);
		Document context = JsonDocument.of(targetReaderContext);
		String compacted_out = JsonLd.compact(document,context).compactToRelative(false)
			      .get().toString();
		System.out.println("Compact_out:\n\n\n"+compacted_out);

		String framed_out = JsonLd.frame(document,context).get().toString();
		System.out.println("Framed_out:\n\n\n"+framed_out);
		//that work!
		//expected:
//		{
//			  "@context": {
//			    "dcterms": "http://purl.org/dc/terms/",
//			    "ex": "http://example.org/vocab#"
//			  },
//			  "@id": "http://example.org/test/#library",
//			  "@type": "ex:Library",
//			  "ex:contains": {
//			    "@id": "http://example.org/test#book",
//			    "@type": "ex:Book",
//			    "ex:contains": {
//			      "@id": "http://example.org/test#chapter",
//			      "@type": "ex:Chapter",
//			      "ex:act": "ex:ActOne",
//			      "dcterms:description": "Fun",
//			      "dcterms:title": "Chapter One"
//			    },
//			    "dcterms:contributor": "Writer",
//			    "dcterms:title": "My Book"
//			  }
//			}
	}
	
	
	
}
