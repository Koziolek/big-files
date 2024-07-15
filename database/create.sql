CREATE TABLE IF NOT EXISTS data_points(
    city varchar,
    mesure_time DATE,
    temperature REAL,
    PRIMARY KEY (city, mesure_time)
);

CREATE TABLE IF NOT EXISTS data_points_tmp(
                                          city varchar,
                                          mesure_time DATE,
                                          temperature REAL,
                                          PRIMARY KEY (city, mesure_time)
);