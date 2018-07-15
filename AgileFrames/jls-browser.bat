@echo off
title Lookup Service Browser -- Setup --

call setenv
set BOWSER_POLICY=%POLICY_ALL%


title Lookup Service Browser -- Start -- 
set CMD="%JAVA_HOME%bin\java" -Djava.security.policy=%BOWSER_POLICY% -Djava.rmi.server.codebase="http://localhost:8089/browser-dl.jar http://localhost:8089/jsk-dl.jar" -jar %JINI_LIB%browser.jar 
echo %CMD%
%CMD%

title Lookup Service Browser -- Started --
echo Press any key
pause > nul