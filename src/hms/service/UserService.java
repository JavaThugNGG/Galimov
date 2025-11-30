package hms.service;

import hms.dao.UserDAO;
import hms.interfaces.ManagementService;
import hms.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserService implements ManagementService<User, String> {

    private UserDAO userDAO;

    public UserService() {
        userDAO = new UserDAO();

        if (userDAO.count() == 0) {
            User adminUser = new User("admin", "admin", "Administrator", "ADMIN");
            userDAO.save(adminUser);
        }
    }

    public User authenticate(String username, String password) {
        return userDAO.authenticate(username, password);
    }

    @Override
    public boolean add(User user) {
        if (userDAO.exists(user.getUsername())) {
            return false;
        }

        return userDAO.save(user);
    }

    @Override
    public User getById(String username) {
        return userDAO.findById(username);
    }

    @Override
    public List<User> getAll() {
        return userDAO.findAll();
    }

    @Override
    public boolean update(User user) {
        User existingUser = userDAO.findById(user.getUsername());
        if (existingUser == null) {
            return false;
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        } else {
            //
        }

        return userDAO.update(user);
    }

    @Override
    public boolean delete(String username) {
        return userDAO.delete(username);
    }

    @Override
    public List<User> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return userDAO.findAll();
        }

        List<User> allUsers = userDAO.findAll();
        List<User> filteredUsers = new ArrayList<>();

        for (User user : allUsers) {
            if (user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                    user.getFullName().toLowerCase().contains(query.toLowerCase()) ||
                    user.getRole().toLowerCase().contains(query.toLowerCase())) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }

    public List<User> findByRole(String role) {
        return userDAO.findByRole(role);
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = userDAO.authenticate(username, oldPassword);
        if (user == null) {
            return false;
        }

        user.setPassword(newPassword);

        return userDAO.update(user);
    }

    public boolean setUserStatus(String username, boolean active) {
        User user = userDAO.findById(username);
        if (user == null) {
            return false;
        }

        user.setActive(active);
        return userDAO.update(user);
    }
}
