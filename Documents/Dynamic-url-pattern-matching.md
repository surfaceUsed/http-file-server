# URL pattern matching

The server defines a set of generic URL structures that are matched up against specific client requests. These URLs are matched up against each other by comparing each URL segment with each other. 

**URL Split into Path and Query Components**

The generic server URL and specific client URL are split into URL path and URL query, and analyzed seperately. 

**Path matching**

The URL path is divided by `/` into segments. These segments are checked to see if they are either static (predefined) values or dynamic parameters (indicated by curly braces `{}`). The client URL path is then compared with the generic URL path. Static values must match exactly, while dynamic parameters are filled in by the clients URL.

An example of a URL path where `name` is a static parameter that has to match, and `{name}` is a dynamic parameter that is filled in by the client UTR:

Consider the following example:

Generic URL path.
```bash
name/{name}
```

Specific URL path.
```bash
name/file-name
```

In this case, the static parameter `name` must match exactly, while `{name}` is a dynamic parameter that will be filled by the client URL (in this case, `file-name`). This allows the server to handle dynamic values in the URL path, where placeholders in the generic URL are replaced by actual values from the client URL.

**Query matching**

The query part of the URL is split into key-value pairs using `&` to separate pairs, and `=` to separate keys and values.

Like the path, static key parameters must match exactly, while dynamic parameters (in curly braces) are replaced by the values from the client URL.

Generic URL query.
```bash
action={action}
```
Specific URL query.
```bash
action=download
```
Here, `action` is static and must match, while `{action}` is dynamic and gets replaced by `download`.

**Overall matching**

The `UrlStructureMatcher` class handles the matching process, ensuring both the path and query parts of the URL are correctly compared. It also checks that the lengths of the paths and query parameters match and that the correct number of static and dynamic parameters are present.

Example:
```bash
Generic URL: name/{name}?action={action}

Specific URL: name/file-name?action=download
```

If either the path or query doesn't match, the comparison returns false, indicating the client URL doesn't adhere to the expected URL structure.

**Key points**

- Dynamic URL patterns: Handles parameters in `{}` by matching them with corresponding values in the client URL.

- Path and Query Matching: Ensures both the path and query parameters match the structure and values expected by the server.

- Modularity: The class handles path and query validation independently, allowing for easy extensions or modifications.

In summary, this utility class enables the server to route requests dynamically, ensuring that both the URL path and query 
parameters conform to the expected structure defined by the server's URL patterns.
