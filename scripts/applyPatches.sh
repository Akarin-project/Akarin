#!/usr/bin/env bash

# SCRIPT HEADER start
basedir=$1
source "$basedir/scripts/functions.sh"
echo "  "
echo "----------------------------------------"
echo "  $(bashcolor 1 32)Task$(bashcolorend) - Apply Patches"
echo "  This will apply all of Akarin patches on top of the Paper."
echo "  "
echo "  $(bashcolor 1 32)Subtask:$(bashcolorend)"
echo "  - Import Sources"
echo "  "
echo "  $(bashcolor 1 32)Modules:$(bashcolorend)"
echo "  - $(bashcolor 1 32)1$(bashcolorend) : API"
echo "  - $(bashcolor 1 32)2$(bashcolorend) : Server"
echo "----------------------------------------"
# SCRIPT HEADER end

needimport=$2

function applyPatch {
    baseproject=$1
    basename=$(basename $baseproject)
    target=$2
    branch=$3
    patch_folder=$4

    # Skip if that software have no patch
    haspatch=-f "$basedir/patches/$patch_folder/"*.patch >/dev/null 2>&1 # too many files
	if [ ! haspatch ]; then
	    echo "  $(bashcolor 1 33)($5/$6) Skipped$(bashcolorend) - No patch found for $target under patches/$patch_folder"
		return
	fi

    echo "  $(bashcolor 1 32)($5/$6)$(bashcolorend) - Setup upstream project.."
    cd "$basedir/$baseproject"
    $gitcmd fetch --all &> /dev/null
	# Create the upstream branch in Paper project with current state
    $gitcmd checkout master >/dev/null 2>&1 # possibly already in
	$gitcmd branch -D upstream &> /dev/null
	$gitcmd branch -f upstream "$branch" &> /dev/null && $gitcmd checkout upstream &> /dev/null
	
	if [[ $needimport != "1" ]]; then
	    if [ $baseproject != "Paper/Paper-API" ]; then
	        echo "  $(bashcolor 1 32)($5/$6)$(bashcolorend) - Import new introduced NMS files.."
	        basedir && $scriptdir/importSources.sh $basedir 1 || exit 1
		fi
    fi

    basedir
	# Create source project dirs
    if [ ! -d  "$basedir/$target" ]; then
        mkdir "$basedir/$target"
        cd "$basedir/$target"
        # $gitcmd remote add origin "$5"
    fi
    cd "$basedir/$target"
	$gitcmd init > /dev/null 2>&1

    echo "  "
	echo "  $(bashcolor 1 32)($5/$6)$(bashcolorend) - Reset $target to $basename.."
	# Add the generated Paper project as the upstream remote of subproject
    $gitcmd remote rm upstream &> /dev/null
    $gitcmd remote add upstream "$basedir/$baseproject" &> /dev/null
	# Ensure that we are in the branch we want so not overriding things
    $gitcmd checkout master &> /dev/null || $gitcmd checkout -b master &> /dev/null
    $gitcmd fetch upstream &> /dev/null
	# Reset our source project to Paper
    cd "$basedir/$target" && $gitcmd reset --hard upstream/upstream &> /dev/null
	echo "  "

	echo "  $(bashcolor 1 32)($5/$6)$(bashcolorend) - Apply patches to $target.."
	# Abort previous applying operation
    $gitcmd am --abort >/dev/null 2>&1
	# Apply our patches on top Paper in our dirs
    $gitcmd am --no-utf8 --3way --ignore-whitespace "$basedir/patches/$patch_folder/"*.patch

    if [ "$?" != "0" ]; then
        echo "  Something did not apply cleanly to $target."
        echo "  Please review above details and finish the apply then"
        echo "  save the changes with rebuildPatches.sh"
		echo "  or use 'git am --abort' to cancel this applying."
        echo "  $(bashcolor 1 33)($5/$6) Suspended$(bashcolorend) - Resolve the conflict or abort the apply"
		echo "  "
		cd "$basedir/$target"
        exit 1
    else
        echo "  $(bashcolor 1 32)($6/$6) Succeed$(bashcolorend) - Patches applied cleanly to $target"
		echo "  "
    fi
}

(applyPatch Tuinity/Tuinity-API ${FORK_NAME}-API HEAD api $API_REPO 0 2 &&
applyPatch Tuinity/Tuinity-Server ${FORK_NAME}-Server HEAD server $SERVER_REPO 1 2) || exit 1
