<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="en" lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>SPARQL Query Results to XHTML - Graph ${graphName}</title>
</head>
<body>
<h1>SPARQL Query Results to XHTML</h1>

<div>
    <#if answerType == "select">
        <h2>Variable Bindings Result</h2>
    <#elseif answerType == "ask">
        <h2>Boolean Result</h2>
    </#if>
    <table border="1">
        <tr>
            <#list answer.variableNames as columnName>
            <th>${columnName}</th>
            </#list>
        </tr>
        <#list answer.columnValuesIterator() as columnValue>
        <tr>
            <#list columnValue as value>
            <td>${value.value?html}</td>
            </#list>
        </tr>
        </#list>
    </table>
</div>
<#if (timeTaken >= 0)>
<div>
    <br/>
    <#if tooManyRows>
    Too many answers returned by query engine, only returning ${maxRows} results.
    </#if>
    Time taken to answer query: ${timeTaken} milliseconds.
</div>
</#if>
<div>
    <br/>
    <br/>
    <a href="/graph/${graphName}">Write a new query</a>
    <br/>
    <br/>
</div>
</body>
</html>