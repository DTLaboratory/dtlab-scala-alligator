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

DtLab is an actor environment that enables the instantiation of DTs (digital twins) with persistence and messaging and basic schema checking.

Base URLs:

* <a href="http://localhost:8081">http://localhost:8081</a>

Email: <a href="mailto:ed@onextent.com">navicore</a> 
License: <a href="https://github.com/SoMind/dtlab-scala-alligator/blob/master/LICENSE">MIT</a>

<h1 id="dtlab-alligator-default">Default</h1>

## delete-dtlab-alligator-type-typeId

<a id="opIddelete-dtlab-alligator-type-typeId"></a>

> Code samples

```python
import requests

r = requests.delete('http://localhost:8081/dtlab-alligator/type/{typeId}')

print(r.json())

```

```shell
# You can also use wget
curl -X DELETE http://localhost:8081/dtlab-alligator/type/{typeId}

```

```javascript

fetch('http://localhost:8081/dtlab-alligator/type/{typeId}',
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
URL obj = new URL("http://localhost:8081/dtlab-alligator/type/{typeId}");
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

`DELETE /dtlab-alligator/type/{typeId}`

Delete the typeId.  

Note, this should be a developer operation and not available to normal client services.  Deleting a type makes instances of that type unavailable.  If you recreate a type and lookup an old instance created under the original type, that type with its old state will be resurected.  See the actor API to clean up instances of actors if that behavior is unwanted.

<h3 id="delete-dtlab-alligator-type-typeid-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|typeId|path|string|true|the name of the type that can show up in a path|

<h3 id="delete-dtlab-alligator-type-typeid-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|202|[Accepted](https://tools.ietf.org/html/rfc7231#section-6.3.3)|Accepted|None|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not Found|None|

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

<h1 id="dtlab-alligator-ask">ask</h1>

## get-dtlab-alligator-typeId

<a id="opIdget-dtlab-alligator-typeId"></a>

> Code samples

```python
import requests
headers = {
  'Accept': 'application/json'
}

r = requests.get('http://localhost:8081/dtlab-alligator/type/{typeId}', headers = headers)

print(r.json())

```

```shell
# You can also use wget
curl -X GET http://localhost:8081/dtlab-alligator/type/{typeId} \
  -H 'Accept: application/json'

```

```javascript

const headers = {
  'Accept':'application/json'
};

fetch('http://localhost:8081/dtlab-alligator/type/{typeId}',
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
URL obj = new URL("http://localhost:8081/dtlab-alligator/type/{typeId}");
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

`GET /dtlab-alligator/type/{typeId}`

*get type*

Look up a type definition.

<h3 id="get-dtlab-alligator-typeid-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|typeId|path|string|true|the name of the type that can show up in a path|

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

Note that OpenAPI 3.0 does not support repeating path components that are very natural in REST.  They feel variable numbers of segments means they are optional - they are not optional at all in the DtLab system.  They are the way to point to the resource, making them correct use of path segments.  The OpenAPI team's solution to turn the segents into query params is hacky and not followed here.  To document a path for every supported level of parent / child relations would create massive duplication of documentation.  So know that DtPaths in DtLab support deeper parent child paths than the spec creates examples for.  `/dtlab-alligator/actor/{grandParentTypeId}/{grandParentInstanceId}/{parentTypeId}/{parentInstanceId}/{typeId}/{instanceId}` is valid.

<h3 id="get-dtlab-alligator-actorid-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|typeId|path|string|true|the name of the type that can show up in a path|
|instanceId|path|string|true|the id of the instance of the type|

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

r = requests.post('http://localhost:8081/dtlab-alligator/type/{typeId}', headers = headers)

print(r.json())

```

```shell
# You can also use wget
curl -X POST http://localhost:8081/dtlab-alligator/type/{typeId} \
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

fetch('http://localhost:8081/dtlab-alligator/type/{typeId}',
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
URL obj = new URL("http://localhost:8081/dtlab-alligator/type/{typeId}");
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

`POST /dtlab-alligator/type/{typeId}`

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

<h3 id="post-dtlab-alligator-type-typeid-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[Type](#schematype)|false|a copy of the successfully created type definition|
|typeId|path|string|true|the name of the type that can show up in a path|

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
  "datetime": "string",
  "idx": 0,
  "value": 0
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

Note that OpenAPI 3.0 does not support repeating path components that are very natural in REST.  They feel variable numbers of segments means they are optional - they are not optional at all in the DtLab system.  They are the way to point to the resource, making them correct use of path segments.  The OpenAPI team's solution to turn the segents into query params is hacky and not followed here.  To document a path for every supported level of parent / child relations would create massive duplication of documentation.  So know that DtPaths in DtLab support deeper parent child paths than the spec creates examples for.  `/dtlab-alligator/actor/{grandParentTypeId}/{grandParentInstanceId}/{parentTypeId}/{parentInstanceId}/{typeId}/{instanceId}` is valid.

> Body parameter

```json
{
  "datetime": "string",
  "idx": 0,
  "value": 0
}
```

<h3 id="post-dtlab-alligator-type-actorid-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[Telemetry](#schematelemetry)|false|The value of the property to upate identified by its index in its type definition.|
|typeId|path|string|true|the name of the type that can show up in a path|
|instanceId|path|string|true|the id of the instance of the type|

<h3 id="post-dtlab-alligator-type-actorid-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|202|[Accepted](https://tools.ietf.org/html/rfc7231#section-6.3.3)|Accepted|None|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|None|
|422|[Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)|Unprocessable Entity (WebDAV)|None|

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

