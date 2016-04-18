# Moscow

## [Moco](https://github.com/dreamhead/moco)'s friends

Moscow is a tool for testing provider's API using Moco's [configuration file](https://github.com/dreamhead/moco/blob/master/moco-doc/apis.md). It is highly influenced by [Consumer-Driven Contracts](http://www.martinfowler.com/articles/consumerDrivenContracts.html) pattern.

Moscow can turn Moco contracts into executable tests. You can also use Moscow as a TDD tool to write RESTful APIs.

## Why Moco & Moscow

Moco uses [JSON to describe API](https://github.com/dreamhead/moco/blob/master/moco-doc/apis.md). With Moco, you can easily describe JSON-based RESTful APIs.

There are similar tools, such as [RAML](http://raml.org/) (with YAML) and [API Blueprint](https://apiblueprint.org/) (with Markdown). But they are not very friendly to JSON. [Swagger](http://swagger.io/) is also a JSON-based tool, but it also uses similar schema as RAML and API Blueprint.

Moco uses example ("Contract") otherthan schema. You can find the reason [here](http://martinfowler.com/bliki/SpecificationByExample.html): It makes Contract Driven Development possible!

Moco and Moscow already contributed on my projects. Hope Moscow can help you too!

## Usages

### Installation

You can get Moscow (SNAPSHOT version) by maven or gradle. To import by gradle:

```groovy
repositories {
    mavenCentral()
    maven {
        url 'https://oss.sonatype.org/content/groups/public/'
    }
}

dependencies {
    testCompile 'com.github.macdao:moscow:0.1-SNAPSHOT'
}

```

If you are using Spring Boot (`spring-boot-starter-web` for more specific) that's all. But if you aren't using Spring Boot and don't want to depend on it, that's OK, Moscow can run without Spring Framework. The only thing you have to do is adding the OkHttp:

```groovy
dependencies {
    testCompile 'com.github.macdao:moscow:0.1-SNAPSHOT'
    testRuntime 'com.squareup.okhttp3:okhttp:3.1.2'
}
```


### Basic Usages


1. Create contract json file and save it into target folder (i.e. `src/test/resources/contracts`).

  ```json
  [
      {
          "description": "should_response_text_foo",
          "response": {
              "text": "foo"
          }
      }
  ]
  ```

  Each contract is a test case, `description` is used to identify the contract. The description can follow TEST naming rules. Such as [BDD](https://en.wikipedia.org/wiki/Behavior_Driven_Development) style and [User story](https://en.wikipedia.org/wiki/User_story) style. I used `$role_can/cannot_$do_something[_when_$sometime]` style in a RESTful API project.
  
2. Create an instance of ContractContainer in contracts directory:

  ```java
  private static final ContractContainer contractContainer = new ContractContainer(Paths.get("src/test/resources/contracts"));
  ```

3. create an instance of ContractAssertion and call the `assertContract` method:

  ```java
    @Test
    public void should_response_text_foo() throws Exception {
        new ContractAssertion(contractContainer.findContracts("should_response_text_foo"))
                .setPort(12306)
                .assertContract();
    }
  ```

  The method `ContractContainer.findContracts` will return a contract list, which means you can assert multiple contracts with same description meanwhile.

  `assertContract` will build request from contract, send it to server and compare the response with contract. It will compare existed status code, headers and body. Moscow only considers headers present in contracts and ignore the rest.

### Path Matcher

Moscow uses path matcher to get created resource from `Location` header in RESTful APIs for 201 reponse.

```json
"headers": {
    "Location": "http://{host}:{port}/bar/{bar-id}"
}
```

`{bar-id}` is the new generated ID. You can get the ID for future usage:

```java
final String barId = new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .assertContract()
                .get("bar-id");
```

`{host}` and `{port}` is special as it will be replaced by real host and port before assertion. Both of them can be used in response json body.

Moscow also support the ID appear in the contract response body:

```json
"json": {
    "id": "{bar-id}"
}
```

### Necessity Mode

Not all the response body is necessary. For example, Spring returns the followings for 401 response:

```json
{
    "timestamp": 1455330165626,
    "status": 401,
    "error": "Unauthorized",
    "exception": "org.springframework.security.access.AccessDeniedException",
    "message": "Full authentication is required to access this resource",
    "path": "/api/users"
}
```

You may not need the timestamp, only message is necessary, so your contract would be:

```json
"response": {
    "status": 401,
    "json": {
        "message": "Full authentication is required to access this resource"
    }
}
```

Moscow can support it by `necessity mode`:

```java
@Test
public void request_text_bar4_should_response_foo() throws Exception {
    new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
            .setPort(12306)
            .setNecessity(true)
            .assertContract();
}
```

### Timeout

Inspired by [Performance testing as a first-class citizen](https://www.thoughtworks.com/radar/techniques/performance-testing-as-a-first-class-citizen), I put execution time limitation in Moscow.

```java
@Test(expected = RuntimeException.class)
public void request_text_bar5_should_response_timeout() throws Exception {
    new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
            .setPort(12306)
            .setExecutionTimeout(100)
            .assertContract();
}
```
### More Examples

<https://github.com/macdao/moscow/tree/master/src/test>

## Best Practice

### Group your json files

If there are many APIs in your project, it will be swarmed with json files. I prefer grouping APIs by URI into one file. The URI will exactly match the file path.

For example, given contract root directory is `src/test/resources/contracts`, contracts `POST /api/users` and `GET /api/users` should be put in `src/test/resources/contracts/api/users.json`, contracts `GET /api/users/user-id-1` should be put in `src/test/resources/contracts/users/user-id-1.json`.

### Static ContractContainer

ContractContainer instance can be reused to avoid duplcate.

### `TestName` Rule

In JUnit, using `TestName` rule can get current test method name.

```java
@Rule
public final TestName name = new TestName();

@Test
public void should_response_text_foo() throws Exception {
    new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
            .setPort(12306)
            .assertContract();
}
```

### Superclass

You can create ContractAssertion only once in superclass. Find examples [here](https://github.com/macdao/moscow/blob/master/src/test/java/com/github/macdao/moscow/spring/MyApiTest.java). It also works for contract names.

```java
public class MyApiTest extends ApiTestBase {
    @Test
    public void request_param_should_response_text_bar4() throws Exception {
        assertContract();
    }
}
```

### Spring Boot Integration

Here is a [sample](https://github.com/macdao/moscow/tree/master/src/test/java/com/github/macdao/moscow/spring) using Spring Boot. Spring's integration testing can auto start application in a servlet container so you don't need to worry about the application starting.

### DB Migration

Because tests may change the data in database, you can re-migrate database before tests. For example, I use [Flyway](http://flywaydb.org/):

```java
@Autowired
private Flyway flyway;

@Before
public void setUp() throws Exception {
    flyway.clean();
    flyway.migrate();
}
```

## Supported Moco Features

Moscow use a subset of Moco contracts:

- request
 - text (with method)
 - file (with method)
 - uri
 - queries
 - method (upper case)
 - headers
 - json (with method)
- response
 - text
 - status
 - headers
 - json
 - file

## Not Supported Moco Features

Because we need to build requests from Moco contracts, some matchers such as `xpaths` and `json_paths` is not supported.

- request
 - version
 - cookies
 - forms
 - text.xml
 - text.json
 - file.xml
 - file.json
 - xpaths
 - json_paths
 - uri.match
 - uri.startsWith
 - uri.endsWith
 - uri.contain
 - exist
- response
 - path_resource
 - version
 - proxy
 - cookies
 - attachment
 - latency
- redirectTo
- mount

## Build

1. start Moco for testing

  ```
  ./startMoco
  ```

2. build

  ```
  ./gradlew clean build
  ```
