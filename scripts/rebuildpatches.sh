#!/usr/bin/env bash

echo "[Akarin] State: Rebuild Patches"

# get base dir regardless of execution location
basedir=$1

source "$basedir/scripts/functions.sh"
gitcmd="git -c commit.gpgsign=false -c core.safecrlf=false"

echo "Rebuild patch files from local sources.."
function savePatches {
    basedir
    mkdir -p $basedir/patches/$2
    if [ -d ".git/rebase-apply" ]; then
        # in middle of a rebase, be smarter
        echo "REBASE DETECTED - PARTIAL SAVE"
        last=$(cat ".git/rebase-apply/last")
        next=$(cat ".git/rebase-apply/next")
        declare -a files=("$basedir/patches/$2/"*.patch)
        for i in $(seq -f "%04g" 1 1 $last)
        do
            if [ $i -lt $next ]; then
                rm "${files[`expr $i - 1`]}"
            fi
        done
    else
        rm -rf $basedir/patches/$2/*.patch
    fi

    cd "$basedir/$1"
    $gitcmd format-patch --no-stat -N -o "$basedir/patches/$2" upstream/upstream >/dev/null
	basedir
    $gitcmd add -A "$basedir/patches/$2"
    echo "  Patches saved for $basedir to patches/$2"
}

savePatches ${FORK_NAME}-API api
savePatches ${FORK_NAME}-Server server
# gitpushproject