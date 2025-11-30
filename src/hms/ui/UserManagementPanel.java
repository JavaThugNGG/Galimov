package hms.ui;

import hms.model.User;
import hms.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private UserService userService;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton resetPasswordButton;
    private JButton refreshButton;

    public UserManagementPanel() {
        userService = new UserService();
        initializeUI();
        loadUsers();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Поиск:");
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Поиск");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButton = new JButton("Добавить пользователя");
        editButton = new JButton("Редактировать");
        deleteButton = new JButton("Удалить");
        resetPasswordButton = new JButton("Сбросить пароль");
        refreshButton = new JButton("Обновить");

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(resetPasswordButton);
        buttonsPanel.add(refreshButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonsPanel, BorderLayout.EAST);

        String[] columns = {"Имя пользователя", "Полное имя", "Роль", "Статус"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(userTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> searchUsers());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchUsers();
                }
            }
        });

        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditUserDialog());
        deleteButton.addActionListener(e -> deleteUser());
        resetPasswordButton.addActionListener(e -> resetPassword());
        refreshButton.addActionListener(e -> loadUsers());
    }

    private void loadUsers() {
        tableModel.setRowCount(0);

        List<User> users = userService.getAll();

        for (User user : users) {
            Object[] row = {
                    user.getUsername(),
                    user.getFullName(),
                    user.getRole(),
                    user.isActive() ? "Active" : "Inactive"
            };
            tableModel.addRow(row);
        }
    }

    private void searchUsers() {
        String query = searchField.getText().trim();
        tableModel.setRowCount(0);
        List<User> users = userService.search(query);

        for (User user : users) {
            Object[] row = {
                    user.getUsername(),
                    user.getFullName(),
                    user.getRole(),
                    user.isActive() ? "Active" : "Inactive"
            };
            tableModel.addRow(row);
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Добавить пользователя", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel usernameLabel = new JLabel("Имя пользователя:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Пароль:");
        JPasswordField passwordField = new JPasswordField();

        JLabel confirmPasswordLabel = new JLabel("Подтвердите пароль:");
        JPasswordField confirmPasswordField = new JPasswordField();

        JLabel fullNameLabel = new JLabel("Полное имя:");
        JTextField fullNameField = new JTextField();

        JLabel roleLabel = new JLabel("Роль:");
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{
                "ADMIN", "DOCTOR", "NURSE", "RECEPTIONIST", "PHARMACIST"
        });

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(confirmPasswordLabel);
        formPanel.add(confirmPasswordField);
        formPanel.add(fullNameLabel);
        formPanel.add(fullNameField);
        formPanel.add(roleLabel);
        formPanel.add(roleComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отменить");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Имя пользователя не может быть пустым", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Пароль не может быть пустым", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dialog, "Пароли не совпадают", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(dialog, "Пароль должен быть не менее 6 символов", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String fullName = fullNameField.getText().trim();
                if (fullName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Полное имя не может быть пустым", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (userService.getById(username) != null) {
                    JOptionPane.showMessageDialog(dialog, "Username already exists", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                User user = new User(
                        username,
                        password,
                        fullName,
                        (String) roleComboBox.getSelectedItem()
                );

                if (userService.add(user)) {
                    JOptionPane.showMessageDialog(dialog, "Пользователь успешно добавлен", "Успешно", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Не удалось добавить пользователя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите пользователя для редактирования", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) userTable.getValueAt(selectedRow, 0);
        User user = userService.getById(username);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Пользователь не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Редактировать пользователя", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel usernameLabel = new JLabel("Имя пользователя:");
        JTextField usernameField = new JTextField(user.getUsername());
        usernameField.setEditable(false);

        JLabel fullNameLabel = new JLabel("Полное имя:");
        JTextField fullNameField = new JTextField(user.getFullName());

        JLabel roleLabel = new JLabel("Роль:");
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{
                "ADMIN", "DOCTOR", "NURSE", "RECEPTIONIST", "PHARMACIST"
        });
        roleComboBox.setSelectedItem(user.getRole());

        JLabel statusLabel = new JLabel("Статус:");
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{
                "Active", "Inactive"
        });
        statusComboBox.setSelectedItem(user.isActive() ? "Active" : "Inactive");

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(fullNameLabel);
        formPanel.add(fullNameField);
        formPanel.add(roleLabel);
        formPanel.add(roleComboBox);
        formPanel.add(statusLabel);
        formPanel.add(statusComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отменить");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            try {
                String fullName = fullNameField.getText().trim();
                if (fullName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Полное имя не может быть пустым", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                user.setFullName(fullName);
                user.setRole((String) roleComboBox.getSelectedItem());
                user.setActive(statusComboBox.getSelectedItem().equals("Active"));

                if (userService.update(user)) {
                    JOptionPane.showMessageDialog(dialog, "Пользователь успешно обновлен", "Успешно", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Не удалось обновить пользователя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите пользователя для удаления.", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) userTable.getValueAt(selectedRow, 0);

        if (username.equals("admin")) {
            JOptionPane.showMessageDialog(this, "Невозможно удалить пользователя-администратора", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "Вы уверены, что хотите удалить пользователя " + username + "?",
                "Подтвердить удаление",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            if (userService.delete(username)) {
                JOptionPane.showMessageDialog(this, "Пользователь успешно удален", "Успешно", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Не удалось удалить пользователя", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetPassword() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, выберите пользователя для сброса пароля", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) userTable.getValueAt(selectedRow, 0);
        User user = userService.getById(username);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Пользователь не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Сбросить пароль", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel usernameLabel = new JLabel("Имя пользователя:");
        JTextField usernameField = new JTextField(user.getUsername());
        usernameField.setEditable(false);

        JLabel passwordLabel = new JLabel("Новый пароль:");
        JPasswordField passwordField = new JPasswordField();

        JLabel confirmPasswordLabel = new JLabel("Подтвердите пароль:");
        JPasswordField confirmPasswordField = new JPasswordField();

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(confirmPasswordLabel);
        formPanel.add(confirmPasswordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Сбросить пароль");
        JButton cancelButton = new JButton("Отменить");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            try {
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Пароль не может быть пустым", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dialog, "Пароли не совпадают", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(dialog, "Пароль должен быть не менее 6 символов", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                user.setPassword(password);

                if (userService.update(user)) {
                    JOptionPane.showMessageDialog(dialog, "Пароль успешно сброшен", "Успешно", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Не удалось сбросить пароль", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
}
