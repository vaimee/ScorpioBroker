## Content

- [Context](#Context)
- [Create ControlUnitEntity (POST)](#Create-ControlUnitEntity)
- [ControlUnitEntity RDF](#ControlUnitEntity-RDF)
- [Create ObservationEntity (POST)](#Create-ObservationEntity))
- [ObservationEntity RDF](#ObservationEntity-RDF)
- [Create ObservablePropertyEntity (POST)](#Create-ObservablePropertyEntity)
- [ObservablePropertyEntity RDF](#ObservablePropertyEntity-RDF)
- [Create SensorEntity (POST)](#Create-SensorEntity)
- [SensorEntity RDF](#SensorEntity-RDF)
- [Create TransformerEntity (POST)](#Create-TransformerEntity)
- [TransformerEntity RDF](#TransformerEntity-RDF)
- [Get by tipes (GET)](#Get-by-tipes)




## Context 
monas.jsonld
```
{
	"@context": {
		"monas": "https://vaimee.com/monas/ngsi/",
		"ssn": "http://www.w3.org/ns/ssn/",
		"sosa": "http://www.w3.org/ns/sosa/",
		"qudt": "http://qudt.org/schema/qudt/",
		"unit": "http://qudt.org/vocab/unit/",
		"rdfs": "http://www.w3.org/2000/01/rdf-schema#",
		"xsd": "http://www.w3.org/2001/XMLSchema#",
		"TransformerEntity": "monas:TransformerEntity",
		"ControlUnitEntity": "monas:ControlUnitEntity",
		"SensorEntity": "monas:SensorEntity",
		"ObservablePropertyEntity": "monas:ObservablePropertyEntity",
		"ObservationEntity": "monas:ObservationEntity",
		"applicableUnitProperty": {
			"@id": "qudt:applicableUnit",
			"@type": "@id"
		},
		"resultTimeProperty": {
			"@id": "sosa:resultTime",
			"@type": "xsd:dateTime"
		},
		"hasSimpleResultProperty": {
			"@id": "sosa:hasSimpleResult",
			"@type": "xsd:int"
		},
		"observesRelationship": {
			"@id": "sosa:observes",
			"@type": "@id"
		},
		"hostsRelationship": {
			"@id": "sosa:hosts",
			"@type": "@id"
		},
		"hasFeatureOfInterestRelationship": {
			"@id": "sosa:hasFeatureOfInterest",
			"@type": "@id"
		},
		"madeBySensorRelationship": {
			"@id": "sosa:madeBySensor",
			"@type": "@id"
		}
	}
}
```
# WARNING
You need host "monas.jsonld" context somewhere and then replace "http://localhost/monas.jsonld" with your URI

## Create ControlUnitEntity
method: ```POST```

url: 	```http://localhost:9090/ngsi-ld/v1/entities/```

headers:

		link:<http://localhost/monas.jsonld>; rel="http://www.w3.org/ns/json-ld#context"; type="application/ld+json"

		content-type:application/ld+json

body:

```
{
"@context": [
"http://localhost/monas.jsonld",
"http://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
],
"id": "urn:epc:id:gid:0000.1.0",
"type": "ControlUnitEntity",
"hostsRelationship": [
	{ "type": "Relationship", "object": "urn:epc:id:gid:0000.2.1" },
	{ "type": "Relationship", "object": "urn:epc:id:gid:0000.2.X" },
	{ "type": "Relationship", "object": "urn:epc:id:gid:0000.2.Y" },
	{ "type": "Relationship", "object": "urn:epc:id:gid:0000.2.Z" }
]
}
```

That entity will be rappresent in RDF as follow,

(outside SCORPIO we will look only for ```<urn:epc:id:gid:0000.1.0/entity/data_without_sysattrs>``` graph)

RDF RESULT:

<table><thead><tr><th>Graph</th><th>S</th><th>P</th><th>O</th></tr></thead><tbody><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Ftemporalentity%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/temporalentity&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Flocalhost%3A3000%2Fngsi%2Fcreatedat%3E">&lt;http://localhost:3000/ngsi/createdat&gt;</a></td><td class="literal">2021-11-15T09:24:13.125029Z</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Flocalhost%3A3000%2Fngsi%2Fid%3E">&lt;http://localhost:3000/ngsi/id&gt;</a></td><td class="literal">urn:epc:&#8203;id:gid:0000.1.0</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Ftemporalentity%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/temporalentity&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Flocalhost%3A3000%2Fngsi%2Fid%3E">&lt;http://localhost:3000/ngsi/id&gt;</a></td><td class="literal">urn:epc:&#8203;id:gid:0000.1.0</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Ftemporalentityattrinstance%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/temporalentityattrinstance&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Flocalhost%3A3000%2Fngsi%2Fid%3E">&lt;http://localhost:3000/ngsi/id&gt;</a></td><td class="literal">urn:epc:&#8203;id:gid:0000.1.0</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Ftemporalentity%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/temporalentity&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Flocalhost%3A3000%2Fngsi%2Fmodifiedat%3E">&lt;http://localhost:3000/ngsi/modifiedat&gt;</a></td><td class="literal">2021-11-15T09:24:13.125029Z</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Flocalhost%3A3000%2Fngsi%2Ftype%3E">&lt;http://localhost:3000/ngsi/type&gt;</a></td><td class="literal">https://vaimee.com/monas/ControlUnitEntity</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Ftemporalentity%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/temporalentity&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Flocalhost%3A3000%2Fngsi%2Ftype%3E">&lt;http://localhost:3000/ngsi/type&gt;</a></td><td class="literal">https://vaimee.com/monas/ControlUnitEntity</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Ftemporalentityattrinstance%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/temporalentityattrinstance&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Flocalhost%3A3000%2Fngsi%2FattributeId%3E">&lt;http://localhost:3000/ngsi/attributeId&gt;</a></td><td class="literal">http://www.w3.org/ns/sosa/hosts</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Flocalhost%3A3000%2Fngsi%2Fdata%3E">&lt;http://localhost:3000/ngsi/data&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Ftemporalentityattrinstance%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/temporalentityattrinstance&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Flocalhost%3A3000%2Fngsi%2Fdata%3E">&lt;http://localhost:3000/ngsi/data&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Ftemporalentityattrinstance%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/temporalentityattrinstance/data&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FcreatedAt%3E">&lt;https://uri.etsi.org/ngsi-ld/createdAt&gt;</a></td><td class="literal">2021-11-15T09:24:13.125029Z</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FmodifiedAt%3E">&lt;https://uri.etsi.org/ngsi-ld/modifiedAt&gt;</a></td><td class="literal">2021-11-15T09:24:13.125029Z</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Flocalhost%3A3000%2Fngsi%2Fdata_without_sysattrs%3E">&lt;http://localhost:3000/ngsi/data_without_sysattrs&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata_without_sysattrs%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data_without_sysattrs&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Flocalhost%3A3000%2Fngsi%2Fkvdata%3E">&lt;http://localhost:3000/ngsi/kvdata&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fkvdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/kvdata&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fkvdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/kvdata&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fhosts%3E">&lt;http://www.w3.org/ns/sosa/hosts&gt;</a></td><td class="literal">urn:epc:&#8203;id:gid:0000.2.1</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fkvdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/kvdata&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fhosts%3E">&lt;http://www.w3.org/ns/sosa/hosts&gt;</a></td><td class="literal">urn:epc:&#8203;id:gid:0000.2.X</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fkvdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/kvdata&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fhosts%3E">&lt;http://www.w3.org/ns/sosa/hosts&gt;</a></td><td class="literal">urn:epc:&#8203;id:gid:0000.2.Y</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fkvdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/kvdata&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fhosts%3E">&lt;http://www.w3.org/ns/sosa/hosts&gt;</a></td><td class="literal">urn:epc:&#8203;id:gid:0000.2.Z</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fhosts%3E">&lt;http://www.w3.org/ns/sosa/hosts&gt;</a></td><td class="bnode">t27261</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata_without_sysattrs%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data_without_sysattrs&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fhosts%3E">&lt;http://www.w3.org/ns/sosa/hosts&gt;</a></td><td class="bnode">t27261</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fhosts%3E">&lt;http://www.w3.org/ns/sosa/hosts&gt;</a></td><td class="bnode">t27262</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata_without_sysattrs%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data_without_sysattrs&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fhosts%3E">&lt;http://www.w3.org/ns/sosa/hosts&gt;</a></td><td class="bnode">t27262</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fhosts%3E">&lt;http://www.w3.org/ns/sosa/hosts&gt;</a></td><td class="bnode">t27263</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata_without_sysattrs%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data_without_sysattrs&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fhosts%3E">&lt;http://www.w3.org/ns/sosa/hosts&gt;</a></td><td class="bnode">t27263</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fhosts%3E">&lt;http://www.w3.org/ns/sosa/hosts&gt;</a></td><td class="bnode">t27264</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata_without_sysattrs%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data_without_sysattrs&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fhosts%3E">&lt;http://www.w3.org/ns/sosa/hosts&gt;</a></td><td class="bnode">t27264</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Fvaimee.com%2Fmonas%2FControlUnitEntity%3E">&lt;https://vaimee.com/monas/ControlUnitEntity&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata_without_sysattrs%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data_without_sysattrs&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Fvaimee.com%2Fmonas%2FControlUnitEntity%3E">&lt;https://vaimee.com/monas/ControlUnitEntity&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fkvdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/kvdata&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Fvaimee.com%2Fmonas%2FControlUnitEntity%3E">&lt;https://vaimee.com/monas/ControlUnitEntity&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="bnode">t27261</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FcreatedAt%3E">&lt;https://uri.etsi.org/ngsi-ld/createdAt&gt;</a></td><td class="literal">2021-11-15T09:24:13.125029Z</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="bnode">t27261</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FmodifiedAt%3E">&lt;https://uri.etsi.org/ngsi-ld/modifiedAt&gt;</a></td><td class="literal">2021-11-15T09:24:13.125029Z</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="bnode">t27261</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasObject%3E">&lt;https://uri.etsi.org/ngsi-ld/hasObject&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.2.1%3E">&lt;urn:epc:&#8203;id:gid:0000.2.1&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata_without_sysattrs%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data_without_sysattrs&gt;</a></td><td class="bnode">t27261</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasObject%3E">&lt;https://uri.etsi.org/ngsi-ld/hasObject&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.2.1%3E">&lt;urn:epc:&#8203;id:gid:0000.2.1&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="bnode">t27261</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FRelationship%3E">&lt;https://uri.etsi.org/ngsi-ld/Relationship&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata_without_sysattrs%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data_without_sysattrs&gt;</a></td><td class="bnode">t27261</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FRelationship%3E">&lt;https://uri.etsi.org/ngsi-ld/Relationship&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="bnode">t27262</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FcreatedAt%3E">&lt;https://uri.etsi.org/ngsi-ld/createdAt&gt;</a></td><td class="literal">2021-11-15T09:24:13.125029Z</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="bnode">t27262</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FmodifiedAt%3E">&lt;https://uri.etsi.org/ngsi-ld/modifiedAt&gt;</a></td><td class="literal">2021-11-15T09:24:13.125029Z</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="bnode">t27262</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasObject%3E">&lt;https://uri.etsi.org/ngsi-ld/hasObject&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.2.X%3E">&lt;urn:epc:&#8203;id:gid:0000.2.X&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata_without_sysattrs%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data_without_sysattrs&gt;</a></td><td class="bnode">t27262</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasObject%3E">&lt;https://uri.etsi.org/ngsi-ld/hasObject&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.2.X%3E">&lt;urn:epc:&#8203;id:gid:0000.2.X&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="bnode">t27262</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FRelationship%3E">&lt;https://uri.etsi.org/ngsi-ld/Relationship&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata_without_sysattrs%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data_without_sysattrs&gt;</a></td><td class="bnode">t27262</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FRelationship%3E">&lt;https://uri.etsi.org/ngsi-ld/Relationship&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="bnode">t27263</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FcreatedAt%3E">&lt;https://uri.etsi.org/ngsi-ld/createdAt&gt;</a></td><td class="literal">2021-11-15T09:24:13.125029Z</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="bnode">t27263</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FmodifiedAt%3E">&lt;https://uri.etsi.org/ngsi-ld/modifiedAt&gt;</a></td><td class="literal">2021-11-15T09:24:13.125029Z</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="bnode">t27263</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasObject%3E">&lt;https://uri.etsi.org/ngsi-ld/hasObject&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.2.Y%3E">&lt;urn:epc:&#8203;id:gid:0000.2.Y&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata_without_sysattrs%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data_without_sysattrs&gt;</a></td><td class="bnode">t27263</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasObject%3E">&lt;https://uri.etsi.org/ngsi-ld/hasObject&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.2.Y%3E">&lt;urn:epc:&#8203;id:gid:0000.2.Y&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="bnode">t27263</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FRelationship%3E">&lt;https://uri.etsi.org/ngsi-ld/Relationship&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata_without_sysattrs%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data_without_sysattrs&gt;</a></td><td class="bnode">t27263</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FRelationship%3E">&lt;https://uri.etsi.org/ngsi-ld/Relationship&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="bnode">t27264</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FcreatedAt%3E">&lt;https://uri.etsi.org/ngsi-ld/createdAt&gt;</a></td><td class="literal">2021-11-15T09:24:13.125029Z</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="bnode">t27264</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FmodifiedAt%3E">&lt;https://uri.etsi.org/ngsi-ld/modifiedAt&gt;</a></td><td class="literal">2021-11-15T09:24:13.125029Z</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="bnode">t27264</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasObject%3E">&lt;https://uri.etsi.org/ngsi-ld/hasObject&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.2.Z%3E">&lt;urn:epc:&#8203;id:gid:0000.2.Z&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata_without_sysattrs%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data_without_sysattrs&gt;</a></td><td class="bnode">t27264</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasObject%3E">&lt;https://uri.etsi.org/ngsi-ld/hasObject&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.2.Z%3E">&lt;urn:epc:&#8203;id:gid:0000.2.Z&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data&gt;</a></td><td class="bnode">t27264</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FRelationship%3E">&lt;https://uri.etsi.org/ngsi-ld/Relationship&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Fentity%2Fdata_without_sysattrs%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/entity/data_without_sysattrs&gt;</a></td><td class="bnode">t27264</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FRelationship%3E">&lt;https://uri.etsi.org/ngsi-ld/Relationship&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Ftemporalentityattrinstance%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/temporalentityattrinstance/data&gt;</a></td><td class="bnode">t27273</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FcreatedAt%3E">&lt;https://uri.etsi.org/ngsi-ld/createdAt&gt;</a></td><td class="literal">2021-11-15T09:24:13.125029Z</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Ftemporalentityattrinstance%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/temporalentityattrinstance/data&gt;</a></td><td class="bnode">t27273</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FinstanceId%3E">&lt;https://uri.etsi.org/ngsi-ld/instanceId&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Angsi-ld%3A163ba859-5c48-422b-86c3-fdb9f0cf488b%3E">&lt;urn:ngsi-ld:163ba859-5c48-422b-86c3-fdb9f0cf488b&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Ftemporalentityattrinstance%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/temporalentityattrinstance/data&gt;</a></td><td class="bnode">t27273</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FmodifiedAt%3E">&lt;https://uri.etsi.org/ngsi-ld/modifiedAt&gt;</a></td><td class="literal">2021-11-15T09:24:13.125029Z</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Ftemporalentityattrinstance%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/temporalentityattrinstance/data&gt;</a></td><td class="bnode">t27273</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasObject%3E">&lt;https://uri.etsi.org/ngsi-ld/hasObject&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.2.Z%3E">&lt;urn:epc:&#8203;id:gid:0000.2.Z&gt;</a></td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%2Ftemporalentityattrinstance%2Fdata%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0/temporalentityattrinstance/data&gt;</a></td><td class="bnode">t27273</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FRelationship%3E">&lt;https://uri.etsi.org/ngsi-ld/Relationship&gt;</a></td></tr></tbody></table>

## ControlUnitEntity RDF
```<urn:epc:id:gid:0000.1.0/entity/data_without_sysattrs>``` GRAPH

<table><thead><tr><th>S</th><th>P</th><th>O</th></tr></thead><tbody><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fhosts%3E">&lt;http://www.w3.org/ns/sosa/hosts&gt;</a></td><td class="bnode">t27261</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fhosts%3E">&lt;http://www.w3.org/ns/sosa/hosts&gt;</a></td><td class="bnode">t27262</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fhosts%3E">&lt;http://www.w3.org/ns/sosa/hosts&gt;</a></td><td class="bnode">t27263</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fhosts%3E">&lt;http://www.w3.org/ns/sosa/hosts&gt;</a></td><td class="bnode">t27264</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Fvaimee.com%2Fmonas%2FControlUnitEntity%3E">&lt;https://vaimee.com/monas/ControlUnitEntity&gt;</a></td></tr><tr><td class="bnode">t27261</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasObject%3E">&lt;https://uri.etsi.org/ngsi-ld/hasObject&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.2.1%3E">&lt;urn:epc:&#8203;id:gid:0000.2.1&gt;</a></td></tr><tr><td class="bnode">t27261</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FRelationship%3E">&lt;https://uri.etsi.org/ngsi-ld/Relationship&gt;</a></td></tr><tr><td class="bnode">t27262</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasObject%3E">&lt;https://uri.etsi.org/ngsi-ld/hasObject&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.2.X%3E">&lt;urn:epc:&#8203;id:gid:0000.2.X&gt;</a></td></tr><tr><td class="bnode">t27262</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FRelationship%3E">&lt;https://uri.etsi.org/ngsi-ld/Relationship&gt;</a></td></tr><tr><td class="bnode">t27263</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasObject%3E">&lt;https://uri.etsi.org/ngsi-ld/hasObject&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.2.Y%3E">&lt;urn:epc:&#8203;id:gid:0000.2.Y&gt;</a></td></tr><tr><td class="bnode">t27263</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FRelationship%3E">&lt;https://uri.etsi.org/ngsi-ld/Relationship&gt;</a></td></tr><tr><td class="bnode">t27264</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasObject%3E">&lt;https://uri.etsi.org/ngsi-ld/hasObject&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.2.Z%3E">&lt;urn:epc:&#8203;id:gid:0000.2.Z&gt;</a></td></tr><tr><td class="bnode">t27264</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FRelationship%3E">&lt;https://uri.etsi.org/ngsi-ld/Relationship&gt;</a></td></tr></tbody></table>

# Create ObservationEntity
method: ```POST```

url: 	```http://localhost:9090/ngsi-ld/v1/entities/```

headers:

		link:<http://localhost/monas.jsonld>; rel="http://www.w3.org/ns/json-ld#context"; type="application/ld+json"

		content-type:application/ld+json

body:

```
{
"@context": [
"http://localhost/monas.jsonld",
"http://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
],
"id": "monas:observation/XYZ",
"type": "ObservationEntity",
"hasFeatureOfInterestRelationship": {
"type": "Relationship",
"object": "urn:epc:id:gid:13101974.0.0"
},
"madeBySensorRelationship": {
"type": "Relationship",
"object": "urn:epc:id:gid:0000.2.1"
},
"resultTimeProperty": {
"type": "Property",
 	"value ": "2020-10-14T11:05:52Z"
},
"hasSimpleResultProperty": {
"type": "Property",
"value ": "125"
}
}
```
# ObservationEntity RDF
```<https://vaimee.com/monas/observation/XYZ/entity/data_without_sysattrs>``` GRAPH

<table><thead><tr><th>S</th><th>P</th><th>O</th></tr></thead><tbody><tr><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Fvaimee.com%2Fmonas%2Fobservation%2FXYZ%3E">&lt;https://vaimee.com/monas/observation/XYZ&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2FhasFeatureOfInterest%3E">&lt;http://www.w3.org/ns/sosa/hasFeatureOfInterest&gt;</a></td><td class="bnode">t27275</td></tr><tr><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Fvaimee.com%2Fmonas%2Fobservation%2FXYZ%3E">&lt;https://vaimee.com/monas/observation/XYZ&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2FhasSimpleResult%3E">&lt;http://www.w3.org/ns/sosa/hasSimpleResult&gt;</a></td><td class="bnode">t27276</td></tr><tr><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Fvaimee.com%2Fmonas%2Fobservation%2FXYZ%3E">&lt;https://vaimee.com/monas/observation/XYZ&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2FmadeBySensor%3E">&lt;http://www.w3.org/ns/sosa/madeBySensor&gt;</a></td><td class="bnode">t27277</td></tr><tr><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Fvaimee.com%2Fmonas%2Fobservation%2FXYZ%3E">&lt;https://vaimee.com/monas/observation/XYZ&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2FresultTime%3E">&lt;http://www.w3.org/ns/sosa/resultTime&gt;</a></td><td class="bnode">t27278</td></tr><tr><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Fvaimee.com%2Fmonas%2Fobservation%2FXYZ%3E">&lt;https://vaimee.com/monas/observation/XYZ&gt;</a></td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Fvaimee.com%2Fmonas%2FObservationEntity%3E">&lt;https://vaimee.com/monas/ObservationEntity&gt;</a></td></tr><tr><td class="bnode">t27275</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasObject%3E">&lt;https://uri.etsi.org/ngsi-ld/hasObject&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A13101974.0.0%3E">&lt;urn:epc:&#8203;id:gid:13101974.0.0&gt;</a></td></tr><tr><td class="bnode">t27275</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FRelationship%3E">&lt;https://uri.etsi.org/ngsi-ld/Relationship&gt;</a></td></tr><tr><td class="bnode">t27276</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FProperty%3E">&lt;https://uri.etsi.org/ngsi-ld/Property&gt;</a></td></tr><tr><td class="bnode">t27277</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasObject%3E">&lt;https://uri.etsi.org/ngsi-ld/hasObject&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.2.1%3E">&lt;urn:epc:&#8203;id:gid:0000.2.1&gt;</a></td></tr><tr><td class="bnode">t27277</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FRelationship%3E">&lt;https://uri.etsi.org/ngsi-ld/Relationship&gt;</a></td></tr><tr><td class="bnode">t27278</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FProperty%3E">&lt;https://uri.etsi.org/ngsi-ld/Property&gt;</a></td></tr></tbody></table>

# Create ObservablePropertyEntity
method: ```POST```

url: 	```http://localhost:9090/ngsi-ld/v1/entities/```

headers:

		link:<http://localhost/monas.jsonld>; rel="http://www.w3.org/ns/json-ld#context"; type="application/ld+json"

		content-type:application/ld+json

body:

```
{
  "@context": [
  "http://localhost/monas.jsonld",
  "http://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
  ],
  "id": "monas:ProbeDTemperature",
  "type": "ObservablePropertyEntity",
  "applicableUnitProperty": {
  "type": "Property",
   "value ": "unit:DEG_C"
  }
}
```
# ObservablePropertyEntity RDF

```<https://vaimee.com/monas/ProbeDTemperature/entity/data_without_sysattrs>``` GRAPH

<table><thead><tr><th>S</th><th>P</th><th>O</th></tr></thead><tbody><tr><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Fvaimee.com%2Fmonas%2Fngsi%2FProbeDTemperature%3E">&lt;https://vaimee.com/monas/ngsi/ProbeDTemperature&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fqudt.org%2Fschema%2Fqudt%2FapplicableUnit%3E">&lt;http://qudt.org/schema/qudt/applicableUnit&gt;</a></td><td class="bnode">t27317</td></tr><tr><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Fvaimee.com%2Fmonas%2Fngsi%2FProbeDTemperature%3E">&lt;https://vaimee.com/monas/ngsi/ProbeDTemperature&gt;</a></td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Fvaimee.com%2Fmonas%2Fngsi%2FObservablePropertyEntity%3E">&lt;https://vaimee.com/monas/ngsi/ObservablePropertyEntity&gt;</a></td></tr><tr><td class="bnode">t27317</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasValue%3E">&lt;https://uri.etsi.org/ngsi-ld/hasValue&gt;</a></td><td class="literal">unit:DEG_C</td></tr><tr><td class="bnode">t27317</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FProperty%3E">&lt;https://uri.etsi.org/ngsi-ld/Property&gt;</a></td></tr></tbody></table>

# Create SensorEntity
method: ```POST```

url: 	```http://localhost:9090/ngsi-ld/v1/entities/```

headers:

		link:<http://localhost/monas.jsonld>; rel="http://www.w3.org/ns/json-ld#context"; type="application/ld+json"

		content-type:application/ld+json

body:

```
{
  "@context":  [
  "http://localhost/monas.jsonld",
  "http://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
  ],
  "id": "urn:epc:id:gid:0000.2.1",
  "type": "SensorEntity",
  "observesRelationship": {
  "type": "Relationship",
  "object": "monas:ProbeDTemperature"
  }
}
```
# SensorEntity RDF

```<urn:epc:id:gid:0000.2.1/entity/data_without_sysattrs>``` GRAPH

<table><thead><tr><th>S</th><th>P</th><th>O</th></tr></thead><tbody><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.2.1%3E">&lt;urn:epc:&#8203;id:gid:0000.2.1&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fobserves%3E">&lt;http://www.w3.org/ns/sosa/observes&gt;</a></td><td class="bnode">t27282</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.2.1%3E">&lt;urn:epc:&#8203;id:gid:0000.2.1&gt;</a></td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Fvaimee.com%2Fmonas%2FSensorEntity%3E">&lt;https://vaimee.com/monas/SensorEntity&gt;</a></td></tr><tr><td class="bnode">t27282</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasObject%3E">&lt;https://uri.etsi.org/ngsi-ld/hasObject&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Fvaimee.com%2Fmonas%2FProbeDTemperature%3E">&lt;https://vaimee.com/monas/ProbeDTemperature&gt;</a></td></tr><tr><td class="bnode">t27282</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FRelationship%3E">&lt;https://uri.etsi.org/ngsi-ld/Relationship&gt;</a></td></tr></tbody></table>


# Create TransformerEntity
method: ```POST```

url: 	```http://localhost:9090/ngsi-ld/v1/entities/```

headers:

		link:<http://localhost/monas.jsonld>; rel="http://www.w3.org/ns/json-ld#context"; type="application/ld+json"

		content-type:application/ld+json

body:

```
{
"@context":  [
"http://localhost/monas.jsonld",
"http://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
],
"id": "urn:epc:id:gid:13101974.0.0",
"type": "TransformerEntity",
"hostsRelationship":  {
"type": "Relationship",
"object": "urn:epc:id:gid:0000.1.0"
}
}
```
# TransformerEntity RDF

```<urn:epc:id:gid:13101974.0.0/entity/data_without_sysattrs>``` GRAPH

<table><thead><tr><th>S</th><th>P</th><th>O</th></tr></thead><tbody><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A13101974.0.0%3E">&lt;urn:epc:&#8203;id:gid:13101974.0.0&gt;</a></td><td class="uri"><a href="#explore:kb:%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fsosa%2Fhosts%3E">&lt;http://www.w3.org/ns/sosa/hosts&gt;</a></td><td class="bnode">t27287</td></tr><tr><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A13101974.0.0%3E">&lt;urn:epc:&#8203;id:gid:13101974.0.0&gt;</a></td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Fvaimee.com%2Fmonas%2FTransformerEntity%3E">&lt;https://vaimee.com/monas/TransformerEntity&gt;</a></td></tr><tr><td class="bnode">t27287</td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FhasObject%3E">&lt;https://uri.etsi.org/ngsi-ld/hasObject&gt;</a></td><td class="uri"><a href="#explore:kb:%3Curn%3Aepc%3Aid%3Agid%3A0000.1.0%3E">&lt;urn:epc:&#8203;id:gid:0000.1.0&gt;</a></td></tr><tr><td class="bnode">t27287</td><td class="uri"><a href="#explore:kb:rdf%3Atype">rdf:type</a></td><td class="uri"><a href="#explore:kb:%3Chttps%3A%2F%2Furi.etsi.org%2Fngsi-ld%2FRelationship%3E">&lt;https://uri.etsi.org/ngsi-ld/Relationship&gt;</a></td></tr></tbody></table>

## Get by tipes
method: ```GET```

url: 	```http://localhost:9090/ngsi-ld/v1/entities?type=ObservationEntity,TransformerEntity,ControlUnitEntity,SensorEntity,ObservablePropertyEntity```

headers:

		link:<http://localhost/monas.jsonld>; rel="http://www.w3.org/ns/json-ld#context"; type="application/ld+json"

		content-type:application/ld+json

Result:

```
  {
"id": "urn:epc:id:gid:0000.1.0",
"type": "ControlUnitEntity",
"hostsRelationship": [
  {
"type": "Relationship",
"object": "urn:epc:id:gid:0000.2.1"
},
  {
"type": "Relationship",
"object": "urn:epc:id:gid:0000.2.X"
},
  {
"type": "Relationship",
"object": "urn:epc:id:gid:0000.2.Y"
},
  {
"type": "Relationship",
"object": "urn:epc:id:gid:0000.2.Z"
}
],
"@context": [
  "http://11.0.0.15/monas.jsonld",
  "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
],
},
  {
"id": "monas:observation/XYZ",
"type": "ObservationEntity",
"hasFeatureOfInterestRelationship": {
"type": "Relationship",
"object": "urn:epc:id:gid:13101974.0.0"
},
"sosa:hasSimpleResult": {
"type": "Property"
},
"madeBySensorRelationship": {
"type": "Relationship",
"object": "urn:epc:id:gid:0000.2.1"
},
"sosa:resultTime": {
"type": "Property"
},
"@context": [
  "http://11.0.0.15/monas.jsonld",
  "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
],
},
  {
"id": "urn:epc:id:gid:13101974.0.0",
"type": "TransformerEntity",
"hostsRelationship": {
"type": "Relationship",
"object": "urn:epc:id:gid:0000.1.0"
},
"@context": [
  "http://11.0.0.15/monas.jsonld",
  "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
],
},
  {
"id": "monas:ProbeDTemperature",
"type": "ObservablePropertyEntity",
"applicableUnitProperty": {
"type": "Property",
"value": "unit:DEG_C"
},
"@context": [
  "http://11.0.0.15/monas.jsonld",
  "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
],
},
  {
"@graph": [
  {
"id": "_:b0",
"type": "Relationship",
"object": "urn:epc:id:gid:0000.2.1"
},
  {
"id": "_:b1",
"type": "Relationship",
"object": "urn:epc:id:gid:0000.2.X"
},
  {
"id": "_:b2",
"type": "Relationship",
"object": "urn:epc:id:gid:0000.2.Y"
},
  {
"id": "_:b3",
"type": "Relationship",
"object": "urn:epc:id:gid:0000.2.Z"
},
  {
"id": "urn:epc:id:gid:0000.1.2",
"type": [
  "Relationship",
  "ControlUnitEntity"
],
"hostsRelationship": [
  {
"id": "_:b0",
"type": "Relationship",
"object": "urn:epc:id:gid:0000.2.1"
},
  {
"id": "_:b1",
"type": "Relationship",
"object": "urn:epc:id:gid:0000.2.X"
},
  {
"id": "_:b2",
"type": "Relationship",
"object": "urn:epc:id:gid:0000.2.Y"
},
  {
"id": "_:b3",
"type": "Relationship",
"object": "urn:epc:id:gid:0000.2.Z"
}
],
}
],
"@context": [
  "http://11.0.0.15/monas.jsonld",
  "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
],
},
  {
"id": "urn:epc:id:gid:0000.2.1",
"type": "SensorEntity",
"observesRelationship": {
"type": "Relationship",
"object": "monas:ProbeDTemperature"
},
"@context": [
  "http://11.0.0.15/monas.jsonld",
  "https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context.jsonld"
],
}
],
```
