<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="en" lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>SPARQL Query Results to XHTML - Graph ${graphName}</title>
</head>
<body>
<h1>SPARQL Query Results to XHTML (FreeMarker)</h1>

<div>
    <h2>Variable Bindings Result</h2>
    <table border="1">
        <tr>
            <#list answer.variableNames as columnName>
            <#assign foo = columnName?split("|")>
            <th>${columnName}</th>
            <th>${foo[0]}</th>
            </#list>
        </tr>
        <#list answer.columnValues as columnValue>
        <tr>
          <#list columnValue as value>
           <td>${value}</td>
          </#list>
        </tr>
        </#list>
    </table>
</div>
<div>
<br/>
<div>
<br/>
<br/>
<a href="/graphs/${graphName}">Write a new query</a>
<br/>
<br/>
</div>
</body>
</html>
