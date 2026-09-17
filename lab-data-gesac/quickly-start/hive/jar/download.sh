#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

DOWNLOAD_URLS=(
  "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar"
)

download_file() {
  local download_url="$1"
  local file_name
  local target_file

  file_name="$(basename "${download_url}")"
  target_file="${SCRIPT_DIR}/${file_name}"

  if [[ -f "${target_file}" ]]; then
    echo "Skip ${file_name}: already exists at ${target_file}"
    return
  fi

  if command -v curl >/dev/null 2>&1; then
    curl -fL "${download_url}" -o "${target_file}"
  elif command -v wget >/dev/null 2>&1; then
    wget -O "${target_file}" "${download_url}"
  else
    echo "Neither curl nor wget is available. Please install one and retry." >&2
    exit 1
  fi

  echo "Downloaded ${file_name} to ${target_file}"
}

for download_url in "${DOWNLOAD_URLS[@]}"; do
  download_file "${download_url}"
done
