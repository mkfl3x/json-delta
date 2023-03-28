## What is it
Library for comparison JSON objects/arrays based on Gson

## How it works
JsonDelta recursively check each field for few mismatch types:
- Mismatch of value type
- Mismatch of value content
- Missed fields in object
- Unexpected fields in object [optional]
- Array size mismatch

You can configure your JsonDelta instance with following features:
- `Feature.IGNORE_EXTRA_FIELDS` - Ignoring all non-expected fields
- `Feature.CHECK_FIELDS_PRESENCE_ONLY` - Check only fields presence, ignoring their values

## How to use
### Gradle
Add following snippet to the **build.gradle** file `dependencies` section:
```
implementation("io.github.mkfl3x:json-delta:0.4-beta")
```
### Maven
Add following snippet to the **pom.xml** file `<dependencies>` section:
```
<dependency>
    <groupId>io.github.mkfl3x</groupId>
    <artifactId>json-delta</artifactId>
    <version>0.4-beta</version>
</dependency>
```
## Quick start
Let's take two json objects for comparison: 
- First one we'll call **'expected'**
```
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
```
{
    "a": "hello",
    "b": {
        "x": 1,
        "y": false,
        "z": [1, 2, 4]
    },
    "c": "I'm unexpected field"
}
```
Then compare them and print report:
#### Java:
```
JsonDelta jsonDelta = new JsonDelta(); // create JsonDelta instance
JsonDeltaReport report = jsonDelta.compare(expected, actual); // compare JSONs
System.out.print(report); // print report
```
#### Kotlin:
```
val jsonDelta = JsonDelta() // create JsonDelta instance
val report = jsonDelta.compare(expected, actual) // compare JSONs
print(report) // print report
```
#### Console output:
```
Status: failed
Mismatches:
 Field "root"
    - Object contains unexpected fields. Fields:"c"
```
Here we can see that **actual** JSON has unexpected field **"c"** on the top level (it's called **root**).<br>
### Functions:
Let's turn on ignoring unexpected fields feature for our **jsonDelta** and make comparison one more time
```
jsonDelta.featureOn(Feature.IGNORE_EXTRA_FIELDS); // use featureOff() for turn off feature
```
#### Console output:
```
Status: failed
Mismatches:
 Field "root.b.y"
    - Value mismatch. Expected: "true"; Actual: "false"
 Field "root.b.z[3]"
    - Value mismatch. Expected: "3"; Actual: "4"
```
Here we can see that **jsonDelta** ignored **"c"** field, dived deeper in our JSONs and found new errors.<br>
### Ignored fields
Let's assume that those problem fields have dynamic values and we can't know them values, so we ignore them.<br>
Compare one more time:
```
jsonDelta.compare(expected, actual, "root.b.y", "root.b.z[3]"); // third parameter is vararg, so ignore as much fields as you want 
```
#### Console output:
```
Status: success
```
Great! JSONs are compared.
