#!/usr/bin/env bash

(
set -e
basedir="$(cd "$1" && pwd -P)"
workdir="$basedir/work"
paperbasedir="$basedir/work/Paper"
paperworkdir="$basedir/work/Paper/work"

if [ "$2" == "--setup" ] || [ "$3" == "--setup" ] || [ "$4" == "--setup" ]; then
	echo "[Akarin] Setup Paper.."
	(
		cd "$paperbasedir"
		./paper patch
	)
fi

echo "[Akarin] Ready to build"
(
	cd "$paperbasedir"
	echo "[Akarin] Touch sources.."
	
	cd "$paperbasedir"
	if [ "$2" == "--fast" ] || [ "$3" == "--fast" ] || [ "$4" == "--fast" ]; then
		echo "[Akarin] Test has been skipped"
		\cp -rf "$basedir/src" "$paperbasedir/Paper-Server/"
		\cp -rf "$basedir/pom.xml" "$paperbasedir/Paper-Server/"
		mvn clean install -DskipTests
	else
		rm -rf Paper-API/src
		rm -rf Paper-Server/src
		./paper patch
		\cp -rf "$basedir/src" "$paperbasedir/Paper-Server/"
		\cp -rf "$basedir/pom.xml" "$paperbasedir/Paper-Server/"
		mvn clean install
	fi
	
	minecraftversion=$(cat "$paperworkdir/BuildData/info.json"  | grep minecraftVersion | cut -d '"' -f 4)
	rawjar="$paperbasedir/Paper-Server/target/akarin-$minecraftversion.jar"
	\cp -rf "$rawjar" "$basedir/akarin-$minecraftversion.jar"
	
	echo ""
	echo "[Akarin] Build successful"
	echo "[Akarin] Migrated final jar to $basedir/akarin-$minecraftversion.jar"
)

)