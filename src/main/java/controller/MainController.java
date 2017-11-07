package controller;

import DAO.StudentDAO;
import DAO.WebTemplateDao;
import UI.UI;
import DAO.ConnectDB;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import models.Student;

import java.io.*;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MainController implements HttpHandler {

    public void handle(HttpExchange httpExchange) throws IOException {
        WebTemplateDao webTemplateDao = new WebTemplateDao();
        String response = "";
        String method = httpExchange.getRequestMethod();

        if (method.equals("GET")) {
            response = webTemplateDao.getSiteTemplate("static/login-page.html");

        }

        if (method.equals("POST")) {
            InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(),
                    "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();
            Map<String,String> inputs = parseFormData(formData);
            String login = inputs.get("login");
            String password = inputs.get("password");
            String user = setUp(login,password);
            System.out.println(user);
            if(user.equals("Admin")){
                response = webTemplateDao.getSiteTemplate("static/admin-page.html");
            }
        }

        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public String setUp(String login,String password){
        try{

            return loginToSystem(login,password);
        } catch (SQLException e){

        }catch (NoSuchAlgorithmException e){

        }
        return "a;";
    }

    public String loginToSystem(String login,String passwordGet) throws SQLException,NoSuchAlgorithmException{
        String password = HashSystem.getStringFromSHA256(passwordGet);
        ConnectDB connectDB = DAO.ConnectDB.getInstance();
        StudentDAO studentd = new StudentDAO();
        String sql = String.format("SELECT * FROM users WHERE email like '%s' and password like '%s'",login,password);
        ResultSet result = connectDB.getResult(sql);

        if (result.next()) {

            if (result.getString("role").equals("student")) {
                ResultSet studentResult = connectDB.getResult(String.format("SELECT users.id, first_name, last_name, email, password, role, klass, money, experience, level from users join wallets on users.id = wallets.id WHERE email like '%s' and password like '%s'",login,password));
                studentResult.next();
                Student student = studentd.createStudent(studentResult);
                StudentController studentController = new StudentController(student);
                studentController.startController();
                return "Student";
            }

            if (result.getString("role").equals("admin")) {
                return "Admin";
            }

            if (result.getString("role").equals("mentor")) {
                MentorController mentorController = new MentorController();
                mentorController.startController();
                return "Mentor";
            }



        } else {
            System.out.println("User doesn't exist");
        }

    return "dupa";
    }

    private Map<String,String> parseFormData(String formData) throws UnsupportedEncodingException {

        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for(String pair : pairs){
            String[] keyValue = pair.split("=");
            String value = new URLDecoder().decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
    }
}
