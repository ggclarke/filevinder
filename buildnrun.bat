call setEnv.bat
call gradle build -x test
call gradle allInOneJar
call run.bat