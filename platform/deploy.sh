#!/bin/bash

secretKey=`head -c 32 /dev/urandom | base64`

set -x
mem=4000

export JAVA_OPTS="-Xmx${mem}m ${JAVA_OPTS}"
PROJECT_LOCATION="${PROJECT_LOCATION:-.}"

function build() {
    if [[ ! -f ${PROJECT_LOCATION}/target/universal/stage/bin/platform ]]; then
        sbt clean stage
    fi
}

function killRunningApp() {
    RUNNING_PID=${PROJECT_LOCATION}/target/universal/stage/RUNNING_PID
    if [[ -f ${RUNNING_PID} ]]; then
        sudo kill -9 `cat ${RUNNING_PID}` && \
             rm -f ${RUNNING_PID}
    fi
}

function setupLogs() {
    LOG_LOCATION=${LOG_LOCATION:-./logs}
    LOG_FILE=${LOG_LOCATION}/analytics.log

    if [[ -f ${LOG_FILE} ]]; then
        mv ${LOG_FILE} ${LOG_FILE}.old
    fi
}


function runApp() {
    nohup sudo -b JAVA_OPTS=${JAVA_OPTS} ${PROJECT_LOCATION}/target/universal/stage/bin/platform \
        -Dhttp.address=0.0.0.0 \
        -Dplay.http.secret.key=${secretKey} \
        -Dplay.filters.hosts.allowed.0=. \
        &> ${LOG_FILE}
}

build
killRunningApp
setupLogs
runApp
