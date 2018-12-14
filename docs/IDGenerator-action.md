# ID Generator Action

Description
-----------

The ID generator will generate UUIDs and set them as pipeline arguments, allowing stages later in
the pipeline to access them through macros. This is useful in situations where the same
unique ID is required in multiple stages in the pipeline.

The plugin takes a list of argument names as configuration. Each argument will be assigned a unique
ID. These arguments can then be access as macros later in the pipeline.

Example
-------

In this example, the user wants to insert a row into a table for every pipeline run.
The row will record when the pipeline began and also record when it completed.

To accomplish this, an ID generator is used to generate a single ID for an argument named 'run-id'.
This is followed by a DB action plugin that is configured to execute query:

insert into runs ('id', 'start') values (${run-id}, current_timestamp());

At the end of the pipeline, another DB action plugin is configured to execute query:

insert into runs ('id', 'end') values (${run-id}, current_timestamp());

Since the 'run-id' argument was generated in a single place, the DB plugins will modify the same row,
resulting in a single row for the pipeline run that records the start and end time.
