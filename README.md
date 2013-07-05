Multitool
========

Welcome
-------

This is the Cascading.Multitool (Multitool) application.

Multitool provides a simple command line interface for building data
processing jobs.  Think of this as `grep`, `sed`, and `awk` for
Hadoop, which also supports joins between multiple data-sets.

For example, with `$HADOOP_HOME/bin/` in your PATH, the following
command,

    $ hadoop jar multitool-<release-date>.jar source=input.txt select=Monday sink=outputDir

will start a Hadoop job to read from the source file `input.txt`, grep
all lines with the word `Monday`, then output the results into the
`outputDir` directory.

Multitool will inherit the underlying Hadoop configuration, so if the
default FileSystem is HDFS, all paths will be relative to the cluster
filesystem, not local. Using fully qualified urls will override the
defaults (`file://some/path` or `s3n:/bucket/file`).

This application is built with Cascading.

Cascading is a feature rich API for defining and executing complex,
scale-free, and fault tolerant data processing workflows on a Hadoop
cluster. It can be found at http://www.cascading.org/

Installing
----------

This step is not necessary if you wish to run Multitool directly from
the uncompressed distribution folder or Multitool was pre-installed
with your Hadoop distribution.

To see if Multitool has already been added to your PATH, type:

    $ which multitool

To install for all users into `/usr/local/bin`:

    $ sudo ./bin/multitool install

or for the current user only into `~/.multitool`:

    $ ./bin/multitool install

For detailed instructions:

    $ ./bin/multitool help install

Choose the method that best suites your environment.

If you are running Multitool on AWS Elastic MapReduce, you need to
follow the
[Elastic MapReduce instructions](https://aws.amazon.com/articles/2293?_encoding=UTF8&jiveRedirect=1)
on the AWS site, which typically expect the
`multitool-<release-date>.jar` to be uploaded to AWS S3.

Using
-----

The environment variable `HADOOP_HOME` should always be set first
before using Multitool.

To run from the command line with the jar, Hadoop should be in the
path:

    $ hadoop jar multitool-<release-date>.jar <args>

...or if Multitool has been installed based on the instructions above:

    $ multitool source=data/artist.100.txt cut=0 sink=output

This will cut the first fields out of the file `artists.100.txt` and
save the results to `output` file.

If no args are given, a comprehensive list of commands will be
printed. That list is also available as `COMMANDS.md` in this
directory.

Examples
--------

For more detailed examples of using Multitool, see also: http://cascading.org/multitool/

Copying:

    $ ./bin/multitool source=input.txt sink=outputDir

Copying while removing the first header line, and overwriting output:

    $ ./bin/multitool source=input.txt source.skipheader=true sink=outputDir sink.replace=true

Filter out data:

    $ ./bin/multitool source=input.txt "reject=some words" sink=outputDir

For a more complex example:

    $ ./bin/multitool source=data/topic.100.txt cut=0 \
    "pgen=(\b[12][09][0-9]{2}\b)" group=0 count=0 group=1 \
    sink=output sink.replace=true sink.parts=1

This will find all years in the input file, count them, and sort them
by counts.

Building
--------

To build Multitool, you may download the source code from GitHub:

   https://github.com/cascading/cascading.multitool

To build multitool with [gradle](http://gradle.org) do this:

    $ gradle build

License
-------

See `apl.txt` in this directory.
