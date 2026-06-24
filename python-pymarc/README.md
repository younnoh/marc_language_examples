# python-pymarc

Finds MARC records whose **876 field has a subfield `$z` equal to `MR`**, using
[pymarc](https://pymarc.readthedocs.io/).

It reads a MARCXML file in `../marc-records/` and streams it record-by-record
with `pymarc.map_xml`, so a large file never has to be loaded into memory all at
once. A path ending in `.gz` is gunzipped on the fly (via `gzip.open` fed
straight into `pymarc.parse_xml`), so the committed sample stays compressed.

## Run

```bash
# uses ../marc-records/sample.xml.gz (the committed sample) by default
uv run main.py

# or point it at a different MARCXML file (plain .xml or .gz)
uv run main.py /path/to/records.xml
```

If you are not using `uv`, install pymarc (`pip install pymarc`) and run
`python3 main.py`.

## Run with Docker

No Python or pymarc install needed on the host — only Docker. From the **repo
root**:

```bash
docker compose run --rm find-mr-py
```

This builds a slim `python:3.13-slim` image and mounts `../marc-records/`
read-only. See the [root README](../README.md) for details.

## Output

Prints the `001` control number of each matching record, then a summary:

```
MATCH  SCSB-14517887
...
Scanned 5000 records, found 196 with 876$z = 'MR'.
Elapsed time: 1.11s
```
