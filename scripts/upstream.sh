#!/usr/bin/env bash

# exit immediately if a command exits with a non-zero status
set -e

# get base dir regardless of execution location
basedir=$1

source "$basedir/scripts/functions.sh"

git submodule update --init --recursive

if [[ "$2" == reset* ]]; then
    paperdir
    gitcmd fetch && gitcmd reset --hard origin/master
    basedir
    gitcmd add Paper
fi

# patch paper
paperVer=$(gethead Paper)
paperdir
./paper patch

cd "Paper-Server"
mcVer=$(mvn -o org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=minecraft_version | sed -n -e '/^\[.*\]/ !{ /^[0-9]/ { p; q } }')

basedir
"$basedir"/scripts/importmcdev.sh $1

minecraftversion=$(cat "$basedir"/Paper/work/BuildData/info.json | grep minecraftVersion | cut -d '"' -f 4)
version=$(echo -e "Paper: $paperVer\nmc-dev:$importedmcdev")
tag="${minecraftversion}-${mcVer}-$(echo -e $version | shasum | awk '{print $2}')"
echo "$tag" > "$basedir"/current-paper

"$basedir"/scripts/generateImports.sh $1
paperdir

function tag {
    cd $1
    if [ "$2" == "1" ]; then
        git tag -d "$tag" 2>/dev/null
    fi
    echo -e "$(date)\n\n$version" | git tag -a "$tag" -F - 2>/dev/null
}

echo "Tagging as $tag"
echo -e "$version"

forcetag=0
if [ "$(cat "$basedir"/current-paper)" != "$tag" ]; then
    forcetag=1
fi

tag "$basedir"/Paper/Paper-API $forcetag
tag "$basedir"/Paper/Paper-Server $forcetag

gitpush "$basedir"/Paper/Paper-API $PAPER_API_REPO $tag
gitpush "$basedir"/Paper/Paper-Server $PAPER_SERVER_REPO $tag
