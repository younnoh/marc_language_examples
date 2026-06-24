package com.example.marc;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

/**
 * Find MARC records whose 876 field has a subfield $z equal to "MR".
 *
 * Uses marc4j to stream a MARCXML file record-by-record (so the 200 MB file
 * does not have to be loaded into memory all at once). A path ending in ".gz"
 * is decompressed on the fly, so the committed sample can stay gzipped.
 */
public class FindMR {

    // Default to the shared, gzipped sample that sits next to the two example projects.
    private static final Path DEFAULT_FILE =
            Paths.get("..", "marc-records", "sample.xml.gz");

    /** Open the file, transparently gunzipping it when the name ends in ".gz". */
    private static InputStream openStream(Path path) throws Exception {
        InputStream in = new BufferedInputStream(new FileInputStream(path.toFile()));
        return path.toString().endsWith(".gz") ? new GZIPInputStream(in) : in;
    }

    /** Return true if any 876 field has a subfield $z whose value is "MR". */
    private static boolean hasMr(Record record) {
        for (VariableField vf : record.getVariableFields("876")) {
            DataField field = (DataField) vf;
            for (Subfield sf : field.getSubfields('z')) {
                if ("MR".equals(sf.getData())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        Path path = args.length > 0 ? Paths.get(args[0]) : DEFAULT_FILE;

        int matches = 0;
        int total = 0;
        long start = System.nanoTime();

        try (InputStream in = openStream(path)) {
            MarcReader reader = new MarcXmlReader(in);

            // marc4j has no built-in "read everything into a List", but you could
            // build one manually by draining the reader first:
            //   List<Record> records = new ArrayList<>();
            //   while (reader.hasNext()) {
            //       records.add(reader.next());
            //   }
            //   for (Record record : records) { ... }
            // That holds every record in memory at once, so for a 200 MB file it's
            // better to stream record-by-record as we do below.

            while (reader.hasNext()) {
                Record record = reader.next();
                total++;
                if (hasMr(record)) {
                    matches++;
                    // 001 is the control number; print it so we can see which records matched.
                    // Alternatively, read the 001 field directly (like Python's record["001"].data):
                    //   ControlField cf = (ControlField) record.getVariableField("001");
                    //   String controlNumber = cf != null ? cf.getData() : null;
                    // (needs:  import org.marc4j.marc.ControlField;)
                    String controlNumber = record.getControlNumber();
                    System.out.println("MATCH  " + (controlNumber != null ? controlNumber : "(no 001)"));
                }
            }
        }

        double elapsed = (System.nanoTime() - start) / 1_000_000_000.0;
        System.out.printf("%nScanned %d records, found %d with 876$z = 'MR'.%n", total, matches);
        System.out.printf("Elapsed time: %.2fs%n", elapsed);
    }
}
