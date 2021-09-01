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
			+ "    \"id\": \"http://people/People1\",\n"
			+ "    \"type\": \"Person\",\n"
			+ "   	\"name\": \"Pluto\",\n"
			+ "    \"nickname\": \"PlutoIsTheBest\",\n"
			+ " 	\"image\":\"https://www.google.com/url?sa=i&url=http%3A%2F%2Fwww.conquistedellavoro.it%2Fcultura%2Fpluto-compie-90-anni-1.2618212&psig=AOvVaw22dRtt20YGxpX6pJQ0QeVt&ust=1630155825058000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJDMwemh0fICFQAAAAAdAAAAABAJ\",\n"
			+ "  	\"telephone\":33333333333,\n"
			+ "    \"@context\": [\n"
			+ "       \"https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld\",\n"
			+ "      \"https://json-ld.org/contexts/person.jsonld\"\n"
			+ "    ]\n"
			+ "}";
	
	private static final String frame = "{\"@context\":\"https://json-ld.org/contexts/person.jsonld\", \"@type\": \"Person\"}";
	
	@Test
	public void testTitaniumNoRDF() throws Exception{
	
		Reader targetReader = new StringReader(in);
		Document document = JsonDocument.of(targetReader);

		Reader targetReaderContext = new StringReader(frame);
		Document context = JsonDocument.of(targetReaderContext);
		String compacted_out = JsonLd.compact(document,context).compactToRelative(false)
			      .get().toString();
		System.out.println("Compact_out:\n\n\n"+compacted_out);

		String framed_out = JsonLd.frame(document,context).get().toString();
		System.out.println("Framed_out:\n\n\n"+framed_out);

	}
	
	
	
}
