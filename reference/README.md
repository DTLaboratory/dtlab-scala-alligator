---
title: dtlab alligator v1.0
language_tabs:
  - shell: Shell
  - http: HTTP
  - javascript: JavaScript
  - ruby: Ruby
  - python: Python
  - php: PHP
  - java: Java
  - go: Go
toc_footers: []
includes: []
search: true
highlight_theme: darkula
headingLevel: 2

---

<!-- Generator: Widdershins v4.0.1 -->

<h1 id="dtlab-alligator">dtlab alligator v1.0</h1>

> Scroll down for code samples, example requests and responses. Select a language for code samples from the tabs above or the mobile navigation menu.

manage DtLab actor runtime cloud

Base URLs:

* <a href="http://localhost:8081">http://localhost:8081</a>

Email: <a href="mailto:ed@onextent.com">navicore</a> 
License: <a href="https://github.com/SoMind/dtlab-scala-alligator/blob/master/LICENSE">MIT</a>

<h1 id="dtlab-alligator-create">create</h1>

## post-dtlab-alligator-type-typename

<a id="opIdpost-dtlab-alligator-type-typename"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8081/dtlab-alligator/type/{typename} \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json'

```

```http
POST http://localhost:8081/dtlab-alligator/type/{typename} HTTP/1.1
Host: localhost:8081
Content-Type: application/json
Accept: application/json

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

fetch('http://localhost:8081/dtlab-alligator/type/{typename}',
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

```ruby
require 'rest-client'
require 'json'

headers = {
  'Content-Type' => 'application/json',
  'Accept' => 'application/json'
}

result = RestClient.post 'http://localhost:8081/dtlab-alligator/type/{typename}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json'
}

r = requests.post('http://localhost:8081/dtlab-alligator/type/{typename}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Content-Type' => 'application/json',
    'Accept' => 'application/json',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('POST','http://localhost:8081/dtlab-alligator/type/{typename}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://localhost:8081/dtlab-alligator/type/{typename}");
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

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Content-Type": []string{"application/json"},
        "Accept": []string{"application/json"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("POST", "http://localhost:8081/dtlab-alligator/type/{typename}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`POST /dtlab-alligator/type/{typename}`

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

<h3 id="post-dtlab-alligator-type-typename-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[CreateTypeRequest](#schemacreatetyperequest)|false|a copy of the successfully created type definition|
|typename|path|string|true|the name of the type that can show up in a path|

> Example responses

> 201 Response

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

<h3 id="post-dtlab-alligator-type-typename-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|If successfully created, returned object will be updated with typename and create datetime|[Type](#schematype)|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict - you must delete the previous entry before creating a type of the same name.|Inline|

<h3 id="post-dtlab-alligator-type-typename-responseschema">Response Schema</h3>

<aside class="success">
This operation does not require authentication
</aside>

<h1 id="dtlab-alligator-ask">ask</h1>

## get-dtlab-alligator-typename

<a id="opIdget-dtlab-alligator-typename"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8081/dtlab-alligator/type/{typename} \
  -H 'Accept: application/json'

```

```http
GET http://localhost:8081/dtlab-alligator/type/{typename} HTTP/1.1
Host: localhost:8081
Accept: application/json

```

```javascript

const headers = {
  'Accept':'application/json'
};

fetch('http://localhost:8081/dtlab-alligator/type/{typename}',
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

```ruby
require 'rest-client'
require 'json'

headers = {
  'Accept' => 'application/json'
}

result = RestClient.get 'http://localhost:8081/dtlab-alligator/type/{typename}',
  params: {
  }, headers: headers

p JSON.parse(result)

```

```python
import requests
headers = {
  'Accept': 'application/json'
}

r = requests.get('http://localhost:8081/dtlab-alligator/type/{typename}', headers = headers)

print(r.json())

```

```php
<?php

require 'vendor/autoload.php';

$headers = array(
    'Accept' => 'application/json',
);

$client = new \GuzzleHttp\Client();

// Define array of request body.
$request_body = array();

try {
    $response = $client->request('GET','http://localhost:8081/dtlab-alligator/type/{typename}', array(
        'headers' => $headers,
        'json' => $request_body,
       )
    );
    print_r($response->getBody()->getContents());
 }
 catch (\GuzzleHttp\Exception\BadResponseException $e) {
    // handle exception or api errors.
    print_r($e->getMessage());
 }

 // ...

```

```java
URL obj = new URL("http://localhost:8081/dtlab-alligator/type/{typename}");
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

```go
package main

import (
       "bytes"
       "net/http"
)

func main() {

    headers := map[string][]string{
        "Accept": []string{"application/json"},
    }

    data := bytes.NewBuffer([]byte{jsonReq})
    req, err := http.NewRequest("GET", "http://localhost:8081/dtlab-alligator/type/{typename}", data)
    req.Header = headers

    client := &http.Client{}
    resp, err := client.Do(req)
    // ...
}

```

`GET /dtlab-alligator/type/{typename}`

*get type*

Look up a type definition.

<h3 id="get-dtlab-alligator-typename-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|typename|path|string|true|the name of the type that can show up in a path|

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

<h3 id="get-dtlab-alligator-typename-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Definition of the type|[Type](#schematype)|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not Found|None|

<aside class="success">
This operation does not require authentication
</aside>

# Schemas

<h2 id="tocS_CreateTypeRequest">CreateTypeRequest</h2>
<!-- backwards compatibility -->
<a id="schemacreatetyperequest"></a>
<a id="schema_CreateTypeRequest"></a>
<a id="tocScreatetyperequest"></a>
<a id="tocscreatetyperequest"></a>

```json
{
  "props": [
    "string"
  ],
  "children": [
    "string"
  ]
}

```

CreateTypeRequest

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|props|[string]|false|none|none|
|children|[string]|false|none|none|

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

