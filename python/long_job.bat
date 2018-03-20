REM To run in the backgroun use "START /B job.bat"
SET COUNT=0
:MyLoop
IF "%COUNT%" == "500" GOTO EndLoop
 ping google.com
 timeout /t 10
 echo "stdout"
 echo "stderr" 1>&2
 SET /A COUNT+=1
GOTO MyLoop
:EndLoop