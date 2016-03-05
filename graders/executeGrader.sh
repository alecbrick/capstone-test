#! /bin/bash
cd /grader

SCC_ID="jOdXT"
EGONET_ID="ONhZm"

do_unzip () {
  7z e -ozipfile $1 > /dev/null
  if [ ! $? -eq 0 ]; then
    echo "{ \"fractionalScore\": 0.0, \"feedback\":\"Could not unzip input file. The grader does not support files such as .rar - try using .zip.\" }"
    exit 0;
  fi
}
  

while [ $# -gt 1 ]
  do
    key="$1"
    case $key in
      partId)
        PARTID="$2"
        shift
        ;;
      userId)
        USERID="$2"
        shift
        ;;
      filename)
        ORIGINAL_FILENAME="$2"
        shift
        ;;
    esac
  shift
done

if [ "$PARTID" == "$EGONET_ID" ]; then
	FILENAME = "graph.grader.EgoGrader"
elif [ "$PARTID" == "$SCC_ID" ]; then
	FILENAME = "graph.grader.SCCGrader"
else
  echo "{ \"fractionalScore\": 0.0, \"feedback\":\"No partID matched: "$PARTID"\" }"
  exit 1
fi

if [ ! $? -eq 0 ]; then
  cp errorfile /grader
  python /grader/compile_error.py
  exit 0
fi

java "$FILENAME" > extra.out 2> err.out
if [ -s output.out ]; then
  cat output.out
else
  cp extra.out err.out /grader
  python /grader/no_output.py
  exit 0
fi
