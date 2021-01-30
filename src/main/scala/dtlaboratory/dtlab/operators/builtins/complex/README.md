# Complex Built-In Operators

Complex builtins can have the expectation that
all simple operators (both builtins and plugins)
have been applied to DtState before they are
called for each telemetry apply cycle.

Complex Operators need a yet-to-be-designed
dependency mechanism so that you can compose
a pipeline of functions for each telemetry
update.