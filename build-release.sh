#!/usr/bin/env bash

########################################################################
#
#                       t-crowd build script
#
#                  <german.braun(a)fi.uncoma.edu.ar>
#
#   Build Requirements
#   - Java 8
#   - Maven
#   - git
#   - wget
#  gab@gab:~$ export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/
#  gab@gab:~$ export PATH=$JAVA_HOME/bin:$PATH
#  gab@gab:~$ echo $JAVA_HOME
#  https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-get-on-debian-8
#  
#  mvn clean verify -Dmaven.javadoc.skip=true
#  mvn clean dependency:copy-dependencies package -Dmaven.javadoc.skip=true
#  

# Run Java class from JAR: java -cp target/your_file.jar your.package.name.YourClass
# java -cp target/dependency/t-crowd-cli-4.0.0-SNAPSHOT.jar it.gilia.tcrowd.cli.TCrowd
#
# Test
# https://www.petrikainulainen.net/programming/testing/junit-5-tutorial-running-unit-tests-with-maven/
# mvn clean test
########################################################################

# user could invoke the script as 'sh build-release.sh'
if [ ! -n "$BASH" ]; then
    echo "Please run this script with bash or run it as ./$0"
    exit 1
fi

if type -p java; then
    JAVA=java
elif [[ -n "${JAVA_HOME}" ]] && [[ -x "${JAVA_HOME}/bin/java" ]]; then
    JAVA="${JAVA_HOME}/bin/java"
else
    echo "ERROR: Java is not installed!"
    exit 1
fi

echo '$ java -version'

${JAVA} -version || exit 1

echo ""

JAVA_VER=$(${JAVA} -version 2>&1 | sed 's/version "\(.*\)\.\(.*\)\..*"/\2/; 1q')
#echo version "$version"
#if [[ "$JAVA_VER" != "java 8" ]]; then
#    echo "ERROR: Java 8 is required for building Ontop! Current Java version: $JAVA_VER"
#    exit 1
#fi



# location for the build ROOT folder (i.e. the directory of this script)
BUILD_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# location for the build dependencies home
ONTOP_DEP_HOME=${BUILD_ROOT}/build/dependencies

echo ""
echo "========================================="
echo " Starting t-crowd build script ... "
echo "-----------------------------------------"
echo ""

#
# Start building the packages
#

cd ${BUILD_ROOT}

echo ""
echo "========================================="
echo " Cleaning                                "
echo "-----------------------------------------"
echo ""

mvn clean -q

echo ""
echo "========================================="
echo " Compiling                               "
echo "-----------------------------------------"
echo ""


mvn install -DskipTests -Dmaven.javadoc.skip=true -q || exit 1

echo "[INFO] Compilation completed"

VERSION=$(cat ${BUILD_ROOT}/target/classes/version.properties | sed 's/version=\(.*\)/\1/')


# Packaging the cli distribution
#
echo ""
echo "========================================="
echo " Building t-crowd CLI distribution package     "
echo "-----------------------------------------"
echo ""

mvn assembly:assembly -Dmaven.javadoc.skip=true
rm -fr ${ONTOP_CLI}
mkdir -p ${ONTOP_CLI}
echo "[INFO] Copying files..."
cp target/${ONTOP_CLI}-${VERSION}.zip ${ONTOP_CLI}

echo ""
echo "========================================="
echo " Done.                                   "
echo "-----------------------------------------"
echo ""
