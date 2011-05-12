# Copyright (c) 2011 Concurrent, Inc.

. `dirname $(cd ${0%/*} && echo $PWD/${0##*/})`/include.sh

describe "jar.inc"

it_runs_silently_if_mt_jar_path_is_set () {
  mt_jar_path=/
  OUTPUT=`include_dependencies jar`
  test "$OUTPUT" = ""
}

it_runs_silently_if_it_finds_multitool_jar () {
  TMPDIR=`mktemp -d /tmp/mt-jar-spec.XXXXXX`
  touch $TMPDIR/multitool-test.jar
  HERE_PATH=`dirname $(cd ${0%/*}/../../../.. && echo $PWD/${0##*/})`
  MT_PATH=.

  OUTPUT=`cd $TMPDIR && . $HERE_PATH/bin/functions/jar.inc`

  rm -rf $TMPDIR
  test "$OUTPUT" = ""
}

it_complains_if_it_cannot_find_multitool_jar () {
  TMPDIR=`mktemp -d /tmp/mt-jar-spec.XXXXXX`
  mt_jar_avoid_exit=1
  HERE_PATH=`dirname $(cd ${0%/*}/../../../.. && echo $PWD/${0##*/})`
  MT_PATH=.

  OUTPUT=`cd $TMPDIR && . $HERE_PATH/bin/functions/jar.inc`

  rm -rf $TMPDIR
  test "$OUTPUT" = "multitool.jar not found"
}