<#ftl ns_prefixes={"D":"http://www.w3.org/2005/sparql-results#"}>

<?xml version="1.0" encoding="UTF-8"?>
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
            <#assign head = doc.sparql.head>
            <#list head.variable as oneVariable>
            <th>${oneVariable.@name}</th>
            </#list>
        </tr>
        <#assign results = doc.sparql.results>
        <#list results.result as result>
            <tr>
            <#list result?children as binding>
                <#list binding?children as oneBinding>
                    <td>
                        <#if oneBinding?node_type = 'element' && oneBinding?node_name == "bnode">
                            nodeID ${oneBinding}
                        <#elseif oneBinding?node_type= 'element' && oneBinding?node_name == "uri">
                            URI <a href="${oneBinding}">${oneBinding}</a>
                        <#elseif oneBinding?node_type = 'element' && oneBinding?node_name == "literal">
                            ${oneBinding}
                            <#if oneBinding.@datatype[0]??>
                                (datatype <a href="${oneBinding.@datatype}">${oneBinding.@datatype}</a>)
                            <#elseif oneBinding.@lang[0]??>
                                @ ${oneBinding.@lang}
                            </#if>
                        </#if>
                    </td>
                </#list>
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
<#if hasMore>
<div>
<br/>
    <a href="/graphs/${graphName}/result.html?next=true">More results</a>
</div>
</#if>

<div>
<br/>
<br/>
<a href="/graphs/${graphName}">Write a new query</a>
<br/>
<br/>
</div>
</body>
</html>
