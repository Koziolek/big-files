# Big files database edition

This is simplified solution. 

## Requirement

* docker
* psql 
* md5sum

## How to run

Just:

```shell
$ ./db_example.sh [city_name] [data_file]
```
It will pull last postgres image and run it.

## How it works

First it create two tables. 

* `data_points` – this table contains current state of data
* `data_points_temp` – this table is used to load data

In next step we load data form file to `data_points_temp` table and copy it to `data_points`. Then we execute query and print results.

Now we start loop that checks if file changed. If file changed then we reload data from that file and execute query. 