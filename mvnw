#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file to
# you under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
# ----------------------------------------------------------------------------

set -e

# OS specific support.
cygwin=false;
darwin=false;
mingw=false
case "$(uname)" in
  CYGWIN*) cygwin=true ;;
  DARWIN*) darwin=true ;;
  MINGW*) mingw=true;;
esac

## Path to the JVM
if [ -n "$JAVA_HOME" ] ; then
  JAVACMD="$JAVA_HOME/bin/java"
  if [ ! -x "$JAVACMD" ] ; then
    die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
  fi
else
  JAVACMD="$(which java)" || die "ERROR: JAVA_HOME not set and java not in PATH"
fi

# Find the project base dir
MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-}"
if [ -z "$MAVEN_PROJECTBASEDIR" ] ; then
  _BASEDIR_CANDIDATE="$PWD"
  while true; do
    if [ -f "$_BASEDIR_CANDIDATE/mvnw" ] ; then
      MAVEN_PROJECTBASEDIR="$_BASEDIR_CANDIDATE"
      break
    fi
    _NEXT_BASEDIR="$(dirname "$_BASEDIR_CANDIDATE")"
    if [ "$_NEXT_BASEDIR" = "$_BASEDIR_CANDIDATE" ] ; then
      break
    fi
    _BASEDIR_CANDIDATE="$_NEXT_BASEDIR"
  done
  if [ -z "$MAVEN_PROJECTBASEDIR" ] ; then
    MAVEN_PROJECTBASEDIR="$PWD"
  fi
fi

MAVEN_WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"
DISTRIBUTION_URL=$(grep "^distributionUrl=" "$MAVEN_WRAPPER_PROPERTIES" | sed 's/^distributionUrl=//')
WRAPPER_URL=$(grep "^wrapperUrl=" "$MAVEN_WRAPPER_PROPERTIES" | sed 's/^wrapperUrl=//' 2>/dev/null || echo "")

MAVEN_USER_HOME="${MAVEN_USER_HOME:-$HOME/.m2}"
MAVEN_WRAPPER_JAR="$MAVEN_USER_HOME/wrapper/dists/maven-wrapper.jar"

# Download the wrapper jar if needed
if [ ! -f "$MAVEN_WRAPPER_JAR" ] ; then
  mkdir -p "$(dirname "$MAVEN_WRAPPER_JAR")"
  if command -v curl > /dev/null 2>&1 ; then
    curl -q -o "$MAVEN_WRAPPER_JAR" "$WRAPPER_URL" 2>/dev/null || true
  elif command -v wget > /dev/null 2>&1 ; then
    wget -q -O "$MAVEN_WRAPPER_JAR" "$WRAPPER_URL" || true
  fi
fi

MAVEN_DIST_BASENAME=$(basename "$DISTRIBUTION_URL" | sed "s/-bin\.zip//")
MAVEN_HOME="$MAVEN_USER_HOME/wrapper/dists/$MAVEN_DIST_BASENAME/$MAVEN_DIST_BASENAME"

# Download and install Maven if needed
if [ ! -d "$MAVEN_HOME" ] ; then
  MAVEN_ZIP_FILE="$MAVEN_USER_HOME/wrapper/dists/$MAVEN_DIST_BASENAME.zip"
  mkdir -p "$(dirname "$MAVEN_ZIP_FILE")"
  if command -v curl > /dev/null 2>&1 ; then
    curl -q -o "$MAVEN_ZIP_FILE" "$DISTRIBUTION_URL"
  elif command -v wget > /dev/null 2>&1 ; then
    wget -q -O "$MAVEN_ZIP_FILE" "$DISTRIBUTION_URL"
  fi
  unzip -q "$MAVEN_ZIP_FILE" -d "$(dirname "$MAVEN_HOME")"
  rm -f "$MAVEN_ZIP_FILE"
fi

MAVEN_CMD="$MAVEN_HOME/bin/mvn"
exec "$MAVEN_CMD" "$@"
