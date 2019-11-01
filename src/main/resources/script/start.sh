#!/bin/bash
git pull
"$(dirname "$0")"/remove_cache.sh
sleep 99999999
