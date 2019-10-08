#!/bin/sh
########################################################################
# Usage: moran-run.sh [JVM OPTIONS] DRIVER_CLASS PROP_FILE1 [PROP_FILE2 ...]
########################################################################

if [ -z "${JAM_HOME}" ]
then
    echo "Environment variable JAM_HOME is not set; exiting."
    exit 1
fi

if [ -z "${MORAN_HOME}" ]
then
    echo "Environment variable MORAN_HOME is not set; exiting."
    exit 1
fi

SCRIPT=`basename $0`
JAMRUN=${JAM_HOME}/bin/jam-run.sh

# -------------------------------------------------
# Extract any JVM flags beginning with a hyphen "-"
# -------------------------------------------------

JVM_FLAGS=""

while [[ "$1" == -* ]]
do
    JVM_FLAGS="${JVM_FLAGS} $1"
    shift
done

if [ $# -lt 2 ]
then
    echo "Usage: $SCRIPT [JVM OPTIONS] DRIVER_CLASS PROP_FILE1 [PROP_FILE2 ...]"
    exit 1
fi

DRIVER_CLASS=$1
shift

$JAMRUN $MORAN_HOME $JVM_FLAGS $DRIVER_CLASS "$@"

