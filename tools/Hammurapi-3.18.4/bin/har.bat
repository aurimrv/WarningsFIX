@echo off

REM   Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
REM   reserved.

REM   Pavel Vlasov: Originally Ant file modified to run Hammurapi

if not "%OS%"=="Windows_NT" goto win9xStart
:winNTStart
@setlocal

rem %~dp0 is name of current script under NT
set DEFAULT_HAMMURAPI_HOME=%~dp0

rem : operator works similar to make : operator
set DEFAULT_HAMMURAPI_HOME=%DEFAULT_HAMMURAPI_HOME%\..

if "%HAMMURAPI_HOME%"=="" set HAMMURAPI_HOME=%DEFAULT_HAMMURAPI_HOME%
set DEFAULT_HAMMURAPI_HOME=

rem Need to check if we are using the 4NT shell...
if "%@eval[2+2]" == "4" goto setup4NT

rem On NT/2K grab all arguments at once
set HAMMURAPI_CMD_LINE_ARGS=%*
goto doneStart

:setup4NT
set HAMMURAPI_CMD_LINE_ARGS=%$
goto doneStart

:win9xStart
rem Slurp the command line arguments.  This loop allows for an unlimited number of 
rem agruments (up to the command line limit, anyway).

set HAMMURAPI_CMD_LINE_ARGS=

:setupArgs
if %1a==a goto doneStart
set HAMMURAPI_CMD_LINE_ARGS=%HAMMURAPI_CMD_LINE_ARGS% %1
shift
goto setupArgs

:doneStart
rem This label provides a place for the argument list loop to break out 
rem and for NT handling to skip to.

rem find HAMMURAPI_HOME
if not "%HAMMURAPI_HOME%"=="" goto checkJava

rem check for Hammurapi in Program Files on system drive
if not exist "%SystemDrive%\Program Files\Hammurapi" goto checkSystemDrive
set HAMMURAPI_HOME=%SystemDrive%\Program Files\Hammurapi
goto checkJava

:checkSystemDrive
rem check for Hammurapi in root directory of system drive
if not exist %SystemDrive%\Hammurapi\nul goto checkCDrive
set HAMMURAPI_HOME=%SystemDrive%\ant
goto checkJava

:checkCDrive
rem check for Hammurapi in C:\Hammurapi for Win9X users
if not exist C:\Hammurapi\nul goto noHammurapiHome
set HAMMURAPI_HOME=C:\Hammurapi
goto checkJava

:noHammurapiHome
echo HAMMURAPI_HOME is not set and ant could not be located. Please set HAMMURAPI_HOME.
goto end

:checkJava
set _JAVACMD=%JAVACMD%
set LOCALCLASSPATH=%CLASSPATH%
for %%i in ("%HAMMURAPI_HOME%\lib\*.jar") do call "%HAMMURAPI_HOME%\bin\lcp.bat" %%i

for %%i in ("%HAMMURAPI_HOME%\lib.for.review\*.jar") do call "%HAMMURAPI_HOME%\bin\lcp.bat" %%i
for %%i in ("%HAMMURAPI_HOME%\lib.for.review\*.zip") do call "%HAMMURAPI_HOME%\bin\lcp.bat" %%i

for %%i in ("hammurapi.lib\*.jar") do call "%HAMMURAPI_HOME%\bin\lcp.bat" %%i
for %%i in ("hammurapi.lib\*.zip") do call "%HAMMURAPI_HOME%\bin\lcp.bat" %%i

if "%JAVA_HOME%" == "" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java
if not exist "%_JAVACMD%.exe" echo Error: "%_JAVACMD%.exe" not found - check JAVA_HOME && goto end
goto runHammurapi

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=java
echo.
echo Warning: JAVA_HOME environment variable is not set.
echo   If build fails because sun.* classes could not be found
echo   you will need to set the JAVA_HOME environment variable
echo   to the installation directory of java.
echo.

:runHammurapi
"%_JAVACMD%" -classpath "%HAMMURAPI_HOME%"\ant.jar;"%LOCALCLASSPATH%" -Dant.home="%HAMMURAPI_HOME%" %HAMMURAPI_OPTS% org.hammurapi.HammurapiArchiver %HAMMURAPI_ARGS% %HAMMURAPI_CMD_LINE_ARGS%
 
set LOCALCLASSPATH=
set _JAVACMD=
set HAMMURAPI_CMD_LINE_ARGS=

:winNTend
@endlocal

