
package testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.database;
import user.User;
import role.ManagerRole;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private User user;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private ByteArrayOutputStream outContent;

    @BeforeEach
    public void setUp() throws Exception {
        user = new User("John Doe", "JD123", "password", "Manager", new ManagerRole());
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test // test for addTask that input empty task name, then input valid task name
    public void addTask() {
        Scanner scanner = new Scanner("\nTest Task\n2024-12-31");
        user.addTask(scanner, user);

        assertEquals(1, user.getTaskManager().getSize());
    }
    
    @Test
    public void getTaskDueDate() {
        Scanner scanner = new Scanner("2024-12-31\n");
        Date date = user.getTaskDueDate(scanner);

        assertNotNull(date);
        assertEquals("2024-12-31", dateFormat.format(date));
    }

    
    //@Test
    public void readDateFromUser_ParseException() {
       
        Scanner scanner = new Scanner("Test Task\ninvalid-date");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            user.addTask(scanner, user);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close(); 
            System.setOut(originalOut); 
        }

        
        String expectedOutput = "Invalid date. Please try again.";
        assertTrue(outContent.toString().contains(expectedOutput));
        assertEquals(0, user.getTaskManager().getSize());
    }

    //@Test
    public void testReadDateFromUser_preDateException() {
        
        Scanner scanner = new Scanner("Test Task\n2023-12-31");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            user.addTask(scanner, user);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close(); 
            System.setOut(originalOut); 
        }

        
        String expectedOutput = "You cannot assign a task to a past date.";
        assertTrue(outContent.toString().contains(expectedOutput));
    }
    @Test
    public void testOperate() {
        Scanner scanner = new Scanner(System.in);
        assertDoesNotThrow(() -> user.operate(scanner));
    }

    @Test
    public void testDisplayInfo() {
        String expectedInfo = "StaffID: JD123 staffName: John Doe Manager";
        assertEquals(expectedInfo, user.displayInfo());
    }

    @Test
    public void testLogin_Success() {
        database db = database.getInstance();
        db.getAllUsers().add(user);

        User loggedInUser = User.Login("JD123", "password");
        assertNotNull(loggedInUser);
        assertEquals("JD123", loggedInUser.getID());
    }

    @Test
    public void testLogin_Failure() {
        User loggedInUser = User.Login("invalidID", "password");
        assertNull(loggedInUser);
    }

    @Test
    public void testLogin_WrongPassword() {
        database db = database.getInstance();
        db.getAllUsers().add(user);

        User loggedInUser = User.Login("JD123", "wrongpassword");
        assertNull(loggedInUser);
    }
}
    
