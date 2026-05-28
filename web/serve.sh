#!/bin/sh
PORT=${1:-3000}
echo "로컬 서버 시작: http://localhost:$PORT"
cd "$(dirname "$0")"
python3 -m http.server "$PORT"
