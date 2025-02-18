package database;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;

import role.AdminRole;
import role.JuniorRole;
import role.ManagerRole;
import role.SeniorRole;

import java.io.FileNotFoundException;
import user.*;

public class database {
	ArrayList<User> userDatabase = new ArrayList<User>();
    private volatile static database instance;
    private int taskID = 1000;
    
    // Private constructor to prevent instantiation from outside
    private database() {
        //For testing
    	String fileName = "src/database/UserList.txt";
    	readFile(fileName);
    	//
    }
   
    protected void readFile(String fileName) {
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                if (parts.length == 4) {
                    String staffName = parts[0];
                    String staffID = parts[1];
                    String password = parts[2];
                    String title = parts[3];
                    // Process the staffName, userName, and password as needed
                    User user = null; // Initialize to null
                    switch (title) {
                        case "Junior":
                            user = new User(staffName, staffID, password, title, new JuniorRole()); //added new JuniorRole()
                            break;
                        case "Senior":
                            user = new User(staffName, staffID, password, title, new SeniorRole()); //added new SeniorRole()
                            break;
                        case "Manager":
                        	user = new User(staffName, staffID, password, title, new ManagerRole()); //added new ManagerRole()
                            break;
                        case "Admin":
                        	user = new User(staffName, staffID, password, title, new AdminRole()); //added new AdminRole()
                            break;    
                        default:
//                            System.err.println("Invalid title: " + title);
                    }
                    userDatabase.add(user);
                } else {
//                    System.err.println("Invalid format in line: " + line);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
//            System.err.println("File not found: " + e.getMessage());
        }
    }
    
    public static database getInstance() {
        if (instance == null) {
        	 synchronized (database.class) {
        		 if (instance == null) {
                        instance = new database();
        		 }
        	 }
        }
        return instance;
    }
    
    public String displayAllUsers() {
        StringBuilder userInfo = new StringBuilder();
        for (User user : userDatabase) {
            userInfo.append(user.displayInfo()).append("\n");
        }
        return userInfo.toString();
    }
    
    // Other database-related methods can be added here
    public User query(String staffID) {
    	
    	for (User user: userDatabase) {
    		if (user.getID().equals(staffID)) {
    			return user;
    		}
    	}
    	
        return null;
    }
    
    public ArrayList<User> getAllUsers() {
    	return userDatabase;
    }
    
    public int gettaskID() {
    	return taskID;
    }
    
	public void addTaskID() {
		taskID++;
	}
	
	public User getUserdb() {
		return userDatabase.get(0);
    
	}
}
