#!/bin/sh
set -e

# 환경변수가 없으면 기본값 설정
BACKEND_HOST=${BACKEND_HOST:-backend-blue}

# nginx.conf 템플릿에서 실제 설정 파일 생성
envsubst '${BACKEND_HOST}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf

# nginx 실행
exec nginx -g "daemon off;"
