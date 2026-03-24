@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
set MAVEN_WRAPPER_PROPERTIES=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.properties

for /f "tokens=2 delims==" %%a in ('findstr "distributionUrl" "%MAVEN_WRAPPER_PROPERTIES%"') do (
  set DISTRIBUTION_URL=%%a
)

set MAVEN_USER_HOME=%USERPROFILE%\.m2
if not exist "%MAVEN_USER_HOME%\wrapper\dists" mkdir "%MAVEN_USER_HOME%\wrapper\dists"

set MAVEN_DIST_BASENAME=apache-maven-3.9.6
set MAVEN_HOME=%MAVEN_USER_HOME%\wrapper\dists\%MAVEN_DIST_BASENAME%\%MAVEN_DIST_BASENAME%

if not exist "%MAVEN_HOME%" (
  set MAVEN_ZIP_FILE=%MAVEN_USER_HOME%\wrapper\dists\%MAVEN_DIST_BASENAME%-bin.zip
  powershell -Command "Invoke-WebRequest -Uri '%DISTRIBUTION_URL%' -OutFile '%MAVEN_ZIP_FILE%'"
  powershell -Command "Expand-Archive -Path '%MAVEN_ZIP_FILE%' -DestinationPath '%MAVEN_USER_HOME%\wrapper\dists'"
  del "%MAVEN_ZIP_FILE%"
)

"%MAVEN_HOME%\bin\mvn.cmd" %*
