# Moscow

## [Moco](https://github.com/dreamhead/moco)'s friends

Moscow is a tool for testing provider's API using Moco's [configuration file](https://github.com/dreamhead/moco/blob/master/moco-doc/apis.md). It is heavily influenced by [Consumer-Driven Contracts](http://www.martinfowler.com/articles/consumerDrivenContracts.html) pattern.

Moscow can turn Moco contracts into executable tests. It can be a TDD tool which can drive RESTful APIs.

## Why Moco and Moscow

Moco use [JSON to describe API](https://github.com/dreamhead/moco/blob/master/moco-doc/apis.md). With Moco it's very convenient to describe JSON based RESTful APIs. While [RAML](http://raml.org/) uses YAML, [API Blueprint](https://apiblueprint.org/) uses Markdown, they are not friendly to JSON.

Although [Swagger](http://swagger.io/) uses JSON, it focus on the schema just like RAML and API Blueprint. Moco use example instead of schema, I call it Contract by Example. The reason I prefer example over shema is from here: [SpecificationByExample](http://martinfowler.com/bliki/SpecificationByExample.html) and examples make contract driven development possible.

I already use Moco and Moscow on my projects. I hope Moscow can help you just like it helped me.

## Usages

### Basic Usages

Now you can use maven or gradle to get Moscow SNAPSHOT version. If you are using gradle:

```groovy
repositories {
    maven {
        url 'https://oss.sonatype.org/content/groups/public/'
    }
}

dependencies {
    testCompile 'com.github.macdao:moscow:0.1-SNAPSHOT'
}

```

1. put your json files into a folder, such as `src/test/resources/contracts`. One of your json files should looks like this:

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

  The `description` is used to identify the contract. One contract actually is a test case, so the description of the contract can follow the test naming rules. For example, you can use [BDD](https://en.wikipedia.org/wiki/Behavior_Driven_Development) style `$given_xxx_$when_xxx_$then_xxx` or [User story](https://en.wikipedia.org/wiki/User_story) style `as_$role_i_want_$goal`. I used `$role_can/cannot_$do_something[_when_$sometime]` style in a RESTful APIs project.
  
2. create an instance of ContractContainer with your contracts directory:

  ```java
  private static final ContractContainer contractContainer = new ContractContainer(Paths.get("src/test/resources/contracts"));
  ```

3. create an instance of ContractAssertion and do the test:

  ```java
    @Test
    public void should_response_text_foo() throws Exception {
        new ContractAssertion(contractContainer.findContracts("should_response_text_foo"))
                .setPort(12306)
                .assertContract();
    }
  ```
  The method `ContractContainer.findContracts` will return a contract list, that means you can assert 2 or more contracts with same description at the same time.

  `assertContract` will build request from contract, send it to server and compare the response with contract. It will compare status code, headers and body (if present in contracts). For headers Moscow only care about the ones present in contract, others in real response will be ignored.

### Path Matcher

In RESTful APIs, 201 reponse will return the location of new created resouce. The location usually cannot be predicted. Moscow use path matcher to achieve the goal.

```json
    "headers": {
        "Location": "http://localhost:{port}/bar/{bar-id}"
    }
```

`{bar-id}` is the new generated ID. You can get the ID for future usage.

```java
final String barId = new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .assertContract()
                .get("bar-id");
```

`{port}` is very special because it will be replaced with real port before assertion.

### Necessity mode

Sometimes you only care part of response body which means it's not necessary that the response body as same as contract exactly. For example Spring will return something like this when 401:

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

While you don't care about the timestamp, the message may be the only info you care about:

```json
    "response": {
        "status": 401,
        "json": {
            "message": "Full authentication is required to access this resource"
        }
    }
```

Moscow support this using `necessity mode`:

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

I'm influenced by [Performance testing as a first-class citizen](https://www.thoughtworks.com/radar/techniques/performance-testing-as-a-first-class-citizen) practice, so I put execution time limitation into Moscow.

```java
    @Test(expected = RuntimeException.class)
    public void request_text_bar5_should_response_timeout() throws Exception {
        new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .setExecutionTimeout(100)
                .assertContract();
    }
```

## Best Practice

### Group your json files

If your project has a lot of APIs, you will have a lot of json files. I prefer group APIs by URI which means all contracts has same URI should be put into one file. And URI exactly matches the file path.

For example, given contract root directory is `src/test/resources/contracts`, contracts `POST /api/users` and `GET /api/users` should be put in `src/test/resources/contracts/api/users.json`, contracts `GET /api/users/user-id-1` should be put in `src/test/resources/contracts/users/user-id-1.json`.

### Static ContractContainer

The creation of ContractContainer instance may be costly, so it should be reused.

### Use `TestName` rule to avoid duplicate

In JUnit, we can use `TestName` rule to get currect test method name.

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

### Use Superclass to avoid duplicate

Use a superclass to reduce duplicate. You can write code of creating ContractAssertion only once in a superclass like I did in [MyApiTest](https://github.com/macdao/moscow/blob/master/src/test/java/com/github/macdao/moscow/spring/MyApiTest.java). Also you can do this to contract names.

```java
public class MyApiTest extends ApiTestBase {
    @Test
    public void request_param_should_response_text_bar4() throws Exception {
        assertContract();
    }
}
```

### Spring Boot Integration

There is a [sample code](https://github.com/macdao/moscow/tree/master/src/test/java/com/github/macdao/moscow/spring) using Spring Boot. Spring's integration testing can auto start application in a servlet container so you don't need to care about the application starting.

### DB Migration

Because tests may change the server status/DB data, I re-migrate DB before tests. For example, I use [Flyway](http://flywaydb.org/):

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

## Not Supported Moco Features

Because we need to build requests from Moco contracts, so the matchers such as `xpaths` and `json_paths` will not be supported.

- request
 - version
 - cookies
 - forms
 - text.xml
 - file.xml
 - xpaths
 - text.json
 - file.json
 - json_paths
 - uri.match
 - uri.startsWith
 - uri.endsWith
 - uri.contain
 - exist
- response
 - file
 - path_resource
 - version
 - proxy
 - cookies
 - attachment
 - latency
- redirectTo
- mount

## Build

```
./startMoco
```

```
./gradlew clean build
```
