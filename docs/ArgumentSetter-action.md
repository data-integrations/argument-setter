# HTTP Argument Setter

Description
-----------

Performs an HTTP request to fetch arguments to set in the pipeline.

This is most commonly used when the structure of a pipeline is static,
and its configuration needs to be managed outside the pipeline itself.

The HTTP response must return the arguments in a list:

    {
        "arguments" : [
            { "name": "argument name", "value": "argument value" },
            ...
        ]
    }

Properties
----------

**URL:** URL to fetch arguments from.

**HTTP Method:** HTTP method to use when making the request.

**Request Body:** Body to use when making the request.

**Connection Timeout:** Maximum amount of time in milliseconds to wait for a connection to be established.

**Read Timeout:** Maximum amount of time in milliseconds to wait for a response after a connection has been established.

**Number of Retries:** Number of times to retry a failed request before failing the pipeline.

**Follow Redirects:** Whether to follow HTTP redirects.

**Request Headers:** Headers to use when making the request.
