# java-marc4j

Finds MARC records whose **876 field has a subfield `$z` equal to `MR`**, using
[marc4j](https://github.com/marc4j/marc4j).

It reads a MARCXML file in `../marc-records/` and streams it record-by-record
with `MarcXmlReader`, so a large file never has to be loaded into memory all at
once. A path ending in `.gz` is gunzipped on the fly (the input stream is
wrapped in a `GZIPInputStream`), so the committed sample stays compressed.

## Requirements

- A JDK (17 or newer)
- Maven (which downloads the marc4j dependency on first build)

## Run

```bash
# uses ../marc-records/sample.xml.gz (the committed sample) by default
mvn compile exec:java

# or point it at a different MARCXML file (plain .xml or .gz)
mvn compile exec:java -Dexec.args="/path/to/records.xml"
```

## Run with Docker

No JDK or Maven needed on the host — only Docker. From the **repo root**:

```bash
docker compose run --rm find-mr
```

This builds a two-stage image (a Maven build stage produces a runnable fat jar,
then a slim JRE stage runs it) and mounts `../marc-records/` read-only. See the
[root README](../README.md) for details.

## Output

Prints the `001` control number of each matching record, then a summary:

```
MATCH  SCSB-14517887
...
Scanned 5000 records, found 196 with 876$z = 'MR'.
Elapsed time: 1.11s
```
