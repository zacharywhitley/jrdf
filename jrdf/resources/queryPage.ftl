<html>
<head>
  <title>Sparql Query Page -- Graph ${graphName}</title>
</head>
<body>
  <h1>
    Enter the Sparql query below and click "Submit" to get the answer.
  </h1>
  Sparql query:
  <form id="sparql" name="sparqlForm" method="post" action="${graphName}">
    <textarea id="sparqlText" name="queryString" rows="6" cols="70"></textarea>
    <p/>
    <fieldset style="width: 240px;">
    <legend>Result format</legend>
    <div>
    HTML: <input type="radio" name="format" value="html" checked="checked"/>, or
    Raw XML: <input type="radio" name="format" value="xml">
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
</body>
</html>