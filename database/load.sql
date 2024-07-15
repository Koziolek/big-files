INSERT INTO data_points
SELECT city, mesure_time, temperature
FROM data_points_tmp AS t(
                          city,
                          mesure_time,
                          temperature
    )
ON CONFLICT (city, mesure_time)
    DO UPDATE SET temperature = EXCLUDED.temperature;