#!/usr/bin/env bash

(
set -e
basedir="$(cd "$1" && pwd -P)"

(git submodule update --init --remote && git add . && git commit -m 'Upstream Paper') || (
	echo "Failed to upstream"
	exit 1
) || exit 1

)