<html>
<head>
  <title>Graphs Summary Page</title>
</head>
<body>
<h1>Choose graphs in the directory <i>${dirName}</i>.</h1>
<br/>
<table border="1">
    <tr>
        <th>Graph ID</th>
        <th>Graph name</th>
    </tr>
    <tr>
        <#list graphs?keys as graph>
            <td>${graph}</td>
            <td><a href="/graphs/${graphs[graph]}">${graphs[graph]}</a></td>
        </#list>
    </tr>
</table>
<div>
<br/>
<br/>
<a href="javascript:window.location.reload()">Refresh</a>
</div>
</body>
</html>
