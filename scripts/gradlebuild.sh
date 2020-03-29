#!/usr/bin/env bash

(
set -e
version="master"
basedir="$(cd "$1" && pwd -P)"
workdir="$basedir/work"
paperbasedir="$basedir/work/Paper"
paperworkdir="$basedir/work/Paper/work"

# init
git submodule update --init --remote
cd "$basedir"

echo "[Akarin] Setup Paper.."
cd "$paperworkdir"
if [[ -d "Minecraft" ]]; then
	rm Minecraft/ -r
fi
git clone https://github.com/Akarin-project/Minecraft.git

echo "[Akarin] Ready to build"

cd "$paperbasedir"
echo "[Akarin] Touch sources.."
	
rm -rf Paper-API/src
rm -rf Paper-Server/src
./paper patch
\cp -rf "$basedir/api/src/main" "$paperbasedir/Paper-API/src/"
\cp -rf "$basedir/api/pom.xml" "$paperbasedir/Paper-API/"
\cp -rf "$basedir/src" "$paperbasedir/Paper-Server/"
\cp -rf "$basedir/pom.xml" "$paperbasedir/Paper-Server/"

cd "$basedir"
bash ./gradlew install
	
minecraftversion=$(cat "$paperworkdir/BuildData/info.json" | grep minecraftVersion | cut -d '"' -f 4)
rawjar="$paperbasedir/Paper-Server/target/akarin-$minecraftversion.jar"
\cp -rf "$rawjar" "$basedir/akarin-$minecraftversion.jar"
rawapi="$paperbasedir/Paper-API/target/akarin-api-1.14.4-R0.1-SNAPSHOT.jar"
\cp -rf "$rawapi" "$basedir/akarin-api-1.14.4-R0.1-SNAPSHOT.jar"
	
echo ""
echo "[Akarin] Build successful"
echo "[Akarin] Migrated the final jar to $basedir/"

)
