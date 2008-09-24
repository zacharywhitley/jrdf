<html>
<head>
  <title>Sparql Query Page</title>
</head>
<body>
  <h1>
    Enter the Sparql query below and click "Submit" to get the answer.
  </h1>
  Sparql query:
  <form id="sparql" name="sparqlForm" method="post" action="foo">
    <textarea id="sparqlText" name="queryString" rows="6" cols="70"></textarea>
    <p/>
    HTML: <input type="radio" name="format" value="html" checked="checked"/>&nbsp;&nbsp;&nbsp;
    Raw XML: <input type="radio" name="format" value="xml">
    <p/>
    <input type="submit" value="Submit" />
</form>
</body>
</html>