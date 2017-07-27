# Driver Argument Setter Action

Description
-----------

Performs an HTTP request some endpoint to get a driver specification. Based on the spec,
it will make another call to a nebula endpoint to get data about the dataset, which it will
use to set 'input.path', 'input.properties', 'directives', and 'output.schema' arguments
that can be used later on in the pipeline through macros.

