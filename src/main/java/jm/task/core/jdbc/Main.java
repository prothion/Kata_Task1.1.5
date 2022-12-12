package jm.task.core.jdbc;

import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;
import jm.task.core.jdbc.util.Util;

public class Main {
    public static void main(String[] args) {
        // реализуйте алгоритм здесь
        UserService userService = new UserServiceImpl();
        userService.createUsersTable();
        userService.saveUser("Вадим", "Дрз", (byte) 78);
        userService.saveUser("Райора", "Чхх", (byte) 74);
        userService.saveUser("Дмитрий", "Ктр", (byte) 59);
        userService.saveUser("Владимир", "Прп", (byte) 74);
        userService.removeUserById(3);
        userService.getAllUsers();
        userService.cleanUsersTable();
        userService.dropUsersTable();
        Util.closeConnection();
    }
}
