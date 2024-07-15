# Big files processing

This is solution of https://github.com/Kyotu-Technology/kyotu/tree/main/recruitment-challenges/large-file-reading-challenge.

I know that I could use DB. Yes then you just need to write something like this:

```postgresql
SELECT date_part('year', mesure_time) as year, round(avg(temperature)::numeric, 2) as temperature
FROM data_points
WHERE city = '$city'
GROUP BY city, year
ORDER BY year;
```

But, where is fun? More information about that approach you can find in [database](./database) folder. 

## Requirements

* Java 21
* Maven 3.9.6

You can use [SDKMAN.io](https://sdkman.io/) to configure your environment.

```shell
$ sdk env install
```

## Usage

### Build

To build this project you need get it from repository and run maven.

```shell
$ git clone git@github.com:Koziolek/big-files.git
$ cd big-files
$ mvn clean verify
```

That will run build process and test.

### Run

In `target` directory there will be `big-files-1.0-SNAPSHOT-jar-with-dependencies.jar` file. Run it:

```shell
$ java -jar target/big-files-1.0-SNAPSHOT-jar-with-dependencies.jar -c Warszawa -f ./src/test/resources/example_file.csv
```

It will calculate avg for Warszawa from example file from original task repo. For help use `-h` option.

#### Generate big files for test

You could generate big file (almost 4GB) for testing:

```shell
for i in {1..10000}; do cat ./src/test/resources/example_file.csv  >> ./src/test/resources/long.csv ; done;
```

### Other options

All options are available by using `-h` or `--help` switch.

#### -c,--city <city>

Name of city that you interested in.

#### -f,--file <file>

Path to data file

#### -C,--calculator <calculator>

Type of calculator NAIVE or INPLACE. Default is INPLACE.

#### -h,--help

Prints help

#### -p,--parallel

Work in parallel mode (faster but need more memory). Default false.

#### -P,--printer <printer>

If set prints data in given format: JSON or CSV. Default format is plain text

#### -s,--smart

If set ignores -C and -p. Calculator is picked in smart way based on number of CPU and memory.

#### -w,--watcher <watcher>

What to do when file change during processing. Default is just log. Use ERROR to stop processing and throw.

### Example usage

Very small memory and very big file example. This will take time, but is possible to finish.

```shell
$ time java -Xmx8m -jar target/big-files-1.0-SNAPSHOT-jar-with-dependencies.jar -c Warszawa -f ./src/test/resources/long.csv -C INPLACE
2018 » 13,56°C
2019 » 13,81°C
2020 » 16,12°C
2021 » 15,61°C
2022 » 14,68°C
2023 » 15,46°C
        0:13.61 real,   13.53 user,     1.34 sys,       0 avg_mem,      87256 max_mem
$ time java -Xmx4m -jar target/big-files-1.0-SNAPSHOT-jar-with-dependencies.jar -c Warszawa -f ./src/test/resources/long.csv -C INPLACE        
2018 » 13,56°C
2019 » 13,81°C
2020 » 16,12°C
2021 » 15,61°C
2022 » 14,68°C
2023 » 15,46°C
        1:53.72 real,   114.02 user,    5.24 sys,       0 avg_mem,      74196 max_mem,         
```

JSON printer and CSV printer

```shell
$ avg_mem,      74196 max_mem,  %I IO_ins       288 IO_outs
koziolek@koziolek-desktop3 ~/workspace/codest/big-files $ time java -Xmx4m -jar target/big-files-1.0-SNAPSHOT-jar-with-dependencies.jar -c Warszawa -f ./src/test/resources/example_file.csv -C INPLACE -P JSON
[
{"year": 2018, "temp": "13,52°C"}
{"year": 2019, "temp": "13,81°C"}
{"year": 2020, "temp": "16,12°C"}
{"year": 2021, "temp": "15,61°C"}
{"year": 2022, "temp": "14,68°C"}
{"year": 2023, "temp": "15,46°C"}
]
        0:00.46 real,   0.37 user,      0.03 sys,       0 avg_mem,      63184 max_mem, 
$ time java -Xmx4m -jar target/big-files-1.0-SNAPSHOT-jar-with-dependencies.jar -c Warszawa -f ./src/test/resources/example_file.csv -C INPLACE -P CSV
2018;13,52°C
2019;13,81°C
2020;16,12°C
2021;15,61°C
2022;14,68°C
2023;15,46°C
        0:00.46 real,   0.35 user,      0.03 sys,       0 avg_mem,      59004 max_mem 
```

Using file watcher (`ERROR` mode)

```shell
$ time java -jar target/big-files-1.0-SNAPSHOT-jar-with-dependencies.jar -c Warszawa -f ./src/test/resources/long.csv -C INPLACE -w ERROR -p
Exception in thread "main" java.lang.IllegalStateException: File changed during processing
        at com.thecodest.bigfiles.calculators.watch.ThrowingStrategy.finisher(ThrowingStrategy.java:22)
        at com.thecodest.bigfiles.calculators.AbstractFileStreamCalculator.getData(AbstractFileStreamCalculator.java:36)
        at com.thecodest.bigfiles.calculators.AvgCalculator.getData(AvgCalculator.java:14)
        at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
        at java.base/java.util.stream.SliceOps$1$1.accept(SliceOps.java:200)
        at java.base/java.util.stream.Streams$StreamBuilderImpl.tryAdvance(Streams.java:397)
        at java.base/java.util.stream.ReferencePipeline.forEachWithCancel(ReferencePipeline.java:129)
        at java.base/java.util.stream.AbstractPipeline.copyIntoWithCancel(AbstractPipeline.java:527)
        at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:513)
        at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499)
        at java.base/java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:151)
        at java.base/java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:174)
        at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
        at java.base/java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:596)
        at com.thecodest.bigfiles.Application.main(Application.java:46)
Command exited with non-zero status 1
        0:03.50 real,   15.39 user,     3.48 sys,       0 avg_mem,      2911992 max_mem
```

But in another terminal you run

```shell
for i in {1..10000}; do cat ./src/test/resources/example_file.csv  >> ./src/test/resources/long.csv ; done;
```

Just after program starts. In this mode watcher throws exception immediately when detect file change.

Using file watcher (`LOG` mode)

```shell
$ time java -jar target/big-files-1.0-SNAPSHOT-jar-with-dependencies.jar -c Warszawa -f ./src/test/resources/long.csv -C INPLACE -p
File was changed during processing. You need to manually restart program.
Returned result could be inconsistent with current state of file.

2018 » 13.56°C
2019 » 13.81°C
2020 » 16.12°C
2021 » 15.61°C
2022 » 14.68°C
2023 » 15.46°C
        0:04.70 real,   34.77 user,     14.43 sys,      0 avg_mem,      8444888 max_mem
```

But in another terminal you run

```shell
for i in {1..10000}; do cat ./src/test/resources/example_file.csv  >> ./src/test/resources/long.csv ; done;
```

Just after program starts. In this mode watcher will log information about change at the end of processing. Results depends on change type.

* If record that changed was already processed then that change will be ignored.
* If record that changed is not yet processed then new value will be taken.
* If new record append at the end of file then will be processed as normal.
* If new record append before currently processed record then that change will be ignored and currently processed record will be processed one more
  time in next processing step.