package pl.bsk.projekt.resources;

import java.io.IOException;
import java.sql.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.*;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

/**
 * Created by Bartosz on 08.04.2017.
 */

@Path("/")
public class Main {
    
    private Connection connection = null;
    
    public List<String> listOfRoles=new ArrayList<>();
    
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
    
    
    private boolean checkRole(String username, String role) throws IOException, SQLException{
        PreparedStatement query = connection.prepareStatement("SELECT Nazwa FROM Rola WHERE ID IN(SELECT ID_Rola FROM RolaUżytkownika WHERE ID_Użytkownik IN(SELECT ID FROM Użytkownik WHERE Login='"+username+"'))");
        ResultSet rs = query.executeQuery();
        while(rs.next()){
            if(role.equals(rs.getString("Nazwa"))){
            return true;
            }
        }
        return false;
    }

    
    @Context
    HttpServletResponse response;

    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMessage() {
        return "Hello world!";
    }
    
    @GET
    @Path("menu/{role}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMenu(@PathParam("role") String role) throws SQLException {
        Connect();
        PreparedStatement query = connection.prepareStatement("SELECT ID FROM Rola WHERE Nazwa='" + role + "';");
        ResultSet rs = query.executeQuery();   
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();     
        String menu = "";
        
        if(rs.next() && columns == 1)
        {
            
            query = connection.prepareStatement("SELECT * FROM Rola WHERE ID=" + rs.getObject(1).toString() + ";");
            rs = query.executeQuery();   
            md = rs.getMetaData();
            columns = md.getColumnCount(); 
            HashMap row = new HashMap(columns);

            if (rs.next())
            {            
                for (int i = 1; i <= columns; ++i)
                    row.put(md.getColumnName(i), rs.getObject(i));
            }

            Disconnect();

            // FORMUŁOWANIE MENU
            menu = "<ul class=\"nav\" id=\"side-menu\">" + 
                "<li>" + 
                    "<a href=\"index.html\"><i class=\"fa fa-dashboard fa-fw\"></i> Strona główna</a>" + 
                "</li>";

            if(row.get("RolaWyświetlanie").toString().equals("true") || row.get("RolaEdycja").toString().equals("true"))
            {
                menu += "<li>" + 
                    "<a href=\"#\"><i class=\"fa fa-cog fa-fw\"></i> Role<span class=\"fa arrow\"></span></a>" + 
                        "<ul class=\"nav nav-second-level\">";
                if(row.get("RolaWyświetlanie").toString().equals("true"))
                {
                    menu += "<li>" + 
                                "<a href=\"roles.html\"><i class=\"fa fa-table fa-fw\"></i> Przeglądaj role</a>" + 
                            "</li>";
                }
                if(row.get("RolaEdycja").toString().equals("true"))
                {
                    menu += "<li>" + 
                                "<a href=\"rolesedit.html\"><i class=\"fa fa-plus-square fa-fw\"></i> Dodaj rolę</a>" + 
                            "</li>";
                }
                menu += "</ul>" + 
                    "<!-- /.nav-second-level -->" + 
                "</li>";            
            }

            if(row.get("PacjentWyświetlanie").toString().equals("true"))
            {
                menu += "<li>" + 
                    "<a href=\"patients.html\"><i class=\"fa fa-table fa-fw\"></i> Pacjenci</a>" + 
                "</li>";
            }

            if(row.get("OsobaWyświetlanie").toString().equals("true") || row.get("OsobaEdycja").toString().equals("true"))
            {
                menu += "<li>" + 
                    "<a href=\"#\"><i class=\"fa fa-users  fa-fw\"></i> Osoby<span class=\"fa arrow\"></span></a>" + 
                        "<ul class=\"nav nav-second-level\">";
                if(row.get("OsobaWyświetlanie").toString().equals("true"))
                {
                    menu += "<li>" + 
                                "<a href=\"people.html\"><i class=\"fa fa-table fa-fw\"></i> Przeglądaj osoby</a>" + 
                            "</li>";
                }
                if(row.get("OsobaEdycja").toString().equals("true"))
                {
                    menu += "<li>" + 
                                "<a href=\"personedit.html\"><i class=\"fa fa-plus-square fa-fw\"></i> Dodaj osobę</a>" + 
                            "</li>";
                }
                menu += "</ul>" + 
                    "<!-- /.nav-second-level -->" + 
                "</li>";
            }
            
            
            if(row.get("RejestracjaWyświetlanie").toString().equals("true") || row.get("RejestracjaEdycja").toString().equals("true") || row.get("RejestracjaDodawanie").toString().equals("true") || row.get("RejestracjaUsuwanie").toString().equals("true"))
            {
                menu += "<li>" + 
                    "<a href=\"#\"><i class=\"fa fa-pencil-square-o fa-fw\"></i> Rejestracje<span class=\"fa arrow\"></span></a>" + 
                        "<ul class=\"nav nav-second-level\">";

                    menu += "<li>" + 
                                "<a href=\"registration.html\"><i class=\"fa fa-table fa-fw\"></i> Przeglądaj rejestracje</a>" + 
                            "</li>";
                    if(row.get("RejestracjaDodawanie").toString().equals("true"))
                    {
                        menu += "<li>" + 
                                    "<a href=\"register.html?id=0\"><i class=\"fa fa-plus-square fa-fw\"></i> Rejestruj</a>" + 
                                "</li>";
                    }
                
                menu += "</ul>" + 
                    "<!-- /.nav-second-level -->" + 
                "</li>";
            }
            
            if(row.get("WizytaWyświetlanie").toString().equals("true") || row.get("WizytaEdycja").toString().equals("true") || row.get("WizytaDodawanie").toString().equals("true") || row.get("WizytaUsuwanie").toString().equals("true"))
            {
                menu += "<li>" + 
                    "<a href=\"#\"><i class=\"fa fa-briefcase fa-fw\"></i> Wizyty <span class=\"fa arrow\"></span></a>" + 
                        "<ul class=\"nav nav-second-level\">";

                    menu += "<li>" + 
                                "<a href=\"visit.html\"><i class=\"fa fa-table fa-fw\"></i> Przeglądaj wizyty</a>" + 
                            "</li>";
                    if(row.get("WizytaDodawanie").toString().equals("true"))
                    {
                        menu += "<li>" + 
                                    "<a href=\"addVisit.html?registerId=0\"><i class=\"fa fa-plus-square fa-fw\"></i> Nowa wizyta</a>" + 
                                "</li>";
                    }
                
                menu += "</ul>" + 
                    "<!-- /.nav-second-level -->" + 
                "</li>";
            }
            
            if(row.get("HistoriaLeczeniaWyświetlanie").toString().equals("true") || row.get("HistoriaLeczeniaEdycja").toString().equals("true") || row.get("HistoriaLeczeniaDodawanie").toString().equals("true") || row.get("HistoriaLeczeniaUsuwanie").toString().equals("true"))
            {
                menu += "<li>" + 
                    "<a href=\"#\"><i class=\"fa fa-folder-o fa-fw\"></i> Kartoteka <span class=\"fa arrow\"></span></a>" + 
                        "<ul class=\"nav nav-second-level\">";

                    menu += "<li>" + 
                                "<a href=\"kartoteka.html\"><i class=\"fa fa-table fa-fw\"></i> Przeglądaj kartotekę</a>" + 
                            "</li>";
                    if(row.get("HistoriaLeczeniaDodawanie").toString().equals("true"))
                    {
                        menu += "<li>" + 
                                    "<a href=\"addKartoteka.html?id=0\"><i class=\"fa fa-plus-square fa-fw\"></i> Nowa kartoteka</a>" + 
                                "</li>";
                    }
                
                menu += "</ul>" + 
                    "<!-- /.nav-second-level -->" + 
                "</li>";
            }
            
            menu += "</ul>";
        }
        
        return menu;
    }
    
    // Funkcja uruchamiana w momencie wchodzenia na stronę logowania.
    // 1. Jeśli użytkownik jest już zalogowany, to przerzuca do panelu.
    // 2. Jeśli użytkownik nie jest zalogowany, zostaje przy oknie logowania.
    @GET
    @Path("login/check")
    @Produces(MediaType.TEXT_PLAIN)
    public String checkIfLogged(@Context HttpServletRequest req) throws IOException {
        // Sprawdzamy, czy istnieje sesja dla użytkownika i jeśli tak to przerzucamy od razu do panelu
        HttpSession session = req.getSession(true);
        Object user = session.getAttribute("user");
        if(user != null)
           return "logged";
        
        return "unlogged";            
    }
    
    // Funkcja zwraca z sesji dane o zalogowanym użytkowniku.
    @GET
    @Path("login/info/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getInfoAboutLoggedUser(@Context HttpServletRequest req){
        // Sprawdzamy, czy istnieje sesja dla użytkownika i jeśli tak to przerzucamy od razu do panelu
        HttpSession session = req.getSession(false);
        Object user = session.getAttribute("user");
        List<String> info = new ArrayList<String>();
        if(user != null){
            info.add(user.toString());
            info.add(session.getAttribute("role").toString());
        }
        
        return info;
    }

    // Funkcja do obsługi formularza logowania
    @POST
    @Path("login/form")
    public void logIn(@Context HttpServletRequest req,
                      @FormParam("lg_username") String username,
                      @FormParam("lg_password") String password,
                      @FormParam("lg_role") String role) throws IOException, SQLException
    {
        Connect();
        boolean isLoginSuccesfull = false;
        Statement statement = connection.createStatement();
        PreparedStatement query = connection.prepareStatement("SELECT * FROM Użytkownik");
        ResultSet rs = query.executeQuery();
        while(rs.next()) {
            // Sprawdzamy, czy jest już zalogowany z określoną rolą i jeśli tak, to z nią logujemy
            if (username.equals(rs.getString("Login")) && password.equals(rs.getString("Hasło"))
                    && !"null".equals(rs.getString("ObecnaRola")) && !"NULL".equals(rs.getString("ObecnaRola")))
            {
                //sprawdzam czy posiada role, z którą próbuje się zalogować
                if(checkRole(rs.getString("Login"),role)){
                
                    isLoginSuccesfull = true;

                    // Ustawiamy sesję dla danego użytkownika, jeżeli aktualnie jej nie ma
                    HttpSession session = req.getSession(true);
                    session.setAttribute("user", rs.getString("Login"));
                    session.setAttribute("role", rs.getString("ObecnaRola"));

                    Disconnect();
                    break;
                }
            }
            // Sprawdzamy czy login i hasło się zgadzają.
            else if (username.equals(rs.getString("Login")) && password.equals(rs.getString("Hasło"))){
                // Jeśli aktualna rola (dla sesji użytkownika) się zgadza lub aktualnie nie ma roli (brak
                // sesji) to logowanie poprawne.
                if(role.equals(rs.getString("ObecnaRola")) || "null".equals(rs.getString("ObecnaRola")) || "NULL".equals(rs.getString("ObecnaRola"))){
                    if(checkRole(rs.getString("Login"),role)){    
                        isLoginSuccesfull = true;
                        
                        // Ustawiamy sesję dla danego użytkownika
                        HttpSession session = req.getSession(true);
                        session.setAttribute("user", rs.getString("Login"));
                        session.setAttribute("role", rs.getString("ObecnaRola"));
                            
                        // Ustawiamy aktualną rolę  
                        statement.executeUpdate("UPDATE Użytkownik SET ObecnaRola = '" + role + "' WHERE ID = "
                                + Integer.parseInt(rs.getString("ID")));

                        Disconnect();
                        break;
                    }
                }       
            }
        }
        if(isLoginSuccesfull)
            response.sendRedirect("../../admin/index.html");
        else
            response.sendRedirect("../../login.html?logged=failed");
    }
    
    // Funkcja do obsługi wylogowywania
    @GET
    @Path("logout")
    public void logout(@Context HttpServletRequest req) throws IOException, SQLException
    {
        // Pobieramy nazwę użytkownika z sesji
        HttpSession session = req.getSession(false);
        Object user = session.getAttribute("user");
        
        // Jeżeli mamy zalogowanego użytkownika, to dokonujemy zmian
        if(user != null)
        {
            Connect();
            Statement statement = connection.createStatement();
            PreparedStatement query = connection.prepareStatement("SELECT * FROM Użytkownik WHERE Login='" +
                    user.toString() + "';");
            ResultSet rs = query.executeQuery();
            
            while(rs.next()) {
                // Sprawdzamy, czy taki użytkownik istnieje w BD i czy jest aktualnie zalogowany
                // (ma ustawioną aktualną rolę)
                if (user.toString().equals(rs.getString("Login")) && rs.getString("ObecnaRola") != "null")
                {
                    // Jeśli znaleziono użytkownika, to ustawiamy jego aktualną rolę na null
                    statement.executeUpdate("UPDATE Użytkownik SET ObecnaRola = 'null' WHERE Login = '" + 
                                    rs.getString("Login") + "';");
                    
                    // Usuwamy atrybut użytkownik z sesji
                    session.removeAttribute("user");
                    session.removeAttribute("role");
                    
                    Disconnect();
                    break;
                }
            }
            
            response.sendRedirect("../login.html");
        }
    }
    
    //zwraca wszystkie dostepne role
    @GET
    @Path("allroles")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList getRoles() throws SQLException {
        Connect();
        PreparedStatement query = connection.prepareStatement("SELECT Nazwa FROM Rola");
        ResultSet rs = query.executeQuery();
        ArrayList<String> list=new ArrayList<>();       
        
        while(rs.next())
            list.add(rs.getString("Nazwa"));
        
        Disconnect();
        return list;
    }
}
