
package testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import role.AdminRole;
import task.Task;
import task.TaskManager;
import user.User;
import database.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdminRoleTest {
    private AdminRole adminRole;
    private User adminUser;
    private User juniorUser;
    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        adminRole = new AdminRole();
        adminUser = new User("Admin", "A001", "password", "Admin", adminRole);
        juniorUser = new User("Target", "J001", "password", "Junior", new role.JuniorRole());
        taskManager = new TaskManager();
        database.getInstance().getAllUsers().clear();
        database.getInstance().getAllUsers().add(adminUser);
        database.getInstance().getAllUsers().add(juniorUser);
    }

    @Test
    public void testOperateAddTask() {
        Scanner scanner = new Scanner("1\nTest Task\n2023-12-31\n0\n0\n");
        adminRole.operate(adminUser, scanner);

        assertEquals(1, adminUser.getTaskManager().getSize());
    }

    @Test
    public void testOperateListAllTasks() {
        Task task = new Task(1, "Test Task", new Date(), adminUser);
        adminUser.getTaskManager().addTask(task);

        Scanner scanner = new Scanner("2\n0\n");
        adminRole.operate(adminUser, scanner);

        assertEquals(1, adminUser.getTaskManager().getSize());
    }

    @Test
    public void testOperateAssignTask() {
        Task task = new Task(1, "Test Task", new Date(), adminUser);
        adminUser.getTaskManager().addTask(task);

        Scanner scanner = new Scanner("6\nT001\n1\n0\n");
        adminRole.operate(adminUser, scanner);

        assertEquals(1, juniorUser.getTaskManager().getSize());
    }

    @Test
    public void testOperateCheckUserTasksProgress() {
        Task task = new Task(1, "Test Task", new Date(), adminUser);
        adminUser.getTaskManager().addTask(task);

        Scanner scanner = new Scanner("7\nT001\n0\n");
        adminRole.operate(adminUser, scanner);

        ArrayList<Task> tasksList = adminRole.checkUserTasksProgress(juniorUser);
        assertEquals(1, tasksList.size());
    }
}
