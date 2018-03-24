
call setEnv.bat

java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1044 -jar .\ui\build\libs\filevinder-ui-all-1.0.0-SNAPSHOT.jar