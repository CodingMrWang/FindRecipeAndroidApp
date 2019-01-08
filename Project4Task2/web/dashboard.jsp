<%-- 
    Document   : dashboard
    Created on : Nov 6, 2018, 11:06:59 AM
    Author     : ZEXIAN
--%>

<%@page import="org.bson.Document"%>
<%@page import="com.mongodb.client.MongoCursor"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <link rel="stylesheet" href="css/dashboard.css">
        <title>Dashboard</title>
    </head>
    <body>
        <h1> Dashboard </h1>
        <div>
            <h2>Analytics</h2>
            <table class="container">
                <thead>
                    <tr>
                        <th># of Total Visit(Success/Fail)</th>
                        <th># of Users</th>
                        <th>Average Delay</th>
                    </tr>
                </thead>
                <tr>
                    <td><%=request.getAttribute("success")%></td>
                    <td><%=request.getAttribute("user")%></td>
                    <td><%=request.getAttribute("delay")%> ms</td>
                </tr>
            </table>
        </div>
        <div>
            <h2>Logs</h2>
            <table class="container">
        <thead>
            <tr>
              <th>#</th>
              <th>client IP</th>
              <th>Request Time</th>
              <th>Response Time</th>
              <th>Delay</th>
              <th>Phone Model</th>
              <th>User Agent</th>
              <th>Keyword</th>
              <th>Success</th>
              <th>URL from client</th>
              <th>URL sent to 3rd party</th>
              <th>Response from 3rd party</th>
              <th>Response to client</th>
            </tr>
        </thead>
        <% int count = 1; %>
        <% while (((MongoCursor<Document>)request.getAttribute("colls")).hasNext()) {%>
            <%Document d = ((MongoCursor<Document>)request.getAttribute("colls")).next();%>
            <tr class="success">
                <td><%=count%></td>
                <td><%=((Document)d).getString("clientIp")%></td>
                <td><%=((Document)d).getString("requestTime")%></td>
                <td><%=((Document)d).getString("responseTime")%></td>
                <td><%=((Document)d).get("timeCost")%> ms</td>
                <td><%=((Document)d).getString("model")%></td>
                <td><%=((Document)d).getString("userAgent")%></td>
                <td><%=((Document)d).getString("keyword")%></td>
                <td><%=((Document)d).get("success")%></td>
                <td><%=((Document)d).get("clientUrl")%></td>
                <td><%=((Document)d).getString("requestUrl_3rd_1")+ "\n" + ((Document)d).getString("requestUrl_3rd_2")%></td>
                <td><textarea><%=((Document)d).getString("response_3rd")%></textarea></td>
                <td><textarea><%=((Document)d).getString("result")%></textarea></td>
              </tr>
              <% count += 1; %>
        <%}%>
        </div>
        </table>
    </body>
</html>
