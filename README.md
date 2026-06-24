# marc_examples

Two small, parallel example programs that find MARC records whose **876 field
has a subfield `$z` equal to `MR`**:

- [`java-marc4j/`](java-marc4j/) — using [marc4j](https://github.com/marc4j/marc4j)
- [`python-pymarc/`](python-pymarc/) — using [pymarc](https://pymarc.readthedocs.io/)

Both stream the MARCXML file in [`marc-records/`](marc-records/)
record-by-record, so a large file never has to be loaded into memory all at
once.

## Sample data

A small gzipped sample, [`marc-records/sample.xml.gz`](marc-records/) (5,000
records, 196 of them matching), is committed so the demo runs out of the box —
both programs default to it.

The apps read `.gz` files **directly**, decompressing on the fly as they stream
(see `openStream` in the Java source and the `gzip.open` branch in `main.py`),
so you don't need to unzip anything to run them. If you do want the plain XML —
e.g. to inspect it — gunzip it:

```bash
gunzip -k marc-records/sample.xml.gz   # -k keeps the .gz; writes sample.xml
```

The original full ~200 MB feed (`CUL_20260530_194500_111.xml`, 30,000 records)
is git-ignored; point either app at it with an argument if you have a copy.

## Run with Docker

The `docker-compose.yml` at the repo root lets you build and run either example
**without installing a JDK, Maven, Python, or any dependency on the host** —
only Docker is required.

```bash
# Build both images (pulls the base images on first run)
docker compose build

# Run the Java example
docker compose run --rm find-mr

# Run the Python example
docker compose run --rm find-mr-py
```

Each service mounts [`marc-records/`](marc-records/) read-only at
`/marc-records`, which is exactly where each program's default data path
(`sample.xml.gz`) resolves — so no arguments are needed. The data is
**mounted**, not copied into the images.

To scan a different MARCXML file (plain `.xml` or `.gz`), drop it in
`marc-records/` and pass the container path as an argument:

```bash
docker compose run --rm find-mr    /marc-records/other.xml
docker compose run --rm find-mr-py /marc-records/other.xml
```

### How the images are built

- **Java** uses a two-stage `Dockerfile`: a `maven` stage compiles a single
  runnable "fat" jar (marc4j bundled in via the shade plugin), then a slim
  `eclipse-temurin:17-jre` stage carries just the JRE and that jar.
- **Python** uses a single slim `python:3.13-slim` stage — being interpreted,
  there is no separate compile step to split out.

## Expected output

Each program prints the `001` control number of every matching record, then a
summary line:

```
MATCH  SCSB-14517887
...
Scanned 5000 records, found 196 with 876$z = 'MR'.
Elapsed time: 1.11s
```

(Those are the numbers for the bundled `sample.xml.gz`; the full feed reports
`Scanned 30000 records, found 994`.)

## Running without Docker

See each subproject's README for the native toolchain instructions:
[`java-marc4j/README.md`](java-marc4j/README.md) and
[`python-pymarc/README.md`](python-pymarc/README.md).
