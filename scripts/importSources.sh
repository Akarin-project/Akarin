#!/usr/bin/env bash

maintask=$2
if [[ $maintask == "0" ]]; then
    TASKTITLE="Import Sources"
else
    TASKTITLE="Import Sources (Subtask)"
fi

# SCRIPT HEADER start
basedir=$1
source "$basedir/scripts/functions.sh"
echo "  "
echo "----------------------------------------"
echo "  $(bashcolor 1 32)Task$(bashcolorend) - $TASKTITLE"
echo "  This will import unimported newly added/mod sources to Paper workspace"
echo "----------------------------------------"
# SCRIPT HEADER end

# For a description of this script, see updateUpstream.sh.
paperworkdir="$basedir/Tuinity/Paper/work"
paperserverdir="$basedir/Tuinity/Tuinity-Server"
papersrcdir="$paperserverdir/src/main/java"
papernmsdir="$papersrcdir/net/minecraft/server"

(
    # fast-fail if Paper not set
    if [ ! -d "$papernmsdir" ]; then
        echo "  $(bashcolor 1 31)Exception$(bashcolorend) - Paper sources not generated, run updateUpstream.sh to setup."
        exit 1
    fi
)

minecraftversion=$(cat "$basedir"/Tuinity/Paper/work/BuildData/info.json | grep minecraftVersion | cut -d '"' -f 4)
decompiledir=$paperworkdir/Minecraft/$minecraftversion/spigot

nms="net/minecraft/server"
export IMPORT_LOG="" # for commit message, list all files and source for libs
basedir

function importToPaperWorkspace {
    if [ -f "$papernmsdir/$1.java" ]; then
        # echo "  $(bashcolor 1 33)Skipped$(bashcolorend) - Already imported $1.java"
        return 0
    fi

    file="$1.java"
    target="$papernmsdir/$file"
    base="$decompiledir/$nms/$file"

    if [[ ! -f "$target" ]]; then
        export IMPORT_LOG="$IMPORT_LOG Import: $file\n";
        echo "Import: $file"
        cp "$base" "$target"
    fi
}

function importLibraryToPaperWorkspace {
    group=$1
    lib=$2
    prefix=$3
    shift 3
    for file in "$@"; do
        file="$prefix/$file"
        target="$papersrcdir/$file"
        targetdir=$(dirname "$target")
        mkdir -p "${targetdir}"
		
        base="$paperworkdir/Minecraft/$minecraftversion/libraries/${group}/${lib}/$file"
        if [ ! -f "$base" ]; then
            echo "  $(bashcolor 1 31)Exception$(bashcolorend) - Cannot find file $file.java of lib $lib in group $group to import, re-decomplie or remove the import."
            exit 1
        fi
		
        export IMPORT_LOG="$IMPORT_LOG Import: $file from lib $lib\n";
		echo "Import: $file ($lib)"
        sed 's/\r$//' "$base" > "$target" || exit 1
    done
}

(
    # Reset to last NORMAL commit if already have imported before
    cd "$paperserverdir"
    lastcommit=$(git log -1 --pretty=oneline --abbrev-commit)
    if [[ "$lastcommit" = *"Extra dev imports of Akarin"* ]]; then
        git reset --hard HEAD^
    fi
)

# Filter and import every files which have patch to modify
patchedFiles=$(cat patches/server/* | grep "+++ b/src/main/java/net/minecraft/server/" | sort | uniq | sed 's/\+\+\+ b\/src\/main\/java\/net\/minecraft\/server\///g' | sed 's/.java//g')

patchedFilesNonNMS=$(cat patches/server/* | grep "create mode " | grep -Po "src/main/java/net/minecraft/server/(.*?).java" | sort | uniq | sed 's/src\/main\/java\/net\/minecraft\/server\///g' | sed 's/.java//g')

(
    cd "$paperserverdir"
    $gitcmd fetch --all &> /dev/null
	# Create the upstream branch in Paper project with current state
    $gitcmd checkout master >/dev/null 2>&1 # possibly already in
	$gitcmd branch -D upstream &>/dev/null
	$gitcmd branch -f upstream HEAD && $gitcmd checkout upstream
)

basedir
for f in $patchedFiles; do
    containsElement "$f" ${patchedFilesNonNMS[@]}
    if [ "$?" == "1" ]; then
        if [ ! -f "$papersrcdir/$nms/$f.java" ]; then
            if [ ! -f "$decompiledir/$nms/$f.java" ]; then
				echo "  $(bashcolor 1 31)Exception$(bashcolorend) - Cannot find NMS file $f.java to import, re-decomplie or remove the import."
                exit 1
            else
                importToPaperWorkspace $f
            fi
        fi
    fi
done

# NMS import format:
# importToPaperWorkspace MinecraftServer

importToPaperWorkspace CommandGive
importToPaperWorkspace PathDestination

# Library import format (multiple files are supported):
# importLibraryToPaperWorkspace com.mojang datafixerupper com/mojang/datafixers/util Either.java

# Submit imports by commit with file descriptions
(
    cd "$paperserverdir"
    # rm -rf nms-patches
    git add . &> /dev/null
    echo -e "Extra dev imports of Akarin\n\n$IMPORT_LOG" | git commit src -F - &> /dev/null
	echo "  $(bashcolor 1 32)Succeed$(bashcolorend) - Sources have been imported to Paper/Paper-Server (branch upstream)"
	
    if [[ $maintask != "0" ]]; then # this is magical
	    echo "----------------------------------------"
		echo "  Subtask finished"
		echo "----------------------------------------"
	fi
)
