package testing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.PermissionException;
import task.Task;
import task.TaskItem;
import task.TaskManager;
import user.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {
    private TaskManager taskManager;
    private User user2;
    private Task task2;
    private User user;
    private Task task;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    public void setUp() throws Exception {
        taskManager = new TaskManager();
        user = new User("John Doe", "JD123", "password", "Manager", null);
        user2 = new User("Jane Doe", "JD124", "password", "Junior", null);
        Date targetDate = dateFormat.parse("2023-12-31");
        task = new Task(1, "Test Task", targetDate, user);
        task2 = new Task(2, "Test Task", targetDate, user2);
    }
      

    @Test // test for addTask
    public void addTask() {
        taskManager.addTask(task);
        assertEquals(1, taskManager.getSize());
        assertEquals(task, taskManager.getTask(0));
    }

    @Test //test for removeTask that task id is valid
    public void removeTask1() {
        taskManager.addTask(task);
        taskManager.removeTask(new Scanner("1\n"), user);
        assertEquals(0, taskManager.getSize());
    }

    @Test //test for removeTask that task id is not valid
    public void removeTask2() {
        taskManager.addTask(task);
        taskManager.removeTask(new Scanner("2\n"), user);
        assertEquals(1, taskManager.getSize());
    }
    
    @Test //test for removeTask that catch PermissionException
    public void removeTask3() {
        taskManager.addTask(task);
        taskManager.removeTask(new Scanner("1\n"), user2);
       
    }

    @Test
    public void getTasksUserIsStaff() {
        task.addAssignedStaff(user);
        taskManager.addTask(task);
        List<Task> userTasks = taskManager.getTasks(user);
        assertEquals(1, userTasks.size());
        assertEquals(task, userTasks.get(0));
    }
    
    @Test
    public void testListAllTask_TasksFound() throws Exception {
        taskManager.addTask(task);
        taskManager.addTask(task2);
        task.addAssignedStaff(user);
        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        taskManager.listAllTask(scanner, user);

        String expectedOutput = "1. 1 2023-12-31 Test Task created by John Doe [Progress: Empty]";
        assertTrue(outContent.toString().contains(expectedOutput));
    }

    @Test // no task found
    public void testListAllTask_NoTasksFound() {
    	taskManager.addTask(task);
        task.addAssignedStaff(user2);
        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        taskManager.listAllTask(scanner, user);

        String expectedOutput = "You have no task.";
        assertTrue(outContent.toString().contains(expectedOutput));
    }
    
    @Test
    public void testListAllTask_InvalidInput() throws Exception {
        taskManager.addTask(task);
        task.addAssignedStaff(user);
        String input = "invalid\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        taskManager.listAllTask(scanner, user);

        String expectedOutput1 = "1. 1 2023-12-31 Test Task created by John Doe [Progress: Empty]";
        String expectedOutput2 = "Invalid option.";
        assertTrue(outContent.toString().contains(expectedOutput1));
        assertTrue(outContent.toString().contains(expectedOutput2));
    }
    
    @Test //invalid input
    public void testListAllTask_invalidInput() {
        String input = "\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        taskManager.listAllTask(scanner, user);

        String expectedOutput = "Invalid option";
        assertTrue(outContent.toString().contains(expectedOutput));
    }

    @Test
    public void testListAllTask_NextPage() throws ParseException {
        // Add 11 tasks to ensure pagination
        for (int i = 1; i <= 11; i++) {
            Task newTask = new Task(i, "Task " + i, dateFormat.parse("2023-12-31"), user);
            newTask.addAssignedStaff(user);
            taskManager.addTask(newTask);
        }
        String input = ">\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        taskManager.listAllTask(scanner, user);

        String expectedOutput = "You are already on the last page.";
        assertTrue(outContent.toString().contains(expectedOutput));
    }

    @Test
    public void testListAllTask_PreviousPage() throws ParseException {
        // Add 11 tasks to ensure pagination
        for (int i = 1; i <= 11; i++) {
            Task newTask = new Task(i, "Task " + i, dateFormat.parse("2023-12-31"), user);
            newTask.addAssignedStaff(user);
            taskManager.addTask(newTask);
        }
        String input = ">\n<\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        taskManager.listAllTask(scanner, user);

        String expectedOutput = "You are already on the first page.";
        assertTrue(outContent.toString().contains(expectedOutput));
    }


    



   
    @Test
    public void testGetTasksUserIsNotStaff() {
        task.addAssignedStaff(user2);
        taskManager.addTask(task);

        List<Task> userTasks = taskManager.getTasks(user);
        assertTrue(userTasks.isEmpty());
    }
    

    @Test
    public void findTaskByNameSuccessfully() {
        taskManager.addTask(task);
        Task foundTask = taskManager.findTaskByName("Test Task");
        assertEquals(task, foundTask);
    }

    @Test
    public void findTaskByNameNotFound() {
    	taskManager.addTask(task);
        Task foundTask = taskManager.findTaskByName("NoThisTask");
        assertNull(foundTask);
    }

    @Test
    public void findTaskByIdSuccessfully() {
        taskManager.addTask(task);
        Task foundTask = taskManager.findTaskById(1);
        assertEquals(task, foundTask);
    }

    @Test
    public void findTaskByIdNotFound() {
    	taskManager.addTask(task);
        Task foundTask = taskManager.findTaskById(2);
        assertNull(foundTask);
    }
    
    @Test //test for listAllTasksByDate that user==staff and date does match
    public void testListAllTasksByDate1() throws Exception {
        taskManager.addTask(task);
        task.addAssignedStaff(user);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Date targetDate = dateFormat.parse("2023-12-31");
        taskManager.listAllTasksByDate(targetDate, user);

        String expectedOutput = "1 2023-12-31 Test Task created by John Doe [Progress: Empty]";
        assertEquals(expectedOutput.trim(), outContent.toString().trim());
    }

    @Test  //test for listAllTasksByDate that user==staff and date does not match
    public void testListAllTasksByDate2() throws Exception {
        taskManager.addTask(task);
        task.addAssignedStaff(user);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Date differentDate = dateFormat.parse("2024-01-01");
        taskManager.listAllTasksByDate(differentDate, user);

        String expectedOutput = "You have no task in 2024-01-01 .";
        assertEquals(expectedOutput.trim(), outContent.toString().trim());
    }

    @Test //test for listAllTasksByDate that user!=staff and date does match
    public void testListAllTasksByDate3() throws Exception {
        taskManager.addTask(task);
        task.addAssignedStaff(user2);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Date targetDate = dateFormat.parse("2023-12-31");
        taskManager.listAllTasksByDate(targetDate, user);

        String expectedOutput = "You have no task in 2023-12-31 .";
        assertEquals(expectedOutput.trim(), outContent.toString().trim());
    }

    @Test //test for listAllTasksByDate that user!=staff and date does not match
    public void testListAllTasksByDate4() throws Exception {
        taskManager.addTask(task);
        task.addAssignedStaff(user2);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Date differentDate = dateFormat.parse("2024-01-01");
        taskManager.listAllTasksByDate(differentDate, user);

        String expectedOutput = "You have no task in 2024-01-01 .";
        assertEquals(expectedOutput.trim(), outContent.toString().trim());
    }




    @Test
    public void isEmptyWhenNoTasks() {
        assertTrue(taskManager.isEmpty());
    }

    @Test
    public void isEmptyWhenTasksExist() {
        taskManager.addTask(task);
        assertFalse(taskManager.isEmpty());
    }
}
