<%-- 
    Document   : Pedidos
    Created on : 17-06-2017, 17:20:17
    Author     : amlazo
--%>

<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>
<%@page import="service.ServiciosFukusuke_Service"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>PedidosAhora</title>
    </head>
    <body>
        <h1>PedidosAhora</h1>
        
        <form action="Controlador" method="POST">
            <%!String hola;%>
            <%service.ServiciosFukusuke_Service servicio=new ServiciosFukusuke_Service();%>
            Nombre de usuario:<input type="text" name="txtUsuario" value="" /><br/>
            Menu:<select name="ddMenues">
                <%
                    String menus=servicio.getServiciosFukusukePort().getListaMenu();

                    JSONObject jsonObject =new JSONObject(menus);
                    JSONArray jSONArray=jsonObject.getJSONArray("menus");
                    for(int i=0;i<jSONArray.length();i++){
                        JSONObject explorObject=jSONArray.getJSONObject(i);
                        %>
                        <option value="<%=String.valueOf(explorObject.getInt("idpromo"))%>"><%=explorObject.getString("descripcion")%></option>
                        <%
                    }
                %>
                </select><br/>
            <input type="submit" value="Buscar" name="btnBuscar" /><br/>
            Este usuario no tiene un pedido asociado
        </form>
    </body>
</html>
