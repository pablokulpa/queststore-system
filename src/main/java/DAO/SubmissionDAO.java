package DAO;

import models.Student;
import models.Submission;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SubmissionDAO implements InterfaceDAO<Submission> {

    private ConnectDB connect;

    public SubmissionDAO() throws SQLException{
        connect = ConnectDB.getInstance();
    }

    public void add(Submission submission) throws SQLException{

        String querry = String.format("INSERT INTO submissions "+
                "(quest_id, student_id, is_marked, description) " +
                "VALUES ('%d', '%d', '%d', '%s')",
                submission.getQuestId(), submission.getStudentId(),
                dbBooleanInjection(submission.isMarked()), submission.getDescription());
        connect.addRecord(querry);
    }

    private Integer dbBooleanInjection(Boolean isMarked) {
        if(isMarked) {
            return 1;
        }else return 0;
    }

    private Boolean dbBooleanWithdraw(Integer isMarked) {
        return isMarked.equals(1);
    }



    private Submission createSubmission(ResultSet result) throws SQLException {

        Integer id = result.getInt("id");
        Integer questId = result.getInt("quest_id");
        Integer studentId = result.getInt("student_id");
        Boolean isMarked = dbBooleanWithdraw(result.getInt("is_marked"));
        String description = result.getString("description");

        return new Submission(id, studentId, questId, isMarked, description);
    }


    public ArrayList<Submission> get() throws SQLException{

        ArrayList<Submission> submissionList = new ArrayList<>();
        ResultSet result = connect.getResult("SELECT * from submissions;");
        while (result.next()) {
            Submission submission = createSubmission(result);
            submissionList.add(submission);

        }

        return submissionList;
    }

    public ArrayList<Submission> getStudentSubmissions(Student student) throws SQLException {

        ArrayList<Submission> submissionList = new ArrayList<>();
        String query = String.format("SELECT * from submissions WHERE student_id = '%d';", student.getID());
        ResultSet result = connect.getResult(query);
        while (result.next()) {
            Submission submission = createSubmission(result);
            submissionList.add(submission);

        }

        return submissionList;
    }

    public Integer getStudentIdBySubmissionId(Integer submissionID) throws SQLException {

        String query = String.format("SELECT student_id from submissions WHERE id = '%d';", submissionID);
        ResultSet result = connect.getResult(query);
        Integer studentID = result.getInt("student_id");



        return studentID;


    }

    public Integer getQuestIdBySubmissionId(Integer submissionID) throws SQLException {

        String query = String.format("SELECT quest_id from submissions WHERE id = '%d';", submissionID);
        ResultSet result = connect.getResult(query);
        Integer questID = result.getInt("quest_id");



        return questID;
    }

    public ArrayList<Submission> getUnfinishedSubmission() throws SQLException {

        ArrayList<Submission> unfinishedSubmissionList = new ArrayList<>();
        String query = String.format("SELECT * from submissions WHERE is_marked = 0;");
        ResultSet result = connect.getResult(query);
        while (result.next()) {
            Submission submission = createSubmission(result);
            unfinishedSubmissionList.add(submission);

        }

        return unfinishedSubmissionList;
    }

    public void completeSubmission(Integer submissionID) throws SQLException {

        String query = String.format("UPDATE submissions SET is_marked = '1' WHERE id = '%d';", submissionID);
        connect.addRecord(query);
    }




    public void set(Submission submission) throws SQLException{
        String querry = String.format("UPDATE submissions " +
         "SET quest_id='%d',student_id = '%d',is_marked = '%d', description = '%s'" +
         "WHERE id = %d", submission.getQuestId(),submission.getStudentId(),dbBooleanInjection(submission.isMarked()),submission.getDescription(), submission.getId());
        connect.addRecord(querry);
    }

    public Integer getSubmissionValue(Integer submissionId) throws SQLException{
        String querry = String.format("SELECT value FROM quests INNER JOIN submissions" +
                " ON quests.id = submissions.quest_id WHERE submissions.id = %d", submissionId );
        ResultSet dbResult = connect.getResult(querry);

        return dbResult.getInt("value");
    }


}
