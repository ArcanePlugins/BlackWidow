#!/bin/bash

set -e

echo "+-------------+"
echo "| Set Version |"
echo "+-------------+"

read -p "What would you like to set the new version to? >" VERSION_NEW

mvn versions:set -DnewVersion="$VERSION_NEW"

read -p "Would you like to proceed with these changes? [y/n] >" PROCEEDING

if [[ "$PROCEEDING" == "y" ]]; then
  mvn versions:commit
  echo "Version updated."
else
  mvn versions:revert
  echo "Version reverted."
fi

echo "Ok"

set +e
