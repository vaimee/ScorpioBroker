package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.codec.binary.Base64;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.JsonLdErrorCode;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.document.RdfDocument;
import com.apicatalog.rdf.Rdf;
import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.RdfNQuad;
import com.apicatalog.rdf.RdfResource;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;
import it.unibo.arces.wot.sepa.commons.sparql.BindingsResults;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;

//JSONLD-RDF-SPARQL-Converter
public class JRSConverter2 extends JRSConverter{

	public JRSConverter2(String table) {
		super(table);
		// TODO Auto-generated constructor stub
	}

	@Override
	public  String rdfDatasetToTripleString(RdfDataset rdf, String key) {
		String turtle = "";
		for ( RdfNQuad iterable_element : rdf.toList()) {
			String s = iterable_element.getSubject().getValue();
			if(iterable_element.getSubject().isBlankNode()) {
				s= "<"+resolveBlankNode(s,key) +">";
			}else {
				s = "<"+s+">";
			}
			String p= "<"+iterable_element.getPredicate().getValue()+">";
			String o =  iterable_element.getObject().getValue();
			if(iterable_element.getObject().isBlankNode()) {
				o= "<"+resolveBlankNode(o,key) +">";
			}else if(iterable_element.getObject().isLiteral()) {
				o = "'"+o+"'";
			}else {
				o = "<"+o+">";
			}
			Optional<RdfResource> graph = iterable_element.getGraphName();
			String g = graph.get().asLiteral().getValue();
			turtle+=s+" "+ p+ " " + o +" "+g+".\n";
		}
		return turtle;
	}

}
