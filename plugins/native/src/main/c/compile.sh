#!/usr/bin/env bash
export JAVA_HOME="$(/usr/libexec/java_home -v 1.8)"
gcc -O2 -DNDEBUG -I $JAVA_HOME/include -I $JAVA_HOME/include/darwin -shared org_mwg_NativeHasherHelper.c -o ../resources/natives.dylib
