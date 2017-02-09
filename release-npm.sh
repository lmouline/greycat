#!/usr/bin/env bash
basedir="$PWD"

cd greycat/target/classes-npm
npm publish ./
cd "$basedir"

cd plugins/websocket/target/classes-npm
npm publish ./
cd "$basedir"