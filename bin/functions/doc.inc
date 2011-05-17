# Copyright (c) 2011 Concurrent, Inc.

module_depends jar help

route_match "^help doc \w+\$" mt_doc
route_before mt_doc mt_hadoop mt_jar

module_annotate doc about "describes a Cascading.Multitool command"

mt_doc_table ()
{
  names=`mt_jar_avoid_exit=1 mt_jar_usage | tail -n +5 | sed -e 's/^[ \t]*//' | cut -d " " -f1 | sort`
  set -- $names
  while [ $# -gt 0 ]
  do
    printf "  %-25s%-25s%-25s" "$1" "$2" "$3"
    echo
    shift 3
  done
}

module_annotate_block doc usage <<EOF
Examine the available operational parameters of Cascading.Multitool.

Usage:
  help doc [parameter]

Valid Arguments:
`mt_doc_table`
EOF

mt_doc_argument_usage ()
{
  module_annotate help banner
  echo
  
  name=$1
  shift
  description=$@
  
  cat <<-USAGE
$name: $description

Usage:
  multitool $name=[value]
USAGE
}

mt_doc_argument_error ()
{
  module_annotate help banner
  module_annotate doc usage | tail -n +5
  echo
  error `echo $@ | sed s/error:/ERROR/`
}

mt_doc ()
{
  request=`mt_jar_usage | egrep "^  $2 "`
  if [ -n "$request" ]
  then
    mt_doc_argument_usage $request
  else
    mt_doc_argument_error "ERROR Invalid argument: $2"
    exit 1
  fi
}