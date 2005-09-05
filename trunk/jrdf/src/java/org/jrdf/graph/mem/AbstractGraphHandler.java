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
            printBySubjects(iterator, out);
        }
    }

    private void printBySubjects(Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator, PrintStream out) {
        Map.Entry<Long, Map<Long, Set<Long>>> subjectEntry = iterator.next();
        Long subject = subjectEntry.getKey();
        int sWidth = subject.toString().length() + STATEMENT_OFFSET;
        out.print(subject.toString() + " --> ");

        Map<Long, Set<Long>> secondIndex = subjectEntry.getValue();
        if (secondIndex.isEmpty()) {
            out.println("X");
            return;
        }
        boolean firstPredicate = true;

        Iterator<Map.Entry<Long, Set<Long>>> predIterator =
            secondIndex.entrySet().iterator();
        printByPredicates(predIterator, firstPredicate, sWidth, out);
    }

    private void printByPredicates(Iterator<Map.Entry<Long, Set<Long>>> predIterator, boolean firstPredicate,
            int sWidth, PrintStream out) {
        while (predIterator.hasNext()) {
            Map.Entry<Long, Set<Long>> predicateEntry = predIterator.next();
            Long predicate = predicateEntry.getKey();
            int pWidth = predicate.toString().length() + STATEMENT_OFFSET;
            if (!firstPredicate) {
                StringBuffer space = new StringBuffer(sWidth);
                space.setLength(sWidth);
                for (int c = 0; c < sWidth; c++) {
                    space.setCharAt(c, ' ');
                }
                out.print(space.toString());
            }
            else {
                firstPredicate = false;
            }
            out.print(predicate.toString() + " --> ");

            Set<Long> thirdIndex = predicateEntry.getValue();
            if (thirdIndex.isEmpty()) {
                out.println("X");
                continue;
            }
            boolean firstObject = true;

            Iterator<Long> objIterator = thirdIndex.iterator();
            printByObjects(objIterator, firstObject, sWidth, pWidth, out);
        }
    }

    private void printByObjects(Iterator<Long> objIterator, boolean firstObject, int sWidth, int pWidth,
            PrintStream out) {
        while (objIterator.hasNext()) {
            Long object = objIterator.next();
            if (!firstObject) {
                StringBuffer sp2 = new StringBuffer(sWidth + pWidth);
                sp2.setLength(sWidth + pWidth);
                for (int d = 0; d < sWidth + pWidth; d++) {
                    sp2.setCharAt(d, ' ');
                }
                out.print(sp2.toString());
            }
            else {
                firstObject = false;
            }
            out.println(object);
        }
    }
}