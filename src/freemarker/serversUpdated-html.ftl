<html>
<head>
    <title>Distributed SPARQL -- Local Servers Updated!</title>
</head>
<body>
<h1>
    Local servers updated successfully!
</h1>
<p/>
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
<div>
Actions:<br/>
    <dd>1. Head to the distributed <a href="/graphs">query</a> page.</dd><br/>
    <dd>2. Make changes to the server list <a href="/">again</a>.</dd>
</div>

