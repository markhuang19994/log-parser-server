#!/bin/bash
git pull
"$(dirname "$0")"/remove_cache.sh
mvn spring-boot:run
