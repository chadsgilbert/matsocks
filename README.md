# MATSOCKS

`matsocks` is a Java library and a MATLAB interface, which allow a user to invoke MATLAB commands on a remote MATLAB instance and view the results locally.

## Using matsocks

  1. Call `ant` to build the jar and `ant doc` to build the documentation.
  2. Add the jar to the MATLAB path by calling `javaaddpath $PATH_TO_JAR`
  3. Start the server by calling jSocketServer in the MATLAB instance to be controlled.
  4. java -jar matsocks.jar localhost 1235` runs the CLI-based interface for the local machine over port 1235.
  5. or call `jSocketClient` in another MATLAB instance to connect to the server

## Version history

This is version 0.1. It works and is super basic. How nice.