#!/usr/bin/env bash

echo "[Akarin] State: Make Akarinclip"

# Copied from https://github.com/PaperMC/Paper/blob/d54ce6c17fb7a35238d6b9f734d30a4289886773/scripts/paperclip.sh
# License from Paper applies to this file

set -e
basedir="$(cd "$1" && pwd -P)"
paperworkdir="$basedir/Paper/work"
mcver=$(cat "$paperworkdir/BuildData/info.json" | grep minecraftVersion | cut -d '"' -f 4)
serverjar="$basedir/Akarin-Server/target/akarin-$mcver.jar"
vanillajar="$paperworkdir/Minecraft/$mcver/$mcver.jar"

(
    cd "$paperworkdir/Paperclip"
    mvn clean package "-Dmcver=$mcver" "-Dpaperjar=$serverjar" "-Dvanillajar=$vanillajar"
)
cp "$paperworkdir/Paperclip/assembly/target/paperclip-${mcver}.jar" "$basedir/akarinclip-${mcver}.jar"

echo ""
echo ""
echo ""
echo "Build success!"
echo "Copied final jar to $(cd "$basedir" && pwd -P)/akarinclip-${mcver}.jar"
