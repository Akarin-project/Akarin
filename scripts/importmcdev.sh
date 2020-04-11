#!/usr/bin/env bash

# get base dir regardless of execution location
basedir=$1

source "$basedir/scripts/functions.sh"

paperworkdir="$basedir/Paper/work"
minecraftversion=$(cat "$basedir"/Paper/work/BuildData/info.json | grep minecraftVersion | cut -d '"' -f 4)
decompiledir=$paperworkdir/Minecraft/$minecraftversion/spigot

nms="net/minecraft/server"
export MODLOG=""
basedir

export importedmcdev=""
function import {
    if [ -f "$basedir/Paper/Paper-Server/src/main/java/net/minecraft/server/$1.java" ]; then
        echo "ALREADY IMPORTED $1"
        return 0
    fi
    export importedmcdev="$importedmcdev $1"
    file="${1}.java"
    target="$basedir/Paper/Paper-Server/src/main/java/$nms/$file"
    base="$decompiledir/$nms/$file"

    if [[ ! -f "$target" ]]; then
        export MODLOG="$MODLOG  Imported $file from mc-dev\n";
        echo "$(bashColor 1 32) Copying $(bashColor 1 34)$base $(bashColor 1 32)to$(bashColor 1 34) $target $(bashColorReset)"
        cp "$base" "$target"
    else
        echo "$(bashColor 1 33) UN-NEEDED IMPORT STATEMENT:$(bashColor 1 34) $file $(bashColorReset)"
    fi
}

function importLibrary {
    group=$1
    lib=$2
    prefix=$3
    shift 3
    for file in "$@"; do
        file="$prefix/$file"
        target="$basedir/Paper/Paper-Server/src/main/java/$file"
        targetdir=$(dirname "$target")
        mkdir -p "${targetdir}"
        base="$paperworkdir/Minecraft/$minecraftversion/libraries/${group}/${lib}/$file"
        if [ ! -f "$base" ]; then
            echo "Missing $base"
            exit 1
        fi
        export MODLOG="$MODLOG  Imported $file from $lib\n";
        sed 's/\r$//' "$base" > "$target" || exit 1
    done
}

(
    cd Paper/Paper-Server/
    lastlog=$(git log -1 --oneline)
    if [[ "$lastlog" = *"Akarin extra mc-dev Imports"* ]]; then
        git reset --hard HEAD^
    fi
)


files=$(cat patches/server/* | grep "+++ b/src/main/java/net/minecraft/server/" | sort | uniq | sed 's/\+\+\+ b\/src\/main\/java\/net\/minecraft\/server\///g' | sed 's/.java//g')

nonnms=$(cat patches/server/* | grep "create mode " | grep -Po "src/main/java/net/minecraft/server/(.*?).java" | sort | uniq | sed 's/src\/main\/java\/net\/minecraft\/server\///g' | sed 's/.java//g')

for f in $files; do
    containsElement "$f" ${nonnms[@]}
    if [ "$?" == "1" ]; then
        if [ ! -f "$basedir/Paper/Paper-Server/src/main/java/net/minecraft/server/$f.java" ]; then
            if [ ! -f "$decompiledir/$nms/$f.java" ]; then
                echo "$(bashColor 1 31) ERROR!!! Missing NMS$(bashColor 1 34) $f $(bashColorReset)";
            else
                import $f
            fi
        fi
    fi
done

###############################################################################################
###############################################################################################
#################### ADD TEMPORARY ADDITIONS HERE #############################################
###############################################################################################
###############################################################################################

# import Foo

########################################################
########################################################
########################################################
#              LIBRARY IMPORTS
# These must always be mapped manually, no automatic stuff
#
#             # group    # lib          # prefix               # many files

importLibrary com.mojang datafixerupper com/mojang/datafixers/util Either.java

################
(
    cd Paper/Paper-Server/
    rm -rf nms-patches
    git add src -A
    echo -e "Akarin extra mc-dev Imports\n\n$MODLOG" | git commit --allow-empty src -F -
    exit 0
)
