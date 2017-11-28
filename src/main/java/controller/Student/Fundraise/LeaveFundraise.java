package controller.Student.Fundraise;

import DAO.ConnectDB;
import DAO.FundraiseDAO;
import DAO.WebTemplate;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.StudentController;
import models.Fundraise;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.*;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static controller.helpers.ParseForm.parseFormData;

public class LeaveFundraise implements HttpHandler {



    public void handle(HttpExchange httpExchange) throws IOException {

        String response = "";
        String method = httpExchange.getRequestMethod();
        String sessionID = StudentController.getSession();



        try {
            String userID = getUserID(sessionID);


            if (method.equals("GET")) {

                response = listAllFundraise(userID);
            }

            if (method.equals("POST")) {
                InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String formData = br.readLine();
                Map<String, String> inputs = parseFormData(formData);


                String fundraiseID = inputs.get("id");

                leaveFundraise(fundraiseID, userID);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        httpExchange.sendResponseHeaders(200, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String getUserID(String sessionIDFull) throws SQLException {

        ConnectDB connectDB = DAO.ConnectDB.getInstance();
        String sql = String.format("SELECT user_id FROM sessions WHERE session_id LIKE '%s'", sessionIDFull);
        ResultSet result = connectDB.getResult(sql);


        String userID = result.getString("user_id");



        return userID;

    }



    private void leaveFundraise(String fundraiseID, String userID) throws SQLException {

        FundraiseDAO fundraiseDAO = new FundraiseDAO();
        fundraiseDAO.leaveFundraise(fundraiseID, userID);



    }

    public String listAllFundraise(String userID) throws SQLException{

        ArrayList<Fundraise> fundraiseList = getFundraiseList(userID);

        ArrayList<ArrayList<String>> data = createJtwigData(fundraiseList);


        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/student/join-fundraise.twig");
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

    private ArrayList<Fundraise> getFundraiseList(String userID) throws SQLException {

        FundraiseDAO fundraiseDAO = new FundraiseDAO();

        ArrayList<Fundraise> fundraiseList = fundraiseDAO.getJoinedFundraiseList(userID);



        return fundraiseList;
    }

    private ArrayList<ArrayList<String>> createJtwigData(ArrayList<Fundraise> fundraiseList) {

        ArrayList<ArrayList<String>> data = new ArrayList<>();
        ArrayList<String> record = new ArrayList<>();

        for(Fundraise fundraise: fundraiseList){
            record.add(fundraise.getFundraiseID().toString());
            record.add(fundraise.getTitle());
            data.add(record);
            record = new ArrayList<>();
        }


        return data;
    }
}