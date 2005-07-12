package org.jrdf.sparql;

import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.Query;
import org.jrdf.query.QueryBuilder;
import org.jrdf.util.param.ParameterUtil;

/**
 * Builds SPARQL queries in {@link String} form into {@link org.jrdf.query.Query} objects.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public class SparqlQueryBuilder implements QueryBuilder {

  /**
   * Builds a SPQRQL query in {@link String} form into a {@link Query}.
   *
   * @param query The query in {@link String} form of the query.
   * @return The <code>query</code> in {@link Query} form.
   * @throws org.jrdf.query.InvalidQuerySyntaxException If the syntax of the <code>query</code> is incorrect.
   */
  public Query buildQuery(String query) throws InvalidQuerySyntaxException {
    ParameterUtil.checkNotEmptyString("query", query);
    try {
//      return new ItqlInterpreter(new HashMap()).parseQuery(query);
//        return new SparqlParser().parse(query);
      throw new UnsupportedOperationException("Implement me!");
    } catch (Throwable t) {
      throw new InvalidQuerySyntaxException("Unable to build query from string: "+query, t);
    }
  }
}
