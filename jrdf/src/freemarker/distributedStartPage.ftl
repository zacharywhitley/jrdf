<html>
<head>
  <title>Distributed Sparql Query Setup Page</title>
</head>
<body>
  <h1>
    Set up the servers and port number for distributed processing
  </h1>
  List of servers:
    <form id="servers" name="serversForm" method="post" action="\">
        <textarea id="serverText" name="serversString" rows="6" cols="70"></textarea>
        <p/>
        Add: <input type="radio" name="${action}" value="add" checked="checked"/>&nbsp;&nbsp;&nbsp;
        Remove: <input type="radio" name="${action}" value="remove">
        <p/>
        <p/>
        <input type="submit" value="Submit" />
    </form>
</body>
</html>