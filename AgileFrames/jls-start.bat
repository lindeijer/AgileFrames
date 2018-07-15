@echo off
title Jini Lookup Service -- Setup --  
  
call setenv
  
set START_POLICY=%POLICY_ALL%

title Jini Lookup Service -- Start --  
    
set CMD="%JAVA_HOME%\bin\java" -Djava.security.policy=%START_POLICY% -jar %JINI_LIB%start.jar config/start-reggie.config
echo %CMD%
%CMD%

title Jini Lookup Service 