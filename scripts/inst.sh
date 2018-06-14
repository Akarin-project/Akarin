#!/usr/bin/env bash

(
set -e
basedir="$pwd"

(git submodule update --init --remote && chmod +x scripts/build.sh && ./scripts/build.sh "$basedir" "$1" "$2" "$3") || (
	echo "Failed to build Akarin"
	exit 1
) || exit 1

)