package org.jrdf.graph.mem;

import org.jrdf.graph.GraphException;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.io.PrintStream;

/**
 * Turn this into delegation rather than inheritance?
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public abstract class AbstractGraphHandler implements GraphHandler {
  /**
   * As 012, 120 and 201 are symmetrical this can be used to reconstruct either
   * two from any one index.  If you put the iterator for in 012 it will add
   * them correctly to 120 (secondIndex) and 201 (thirdIndex), or 120 will make
   * 201 (secondIndex) and 012 (thirdIndex) and 201 will
   * produce 120 and 201.
   *
   * @param firstEntries the list of items to reconstruct the index from.
   * @param secondIndex the second index.
   * @param thirdIndex the third index.
   * @throws org.jrdf.graph.GraphException if the adds fail.
   */
  public void reconstructIndices(
      Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> firstEntries,
      LongIndex secondIndex, LongIndex thirdIndex)
      throws GraphException {

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
  public static void dumpIndex(PrintStream out, GraphHandler handler) {
    Iterator iterator = handler.getEntries();
    while (iterator.hasNext()) {
      Map.Entry subjectEntry = (Map.Entry) iterator.next();
      Long subject = (Long) subjectEntry.getKey();
      int sWidth = subject.toString().length() + 5;
      out.print(subject.toString() + " --> ");

      Map secondIndex = (Map) subjectEntry.getValue();
      if (secondIndex.isEmpty()) {
        out.println("X");
        continue;
      }
      boolean firstPredicate = true;

      Iterator predIterator = secondIndex.entrySet().iterator();
      while (predIterator.hasNext()) {
        Map.Entry predicateEntry = (Map.Entry) predIterator.next();
        Long predicate = (Long) predicateEntry.getKey();
        int pWidth = predicate.toString().length() + 5;
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

        Set thirdIndex = (Set) predicateEntry.getValue();
        if (thirdIndex.isEmpty()) {
          out.println("X");
          continue;
        }
        boolean firstObject = true;

        Iterator objIterator = thirdIndex.iterator();
        while (objIterator.hasNext()) {
          Long object = (Long) objIterator.next();
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
  }
}