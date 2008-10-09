<html>
<head>
  <title>Sparql Query Page -- Graph ${graphName}</title>
<script type="text/javascript">
function clearForm(formIdent) {
  var formname = formIdent;
  var inp = document.getElementsByTagName('input');
	for(var i = 0; i < inp.length; i++) {
		if(inp[i].type == 'text') {
			inp[i].value = '';
		}
	}
  var inp = document.getElementsByTagName('select');
	for(var i = 0; i < inp.length; i++) {
		inp[i].selectedIndex=0
	}
  document.listsearch.submit();

}
</script>
</head>
<body>
  <h1>
    Sparql query for graph <i>${graphName}</i>
  </h1>
  <div>
  <form id="sparql" name="sparqlForm" method="post" action="${graphName}/result.html">
  Query:
  <br/>
    <textarea id="sparqlText" name="queryString" rows="6" cols="70">
SELECT *
WHERE {
    ?s ?p ?o .
}
    </textarea>
    <a href="#" onclick="document.sparqlForm.queryString.value='';">Clear</a>
    <br/>
    <br/>
    <br/>
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
</div>
<div>
<a href="/graphs">Select another graph<a/>
</div>
</body>
</html>