@echo off
title HTTP Daemon 4 Jini-DL -- Setup --                       -
echo.
echo.

call setenv

set HTTPD_PORT=8089

REM:  %JINI_LIB_DL% should not be quoted in command below.

"%JAVA_HOME%\bin\java.exe" -version
echo HTTP Daemon -- Start: java.exe -jar "%JINI_LIB%tools.jar" -port %HTTPD_PORT% -dir %JINI_LIB_DL% -verbose
"%JAVA_HOME%\bin\java.exe" -jar "%JINI_LIB%tools.jar" -port %HTTPD_PORT% -dir %JINI_LIB_DL% -verbose
