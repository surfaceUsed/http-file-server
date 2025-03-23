# Servlet routing

When the server receives an HTTP request, it identifies the appropriate handler by extracting the HTTP method and URL, 
then matching them to predefined routes. These routes are managed by a `UrlRouter`, which directs requests to the correct 
handler (`Command` object).

The endpoint, acting as an entry point to the servers file system, is handled by an `HttpServlet` instance. The servlet
is initialized once and remains active throughout the servers lifecycle, reducing overhead. During initialization, it 
loads predefined URL mappings to ensure efficient request routing.

This design enables dynamic processing of HTTP methods (`GET`, `POST`, `PUT`, `DELETE`) while maintaining a structured, 
scalable, and modular routing mechanism. The structure ensures requests follow predefined URL rules, scalability allows efficient
handling of increasing traffic, and modularity enables seamless addition or modification of endpoints.

When the application is terminated, all active servlets are destroyed, resources are released, and any necessary modifications are saved.

If a request does not match a valid route, the server returns an appropriate error response (e.g., `404 Not Found`). 
Integrated logging enhances debugging and monitoring, improving reliability.
