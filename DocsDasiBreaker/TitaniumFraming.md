## Content

- [Entity](#Entity)
- [Titanium Conversion](#Titanium Conversion)
- [Titanium Framing](#Titanium Framing)




## Entity 
The original json-ld entity

```
{
  "id": "urn:ngsi-ld:T:01",
  "type": "T",
  "P1": {
    "type": "Property",
    "value": 12,
    "observedAt": "2018-12-04T12:00:00.000Z",
    "P1_R1": {
      "type": "Relationship",
      "object": "urn:ngsi-ld:T2:6789"
    },
    "P1_P1": {
      "type": "Property",
      "value": 0.79
    }
  }
}
```

## Titanium Conversion 
The upper json-ld entity converted by Titanium (json-ld -> RDF)

and reconverted in json-ld (RDF -> json-ld):

```
[
   {
      "@id":"urn:ngsi-ld:T:01",
      "http://example.org/P1":[
         {
            "@id":"_:b0"
         }
      ],
      "@type":[
         "http://example.org/T"
      ]
   },
   {
      "@id":"_:b0",
      "https://uri.etsi.org/ngsi-ld/hasValue":[
         {
            "@value":12
         }
      ],
      "http://example.org/P1_P1":[
         {
            "@id":"_:b1"
         }
      ],
      "http://example.org/P1_R1":[
         {
            "@id":"_:b2"
         }
      ],
      "https://uri.etsi.org/ngsi-ld/observedAt":[
         {
            "@value":"2018-12-04T12:00:00.000Z",
            "@type":"https://uri.etsi.org/ngsi-ld/DateTime"
         }
      ],
      "@type":[
         "https://uri.etsi.org/ngsi-ld/Property"
      ]
   },
   {
      "@id":"_:b1",
      "https://uri.etsi.org/ngsi-ld/hasValue":[
         {
            "@value":0.79
         }
      ],
      "@type":[
         "https://uri.etsi.org/ngsi-ld/Property"
      ]
   },
   {
      "@id":"_:b2",
      "https://uri.etsi.org/ngsi-ld/hasObject":[
         {
            "@id":"urn:ngsi-ld:T2:6789"
         }
      ],
      "@type":[
         "https://uri.etsi.org/ngsi-ld/Relationship"
      ]
   }
]
```

## Titanium Framing
Framing the entty just by @type (no @context)
this will resolve the blank nodes

frame:
```{"@type":"http://example.org/T"}```

framed:
```
{
  "@id": "urn:ngsi-ld:T:01",
  "@type": "http://example.org/T",
  "http://example.org/P1": {
    "@type": "https://uri.etsi.org/ngsi-ld/Property",
    "http://example.org/P1_P1": {
      "@type": "https://uri.etsi.org/ngsi-ld/Property",
      "https://uri.etsi.org/ngsi-ld/hasValue": 0.79
    },
    "http://example.org/P1_R1": {
      "@type": "https://uri.etsi.org/ngsi-ld/Relationship",
      "https://uri.etsi.org/ngsi-ld/hasObject": {
        "@id": "urn:ngsi-ld:T2:6789"
      }
    },
    "https://uri.etsi.org/ngsi-ld/hasValue": 12,
    "https://uri.etsi.org/ngsi-ld/observedAt": {
      "@type": "https://uri.etsi.org/ngsi-ld/DateTime",
      "@value": "2018-12-04T12:00:00.000Z"
    }
  }
}
```



