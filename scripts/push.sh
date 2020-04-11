#!/usr/bin/env bash

# get base dir regardless of execution location
basedir=$1

source "$basedir/scripts/functions.sh"

minecraftversion=$(cat $basedir/Paper/work/BuildData/info.json | grep minecraftVersion | cut -d '"' -f 4)

basedir
gitpush ${FORK_NAME}-API $API_REPO master:$minecraftversion
gitpush ${FORK_NAME}-Server $SERVER_REPO master:$minecraftversion
