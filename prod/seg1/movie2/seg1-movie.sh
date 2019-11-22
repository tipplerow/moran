#!/bin/sh

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

# Run the master script from the directory containing the data files
# so that they may be specified without an absolute path name...
cd `dirname $0`

REPORT_DIR=`pwd`

${MORAN_HOME}/bin/moran-run.sh \
    -Djam.app.reportDir=$REPORT_DIR \
    moran.segment.SegmentCNDriver \
    seg1_movie.prop
