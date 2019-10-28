#!/usr/bin/env bash

trap "killall background" EXIT

main() {
  BASE_URL=${1:-http://localhost:17778}

  start_server
  if [[ $? -ne 0 ]]; then new_line_and_exit 1; fi

  setMainArgs
  if [[ $? -ne 0 ]]; then new_line_and_exit 1; fi

  execInstruct
  if [[ $? -ne 0 ]]; then new_line_and_exit 1; fi
}

function start_server() {
  echo 'server start'
}

function setMainArgs() {
  read -rp 'Main config path:' main_config_path
  send_post "${BASE_URL}/set/main_args" "${main_config_path}"
}

function execInstruct() {
  state=''
  while IFS= read -erp "lvadmin@${state}>> " args_arr; do
    history -s "$args_arr"
    if [[ "${args_arr}" == exit ]]; then
      exit 0
    elif [[ "${args_arr}" == method ]]; then
      state='method'
    elif [[ "${args_arr}" == life ]]; then
      state='life'
    else
#       shellcheck disable=SC2086
#      arg_arr_str="$(arg_str_to_array_str ${args_arr})"
      send_post "${BASE_URL}/exec/instruct/${state}" "${args_arr}"
    fi
  done
}

function send_post() {
  url=$1
  data=$2
  #Send post request to server
  #  http_detail=$(curl --fail -i -v --request POST -sL \
  #    --url "${url}" \
  #    --data-urlencode "data=${data}" 2>&1)

  curl --fail --request POST -sL \
    --url "${url}" \
    --data-urlencode "data=${data}"
  exitCode=$?
  #After curl send post request
  if [[ "${exitCode}" -ne 0 ]]; then
    printf "\nCurl error, Code: %s\n" "${exitCode}"
    echo url:"${url}"
    echo data:"${data}"
    #    echo detail:"${http_detail}"
  fi

  printf '\n'
  return $exitCode
}

function arg_str_to_array_str() {
  args_arr=("$@")
  arg_arr_str=''
  for arg in "${args_arr[@]}"; do
    arg=${arg//\"/\\\"}
    arg_arr_str+="\"${arg}\", "
  done

  len=$(echo "${#arg_arr_str}" -2 | bc)
  arg_arr_str=[$(echo "${arg_arr_str}" | cut -c1-"$len")]
  echo "$arg_arr_str"
}

function new_line_and_exit() {
  printf '\n'
  exit "$1"
}

main "$@"
exit 0
