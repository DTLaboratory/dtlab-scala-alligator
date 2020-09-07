---
title: dtlab alligator v1.0
language_tabs:
  - python: Python
  - shell: Shell
  - javascript: Javascript
  - java: Java
toc_footers: []
includes: []
search: true
highlight_theme: darkula
headingLevel: 2

---

<!-- Generator: Widdershins v4.0.1 -->

<h1 id="dtlab-alligator">dtlab alligator v1.0</h1>

> Scroll down for code samples, example requests and responses. Select a language for code samples from the tabs above or the mobile navigation menu.

Manage DtLab actor runtime cloud.

The DtLab is a distributed programmable actor runtime environment.

  * optimized to enable graphs of digital twins
  * for IOT and non-IOT applications (neural nets, etc...)
  * programmable via API (cURL)
  * persistent
  * asynchronous
  * horizontally scalable
  * schema-enforcing safety and correctness
  
The DtLab API is the lowest level of interaction with the software that hosts the digital twins.

The building blocks of the DtLab system are:

  1. DtType
  2. DtActor
  3. DtOperator (UNDER CONSTRUCTION)
  4. DtLink (UNDER CONSTRUCTION)

The foundation of DtLab is that a DT (actor) state consists of a collection of the most recent named numeric observations of the DT's external analog.  IE: a DT for an oven would be a DtActor of a predefined DtType of "oven" that has a property called "temperature" that holds the last measured temperature of the oven.  The temperature observation is sent in a strict standard DTLab telemetry representation consisting of a "name", a datetime, and a numerical value.

Original data sources will not transmit observations in this terse format.  Often, observations are sent from external systems in complex messages containing hierarchical collections of information.  It is the responsibility of preprocessing systems like dtlab-ingest to decompose these complex verbose noisy inputs into the normalized timeseries observations expected by the DTs.

DTs may be programmed to maintain properties that reflect computed states as well as observations.  A DtType for the above oven example may also have a "tempurature_stability" property that is maintained by a DtOperator assigned to the DtType.  The operator will be executed inside the actor every time its state advances - the operator can maintain other properties and has access to the actor's journal so it can perform timeseries-assisted calculations for every update.

DTs can be linked (TBD - there is lifecycle and the normal hard graph issues like loop short circuits to figure out when applying links).  The links let DTs monitor the state of collections of other DTs and they themselves can in turn be linked.  This graph resulting from DtLink connections supports complex graph systems needed for IOT, AR, and pervasive computing.

Base URLs:

* <a href="http://localhost:8081">http://localhost:8081</a>

Email: <a href="mailto:ed@onextent.com">navicore</a> Web: <a href="https://somind.github.io/">navicore</a> 
License: <a href="https://github.com/SoMind/dtlab-scala-alligator/blob/master/LICENSE">MIT</a>

<h1 id="dtlab-alligator-ask">ask</h1>

## get-dtlab-alligator-typeId

<a id="opIdget-dtlab-alligator-typeId"></a>

> Code samples

```python
import requests
headers = {
  'Accept': 'application/json'
}

r = requests.get('http://localhost:8081/dtlab-alligator/type/{typer}', headers = headers)

print(r.json())

```

```shell
# You can also use wget
curl -X GET http://localhost:8081/dtlab-alligator/type/{typer} \
  -H 'Accept: application/json'

```

```javascript

const headers = {
  'Accept':'application/json'
};

fetch('http://localhost:8081/dtlab-alligator/type/{typer}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```java
URL obj = new URL("http://localhost:8081/dtlab-alligator/type/{typer}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`GET /dtlab-alligator/type/{typer}`

*get type*

Look up a type definition.

<h3 id="get-dtlab-alligator-typeid-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|typer|path|string|true|none|

> Example responses

> Definition of the type

```json
{
  "children": [
    "alternator_module",
    "starter_module"
  ],
  "created": "2020-07-23T01:30:24.783Z",
  "name": "machinery1",
  "props": [
    "temp",
    "speed"
  ]
}
```

<h3 id="get-dtlab-alligator-typeid-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Definition of the type|[Type](#schematype)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not Found|None|

<aside class="success">
This operation does not require authentication
</aside>

## get-dtlab-alligator-actorId

<a id="opIdget-dtlab-alligator-actorId"></a>

> Code samples

```python
import requests
headers = {
  'Accept': 'application/json'
}

r = requests.get('http://localhost:8081/dtlab-alligator/actor/{typeId}/{instanceId}', headers = headers)

print(r.json())

```

```shell
# You can also use wget
curl -X GET http://localhost:8081/dtlab-alligator/actor/{typeId}/{instanceId} \
  -H 'Accept: application/json'

```

```javascript

const headers = {
  'Accept':'application/json'
};

fetch('http://localhost:8081/dtlab-alligator/actor/{typeId}/{instanceId}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```java
URL obj = new URL("http://localhost:8081/dtlab-alligator/actor/{typeId}/{instanceId}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`GET /dtlab-alligator/actor/{typeId}/{instanceId}`

*get actor*

Look up a the state of an actor.

Note that OpenAPI 3.0 does not support repeating path components.  The OpenAPI position is that variable numbers of segments means they are optional - however, they are not optional in the DtLab system.  Fully specified paths point to a single unambiguous resource.  The OpenAPI team's solution to turn the segments into query params is not followed here.  To document a path for every supported level of parent / child relations would create massive duplication of documentation.  So know that DtPaths in DtLab support more parent child paths than the doc generation tools create examples for.  `/dtlab-alligator/actor/{grandParentTypeId}/{grandParentInstanceId}/{parentTypeId}/{parentInstanceId}/{typeId}/{instanceId}` is valid.

<h3 id="get-dtlab-alligator-actorid-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|format|query|string|false|none|
|typeId|path|string|true|the name of the type that can show up in a path|
|instanceId|path|string|true|the id of the instance of the type|

#### Enumerated Values

|Parameter|Value|
|---|---|
|format|named|
|format|pathed|

> Example responses

> OK

```json
[
  {
    "datetime": "2020-09-13T15:31:21.671Z",
    "idx": 0,
    "value": 2.1
  },
  {
    "datetime": "2020-07-26T17:25:21.803Z",
    "idx": 1,
    "value": 2.2
  }
]
```

```json
[
  {
    "datetime": "2020-09-07T17:32:58.407Z",
    "name": "height",
    "value": 5.5
  }
]
```

```json
[
  {
    "datetime": "2020-09-07T17:32:58.407Z",
    "name": "machinery.one.height",
    "value": 5.5
  }
]
```

> 200 Response

```xml
<?xml version="1.0" encoding="UTF-8" ?>
```

<h3 id="get-dtlab-alligator-actorid-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not Found|None|

<h3 id="get-dtlab-alligator-actorid-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[[Telemetry](#schematelemetry)]|false|none|[Telemetry is a time series entity - each change to a DT's state is journaled with a datetime.]|
|» Telemetry|[Telemetry](#schematelemetry)|false|none|Telemetry is a time series entity - each change to a DT's state is journaled with a datetime.|
|»» datetime|string|false|none|none|
|»» idx|integer|true|none|none|
|»» value|number|true|none|none|

<aside class="success">
This operation does not require authentication
</aside>

<h1 id="dtlab-alligator-tell">tell</h1>

## post-dtlab-alligator-type-typeId

<a id="opIdpost-dtlab-alligator-type-typeId"></a>

> Code samples

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json'
}

r = requests.post('http://localhost:8081/dtlab-alligator/type/{typer}', headers = headers)

print(r.json())

```

```shell
# You can also use wget
curl -X POST http://localhost:8081/dtlab-alligator/type/{typer} \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json'

```

```javascript
const inputBody = '{
  "props": [
    "temp",
    "speed"
  ],
  "children": [
    "alternator_module",
    "starter_module"
  ]
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json'
};

fetch('http://localhost:8081/dtlab-alligator/type/{typer}',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```java
URL obj = new URL("http://localhost:8081/dtlab-alligator/type/{typer}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`POST /dtlab-alligator/type/{typer}`

*create type*

create a new type with property names and allowable children types

> Body parameter

```json
{
  "props": [
    "temp",
    "speed"
  ],
  "children": [
    "alternator_module",
    "starter_module"
  ]
}
```

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<Type>
  <props>temp</props>
  <props>speed</props>
  <children>alternator_module</children>
  <children>starter_module</children>
</Type>
```

<h3 id="post-dtlab-alligator-type-typeid-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[Type](#schematype)|false|a copy of the successfully created type definition|
|typer|path|string|true|none|

> Example responses

> If successfully created, returned object will be updated with typeId and create datetime

```json
{
  "children": [
    "alternator_module",
    "starter_module"
  ],
  "created": "2020-07-26T18:09:06.592Z",
  "name": "machinery66",
  "props": [
    "temp",
    "speed"
  ]
}
```

> 409 Response

```json
{}
```

<h3 id="post-dtlab-alligator-type-typeid-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|If successfully created, returned object will be updated with typeId and create datetime|[Type](#schematype)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|None|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict - you must delete the previous entry before creating a type of the same name.|Inline|

<h3 id="post-dtlab-alligator-type-typeid-responseschema">Response Schema</h3>

<aside class="success">
This operation does not require authentication
</aside>

## delete-dtlab-alligator-type-typeId

<a id="opIddelete-dtlab-alligator-type-typeId"></a>

> Code samples

```python
import requests

r = requests.delete('http://localhost:8081/dtlab-alligator/type/{typer}')

print(r.json())

```

```shell
# You can also use wget
curl -X DELETE http://localhost:8081/dtlab-alligator/type/{typer}

```

```javascript

fetch('http://localhost:8081/dtlab-alligator/type/{typer}',
{
  method: 'DELETE'

})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```java
URL obj = new URL("http://localhost:8081/dtlab-alligator/type/{typer}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("DELETE");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`DELETE /dtlab-alligator/type/{typer}`

*delete dttype*

Delete the typeId.  

Note, this should be a developer operation and not available to normal client services.  Deleting a type makes instances of that type unavailable.  If you recreate a type and lookup an old instance created under the original type, that type with its old state will be resurected.  See the actor API to clean up instances of actors if that behavior is unwanted.

<h3 id="delete-dtlab-alligator-type-typeid-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|typer|path|string|true|none|

<h3 id="delete-dtlab-alligator-type-typeid-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|202|[Accepted](https://tools.ietf.org/html/rfc7231#section-6.3.3)|Accepted|None|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not Found|None|

<aside class="success">
This operation does not require authentication
</aside>

## post-dtlab-alligator-type-actorId

<a id="opIdpost-dtlab-alligator-type-actorId"></a>

> Code samples

```python
import requests
headers = {
  'Content-Type': 'application/json'
}

r = requests.post('http://localhost:8081/dtlab-alligator/actor/{typeId}/{instanceId}', headers = headers)

print(r.json())

```

```shell
# You can also use wget
curl -X POST http://localhost:8081/dtlab-alligator/actor/{typeId}/{instanceId} \
  -H 'Content-Type: application/json'

```

```javascript
const inputBody = '{
  "idx": 0,
  "value": 5.5
}';
const headers = {
  'Content-Type':'application/json'
};

fetch('http://localhost:8081/dtlab-alligator/actor/{typeId}/{instanceId}',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```java
URL obj = new URL("http://localhost:8081/dtlab-alligator/actor/{typeId}/{instanceId}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`POST /dtlab-alligator/actor/{typeId}/{instanceId}`

*update a single actor property*

Update an actor instance with attached property value indentified by the index of the property in the typeId.

Note that OpenAPI 3.0 does not support repeating path components.  The OpenAPI position is that variable numbers of segments means they are optional - however, they are not optional in the DtLab system.  Fully specified paths point to a single unambiguous resource.  The OpenAPI team's solution to turn the segments into query params is not followed here.  To document a path for every supported level of parent / child relations would create massive duplication of documentation.  So know that DtPaths in DtLab support more parent child paths than the doc generation tools create examples for.  `/dtlab-alligator/actor/{grandParentTypeId}/{grandParentInstanceId}/{parentTypeId}/{parentInstanceId}/{typeId}/{instanceId}` is valid.

> Body parameter

```json
{
  "idx": 0,
  "value": 5.5
}
```

<h3 id="post-dtlab-alligator-type-actorid-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|format|query|string|false|telemetry in idx, named, or pathed formats|
|body|body|[Telemetry](#schematelemetry)|false|The value of the property to update identified by its index in its type definition.|
|typeId|path|string|true|the name of the type that can show up in a path|
|instanceId|path|string|true|the id of the instance of the type|

#### Enumerated Values

|Parameter|Value|
|---|---|
|format|named|
|format|pathed|

<h3 id="post-dtlab-alligator-type-actorid-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|202|[Accepted](https://tools.ietf.org/html/rfc7231#section-6.3.3)|Accepted|None|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|None|
|422|[Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)|Unprocessable Entity (WebDAV)|None|

<aside class="success">
This operation does not require authentication
</aside>

## delete-dtlab-alligator-actor-typeId-instanceId

<a id="opIddelete-dtlab-alligator-actor-typeId-instanceId"></a>

> Code samples

```python
import requests

r = requests.delete('http://localhost:8081/dtlab-alligator/actor/{typeId}/{instanceId}')

print(r.json())

```

```shell
# You can also use wget
curl -X DELETE http://localhost:8081/dtlab-alligator/actor/{typeId}/{instanceId}

```

```javascript

fetch('http://localhost:8081/dtlab-alligator/actor/{typeId}/{instanceId}',
{
  method: 'DELETE'

})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```java
URL obj = new URL("http://localhost:8081/dtlab-alligator/actor/{typeId}/{instanceId}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("DELETE");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`DELETE /dtlab-alligator/actor/{typeId}/{instanceId}`

*delete dtactor*

remove all traces of the DT - removes the journal.

<h3 id="delete-dtlab-alligator-actor-typeid-instanceid-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|typeId|path|string|true|the name of the type that can show up in a path|
|instanceId|path|string|true|the id of the instance of the type|

<h3 id="delete-dtlab-alligator-actor-typeid-instanceid-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|202|[Accepted](https://tools.ietf.org/html/rfc7231#section-6.3.3)|Accepted|None|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not Found|None|

<aside class="success">
This operation does not require authentication
</aside>

# Schemas

<h2 id="tocS_Type">Type</h2>
<!-- backwards compatibility -->
<a id="schematype"></a>
<a id="schema_Type"></a>
<a id="tocStype"></a>
<a id="tocstype"></a>

```json
{
  "name": "string",
  "created": "string",
  "props": [
    "string"
  ],
  "children": [
    "string"
  ]
}

```

Type

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|name|string|false|none|none|
|created|string|false|none|none|
|props|[string]|false|none|none|
|children|[string]|false|none|none|

<h2 id="tocS_Telemetry">Telemetry</h2>
<!-- backwards compatibility -->
<a id="schematelemetry"></a>
<a id="schema_Telemetry"></a>
<a id="tocStelemetry"></a>
<a id="tocstelemetry"></a>

```json
{
  "datetime": "string",
  "idx": 0,
  "value": 0
}

```

Telemetry

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|datetime|string|false|none|none|
|idx|integer|true|none|none|
|value|number|true|none|none|

<h2 id="tocS_NamedTelemetry">NamedTelemetry</h2>
<!-- backwards compatibility -->
<a id="schemanamedtelemetry"></a>
<a id="schema_NamedTelemetry"></a>
<a id="tocSnamedtelemetry"></a>
<a id="tocsnamedtelemetry"></a>

```json
{
  "datetime": "string",
  "name": "string",
  "value": 0
}

```

NamedTelemetry

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|datetime|string|false|none|none|
|name|string|true|none|none|
|value|number|true|none|none|

