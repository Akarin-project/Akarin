#!/usr/bin/env bash

# Initialize Paper upstream for Akarin

(
set -e
PS1="$"

paperCommit=$(grep -oP 'paperCommit = \K[a-f0-9]+' gradle.properties)

if [ ! -d ".gradle/caches/paperweight/upstreams/paper" ]; then
    mkdir -p .gradle/caches/paperweight/upstreams/paper
fi

cd .gradle/caches/paperweight/upstreams/paper

if [ ! -d ".git" ]; then
    git init
    git remote add origin https://github.com/PaperMC/Paper.git
fi

git fetch origin $paperCommit
git checkout -f $paperCommit

echo "Paper upstream initialized at commit $paperCommit"
) || exit 1 