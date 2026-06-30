# python-pymarc

Finds MARC records whose **876 field has a subfield `$z` equal to `MR`**, using
[pymarc](https://pymarc.readthedocs.io/).

It reads a MARCXML file in `../marc-records/` and streams it record-by-record
with `pymarc.map_xml`, so a large file never has to be loaded into memory all at
once. A path ending in `.gz` is gunzipped on the fly (via `gzip.open` fed
straight into `pymarc.parse_xml`), so the committed sample stays compressed.

## Run

```bash
# uses ../marc-records/marc_language_examples.xml (the committed sample) by default
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

Prints info on each matching record, then a summary:

```
MATCH  9990349333408651
TITLE  Pojp wuuj pa tz'utujil.
CODES quc tzj quc tzj iso639-3
NOTE  In Tzutuhil; includes Tzutuhil-Spanish glossary.
...
Scanned 14 records, found 13 with ISO 639-3 codes or language note.
Elapsed time: 0.02s
```
