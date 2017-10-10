package UI;

import java.util.Arrays;
import java.util.List;

public class MentorUI extends UI {

    public static String mainMenuLabel = "MENTOR - Main Menu";

    public static String studentMenuLabel = "MENTOR - Student Menu";

    public static String artifactMenuLabel = "MENTOR - Artifact Menu";

    public static String questMenuLabel = "MENTOR - Quest Menu";

    public static List<String> menuMainOptions = Arrays.asList(
        "Student Panel",
        "Artifact Panel",
        "Quest Panel",
        "See student wallet",
        "EXIT");

    public static List<String> menuStudentOptions = Arrays.asList(
        "Create Student",
        "Edit Student",
        "Delete Student",
        "List All Students",
        "EXIT");

    public static List<String> menuArtifactOptions = Arrays.asList(
        "Create Artifact",
        "Edit Artifact",
        "Delete Artifact",
        "List All Artifacts",
        "EXIT");

    public static List<String> menuQuestOptions = Arrays.asList(
        "Create Quest",
        "Edit Quest",
        "Mark Quest",
        "EXIT");



}
