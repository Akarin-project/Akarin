#!/usr/bin/env bash

echo "[Akarin] State: Commit Upstream"

(
set -e

function changeLog() {
    base=$(git ls-tree HEAD $1  | cut -d' ' -f3 | cut -f1)
    cd $1 && git log --oneline ${base}...HEAD
}
paper=$(changeLog Paper)

updated=""
logsuffix=""
if [ ! -z "$paper" ]; then
    logsuffix="$logsuffix\n\nPaper Changes:\n$paper"
    if [ -z "$updated" ]; then updated="Paper"; else updated="$updated/Paper"; fi
fi
disclaimer="Upstream has released updates that appears to apply and compile correctly"

if [ ! -z "$1" ]; then
    disclaimer="$@"
fi

log="Updated Upstream ($updated)\n\n${disclaimer}${logsuffix}"

echo -e "$log" | git commit -F -

) || exit 1