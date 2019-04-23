# Task 3 - solution comment

## Hardware configuration

Full environment specification can be accessed [here](https://termbin.com/ecjy).

Most important parameters were as follows:
* Processor: 8th Generation Intel® Core™ i7-8750H CPU @ 2.20GHz × 6 cores, 12 threads
* Memory: 32 GB
* System: Linux Mint 19.1 Cinnamon
* Kernel: 4.15.0-47-generic

## Execution

With the above configuration I was able to run unchanged version and find paths for the biggest graph in around [2 minutes](docs/logs/with_optimizations.log). The whole run takes around 3 minutes:
```bash
rnd-task/task-3$ sbt -J-Xmx24g ";clean;run"
[info] Loading settings for project global-plugins from idea.sbt ...
[info] Loading global plugins from .sbt/1.0/plugins
[info] Loading project definition from rnd-task/task-3/project
[info] Loading settings for project task-3 from build.sbt ...
[info] Set current project to optimize (in build file:rnd-task/task-3/)
[success] Total time: 0 s, completed Apr 20, 2019, 7:16:06 PM
[info] Updating ...
[info] Done updating.
[info] Compiling 13 Scala sources to rnd-task/task-3/target/scala-2.12/classes ...
[info] Done compiling.
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by com.google.protobuf.UnsafeUtil (file:.sbt/boot/scala-2.12.7/org.scala-sbt/sbt/1.2.8/protobuf-java-3.3.1.jar) to field java.nio.Buffer.address
WARNING: Please consider reporting this to the maintainers of com.google.protobuf.UnsafeUtil
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
[info] Packaging rnd-task/task-3/target/scala-2.12/optimize_2.12-1.0.0.jar ...
[info] Done packaging.
[info] Running task1.Main 
Reading connections...
Graph created in: 15330 [ms]
Calculating paths...
Found 14285385 paths in 126708 [ms]
Printing to file... done in 32730 [ms]
[success] Total time: 181 s, completed Apr 20, 2019, 7:19:07 PM

```

You can see in VisualVM that it needs at least 10 GB of memory to run and GC activity could be better:

![without_optimizations](docs/pictures/without_optimizations.png?raw=true "Running without optimizations")

After applying changes finding paths lasts around 50% less ([~ 1 minute](docs/logs/with_optimizations.log))
```bash
rnd-task/task-3$ sbt -J-Xmx6g ";clean;run"
[info] Loading settings for project global-plugins from idea.sbt ...
[info] Loading global plugins from .sbt/1.0/plugins
[info] Loading project definition from rnd-task/task-3/project
[info] Loading settings for project task-3 from build.sbt ...
[info] Set current project to optimize (in build file:rnd-task/task-3/)
[success] Total time: 0 s, completed Apr 20, 2019, 7:54:57 PM
[info] Updating ...
[info] Done updating.
[info] Compiling 13 Scala sources to rnd-task/task-3/target/scala-2.12/classes ...
[info] Done compiling.
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by com.google.protobuf.UnsafeUtil (file:.sbt/boot/scala-2.12.7/org.scala-sbt/sbt/1.2.8/protobuf-java-3.3.1.jar) to field java.nio.Buffer.address
WARNING: Please consider reporting this to the maintainers of com.google.protobuf.UnsafeUtil
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
[info] Packaging rnd-task/task-3/target/scala-2.12/optimize_2.12-1.0.0.jar ...
[info] Done packaging.
[info] Running task1.Main 
Reading connections...
Graph created in: 16300 [ms]
Calculating paths...
Found 14285385 paths in 58447 [ms]
Printing to file... done in 38941 [ms]
[success] Total time: 122 s, completed Apr 20, 2019, 7:56:59 PM

```

What is more, 6GB of memory is enough to perform execution on the largest provided test graph and GC work is much more stable:

![with_optimizations](docs/pictures/with_optimizations.png?raw=true "Running with optimizations")

In fact, even 5GB of memory is enough, but it executes [slightly longer](docs/logs/with_optimizations_Xmx5g.log):
```bash
rnd-task/task-3$ sbt -J-Xmx5g ";clean;run"
[info] Loading settings for project global-plugins from idea.sbt ...
[info] Loading global plugins from .sbt/1.0/plugins
[info] Loading project definition from rnd-task/task-3/project
[info] Loading settings for project task-3 from build.sbt ...
[info] Set current project to optimize (in build file:rnd-task/task-3/)
[success] Total time: 0 s, completed Apr 20, 2019, 8:10:50 PM
[info] Updating ...
[info] Done updating.
[info] Compiling 13 Scala sources to rnd-task/task-3/target/scala-2.12/classes ...
[info] Done compiling.
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by com.google.protobuf.UnsafeUtil (file:.sbt/boot/scala-2.12.7/org.scala-sbt/sbt/1.2.8/protobuf-java-3.3.1.jar) to field java.nio.Buffer.address
WARNING: Please consider reporting this to the maintainers of com.google.protobuf.UnsafeUtil
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
[info] Packaging rnd-task/task-3/target/scala-2.12/optimize_2.12-1.0.0.jar ...
[info] Done packaging.
[info] Running task1.Main 
Reading connections...
Graph created in: 17891 [ms]
Calculating paths...
Found 14285385 paths in 59901 [ms]
Printing to file... done in 44696 [ms]
[success] Total time: 131 s, completed Apr 20, 2019, 8:13:00 PM

```

![with_optimizations_Xmx5g](docs/pictures/with_optimizations_Xmx5g.png?raw=true "Running with optimizations on 5GB RAM")

## Changes

As said in the instruction:
```
The final version of the project MUST NOT modify anything outside of the `modifyme` package and build.sbt!
```
I tried to comply that requirement as much as possible and changes are placed only in:
* `build.sbt`
* inside `task1.modifyme` package
* `.jvmopts` file with JVM settings
* test files: added a correctness test and needed resources
* documentation files: for the above description

## Priorities

> For this task the priorities are
> being correct

To ensure that property I generated paths for the smaller graph before making any changes in code.
The produced file is used in the correctness test which runs on smaller graph input files. Total number of paths together with cost, source and destination node is checked for each expected and generated path.

> being fast

I hope that reached speed up and resources consumption optimizations (expressed relatively in %) can be considered as substantial.

> being readable or extensible

I also hope that proposed enhancements did not decrease code readability.