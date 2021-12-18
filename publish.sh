#!/bin/sh
#stop script on failure
set -e

cd "klutter-annotations"
/bin/bash publish.sh
cd ".."

cd "klutter-annotations-jvm"
/bin/bash publish.sh
cd ".."

cd "klutter-core"
/bin/bash publish.sh
cd ".."

cd "klutter-plugins"
/bin/bash publish.sh
cd ".."