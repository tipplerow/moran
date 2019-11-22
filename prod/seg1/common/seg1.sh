#!/bin/sh
########################################################################
# Runs a single-segment Moran simulation.
#
# Usage: seg1.sh REPORT_DIR STRUCT_PROP
#
# REPORT_DIR  - full path name of the report directory
# STRUCT_PROP - full path name of the spatial structure property file
########################################################################

if [ $# -ne 2 ]
then
    echo "Usage:" `basename $0` "REPORT_DIR STRUCT_PROP"
    exit 1
fi

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

REPORT_DIR=$1
STRUCT_PROP=$2

# Run the master script from the directory containing the data files
# so that they may be specified without an absolute path name...
cd `dirname $0`

${MORAN_HOME}/bin/moran-run.sh \
    -Djam.app.reportDir=$REPORT_DIR \
    moran.segment.SegmentCNDriver \
    $STRUCT_PROP seg1_common.prop
