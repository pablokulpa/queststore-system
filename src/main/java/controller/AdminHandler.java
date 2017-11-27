package controller;

import java.io.*;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import DAO.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.helpers.HashSystem;
import controller.helpers.ParseForm;
import controller.helpers.Sessions;
import models.Mentor;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class AdminHandler  implements HttpHandler {

    public void handle(HttpExchange httpExchange) throws IOException {
        URI uri = httpExchange.getRequestURI();
        String path = uri.getPath();
        String response = "";
        String method = httpExchange.getRequestMethod();
        String sessionId = getSessionIdFromCookie(httpExchange);

        if (method.equals("GET") && Sessions.checkSession(sessionId,"Admin")) {
            response = getResponse(path);
        }
        else if (method.equals("POST")){
            Map<String,String> parsedPost = parsePost(httpExchange);
            boolean handleStatus = handleParsedPost(path,parsedPost);
            response = getHandleResponse(handleStatus);
        }
        else{
            Sessions.redirect(httpExchange);
        }
        httpExchange.sendResponseHeaders(200, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();

    }

    private String getHandleResponse(boolean handleStatus) {
        if(handleStatus){
            return WebTemplate.getSiteContent("templates/success.twig");
        }
        else{
            return WebTemplate.getSiteContent("templates/error.twig");
        }
    }


    public String getResponse(String path){
        String response = "";
        if(path.equals("/admin")){
            response = WebTemplate.getSiteContent("templates/admin/admin-menu.twig");
        }
        else if(path.equals("/admin/create-group")){
            response = WebTemplate.getSiteContent("templates/admin/create-group.twig");
        }
        else if (path.equals("/admin/create-mentor")){
            response = WebTemplate.getSiteContent("templates/admin/create-mentor.twig");
        }
        else if (path.equals("/admin/edit-mentor")){
            response = WebTemplate.getSiteContent("templates/admin/edit-mentor.twig");
        }
        else if (path.equals("/admin/mentor-list")){
            response = getMentorListRespone();
        }

        return response;
    }

    public boolean handleParsedPost(String path, Map<String, String> parsedForm) throws IOException {
        boolean handleStatus = false;
        if(path.equals("/admin/create-mentor")){
            handleStatus = createMentor(parsedForm);

        }
        return handleStatus;
    }

    public Map<String, String> parsePost(HttpExchange httpExchange) throws IOException {
        InputStreamReader inputStreamReader;
        Map<String,String> inputs = null;
        try {
            inputStreamReader = new InputStreamReader(httpExchange.getRequestBody(),
                    "utf-8");
            BufferedReader br = new BufferedReader(inputStreamReader);
            String formData = br.readLine();
            System.out.println(formData);
            inputs = ParseForm.parseFormData(formData);
        } catch (UnsupportedEncodingException e) {
            return inputs;
        }
        return inputs;
    }

    public String getSessionIdFromCookie(HttpExchange httpExchange) throws IOException {
        String sessionIDFull = "";
        try {
            String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");
            String[] sessionID = cookieStr.split("sessionId=");
            sessionIDFull= sessionID[1].replace("\"", "");
        }catch (NullPointerException e){
            return sessionIDFull;
        }
        return sessionIDFull;
    }

    public boolean createMentor(Map<String, String> parsedForm){
        MentorDAO mentorDAO = null;
        try {
            mentorDAO = new MentorDAO();
            String firstName = parsedForm.get("first-name");
            String lastName = parsedForm.get("last-name");
            String email = parsedForm.get("email");
            String password = parsedForm.get("password");
            String passwordHash = HashSystem.getStringFromSHA256(password);
            String klass = parsedForm.get("class");
            Mentor mentor = new Mentor(firstName, lastName, email, passwordHash, klass);
            mentorDAO.add(mentor);
        } catch (SQLException e) {
            return false;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
        return true;
    }

    public String listAllMentors() throws SQLException{

        MentorDAO mDAO = new MentorDAO();
        ArrayList<ArrayList<String>> data = new ArrayList<>();

        ArrayList<String> record = new ArrayList<>();
        ArrayList<Mentor> mentorList = mDAO.get();
        for(Mentor mentor: mentorList){
            record.add(mentor.getFirstName());
            record.add(mentor.getLastName());
            record.add(mentor.getEmail());
            data.add(record);
            record = new ArrayList<>();
        }

        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/admin/view-mentor.twig");
        JtwigModel model = JtwigModel.newModel();

        String response = "";
        model.with("data", data);
        try {
            response = template.render(model);
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    public String getMentorListRespone(){
        String response = "";
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/admin/view-mentor.twig");
        JtwigModel model = JtwigModel.newModel();
        ArrayList<Mentor> mentorList= getMentorList();
        model.with("mentorList", mentorList);
        response = template.render(model);
        return response;
    }


    public ArrayList<Mentor> getMentorList(){
        MentorDAO mentorDAO = null;
        ArrayList<Mentor> mentorList = null;
        try {
            mentorDAO = new MentorDAO();
            mentorList = mentorDAO.get();
        } catch (SQLException e) {
            return mentorList;
        }
        return mentorList;
    }
}
