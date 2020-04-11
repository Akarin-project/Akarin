#!/usr/bin/env bash

# get base dir regardless of execution location
basedir=$1

source "$basedir/scripts/functions.sh"

paperVer=$(cat current-paper)

paperworkdir="$basedir/Paper/work"
minecraftversion=$(cat $paperworkdir/BuildData/info.json | grep minecraftVersion | cut -d '"' -f 4)
decompile="$paperworkdir/Minecraft/$minecraftversion/spigot"

# create dev dir
basedir
mkdir -p mc-dev/src/net/minecraft/server
cd mc-dev

# prepare to push
if [ ! -d ".git" ]; then
    gitcmd init
fi

# reset dev files to spigot
rm src/net/minecraft/server/*.java
cp $decompile/net/minecraft/server/*.java src/net/minecraft/server

# diff and only preserve new added files
paperserver="$basedir/Paper/Paper-Server/src/main/java/net/minecraft/server"
cd $basedir/mc-dev/src/net/minecraft/server/

for file in $(/bin/ls $paperserver)
do
    if [ -f "$file" ]; then
        rm -f "$file"
    fi
done

# push the dev project
cd $basedir/mc-dev
gitcmd add . -A
gitcmd commit . -m "$paperVer"
gitcmd tag -a "$paperVer" -m "$paperVer" 2>/dev/null
push . $MCDEV_REPO $paperVer
