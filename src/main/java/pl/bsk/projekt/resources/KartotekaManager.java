/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.bsk.projekt.resources;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Grzegorz
 */
@Path("/kartoteka")
public class KartotekaManager {
    private Connection connection = null;
    
    private void Connect() {
        try {
            connection = DatabaseConnection.getConnection();
            if (connection != null) {
                System.out.println("Connection estabilished");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Disconnect(){
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Context
    HttpServletResponse response;
    
    @GET
    @Path("getAll/{patientID}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList getKartotekaWithPatient(@PathParam("patientID") int patientID) throws SQLException {
        Connect();
        PreparedStatement query = connection.prepareStatement("SELECT * From HistoriaChoroby where ID_Pacjent="+patientID);
        ResultSet rs = query.executeQuery();
        int count = 0;        
   
        while(rs.next())
            count++;
        
        ArrayList list = new ArrayList(count);
        if(count > 0)
        {
            rs = query.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();
            
            
            while (rs.next())
            {
                HashMap row = new HashMap(columns);
                for (int i = 1; i <= columns; ++i)
                    row.put(md.getColumnName(i), rs.getObject(i));
                list.add(row);
            }
            Disconnect();
            return list;
        }
        
        Disconnect();
        return list;
    }
    
    @GET
    @Path("getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList getKartoteka() throws SQLException {
        Connect();
        PreparedStatement query = connection.prepareStatement("SELECT * From HistoriaChoroby" );
        ResultSet rs = query.executeQuery();
        int count = 0;        
   
        while(rs.next())
            count++;
        
        ArrayList list = new ArrayList(count);
        if(count > 0)
        {
            rs = query.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();
            
            
            while (rs.next())
            {
                HashMap row = new HashMap(columns);
                for (int i = 1; i <= columns; ++i)
                    row.put(md.getColumnName(i), rs.getObject(i));
                list.add(row);
            }
            Disconnect();
            return list;
        }
        
        Disconnect();
        return list;
    }
    
    //dodanie historii choroby
    @POST
    @Path("add")
    public void add(
                      @FormParam("idPacjent") String idPacjent,
                      @FormParam("dataStart") String dataStart,
                      @FormParam("dataStop") String dataStop,
                      @FormParam("leczenie") String leczenie) throws IOException, SQLException
    {
        Connect();
        Statement statement = connection.createStatement();
        
        statement.executeUpdate("INSERT INTO HistoriaChoroby VALUES ('"+idPacjent+"','"+dataStart+"','"+dataStop+"','"+leczenie+"')");

        Disconnect();
        
        response.sendRedirect("../../admin/pages/kartoteka.html");
    }
    
    //usuwanie historii chorby po id
    @POST
    @Path("{id}/delete")
    public void delete(@PathParam("id") Integer IDChoroby) throws SQLException, IOException {
        Connect();        
        Statement statement = connection.createStatement(); 
        
        statement.executeUpdate("DELETE FROM HistoriaChoroby WHERE ID = " + IDChoroby.toString());               
        Disconnect();
        
        response.sendRedirect("../../../admin/pages/kartoteka.html");
    }
    
    //Zwraca info o historii choroby
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList getInfo(@PathParam("id") int id) throws SQLException {
        Connect();
        PreparedStatement query = connection.prepareStatement("SELECT * from HistoriaChoroby WHERE ID="+id);
        ResultSet rs = query.executeQuery();
        
        ArrayList list=new ArrayList(1);
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        
        while(rs.next()){
        HashMap row = new HashMap(columns);   
        for (int i = 1; i <= columns; ++i)
            row.put(md.getColumnName(i), rs.getObject(i));
        list.add(row);
        }
        
        
        Disconnect();
        return list;
    }
    
    // Funkcja edytuje w bazie danych info o chorobie
    @POST
    @Path("{id}/edit")
        public void editWithID(@PathParam("id") Integer IDChoroby,
                        @FormParam("idPacjent") String IDPacjent,
                        @FormParam("dataStart") String dataStart,
                        @FormParam("dataStop") String dataStop,
                        @FormParam("leczenie") String leczenie) throws IOException, SQLException
    {
        Connect();        
        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE HistoriaChoroby SET ID_Pacjent='" + IDPacjent + "', DataRozpoczęciaLeczenia='" + dataStart + "', DataZakończeniaLeczenia='" + dataStop + "',Leczenie='"+leczenie+"' WHERE ID = " + IDChoroby);           
        Disconnect();
        
        response.sendRedirect("../../../admin/pages/kartoteka.html");
    }
}
