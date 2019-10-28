#!/usr/bin/env bash
BASE_URL='http://localhost:8080'
args=("$@")

#Generate http post content
arg_arr_str=''
for arg in "${args[@]}"; do
  arg=${arg//\"/\\\"}
  arg_arr_str+="\"${arg}\", "
done

len=$(echo "${#arg_arr_str}" -2 | bc)
arg_arr_str=[$(echo "${arg_arr_str}" | cut -c1-"$len")]

exit 0
#Send post request to server
url="${BASE_URL}/set/main_args"
curl --request POST -sL \
  --url "${url}" \
  --data-urlencode "args=${arg_arr_str}"

#After curl send post request
curlCode=$?
if [[ "${curlCode}" -ne 0 ]]; then
  printf "\nCurl error, Code: %s\n" "${curlCode}"
  echo args:"${args[*]}"
  echo arg_arr_str:"${arg_arr_str}"
  echo url:"${url}"
fi

printf '\n'
