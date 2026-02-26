#!/usr/bin/env bash
set -e

HOST=${DB_HOST:-db}
PORT=${DB_PORT:-5432}
USER=${DB_USER:-postgres}

echo "Waiting for Postgres at $HOST:$PORT as user $USER..."
until pg_isready -h "$HOST" -p "$PORT" -U "$USER" >/dev/null 2>&1; do
  sleep 1
done
echo "Postgres is up!"
