package hms.ui;

import hms.model.Doctor;
import hms.service.DoctorService;
import hms.util.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class DoctorManagementPanel extends JPanel {
    private DoctorService doctorService;
    private JTable doctorTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewButton;
    private JButton refreshButton;

    public DoctorManagementPanel() {
        doctorService = new DoctorService();
        initializeUI();
        loadDoctors();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create top panel with search and buttons
        JPanel topPanel = new JPanel(new BorderLayout());

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Поиск:");
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Поиск");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButton = new JButton("Добавить доктора");
        editButton = new JButton("Редактировать");
        deleteButton = new JButton("Удалить");
        viewButton = new JButton("Посмотреть детали");
        refreshButton = new JButton("Обновить");

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(viewButton);
        buttonsPanel.add(refreshButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonsPanel, BorderLayout.EAST);

        // Create table
        String[] columns = {"Идентификатор", "Фио", "Специализация", "Доступность", "Номер телефона"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        doctorTable = new JTable(tableModel);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        doctorTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(doctorTable);

        // Add components to panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        searchButton.addActionListener(e -> searchDoctors());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchDoctors();
                }
            }
        });

        addButton.addActionListener(e -> showAddDoctorDialog());
        editButton.addActionListener(e -> showEditDoctorDialog());
        deleteButton.addActionListener(e -> deleteDoctor());
        viewButton.addActionListener(e -> viewDoctorDetails());
        refreshButton.addActionListener(e -> loadDoctors());
    }

    private void loadDoctors() {
        // Clear table
        tableModel.setRowCount(0);

        // Load doctors from service
        List<Doctor> doctors = doctorService.getAll();

        // Add doctors to table
        for (Doctor doctor : doctors) {
            Object[] row = {
                    doctor.getId(),
                    doctor.getName(),
                    doctor.getSpecialization(),
                    doctor.getAvailability(),
                    doctor.getContact()
            };
            tableModel.addRow(row);
        }
    }

    private void searchDoctors() {
        String query = searchField.getText().trim();

        // Clear table
        tableModel.setRowCount(0);

        // Search doctors
        List<Doctor> doctors = doctorService.search(query);

        // Add doctors to table
        for (Doctor doctor : doctors) {
            Object[] row = {
                    doctor.getId(),
                    doctor.getName(),
                    doctor.getSpecialization(),
                    doctor.getAvailability(),
                    doctor.getContact()
            };
            tableModel.addRow(row);
        }
    }

    private void showAddDoctorDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Добавить доктора", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Generate doctor ID
        String doctorId = doctorService.generateDoctorId();

        JLabel idLabel = new JLabel("Идентификатор доктора:");
        JTextField idField = new JTextField(doctorId);
        idField.setEditable(false);

        JLabel nameLabel = new JLabel("Фио:");
        JTextField nameField = new JTextField();

        JLabel ageLabel = new JLabel("Возраст:");
        JTextField ageField = new JTextField();

        JLabel contactLabel = new JLabel("Номер телефона:");
        JTextField contactField = new JTextField();

        JLabel emailLabel = new JLabel("Электронная почта:");
        JTextField emailField = new JTextField();

        JLabel addressLabel = new JLabel("Адрес:");
        JTextField addressField = new JTextField();

        JLabel genderLabel = new JLabel("Пол:");
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Мужчина", "Женщина", "Другое"});

        JLabel specializationLabel = new JLabel("Специализация:");
        JTextField specializationField = new JTextField();

        JLabel qualificationLabel = new JLabel("Квалификация:");
        JTextField qualificationField = new JTextField();

        JLabel availabilityLabel = new JLabel("Доступность:");
        JTextField availabilityField = new JTextField();

        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(ageLabel);
        formPanel.add(ageField);
        formPanel.add(contactLabel);
        formPanel.add(contactField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(addressLabel);
        formPanel.add(addressField);
        formPanel.add(genderLabel);
        formPanel.add(genderComboBox);
        formPanel.add(specializationLabel);
        formPanel.add(specializationField);
        formPanel.add(qualificationLabel);
        formPanel.add(qualificationField);
        formPanel.add(availabilityLabel);
        formPanel.add(availabilityField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отменить");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        saveButton.addActionListener(e -> {
            try {
                // Validate input
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Фио не может быть пустым", "Ошибка проверки", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int age;
                try {
                    age = Integer.parseInt(ageField.getText().trim());
                    if (age <= 0 || age > 120) {
                        JOptionPane.showMessageDialog(dialog, "Возраст должен быть от 1 до 120", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Возраст должен быть числом", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String contact = contactField.getText().trim();
                if (contact.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Номер телефона не может быть пустым", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String email = emailField.getText().trim();
                if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
                    JOptionPane.showMessageDialog(dialog, "Неверный формат электронной почты", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String specialization = specializationField.getText().trim();
                if (specialization.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Специализация не может быть пустой", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String availability = availabilityField.getText().trim();
                if (availability.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Доступность не может быть пустой", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create doctor object
                Doctor doctor = new Doctor(
                        idField.getText(),
                        name,
                        age,
                        contact,
                        email,
                        addressField.getText().trim(),
                        genderComboBox.getSelectedItem().toString(),
                        specialization,
                        qualificationField.getText().trim(),
                        availability,
                        0.0 // Default consultation fee
                );

                // Save doctor
                if (doctorService.add(doctor)) {
                    JOptionPane.showMessageDialog(dialog, "Доктор успешно добавлен", "Успешно", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadDoctors();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Не удалось добавить врача", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showEditDoctorDialog() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите врача для редактирования", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String doctorId = (String) doctorTable.getValueAt(selectedRow, 0);
        Doctor doctor = doctorService.getById(doctorId);

        if (doctor == null) {
            JOptionPane.showMessageDialog(this, "Доктор не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Редактировать доктора", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel idLabel = new JLabel("Идентификатор доктора:");
        JTextField idField = new JTextField(doctor.getId());
        idField.setEditable(false);

        JLabel nameLabel = new JLabel("Фио:");
        JTextField nameField = new JTextField(doctor.getName());

        JLabel ageLabel = new JLabel("Возраст:");
        JTextField ageField = new JTextField(String.valueOf(doctor.getAge()));

        JLabel contactLabel = new JLabel("Номер телефона:");
        JTextField contactField = new JTextField(doctor.getContact());

        JLabel emailLabel = new JLabel("Электронная почта:");
        JTextField emailField = new JTextField(doctor.getEmail());

        JLabel addressLabel = new JLabel("Адрес:");
        JTextField addressField = new JTextField(doctor.getAddress());

        JLabel genderLabel = new JLabel("Пол:");
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Мужчина", "Женщина", "Другое"});
        genderComboBox.setSelectedItem(doctor.getGender());

        JLabel specializationLabel = new JLabel("Специализация:");
        JTextField specializationField = new JTextField(doctor.getSpecialization());

        JLabel qualificationLabel = new JLabel("Квалификация:");
        JTextField qualificationField = new JTextField(doctor.getQualification());

        JLabel availabilityLabel = new JLabel("Доступность:");
        JTextField availabilityField = new JTextField(doctor.getAvailability());

        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(ageLabel);
        formPanel.add(ageField);
        formPanel.add(contactLabel);
        formPanel.add(contactField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(addressLabel);
        formPanel.add(addressField);
        formPanel.add(genderLabel);
        formPanel.add(genderComboBox);
        formPanel.add(specializationLabel);
        formPanel.add(specializationField);
        formPanel.add(qualificationLabel);
        formPanel.add(qualificationField);
        formPanel.add(availabilityLabel);
        formPanel.add(availabilityField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отменить");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        saveButton.addActionListener(e -> {
            try {
                // Validate input
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Фио не может быть пустым", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int age;
                try {
                    age = Integer.parseInt(ageField.getText().trim());
                    if (age <= 0 || age > 120) {
                        JOptionPane.showMessageDialog(dialog, "Возраст должен быть от 1 до 120", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Возраст должен быть числом", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String contact = contactField.getText().trim();
                if (contact.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Номер телефона не может быть пустым", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String email = emailField.getText().trim();
                if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
                    JOptionPane.showMessageDialog(dialog, "Неверный формат электронной почты", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String specialization = specializationField.getText().trim();
                if (specialization.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Специализация не может быть пустой", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String availability = availabilityField.getText().trim();
                if (availability.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Доступность не может быть пустой", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update doctor object
                doctor.setName(name);
                doctor.setAge(age);
                doctor.setContact(contact);
                doctor.setEmail(email);
                doctor.setAddress(addressField.getText().trim());
                doctor.setGender(genderComboBox.getSelectedItem().toString());
                doctor.setSpecialization(specialization);
                doctor.setQualification(qualificationField.getText().trim());
                doctor.setAvailability(availability);

                // Save doctor
                if (doctorService.update(doctor)) {
                    JOptionPane.showMessageDialog(dialog, "Доктор обновлен успешно", "Успешно", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadDoctors();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Не удалось обновить доктора", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void deleteDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите врача для удаления", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String doctorId = (String) doctorTable.getValueAt(selectedRow, 0);
        String doctorName = (String) doctorTable.getValueAt(selectedRow, 1);

        int option = JOptionPane.showConfirmDialog(
                this,
                "Вы уверены, что хотите удалить доктора " + doctorName + "?",
                "Подтвердить удаление",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            if (doctorService.delete(doctorId)) {
                JOptionPane.showMessageDialog(this, "Доктор успешно удален", "Успешно", JOptionPane.INFORMATION_MESSAGE);
                loadDoctors();
            } else {
                JOptionPane.showMessageDialog(this, "Не удалось удалить доктора", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewDoctorDetails() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, выберите врача для просмотра", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String doctorId = (String) doctorTable.getValueAt(selectedRow, 0);
        Doctor doctor = doctorService.getById(doctorId);

        if (doctor == null) {
            JOptionPane.showMessageDialog(this, "Доктор не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Подробности о докторе", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Doctor details
        JLabel titleLabel = new JLabel("Информация о докторе");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addDetailRow(infoPanel, "Идентификатор доктора:", doctor.getId());
        addDetailRow(infoPanel, "Фио:", doctor.getName());
        addDetailRow(infoPanel, "Возраст:", String.valueOf(doctor.getAge()));
        addDetailRow(infoPanel, "Пол:", doctor.getGender());
        addDetailRow(infoPanel, "Номер телефона:", doctor.getContact());
        addDetailRow(infoPanel, "Электронная почта:", doctor.getEmail());
        addDetailRow(infoPanel, "Адрес:", doctor.getAddress());
        addDetailRow(infoPanel, "Специализация:", doctor.getSpecialization());
        addDetailRow(infoPanel, "Квалификация:", doctor.getQualification());
        addDetailRow(infoPanel, "Доступность:", doctor.getAvailability());
        addDetailRow(infoPanel, "Плата за консультацию:", String.valueOf(doctor.getConsultationFee()));

        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        detailsPanel.add(infoPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Закрыть");

        buttonPanel.add(closeButton);

        dialog.add(detailsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        closeButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel valueComponent = new JLabel(value != null && !value.isEmpty() ? value : "N/A");
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(labelComponent);
        panel.add(valueComponent);
    }
}
