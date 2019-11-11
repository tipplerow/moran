
package moran.segment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.io.DataReader;
import jam.lang.JamException;
import jam.lang.Ordinal;
import jam.lang.OrdinalIndex;
import jam.util.RegexUtil;

/**
 * Represents a segment of the genome that is tracked during a Moran
 * simulation.
 *
 * <p><b>Definition file.</b> The genome segments are defined in a
 * file specified by the {@code moran.segment.definitionFile} system
 * property.  Each line of the file should contain the segment code
 * and an optional description separated by a comma.  The segments may
 * be whole chromosomes, chromosome arms, or smaller chromosome units.
 * For example:
 *
 * <pre>
   6p21, Contains HLA genes...loss may enable immune evasion
   chr17, TP53 
   9q, Many copy number gains observed
 * </pre>
 */
public final class GenomeSegment extends Ordinal {
    private final String key;
    private final String desc;

    private static final OrdinalIndex ordinalIndex = OrdinalIndex.create();

    private GenomeSegment(String key, String desc) {
        super(ordinalIndex.next());

        this.key = key;
        this.desc = desc;
    }

    /**
     * Name of the system property that defines the file containing
     * the genome segment definitions.
     */
    public static final String DEFINITION_FILE_PROPERTY = "moran.segment.definitionFile";

    /**
     * Comment character allowed in genome segment definition files. 
     */
    public static final Pattern DEFINITION_FILE_COMMENT = RegexUtil.PYTHON_COMMENT;

    /**
     * Delimeter that separates segment keys and descriptions in
     * definition files.
     */
    public static final Pattern DEFINITION_FILE_DELIM = RegexUtil.COMMA;

    // These methods use the comment and delimiter characters, so they
    // must occur after those variables are assigned....
    private static final List<GenomeSegment> segmentList = loadSegments();
    private static final Map<String, GenomeSegment> segmentMap = mapSegments();

    /**
     * Returns the number of defined genome segments.
     *
     * @return the number of defined genome segments.
     */
    public static int count() {
        return segmentList.size();
    }

    /**
     * Returns the ordinal index of this genome segment cast to an
     * {@code int} value (to avoid lossy conversion errors when it
     * is used as array element index).
     *
     * @return the ordinal index of this genome segment cast to an
     * {@code int} value.
     */
    public int indexOf() {
        return (int) getIndex();
    }

    /**
     * Retrieves a genome segment by ordinal index.
     *
     * @param index the ordinal index for the segment of interest.
     *
     * @return the genome segment with the specified index.
     *
     * @throws IndexOutOfBoundsException unless the index is valid.
     */
    public static GenomeSegment instance(int index) {
        return segmentList.get(index);
    }

    /**
     * Retrieves a genome segment by key string.
     *
     * @param key the key string for the segment of interest.
     *
     * @return the genome segment with the specified key, or
     * {@code null} if there is no such segment.
     */
    public static GenomeSegment instance(String key) {
        return segmentMap.get(key);
    }

    /**
     * Returns a read-only list of the number of defined genome
     * segments.
     *
     * @return a read-only list of the number of defined genome
     * segments.
     */
    public static List<GenomeSegment> list() {
        return segmentList;
    }

    /**
     * Retrieves a genome segment by key string.
     *
     * @param key the key string for the segment of interest.
     *
     * @return the genome segment with the specified key.
     *
     * @throws RuntimeException unless a matching genome segment
     * exists.
     */
    public static GenomeSegment require(String key) {
        GenomeSegment segment = instance(key);

        if (segment != null)
            return segment;
        else
            throw JamException.runtime("Undefined genome segment: [%s].", key);
    }

    /**
     * Returns the key of this genome segment.
     *
     * @return the key of this genome segment.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the description for this genome segment.
     *
     * @return the description for this genome segment.
     */
    public String getDesc() {
        return desc;
    }

    @Override public String toString() {
        return "GenomeSegment(" + key + ")";
    }

    // -----------------------------------------------------------------
    // Private code to load genome segments from the definition file.
    // -----------------------------------------------------------------

    private static List<GenomeSegment> loadSegments() {
        JamLogger.info("Loading genome segments...");

        ArrayList<GenomeSegment> segments =
            new ArrayList<GenomeSegment>();

        DataReader reader =
            DataReader.open(resolveDefinitionFile(),
                            DEFINITION_FILE_COMMENT);

        try {
            for (String line : reader)
                segments.add(parseSegment(line));
        }
        finally {
            reader.close();
        }

        segments.trimToSize();
        return Collections.unmodifiableList(segments);
    }

    private static String resolveDefinitionFile() {
        return JamProperties.getRequired(DEFINITION_FILE_PROPERTY);
    }

    private static GenomeSegment parseSegment(String line) {
        String key = null;
        String desc = null;

        String[] fields = RegexUtil.split(DEFINITION_FILE_DELIM, line);

        if (fields.length == 1) {
            key = fields[0];
            desc = fields[0];
        }
        else if (fields.length == 2) {
            key = fields[0];
            desc = fields[1];
        }
        else {
            throw JamException.runtime("Invalid genome segment: [%s].", line);
        }

        return new GenomeSegment(key, desc);
    }

    private static Map<String, GenomeSegment> mapSegments() {
        Map<String, GenomeSegment> map =
            new HashMap<String, GenomeSegment>(segmentList.size());

        for (GenomeSegment segment : segmentList)
            map.put(segment.getKey(), segment);

        return Collections.unmodifiableMap(map);
    }
}
