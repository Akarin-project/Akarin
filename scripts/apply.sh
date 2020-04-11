#!/usr/bin/env bash

# get base dir regardless of execution location
basedir=$1

source "$basedir/scripts/functions.sh"

. $basedir/functions.sh

paperVer=$(cat current-paper)
gpgsign="$(git config commit.gpgsign || echo "false")"
echo "Rebuilding Forked projects.... "
function applyPatch {
    what=$1
    what_name=$(basename $what)
    target=$2
    branch=$3
    patch_folder=$4

    cd "$basedir/$what"
    git fetch --all
    git branch -f upstream "$branch" >/dev/null

    cd "$basedir"
    if [ ! -d  "$basedir/$target" ]; then
        mkdir "$basedir/$target"
        cd "$basedir/$target"
        git init
        git remote add origin "$5"
        cd "$basedir"
    fi
    cd "$basedir/$target"

    # Disable GPG signing before AM, slows things down and doesn't play nicely.
    # There is also zero rational or logical reason to do so for these sub-repo AMs.
    # Calm down kids, it's re-enabled (if needed) immediately after, pass or fail.
    git config commit.gpgsign false

    echo "Resetting $target to $what_name..."
    git remote rm upstream > /dev/null 2>&1
    git remote add upstream "$basedir/$what" >/dev/null 2>&1
    git checkout master 2>/dev/null || git checkout -b master
    git fetch upstream >/dev/null 2>&1
    git reset --hard upstream/upstream
    echo "  Applying patches to $target..."
    git am --abort >/dev/null 2>&1
    git am --3way --ignore-whitespace "$basedir/patches/$patch_folder/"*.patch
    if [ "$?" != "0" ]; then
        echo "  Something did not apply cleanly to $target."
        echo "  Please review above details and finish the apply then"
        echo "  save the changes with rebuildPatches.sh"
        exit 1
    else
        echo "  Patches applied cleanly to $target"
    fi
}

function enableCommitSigningIfNeeded {
    if [[ "$gpgsign" == "true" ]]; then
        git config commit.gpgsign true
    fi
}

    echo "Importing MC-DEV"
    ./scripts/importmcdev.sh "$basedir" || exit 1
(
    (applyPatch Paper/Paper-API ${FORK_NAME}-API HEAD api $API_REPO &&
    applyPatch Paper/Paper-Server ${FORK_NAME}-Server HEAD server $SERVER_REPO) || exit 1
    enableCommitSigningIfNeeded
) || (
    echo "Failed to apply patches"
    enableCommitSigningIfNeeded
    exit 1
) || exit 1
