<#-- @ftlvariable name="graphName" type="java.lang.String" -->
<#-- @ftlvariable name="tooManyRows" type="boolean" -->
<#-- @ftlvariable name="timeTaken" type="long" -->
<#-- @ftlvariable name="answerType" type="java.lang.String" -->
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="en" lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>SPARQL Query Results to XHTML - Graph ${graphName}</title>
</head>
<body>
<h1>SPARQL Query Results to XHTML (FreeMarker)</h1>

<div>
    <h2>
        <#if answerType == "select">
        Variable Bindings Result
        <#elseif answerType == "ask">
        Boolean Result
        </#if>
    </h2>

    <p>
        <#list answer.columnValuesIterator() as columnValue>
        <#list columnValue as value>
        Value ${value.value}
        </#list>
        </#list>
    </p>
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