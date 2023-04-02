## json-delta
Java (Kotlin) library for comparing JSON objects/arrays. Based on [**Gson**](https://github.com/google/gson).  
It recursively checks each fields for following mismatch types:
- Field/value type
- Field value content
- Missed fields
- Unexpected fields
- Array size
## How to use

### Gradle
Add following snippet to the **build.gradle** file `dependencies{}` section:
```groovy
implementation("io.github.mkfl3x:json-delta:0.6-beta")
```

### Maven
Add following snippet to the **pom.xml** file `<dependencies>` section:
```xml
<dependency>
    <groupId>io.github.mkfl3x</groupId>
    <artifactId>json-delta</artifactId>
    <version>0.6-beta</version>
</dependency>
```
## Features
You can customize `JsonDelta` instance with following functions:
- `Feature.IGNORE_EXTRA_FIELDS` - Do not consider non-expected fields in 'actual' JSON
- `Feature.IGNORE_MISSED_FIELDS` - Do not consider missed fields in 'actual' JSON
- `Feature.IGNORE_NUMBERS_TYPE` - Do not consider is value 'float' or 'integer' and compare as is
- `Feature.CHECK_FIELDS_PRESENCE_ONLY` - Check only fields presence and ignore their values

For turning on or off use method `feature()`:
```java
jsonDelta.feature(Feature.IGNORE_EXTRA_FIELDS, true);  // turn on
jsonDelta.feature(Feature.IGNORE_EXTRA_FIELDS, false); // turn off
```
## Ignoring fields
You can ignore any number of compared JSON fields via `vararg ignoredFields` parameter of `compare()` method.
> ##### '$' symbol means JSON object or array provided for comparison

Please, use following syntax:
- For fields/arrays: `"$.some_field.some_sub_field"` - split field names by `.`
- For specific array element: `"$.some_field.some_array[3]"` - split field names by `.` and add index for array field with `[]`

Example:
```java
{
  "a": 1,
  "b": 2,
  "c": [
    {"x": 1, "y": 2},
    {"x": 3, "y": 4},
    {"x": 5, "y": 6}
  ]
}

// field "a" and field "x" of element with index 2 from array "c" will be excluded from comparison
jsonDelta.compare(expected, actual, "$.a", "$.c[2].x");
```
## Report:
Method `compare()` returns  `JsonDeltaReport` object where you can find following fields:
- **success** (`Boolean`): Comparison result (_success_ if JSONs are equals)
- **mismatches** (`List<Mismatch>`): List of all mismatches

And you can print it as string. Output example:
```text
Status: failed
Mismatches:
"$.a": Value mismatch. Expected: "ciao"; Actual: "hello"
"$.b.z[3]": Object types are mismatched. Expected: integer; Actual: float
```
## Quick start
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
    .feature(Feature.IGNORE_EXTRA_FIELDS, true)  // for exclude non-expected field "$.c"
    .feature(Feature.IGNORE_NUMBERS_TYPE, true); // for compare value with index 3 in array "$.b.z[3]" as is
// JsonDeltaReport report = jsonDelta.compare(expected, actual, "$.b.y"); // comparison with excluded field "$.b.y"
System.out.println(report); // method toString() of JsonDeltaReport class is overridden
```
#### Kotlin:
```kotlin
val jsonDelta = JsonDelta()
    .feature(Feature.IGNORE_EXTRA_FIELDS, true) // for exclude non-expected field "$.c"
    .feature(Feature.IGNORE_NUMBERS_TYPE, true) // for compare value with index 3 in array "$.b.z[3]" as is
val report = jsonDelta.compare(expected, actual, "$.b.y") // comparison with excluded field "$.b.y"
println(report) // method toString() of JsonDeltaReport class is overridden
```
Method `compare()` is overloaded for following types combinations:
- String String
- String Object
- Object String
- Object Object
---
#### Thanks for using!