package org.jrdf.graph.mem;

import org.jrdf.graph.GraphException;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Turn this into delegation rather than inheritance?
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public abstract class AbstractGraphHandler implements GraphHandler {

    protected GraphImpl graph;
    private static final int STATEMENT_OFFSET = 5;

    /**
     * As 012, 120 and 201 are symmetrical this can be used to reconstruct either
     * two from any one index.  Using the 012 index it will add entries correctly
     * to 120 (secondIndex) and 201 (thirdIndex), or 120 will make
     * 201 (secondIndex) and 012 (thirdIndex) and 201 will
     * produce 120 and 201.
     *
     * @param firstIndex
     * @param secondIndex the second index.
     * @param thirdIndex the third index.
     * @throws org.jrdf.graph.GraphException if the adds fail.
     */
    public void reconstructIndices(LongIndex firstIndex, LongIndex secondIndex,
        LongIndex thirdIndex) throws GraphException {
        Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> firstEntries =
            firstIndex.iterator();
        while (firstEntries.hasNext()) {
            Map.Entry<Long, Map<Long, Set<Long>>> firstEntry = firstEntries.next();
            Long first = firstEntry.getKey();

            // now iterate over the second column
            Iterator<Map.Entry<Long, Set<Long>>> secondEntries =
                firstEntry.getValue().entrySet().iterator();
            while (secondEntries.hasNext()) {
                Map.Entry<Long, Set<Long>> secondEntry = secondEntries.next();
                Long second = secondEntry.getKey();

                // now iterate over the third column
                Iterator<Long> thirdValues = secondEntry.getValue().iterator();
                while (thirdValues.hasNext()) {
                    Long third = thirdValues.next();

                    // now add the row to the other two indexes
                    secondIndex.add(second, third, first);
                    thirdIndex.add(third, first, second);
                }
            }
        }
    }

    /**
     * Debug method to see the current state of the first index.
     */
    public void dumpIndex(PrintStream out) {
        // TODO Now this is smaller test drive.
        Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator = getEntries();
        while (iterator.hasNext()) {
            printSubjects(out, iterator.next());
        }
    }

    private void printSubjects(PrintStream out, Map.Entry<Long, Map<Long, Set<Long>>> subjectEntry) {
        Map<Long, Set<Long>> secondIndex = subjectEntry.getValue();

        String subject = subjectEntry.getKey().toString();
        Iterator<Map.Entry<Long, Set<Long>>> predIterator = secondIndex.entrySet().iterator();

        out.print(subject + " --> ");

        if (!predIterator.hasNext()) {
            out.println("X");
        }
        else {
            int sWidth = subject.length() + STATEMENT_OFFSET;
            printPredicates(out, predIterator, createSpaces(sWidth));
        }
    }

    private void printPredicates(PrintStream out, Iterator<Map.Entry<Long, Set<Long>>> predIterator, String spaces) {
        int numberOfPredicates = 0;
        while (predIterator.hasNext()) {
            Map.Entry<Long, Set<Long>> predicateEntry = predIterator.next();

            String predicate = predicateEntry.getKey().toString();
            Iterator<Long> objIterator = predicateEntry.getValue().iterator();

            if (++numberOfPredicates > 1) {
                out.print(spaces);
            }
            out.print(predicate + " --> ");

            if (!objIterator.hasNext()) {
                out.println("X");
            }
            else {
                int pWidth = predicate.length() + STATEMENT_OFFSET;
                printObjects(out, objIterator, spaces + createSpaces(pWidth));
            }
        }
    }

    private void printObjects(PrintStream out, Iterator<Long> objIterator, String spaces) {
        if (objIterator.hasNext()) {
            out.println(objIterator.next());
            while (objIterator.hasNext()) {
                out.println(spaces + objIterator.next());
            }
        }
    }

    private String createSpaces(int numberOfSpaces) {
        StringBuffer space = new StringBuffer(numberOfSpaces);
        space.setLength(numberOfSpaces);
        for (int c = 0; c < numberOfSpaces; c++) {
            space.setCharAt(c, ' ');
        }
        return space.toString();
    }
}