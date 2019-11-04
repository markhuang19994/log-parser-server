#!/bin/bash
git pull
git reset --hard origin/master
"$(dirname "$0")"/remove_cache.sh
mvn spring-boot:run
