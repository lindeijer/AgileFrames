@echo off
title RMI Daemon 4 Jini-DL -- Setup --                       -
echo.
echo.

call setenv

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_162\
set RMID_LOG_DIR=%LOG_DIR%rmid_log
set RMID_POLICY=%POLICY_ALL%

"%JAVA_HOME%\bin\java.exe" -version
title RMI Daemon 4 Jini-DL -- Start --  
set CMD="%JAVA_HOME%bin\rmid.exe" -J-Djava.security.policy=%RMID_POLICY% -log %RMID_LOG_DIR%
echo %CMD%
%CMD%


