@echo off
title RMI Daemon 4 Jini-DL -- Setup --                       -
echo.
echo.

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_162\

echo stop
"%JAVA_HOME%\bin\rmid.exe" -stop
echo stopped

