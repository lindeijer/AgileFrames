@echo off
title HTTPD Jini-DL -- Setup --
echo.
echo.

call setenv
set HTTPD_PORT=5000
REM:  %JINI_LIB_DL% should not be quoted in command below.

title HTTPD Jini-DL port=%HTTPD_PORT%
set CMD="%JAVA_HOME%\bin\java.exe" -jar "%JINI_LIB%tools.jar" -port %HTTPD_PORT% -dir %JINI_LIB_DL% -verbose
echo %CMD%
%CMD%

