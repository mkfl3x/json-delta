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

## Example
Let's take two following json objects  
- expected:
```
{
    "a": "hello",
    "b": {
        "x": 1,
        "y": 2
    }
}
```
- actual:
```
{
    "a": "hello",
    "b": {
        "x": 1,
        "y": 3,
        "z": "I am extra field"
    }
}
```
Then compare them
```
val jsonDelta = JsonDelta() // Create jsonDelta instance
jsonDelta.featureOn(Feature.IGNORE_EXTRA_FIELDS) // Exclude all unexpected fields from comparison
val report: ComparisonReport = jsonDelta.compare(expected, actual) // Get comparison report
```
Then print report
```
print(report)

-- Output --
Status: failed
Mismatches:
 Field "root.b.y"
    - Value mismatch. Expected: "2"; Actual: "3"
```
## Ignoring fields
You can specify fields, which should not be checked with third parameter of `compare()` method like that:
```
val report = jsonDelta.compare(expected, actual, "root.b.y")
print(report)

-- Output --
Status: success
```
