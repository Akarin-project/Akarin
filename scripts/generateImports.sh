#!/usr/bin/env bash

echo "[Akarin] State: Generate Imports"

# For a description of this script, see updateUpstream.sh.

# get base dir regardless of execution location
basedir=$1

source "$basedir/scripts/functions.sh"

paperworkdir="$basedir/Tuinity/Paper/work"
minecraftversion=$(cat $paperworkdir/BuildData/info.json | grep minecraftVersion | cut -d '"' -f 4)
decompile="$paperworkdir/Minecraft/$minecraftversion/spigot"

# create dev dir
basedir
mkdir -p mc-dev/src/net/minecraft/server
cd mc-dev

# prepare to push
if [ ! -d ".git" ]; then
    $gitcmd init
fi

# reset dev files to raw nms in spigot naming
rm src/net/minecraft/server/*.java
cp $decompile/net/minecraft/server/*.java src/net/minecraft/server

# diff and only preserve new added files
paperserver="$basedir/Tuinity/Tuinity-Server/src/main/java/net/minecraft/server"
cd $basedir/mc-dev/src/net/minecraft/server/

for file in $(/bin/ls $paperserver)
do
    if [ -f "$file" ]; then
        rm -f "$file"
    fi
done

# push the dev project
cd $basedir/mc-dev
$gitcmd add . -A
$gitcmd commit . -m "akarin-base"
$gitcmd tag -a "akarin-base" -m "akarin-base" 2>/dev/null
# gitpush . $MCDEV_REPO $paperVer
