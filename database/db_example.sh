#!/usr/bin/env bash

city=$1
data_file=$2

function load_data() {
  PGPASSWORD=123 psql -h localhost -p 5432 -U postgres -c "truncate data_points_tmp";
  PGPASSWORD=123 psql -h localhost -p 5432 -U postgres -c "\COPY data_points_tmp FROM '$data_file' DELIMITER ';' CSV";
  PGPASSWORD=123 psql -h localhost -p 5432 -U postgres -f load.sql;
}
function query_data() {
  PGPASSWORD=123 psql -h localhost -p 5432 -U postgres -c "SELECT date_part('year', mesure_time) as year, round(avg(temperature)::numeric, 2) as temperature FROM data_points WHERE city = '$city' GROUP BY city, year ORDER BY year;"
}

function get_sum() {
    md5sum $data_file | cut -d ' ' -f 1
}

docker run --rm --name bf-postgres -e POSTGRES_PASSWORD=123 -p 5432:5432 -d postgres

#await to start db

sleep 10

file_sum=$(get_sum)

PGPASSWORD=123 psql -h localhost -p 5432 -U postgres -f create.sql
load_data
query_data

echo "Initial loading done"
echo "Press [CTRL+C] to stop.."

while :;
do
    new_sum=$(get_sum)
    if [ "$file_sum" != "$new_sum" ]; then
      file_sum=$new_sum
      load_data
      query_data
    fi
    sleep 1
done