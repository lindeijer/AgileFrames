@echo off
title Transaction Manager -- Setup --  
  
call setenv
  
set START_POLICY=%POLICY_ALL%

title Transaction Manager -- Start --  

set CMD="%JAVA_HOME%\bin\java" ^
  -Djava.security.policy=%START_POLICY% ^
  -Djava.util.logging.config.file=logging.properties ^
  -jar %JINI_LIB%start.jar config/start-mahalo-group.config
echo %CMD%
%CMD%

title Transaction Manager