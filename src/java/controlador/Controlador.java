/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import service.ServiciosFukusuke_Service;

/**
 *
 * @author amlazo
 */
public class Controlador extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws javax.jms.JMSException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, JMSException {
        response.setContentType("text/html;charset=UTF-8");
        service.ServiciosFukusuke_Service servicio=new ServiciosFukusuke_Service();
        String menuSeleccionado=request.getParameter("ddMenues");
        String user=request.getParameter("txtUsuario");
        
        if(user.isEmpty()){
            //Usuario vacio
            response.sendRedirect("Pedidos.jsp?error=Usuario vacio");
        }else{
            String menus=servicio.getServiciosFukusukePort().getDatosCliente("lazo");

            JSONObject jsonObject =new JSONObject(menus);
            JSONArray jSONArray=jsonObject.getJSONArray("usuarios");
            boolean existe=false;
            for(int i=0;i<jSONArray.length();i++){
                JSONObject explorObject=jSONArray.getJSONObject(i);
                System.out.println(explorObject.getString("appat"));
                existe=true;
            }
            
            if(existe){
                servicio.getServiciosFukusukePort().getDatosCliente(user);
                int menu=servicio.getServiciosFukusukePort().getIdMenu(menuSeleccionado);
                servicio.getServiciosFukusukePort().setPedido(user, menu);
                
                try{
                    String nombreCola="queue.PedidoUsuario";
                    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
                    Connection connection= connectionFactory.createConnection();
                    Session session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    Destination cola= session.createQueue(nombreCola);
                    MessageProducer producer= session.createProducer(cola);
                    
                    TextMessage message=session.createTextMessage(user/*Aqui debe ir el id de venta*/);
                    producer.send(message);
                    producer.close();
                    connection.close();
                    
                }catch(JMSException e){
                    response.sendRedirect("Pedidos.jsp?error=Cola bad: "+e.toString());
                    //Problemas en la cola
                }
                
            }else{
                response.sendRedirect("Pedidos.jsp?error=Usuario no existe");
                //Usuario no existe
                
            }
        }
        
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Controlador</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Controlador at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (JMSException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (JMSException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
