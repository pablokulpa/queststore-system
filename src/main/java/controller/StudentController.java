package controller;

import DAO.ArtifactDAO;
import DAO.GroupDAO;
import DAO.MentorDAO;
import DAO.StudentDAO;
import UI.StudentUI;
import UI.UI;
import models.Artifact;
import models.Mentor;
import models.Student;

import java.util.ArrayList;

public class StudentController {

    private ArtifactDAO artifactDAO = new ArtifactDAO();
    public StudentDAO student = new StudentDAO();

    private Student student_me;

    public StudentController(Student student){

        student_me = student;
    }


    public void startController(){

        handleMainMenu();

    }


    public void handleMainMenu() {

        String choice;

        do {
            StudentUI.printLabel(StudentUI.mainMenuLabel);
            StudentUI.printMenu(StudentUI.menuMainOptions);
            choice = StudentUI.getChoice();

            switch (choice){

                case "1": {
                    artifactPanel();
                    break;
                }
                case "2": {
                    experience();
                    break;
                }
            }
        } while(!choice.equals("0"));
    }

    public void artifactPanel() {

        String choice;
        do {
            StudentUI.printLabel(StudentUI.artifactMenuLabel);
            StudentUI.printMenu(StudentUI.menuArtifactOptions);
            choice = StudentUI.getChoice();

            switch (choice) {

                case "1": {
                    listAllArtifacts();
                    break;
                }
                case "2": {
                    buyArtifact();
                    break;
                }
                case "3": {
                   donateFundraise();
                    break;
                }
                case "4":{
                    checkExperience();
                    break;
                }
            }
        } while(!choice.equals("0"));

    }



    public void checkWallet() {

        Integer balance = student_me.wallet.getBalance();
        System.out.println("Your balance: " + balance);
    }

    public void buyArtifact() {

        listAllArtifacts();

    }

    public void donateFundraise() {

    }

    public void experience() {

    }

    public void checkExperience() {
        Integer experience = student_me.wallet.getExperience();
        UI.showMessage("Your experience: " + experience);

    }

    private void listAllArtifacts() {

        ArrayList<Artifact> artifactList = artifactDAO.get();
        if(artifactList.size() == 0){
            UI.showMessage("Artifact list is empty!");
        } else {
            for(Artifact artifact: artifactList){
                System.out.println(artifact.toString());
            }
        }
    }
}
