"""Find MARC records whose 876 field has a subfield $z equal to "MR".

Uses pymarc to stream a MARCXML file record-by-record (so the 200 MB file
does not have to be loaded into memory all at once). A path ending in ".gz" is
decompressed on the fly, so the committed sample can stay gzipped.
"""

import gzip
import sys
import time
from pathlib import Path

from pymarc import XmlHandler, map_xml, parse_xml

# Default to the shared, gzipped sample that sits next to the two example projects.
DEFAULT_FILE = Path(__file__).resolve().parent.parent / "marc-records" / "sample.xml.gz"


def has_mr(record):
    """Return True if any 876 field has a subfield $z whose value is 'MR'."""
    for field in record.get_fields("876"):
        if "MR" in field.get_subfields("z"):
            return True
    return False


def main():
    path = Path(sys.argv[1]) if len(sys.argv) > 1 else DEFAULT_FILE

    matches = 0
    total = 0
    start = time.monotonic()

    def handle(record):
        nonlocal matches, total
        total += 1
        if has_mr(record):
            matches += 1
            # 001 is the control number; print it so we can see which records matched.
            control_number = record["001"].data if record["001"] else "(no 001)"
            print(f"MATCH  {control_number}")

    # Alternatively, you could load every record into a list first:
    #   from pymarc import parse_xml_to_array
    #   records = parse_xml_to_array(str(path))
    #   for record in records:
    #       handle(record)
    # but parse_xml_to_array reads the whole file into memory at once, so for a
    # 200 MB file it's better to stream with map_xml below.

    if path.suffix == ".gz":
        # map_xml only takes filenames, but under the hood it just feeds the file
        # to xml.sax, which also accepts a file-like object. So we gunzip-stream
        # the file straight into the parser with our own XmlHandler.
        handler = XmlHandler()
        handler.process_record = handle
        with gzip.open(path, "rb") as fh:
            parse_xml(fh, handler)
    else:
        # map_xml feeds each parsed record to our callback as it is read.
        map_xml(handle, str(path))

    elapsed = time.monotonic() - start
    print(f"\nScanned {total} records, found {matches} with 876$z = 'MR'.")
    print(f"Elapsed time: {elapsed:.2f}s")


if __name__ == "__main__":
    main()
