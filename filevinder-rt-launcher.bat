@echo off

set JLINK_VM_OPTIONS=--add-opens org.filevinder.ui/org.filevinder.ui=javafx.graphics
set JLINK_VM_OPTIONS=%JLINK_VM_OPTIONS% --add-opens org.controlsfx.controls/impl.org.controlsfx.autocompletion=org.filevinder.ui
set JLINK_VM_OPTIONS=%JLINK_VM_OPTIONS% --add-opens javafx.base/com.sun.javafx.event=org.controlsfx.controls

set DIR=C:\source\filevinder\filevinder-rt\bin
"%DIR%\java" %JLINK_VM_OPTIONS% -m org.filevinder.ui/org.filevinder.ui.Main %*
