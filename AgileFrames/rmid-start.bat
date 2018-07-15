@echo off
title RMI Daemon -- Setup --                       -
echo.
echo.

call setenv

set RMID_LOG_DIR=%LOG_DIR%rmid_log
set RMID_POLICY=%POLICY_ALL%

"%JAVA_HOME%\bin\java.exe" -version
title RMI Daemon  
set CMD="%JAVA_HOME%bin\rmid.exe" -J-Djava.security.policy=%RMID_POLICY% -log %RMID_LOG_DIR%
echo %CMD%
%CMD%


