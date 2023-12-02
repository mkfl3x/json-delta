![Build](https://img.shields.io/github/actions/workflow/status/mkfl3x/json-delta/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mkfl3x/json-delta?color=blue)](https://central.sonatype.com/artifact/io.github.mkfl3x/json-delta)

## json-delta
Java (Kotlin) library for comparing JSON objects/arrays. Based on [**Gson**](https://github.com/google/gson).  
It recursively checks each field for following mismatch types:
- Field/value type
- Field value content
- Missed fields
- Unexpected fields
- Array size
## How to use

### Gradle
Add following snippet to the **build.gradle** file `dependencies{}` section:
```groovy
implementation("io.github.mkfl3x:json-delta:1.3")
```

### Maven
Add following snippet to the **pom.xml** file `<dependencies>` section:
```xml
<dependency>
    <groupId>io.github.mkfl3x</groupId>
    <artifactId>json-delta</artifactId>
    <version>1.3</version>
</dependency>
```
## Features
You can customize `JsonDelta` instance with following features:
- `Feature.IGNORE_EXTRA_FIELDS` - Do not consider non-expected fields in 'actual' JSON  
  example: `expected {"x": 1} == actual {"x": 1, "y": 2}`


- `Feature.IGNORE_MISSED_FIELDS` - Do not consider missed fields in 'actual' JSON  
  example: `expected {"x": 1, "y": 2} == actual {"x": 1}`


- `Feature.IGNORE_NUMBERS_TYPE` - Do not consider is value 'float' or 'integer' and compare as is  
  example: `expected {"x": 1.0} == actual {"x": 1}`


- `Feature.IGNORE_STRING_CASE` - Do not consider case for string values  
  example: `expected {"x": "HeLlO"} == actual {"x": "hello"}`


- `Feature.CHECK_FIELDS_PRESENCE_ONLY` - Check only fields presence and ignore their values  
  example: `expected {"x": 1"} == actual {"x": 2}`


- `Feature.IGNORE_ARRAYS_ORDER` - Do not consider array elements order   
  example: `expected {"arr": [1, 2, 3]} == actual {"arr": [3, 2, 1]}`

For turning on or off use method `feature()`:
```java
jsonDelta.feature(Feature.IGNORE_EXTRA_FIELDS, true);  // turn on
jsonDelta.feature(Feature.IGNORE_EXTRA_FIELDS, false); // turn off
```
## Ignoring fields
You can ignore any number of compared JSON fields via `vararg ignoredFields` parameter of `compare()` method.  
There are two ways:
- Using explicit field path `root.object.field` or `root.array[2].field` for arrays
- Using **regex**, like this `root.array\[[1-3]\].\[[a,b]\]`

> - '**root**' means the top level of JSON object or array
> - all fields should be splitted by dots ( **.** )
> - array indexes should be placed in square brackets ( **[ ]** )

**<font color="red">Attention!</font>** Ignoring array indexes with turned on `Feature.IGNORE_ARRAYS_ORDER` will lead to throwing `IgnoreArrayIndexException`

**Example**:
```java
// expected
{
  "a": "hello",
  "b": 2,
  "c": [1, 2, 3, 4, 5]
}

// actual
{
  "a": null,
  "b": 2,
  "c": [1, 2, 3, 0, 0]
}

// let's assume that field "root.a" is flaky 
// and also we are interested only in first 3 elements of array "root.c"
        
// exlude fields with explicit field paths
jsonDelta.compare(expected, actual, "root.a", "root.c[4]", "root.c[5]");

// exlude fields with regular expressions
jsonDelta.compare(expected, actual, "root.a", "root.c\\[[^1-3]\\]");
```
## Report:
Method `compare()` returns  `JsonDeltaReport` object where you can find following fields:
- **equals** (`Boolean`): Comparison result (_true_ if JSONs are equals)
- **mismatches** (`List<Mismatch>`): List of all mismatches

And you can print it as string. Output example:
```text
Equals: false
Mismatches:
"root.a": Value mismatch. Expected: "ciao"; Actual: "hello"
"root.b.z[3]": Type mismatch. Expected: integer; Actual: float
```
## Quickstart
Let's take two JSON objects for comparison:
- First one we'll call **'expected'**
```json
{
    "a": "hello",
    "b": {
        "x": 1,
        "y": true,
        "z": [1, 2, 3]
    }
}
```
- Second one we'll call **'actual'**
```json
{
    "a": "hello",
    "b": {
        "x": 1,
        "y": false,
        "z": [1, 2, 3.0]
    },
    "c": "I'm unexpected field"
}
```
Then compare them and print report:
#### Java:
```java
JsonDelta jsonDelta = new JsonDelta()
    .feature(Feature.IGNORE_EXTRA_FIELDS, true)  // for exclude non-expected field "root.c"
    .feature(Feature.IGNORE_NUMBERS_TYPE, true); // for compare value with index 3 in array "root.b.z[3]" as is
JsonDeltaReport report = jsonDelta.compare(expected, actual, "root.b.y"); // comparison with excluded field "root.b.y"
System.out.println(report); // method toString() of JsonDeltaReport class is overridden
```
#### Kotlin:
```kotlin
val jsonDelta = JsonDelta()
    .feature(Feature.IGNORE_EXTRA_FIELDS, true) // for exclude non-expected field "root.c"
    .feature(Feature.IGNORE_NUMBERS_TYPE, true) // for compare value with index 3 in array "root.b.z[3]" as is
val report = jsonDelta.compare(expected, actual, "root.b.y") // comparison with excluded field "root.b.y"
println(report) // method toString() of JsonDeltaReport class is overridden
```
Method `compare()` is overloaded for following types combinations:
- String - String
- String - Object
- Object - String
- Object - Object
---
#### Thanks for using!