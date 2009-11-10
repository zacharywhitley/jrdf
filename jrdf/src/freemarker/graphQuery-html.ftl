<html>
<head>
  <title>Sparql Query Page -- Graph ${graphName}</title>
</head>
<body>
  <h1>
    Sparql query for graph <i>${graphName}</i>
  </h1>
  <div>
  <form id="sparql" name="sparqlForm" method="get" action="${graphName}/">
  Query:
  <br/>
    <textarea id="sparqlText" name="query" rows="6" cols="70">
SELECT *
WHERE {
    ?s ?p ?o .
}
    </textarea>
    <a href="#" onclick="document.sparqlForm.query.value='';">Clear</a>
    <br/>
    <br/>
    <br/>
    <fieldset style="width: 240px;">
    <legend>Result format</legend>
    <div>
    HTML: <input type="radio" name="format" value="html" checked="checked"/>, or
    Raw XML: <input type="radio" name="format" value="xml"/>
    <p/>
    </div>
    </fieldset>
    <fieldset style="width: 240px;">
    <legend>Number of answers to display:</legend>
    <select name="noRows">
        <option value="10">10</option>
        <option value="50">50</option>
        <option value="100">100</option>
        <option value="all">All</option>
    </select>
    </fieldset>
    <p/>
    <p/>
    <input type="submit" value="Submit" />
</form>
</div>
<div>
<a href="/graph/">Select another graph<a/>
</div>
</body>
</html>