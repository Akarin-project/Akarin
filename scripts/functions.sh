#!/usr/bin/env bash
# CONFIG set
FORK_NAME="Akarin"
API_REPO=""
SERVER_REPO=""
PAPER_API_REPO=""
PAPER_SERVER_REPO=""
MCDEV_REPO=""

# Added Multithreading to builds
# By JosephWorks
mvncmd="mvn -T 1.5C"

gitcmd="git -c commit.gpgsign=false -c core.quotepath=false -c core.safecrlf=false -c i18n.commit.encoding=UTF-8 -c i18n.logoutputencoding=UTF-8"

# DIR configure
# resolve shell-specifics
case "$(echo "$SHELL" | sed -E 's|/usr(/local)?||g')" in
    "/bin/zsh")
        RCPATH="$HOME/.zshrc"
        SOURCE="${BASH_SOURCE[0]:-${(%):-%N}}"
    ;;
    *)
        RCPATH="$HOME/.bashrc"
        if [[ -f "$HOME/.bash_aliases" ]]; then
            RCPATH="$HOME/.bash_aliases"
        fi
        SOURCE="${BASH_SOURCE[0]}"
    ;;
esac

while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
    DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
    SOURCE="$(readlink "$SOURCE")"
    [[ "$SOURCE" != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
SOURCE=$([[ "$SOURCE" = /* ]] && echo "$SOURCE" || echo "$PWD/${SOURCE#./}")
scriptdir=$(dirname "$SOURCE")
basedir=$(dirname "$scriptdir")

function basedir {
    cd "$basedir"
}

function paperdir {
    cd "$basedir/Tuinity"
}

gitcmd() {
    $gitcmd "$@"
}

# COLOUR functions
color() {
    if [ $2 ]; then
        echo -e "\e[$1;$2m"
    else
        echo -e "\e[$1m"
    fi
}

colorend() {
    echo -e "\e[m"
}

function bashcolor {
    if [ $2 ]; then
        echo -e "\e[$1;$2m"
    else
        echo -e "\e[$1m"
    fi
}

function bashcolorend {
    echo -e "\e[m"
}

# GIT functions
gitstash() {
    STASHED=$($gitcmd stash  2>/dev/null|| return 0) # errors are ok
}

gitunstash() {
    if [[ "$STASHED" != "No local changes to save" ]] ; then
        $gitcmd stash pop 2>/dev/null|| return 0 # errors are ok
    fi
}

function gethead {
    basedir
    git log -1 --oneline
}

function gitpush {
    if [ "$(git config minecraft.push-${FORK_NAME})" == "1" ]; then
    echo "Push - $1 ($3) to $2"
    (
        basedir
        git remote rm script-push > /dev/null 2>&1
        git remote add script-push $2 >/dev/null 2>&1
        git push script-push $3 -f
    )
    fi
}

# PATCH functions
function cleanupPatches {
    cd "$1"
    for patch in *.patch; do
        gitver=$(tail -n 2 $patch | grep -ve "^$" | tail -n 1)
        diffs=$(git diff --staged $patch | grep -E "^(\+|\-)" | grep -Ev "(From [a-z0-9]{32,}|\-\-\- a|\+\+\+ b|.index|Date\: )")

        testver=$(echo "$diffs" | tail -n 2 | grep -ve "^$" | tail -n 1 | grep "$gitver")
        if [ "x$testver" != "x" ]; then
            diffs=$(echo "$diffs" | tail -n +3)
        fi

        if [ "x$diffs" == "x" ] ; then
            git reset HEAD $patch >/dev/null
            git checkout -- $patch >/dev/null
        fi
    done
}

function containsElement {
    local e
    for e in "${@:2}"; do
        [[ "$e" == "$1" ]] && return 0;
    done
    return 1
}
