#!/usr/bin/env bash

# SCRIPT HEADER start
basedir=$1
source "$basedir/scripts/functions.sh"
echo "----------------------------------------"
echo "  $(bashcolor 1 32)Task$(bashcolorend) - Update Upstream"
echo "  This will update and patch Paper, importing necessary sources for patching."
#echo "  "
#echo "  $(bashcolor 1 32)Subtask:$(bashcolorend)"
#echo "  - Import Sources"
echo "  "
echo "  $(bashcolor 1 32)Projects:$(bashcolorend)"
echo "  - $(bashcolor 1 32)1$(bashcolorend) : Paper"
echo "  - $(bashcolor 1 32)2$(bashcolorend) : Akarin"
echo "----------------------------------------"
# SCRIPT HEADER end

# This script are capable of patching paper which have the same effect with renewing the source codes of paper to its corresponding remote/official state, and also are able to reset the patches of paper to its head commit to override dirty changes which needs a argument with --resetPaper.

# After the patching, it will copying sources that do no exist in the akarin workspace but referenced in akarin patches into our workspace, depending on the content of our patches, this will be addressed by calling importSources.sh.

# Following by invoking generateImports.sh,  it will generate new added/imported files of paper compared to the original decompiled sources into mc-dev folder under the root dir of the project, whose intention is unclear yet.

# exit immediately if a command exits with a non-zero status
set -e

subtasks=1
updatepaper=$2
if [ "$updatepaper" == "1" ]; then
    echo "  $(bashcolor 1 32)(0/$subtasks)$(bashcolorend) - Update Git submodules.."
    $gitcmd submodule update --init --remote
fi

if [[ "$2" == "--resetPaper" ]]; then
    echo "  $(bashcolor 1 32)(0/$subtasks)$(bashcolorend) - Reset Paper submodule.."
    paperdir
    $gitcmd fetch && $gitcmd reset --hard origin/master
    basedir
    $gitcmd add Paper
fi

# patch paper
echo "  $(bashcolor 1 32)(0/$subtasks)$(bashcolorend) - Apply patches of Tuinity.."
echo "  "
paperVer=$(gethead Tuinity)
paperdir
./tuinity patch

#cd "Paper-Server"
#mcVer=$($mvncmd -o org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=minecraft_version | sed -n -e '/^\[.*\]/ !{ /^[0-9]/ { p; q } }')

#echo "  $(bashcolor 1 32)(1/$subtasks)$(bashcolorend) - Import necessary sources.."
#basedir
#"$basedir"/scripts/importSources.sh $1

#minecraftversion=$(cat "$basedir"/Paper/work/BuildData/info.json | grep minecraftVersion | cut -d '"' -f 4)
#version=$(echo -e "Paper: $paperVer\nmc-dev:$importedmcdev")
#tag="${minecraftversion}-${mcVer}-$(echo -e $version | shasum | awk '{print $2}')"
#echo "$tag" > "$basedir"/current-paper

# "$basedir"/scripts/generateImports.sh $1 # unused

#echo "  $(bashcolor 1 32)(1/$subtasks)$(bashcolorend) - Tagging Paper submodules.."
#function tag {
#    paperdir && cd $1
#    if [ "$3" == "1" ]; then
#        git tag -d "$tag" 2>/dev/null
#    fi
#    echo -e "$(date)\n\n$version" | git tag -a "$tag" -F - 2>/dev/null
#}

#echo -e "$version"

#forcetag=0
#if [ "$(cat "$basedir"/current-paper)" != "$tag" ]; then
#    forcetag=1
#fi

#tag Paper-API $forcetag
#tag Paper-Server $forcetag

echo "  $(bashcolor 1 32)($subtasks/$subtasks) Succeed$(bashcolorend) - Submodules have been updated, regenerated and imported, run 'akarin patch' to test/fix patches, and by 'akarin rbp' to rebuild patches that fixed with the updated upstream."
echo "  "

# gitpush Paper-API $PAPER_API_REPO $tag
# gitpush Paper-Server $PAPER_SERVER_REPO $tag
