package controller;

import DAO.*;
import UI.StudentUI;
import UI.UI;
import com.sun.net.httpserver.HttpHandler;
import controller.helpers.Sessions;
import models.*;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringJoiner;

public class StudentController implements HttpHandler {

    private ArtifactDAO artifactDAO;
    private StudentDAO studentDAO;
    private InventoryDAO inventoryDAO;
    private FundraiseDAO fundraiseDAO;
    private SubmissionDAO submissionDAO;
    private QuestDAO questDAO;
    private Student user;
    static private String sessionIDFull;

    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "";
        String method = httpExchange.getRequestMethod();


        try {
            String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");
            String[] sessionID = cookieStr.split("sessionId=");
            sessionIDFull = sessionID[1].replace("\"", "");



            if (method.equals("GET") && Sessions.checkSession(sessionIDFull,"Student")) {
                response = WebTemplate.getSiteContent("templates/student/student-menu.twig");
            }
            else{
                System.out.println(sessionIDFull);
                Sessions.redirect(httpExchange);

            }
        }catch (NullPointerException e){
            Sessions.redirect(httpExchange);
        }

        httpExchange.sendResponseHeaders(200, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static String getSession() {

        return sessionIDFull;
    }

    public StudentController() throws SQLException {
        artifactDAO = new ArtifactDAO();
        studentDAO = new StudentDAO();
        studentDAO = new StudentDAO();
        inventoryDAO = new InventoryDAO();
        fundraiseDAO = new FundraiseDAO();
        submissionDAO = new SubmissionDAO();
        questDAO = new QuestDAO();
//        user = student;
    }





    private void checkBalance() {

        Integer balance = user.getWallet().getBalance();
        System.out.println("Your balance: " + balance);
    }


    private boolean checkEnoughBalance(Artifact artifact) {

        boolean bool = false;

        Integer balance = user.getWallet().getBalance();
        if (balance >= artifact.getPrice()) {
            bool = true;
        } else {
            UI.showMessage("Not Enough Money!");
        }
        return bool;
    }


//    public void buyArtifact() throws SQLException {
//
//        ArrayList<Artifact> artifactList = artifactDAO.get();
//        listAllArtifacts();
//
//
//        if (artifactList.size() != 0) {
//
//            boolean isTrue = true;
//
//            while (isTrue) {
//
//                Integer ID = UI.getInteger("Choose Artifact by ID :");
//
//                for (Artifact artifact : artifactList) {
//
//                    if (ID.equals(artifact.getID())) {
//
//                        isTrue = false;
//
//                        if (checkEnoughBalance(artifact)) {
//
//                            if (UI.getBoolean("Do you want to buy : " + artifact.getName() + " ?")) {
//                                user.getWallet().substract(artifact.getPrice());
//                                studentDAO.editWalletValue(user);
//                                Inventory inventory = new Inventory(user.getID(), artifact.getID(), UI.getCurrentDate());
//                                inventoryDAO.add(inventory);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }













    private void walletPanel() {
        String choice;
        do {
            StudentUI.printLabel(StudentUI.walletMenuLabel);
            StudentUI.printMenu(StudentUI.walletMenuOptions);
            choice = StudentUI.getChoice();

            switch (choice) {

                case "1": {
                    printWalletStatus();
                    break;
                }


            }
        } while (!choice.equals("0"));


    }

    private void printWalletStatus() {
        System.out.println(user.getWallet().toString());
    }

    private void listAllArtifacts() throws SQLException {

        ArrayList<Artifact> artifactList = artifactDAO.get();
        if (artifactList.size() == 0) {
            UI.showMessage("Artifact list is empty!");
        } else {
            for (Artifact artifact : artifactList) {
                System.out.println(artifact.toString());
            }
        }
    }

    private void listAllMagicArtifacts() throws SQLException {

        ArrayList<Artifact> artifactList = artifactDAO.getMagicItems();
        if (artifactList.size() == 0) {
            UI.showMessage("Artifact list is empty!");
        } else {
            for (Artifact artifact : artifactList) {
                System.out.println(artifact.toString());
            }
        }
    }




//    public void checkStudentArtifacts() throws SQLException {
//
//        ArrayList<Inventory> inventoryList = inventoryDAO.getStudentInventory(user);
//        int no = 0;
//        if (inventoryList.size() == 0) {
//            UI.showMessage("Purchase list is empty!");
//        } else {
//            for (Inventory inventory : inventoryList) {
//                no++;
//                System.out.println(String.format("ID: %d | %s", no, inventory.toString()));
//
//
//            }
//        }
//    }

    private void submissionPanel() throws SQLException {

        String choice;
        do {
            StudentUI.printLabel(StudentUI.submissionMenuLabel);
            StudentUI.printMenu(StudentUI.submitMenuOptions);
            choice = StudentUI.getChoice();

            switch (choice) {

                case "1": {
                    UI.printList(questDAO.get());
                    break;
                }
                case "2": {
                    UI.printList(getUserSubmits());
                    break;
                }
                case "3": {
                    submitQuest();
                    break;
                }


            }
        } while (!choice.equals("0"));


    }


    private ArrayList<Submission> getUserSubmits() throws SQLException {

        return submissionDAO.get();
    }


    private void submitQuest() throws SQLException {

        Integer questId = UI.getInteger("Enter questID: ");

        boolean questAlreadyExists = questDAO.checkQuestExist(questId);
        boolean submissionAlreadyExists = submissionDAO.checkAlreadySubmitted(questId, user.getID());
        StringJoiner errorMessage = new StringJoiner(", ");

        if (!questAlreadyExists) {
            errorMessage.add("You have entered quest that does not exist");

        } else if (submissionAlreadyExists) {
            errorMessage.add("You did already submit this quest");
        }

        if (questAlreadyExists && !submissionAlreadyExists) {

            String description = UI.getString("Enter your submit: ");
            Submission submission = new Submission(user.getID(), questId, description);

            submissionDAO.add(submission);
            UI.showMessage("Submit successful!");

        } else {
            UI.showMessage(errorMessage.toString());
        }
    }
}
