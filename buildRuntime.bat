REM Show module dependencies: eng-inerface
REM jdeps .\eng-interface\build\libs\filevinder-eng-interface-1.0.0-SNAPSHOT.jar

REM Show module dependencies: eng
REM jdeps --module-path .\eng-interface\build\libs\filevinder-eng-interface-1.0.0-SNAPSHOT.jar .\eng\build\libs\filevinder-eng-1.0.0-SNAPSHOT.jar

REM SET JARS=.\eng-interface\build\libs\filevinder-eng-interface-1.0.0-SNAPSHOT.jar
REM SET JARS=%JARS%;.\eng\build\libs\filevinder-eng-1.0.0-SNAPSHOT.jar
REM SET JARS=%JARS%;.\jdeps\commons-io-2.8.0\commons-io-2.8.0.jar
REM SET JARS=%JARS%;.\jdeps\controlsfx-11.1.0.jar
REM SET JARS=%JARS%;.\jdeps\javafx\javafx-sdk-15.0.1\lib

REM Show module dependencies: ui
REM jdeps --module-path %JARS% .\ui\build\libs\filevinder-ui-1.0.0-SNAPSHOT.jar

SET MODULES=.\jdeps\javafx\javafx-jmods-15.0.1
SET MODULES=%MODULES%;.\eng\build\libs\filevinder-eng-1.0.0-SNAPSHOT.jar
SET MODULES=%MODULES%;.\eng-interface\build\libs\filevinder-eng-interface-1.0.0-SNAPSHOT.jar
SET MODULES=%MODULES%;.\ui\build\libs\filevinder-ui-1.0.0-SNAPSHOT.jar
SET MODULES=%MODULES%;.\jdeps\controlsfx-11.1.0.jar
SET MODULES=%MODULES%;.\jdeps\commons-io-2.8.0\commons-io-2.8.0.jar

REM Create a module for commons-io
jdeps --generate-module-info . .\jdeps\commons-io-2.8.0\commons-io-2.8.0.jar 
javac --patch-module org.apache.commons.io=.\jdeps\commons-io-2.8.0\commons-io-2.8.0.jar ./org.apache.commons.io/module-info.java 
jar uf .\jdeps\commons-io-2.8.0\commons-io-2.8.0.jar -C org.apache.commons.io module-info.class

REM Create a custom Runtime wih JFX modules
SET REQUIRED_MODULES=org.filevinder.eng
SET REQUIRED_MODULES=%REQUIRED_MODULES%,org.filevinder.eng.interf
SET REQUIRED_MODULES=%REQUIRED_MODULES%,org.filevinder.ui
SET REQUIRED_MODULES=%REQUIRED_MODULES%,java.base,javafx.base
SET REQUIRED_MODULES=%REQUIRED_MODULES%,javafx.controls
SET REQUIRED_MODULES=%REQUIRED_MODULES%,javafx.graphics
SET REQUIRED_MODULES=%REQUIRED_MODULES%,javafx.web
SET REQUIRED_MODULES=%REQUIRED_MODULES%,jdk.jsobject
SET REQUIRED_MODULES=%REQUIRED_MODULES%,org.apache.commons.io
SET REQUIRED_MODULES=%REQUIRED_MODULES%,org.controlsfx.controls

rmdir /Q /S .\filevinder-rt
jlink --launcher customjrelauncher=org.filevinder.ui/org.filevinder.ui.Main --output filevinder-rt --module-path %MODULES% --add-modules %REQUIRED_MODULES%

REM Execute Runtime
CALL .\filevinder-rt\bin\customjrelauncher.bat
