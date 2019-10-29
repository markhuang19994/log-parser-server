#!/usr/bin/env bash

history_dir="${HOME}/lvadmin/history"
state='default'
mkdir -p ~/lvadmin/history

trap "killall background" EXIT
trap save_state_history EXIT

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
  config_his="${HOME}/lvadmin/history/.lvadmin_config_his"
  history -r "$config_his"
  read -erp 'Main config path:' main_config_path
  history -s "$main_config_path"
  history -w "$config_his"
  send_post "${BASE_URL}/set/main_args" "${main_config_path}"
}

function execInstruct() {
  history -c
  history -r "${history_dir}/.lvadmin_state_${state}_his"

  # shellcheck disable=SC2006
  while true; do
    IFS= read -erp "lvadmin@${state}>> " args_arr
    if [[ "${args_arr}" == exit ]]; then
      exit 0
    elif [[ "${args_arr}" == cmethod ]]; then
      change_state "${state}" 'cmethod'
    elif [[ "${args_arr}" == fmethod ]]; then
      change_state "${state}" 'fmethod'
    elif [[ "${args_arr}" == life ]]; then
      change_state "${state}" 'life'
    elif [[ "${args_arr}" == format ]]; then
      change_state "${state}" 'format'
    else
      history -s "$args_arr"
      send_post "${BASE_URL}/exec/instruct/${state}" "${args_arr}"
    fi
  done
}

function change_state() {
  old_state="$1"
  new_state="$2"
  change_state_history "${old_state}" "$new_state"
  state="$new_state"
}

function change_state_history() {
  old_state="$1"
  new_state="$2"
  history -w "${history_dir}/.lvadmin_state_${old_state}_his"
  history -c
  history -r "${history_dir}/.lvadmin_state_${new_state}_his"
}

function save_state_history() {
  history -w "${history_dir}/.lvadmin_state_${state}_his"
}

function send_post() {
  url=$1
  data=$2

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
