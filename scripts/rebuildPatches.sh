#!/usr/bin/env bash

# SCRIPT HEADER start
basedir=$1
source "$basedir/scripts/functions.sh"
echo "  "
echo "----------------------------------------"
echo "  $(bashcolor 1 32)Task$(bashcolorend) - Rebuild Patches"
echo "  This will diff the sources of Akarin and Paper to build patches."
echo "  "
echo "  $(bashcolor 1 32)Modules:$(bashcolorend)"
echo "  - $(bashcolor 1 32)1$(bashcolorend) : API"
echo "  - $(bashcolor 1 32)2$(bashcolorend) : Server"
echo "----------------------------------------"
# SCRIPT HEADER end

function savePatches {
    targetname=$1
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

    cd "$basedir/$targetname"
    $gitcmd format-patch --no-signature --zero-commit --full-index --no-stat -N -o "$basedir/patches/$2" upstream/upstream >/dev/null
	basedir
    $gitcmd add -A "$basedir/patches/$2"
	echo "  $(bashcolor 1 32)($3/$4)$(bashcolorend) - Patches saved for $targetname to patches/$2"
}

savePatches ${FORK_NAME}-API api 1 2
savePatches ${FORK_NAME}-Server server 2 2
# gitpushproject
