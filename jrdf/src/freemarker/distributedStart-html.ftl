<#-- @ftlvariable name="serverList" type="java.lang.String[]" -->
<html>
<head>
    <title>Distributed SPARQL Query Setup Page</title>
</head>
<body>
<h1>
    Set up local query servers for distributed processing
</h1>
<div>
    Current query servers
    <table border="1">
        <tr>
            <th>Local query servers</th>
        </tr>
        <#list serverList as server>
        <tr>
            <td><a href="http://${server}:8182">${server}</a></td>
        </tr>
        </#list>
    </table>
</div>
<p/>
<p/>
<p/>
<div>
    List of servers:
    <form id="servers" name="serversForm" method="post" action="serversUpdated.html">
        <textarea id="serverText" name="serversString" rows="6" cols="70"></textarea>
        <p/>
        Add: <input type="radio" name="${action}" value="add" checked="checked"/>&nbsp;&nbsp;&nbsp;
        Remove: <input type="radio" name="${action}" value="remove">
        <p/>
        <p/>
        <input type="submit" value="Submit" />
    </form>
</div>
</body>
</html>