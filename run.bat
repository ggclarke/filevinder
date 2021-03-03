
call setEnv.bat

SET LOCAL_JFX_PATH=C:/javafx/javafx-sdk-15.0.1/lib

SET JFX_PATH=--module-path %LOCAL_JFX_PATH%
SET JFX_MODS=--add-modules javafx.controls,javafx.web
SET JFX_EXPORT=--add-exports javafx.base/com.sun.javafx.event=ALL-UNNAMED

java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1044 %JFX_EXPORT% %JFX_PATH% %JFX_MODS% -jar .\ui\build\libs\filevinder-ui-all-1.0.0-SNAPSHOT.jar