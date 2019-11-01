#!/bin/bash
find "${HOME}/.log_cache" -mtime +10 -type f -exec rm "{}" \;
find "${HOME}/lvadmin/history" -mtime +10 -type f -exec rm -r "{}" \;
find /tmp -mtime +10 -type d -regex '^.*_log_history$' -exec rm -r "{}" \;
