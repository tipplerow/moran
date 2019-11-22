#!/bin/sh
cd `dirname $0`

REPORT_DIR=`pwd`
STRUCT_PROP=${REPORT_DIR}/seg1-hex.prop
COMMON_SCRIPT=../../common/seg1.sh

$COMMON_SCRIPT $REPORT_DIR $STRUCT_PROP
