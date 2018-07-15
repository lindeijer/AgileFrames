@echo off
title HTTPD VR AgileFrames -- Setup --
echo.
echo.

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_162\
set JINI_HOME=C:\Users\Gebruiker\eclipse-workspace\2.2.3\
set JINI_LIB=%JINI_HOME%lib\
set HTTPD_PORT=5005
set DIR=target

title HTTPD VR AgileFrames port=%HTTPD_PORT% dir=%DIR%
set CMD="%JAVA_HOME%\bin\java.exe" -jar "%JINI_LIB%tools.jar" -port %HTTPD_PORT% -dir %DIR% -verbose
echo %CMD%
%CMD%
