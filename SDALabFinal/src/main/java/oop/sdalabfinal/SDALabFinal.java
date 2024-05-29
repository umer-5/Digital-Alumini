/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package oop.sdalabfinal;

import java.io.Serializable;
import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Abstract class Person
abstract class Person implements Serializable {
    protected String username;
    protected String email;

    public Person(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public abstract void displayInfo();
}

// Interface Authentication
interface Authentication {
    boolean authenticate(String username, String password);
}

// User class inheriting from Person and implementing Authentication
class User extends Person implements Authentication {
    private String password;

    public User(String username, String password, String email) {
        super(username, email);
        this.password = password;
    }

    @Override
    public void displayInfo() {
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
    }

    @Override
    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

// Job class
class Job implements Serializable {
    private String title;
    private String category;
    private int experience;
    private double salary;

    public Job(String title, String category, int experience, double salary) {
        this.title = title;
        this.category = category;
        this.experience = experience;
        this.salary = salary;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public int getExperience() {
        return experience;
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return "Job{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", experience=" + experience +
                ", salary=" + salary +
                '}';
    }
}

// FileHandler class
class FileHandler {
    private static final String USER_FILE = "users.dat";
    private static final String JOB_FILE = "jobs.dat";

    public static void saveUsers(List<User> users) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(users);
        }
    }

    public static List<User> loadUsers() throws IOException, ClassNotFoundException {
        File file = new File(USER_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE))) {
            return (List<User>) ois.readObject();
        }
    }

    public static void saveJobs(List<Job> jobs) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(JOB_FILE))) {
            oos.writeObject(jobs);
        }
    }

    public static List<Job> loadJobs() throws IOException, ClassNotFoundException {
        File file = new File(JOB_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(JOB_FILE))) {
            return (List<Job>) ois.readObject();
        }
    }
}

// Interface JobSearch
interface JobSearch {
    void searchJobs();
}

// JobBoard class implementing JobSearch
class JobBoard implements JobSearch {
    private List<Job> jobs;

    public JobBoard(List<Job> jobs) {
        this.jobs = jobs;
    }

    @Override
    public void searchJobs() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter required experience (years): ");
        int experience = scanner.nextInt();
        System.out.println("Enter minimum salary: ");
        double salary = scanner.nextDouble();
        scanner.nextLine();  // consume newline
        System.out.println("Enter job category: ");
        String category = scanner.nextLine();

        boolean found = false;
        for (Job job : jobs) {
            if (job.getExperience() <= experience && job.getSalary() >= salary && job.getCategory().equalsIgnoreCase(category)) {
                System.out.println(job);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No jobs match your criteria.");
        }
    }
}

// Main class SDALabFinal
public class SDALabFinal {
    private static List<User> users;
    private static List<Job> jobs;
    private static User loggedInUser = null;

    public static void main(String[] args) {
        try {
            users = FileHandler.loadUsers();
            jobs = FileHandler.loadJobs();
        } catch (IOException | ClassNotFoundException e) {
            users = new ArrayList<>();
            jobs = new ArrayList<>();
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Sign Up");
            System.out.println("2. Sign In");
            System.out.println("3. Forget Password");
            System.out.println("4. Log Out");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // consume newline

            switch (choice) {
                case 1:
                    signUp(scanner);
                    break;
                case 2:
                    signIn(scanner);
                    break;
                case 3:
                    forgetPassword(scanner);
                    break;
                case 4:
                    logOut();
                    break;
                case 5:
                    saveAndExit();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            if (loggedInUser != null) {
                showJobBoardMenu(scanner);
            }
        }
    }

    private static void signUp(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        users.add(new User(username, password, email));
        System.out.println("User signed up successfully!");
    }

    private static void signIn(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        for (User user : users) {
            if (user.authenticate(username, password)) {
                loggedInUser = user;
                System.out.println("User logged in successfully!");
                return;
            }
        }
        System.out.println("Invalid username or password.");
    }

    private static void forgetPassword(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getEmail().equals(email)) {
                System.out.print("Enter new password: ");
                String newPassword = scanner.nextLine();
                user.setPassword(newPassword);
                System.out.println("Password updated successfully!");
                return;
            }
        }
        System.out.println("Invalid username or email.");
    }

    private static void logOut() {
        if (loggedInUser != null) {
            loggedInUser = null;
            System.out.println("User logged out successfully!");
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

    private static void showJobBoardMenu(Scanner scanner) {
        while (true) {
            System.out.println("1. Search Jobs");
            System.out.println("2. Log Out");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // consume newline

            if (choice == 1) {
                JobSearch jobBoard = new JobBoard(jobs);
                jobBoard.searchJobs();
            } else if (choice == 2) {
                logOut();
                return;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void saveAndExit() {
        try {
            FileHandler.saveUsers(users);
            FileHandler.saveJobs(jobs);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
        System.out.println("Data saved. Exiting...");
    }
}
