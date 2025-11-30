package hms.ui;

import hms.model.Patient;
import hms.service.PatientService;
import hms.util.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PatientManagementPanel extends JPanel {
    private PatientService patientService;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewButton;
    private JButton refreshButton;

    public PatientManagementPanel() {
        patientService = new PatientService();
        initializeUI();
        loadPatients();
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
        addButton = new JButton("Добавить пациента");
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
        String[] columns = {"Идентификатор", "Фио", "Возраст", "Номер телефона", "Диагноз"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(patientTable);

        // Add components to panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        searchButton.addActionListener(e -> searchPatients());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchPatients();
                }
            }
        });

        addButton.addActionListener(e -> showAddPatientDialog());
        editButton.addActionListener(e -> showEditPatientDialog());
        deleteButton.addActionListener(e -> deletePatient());
        viewButton.addActionListener(e -> viewPatientDetails());
        refreshButton.addActionListener(e -> loadPatients());
    }

    private void loadPatients() {
        // Clear table
        tableModel.setRowCount(0);

        // Load patients from service
        List<Patient> patients = patientService.getAll();

        // Add patients to table
        for (Patient patient : patients) {
            Object[] row = {
                    patient.getId(),
                    patient.getName(),
                    patient.getAge(),
                    patient.getContact(),
                    patient.getDisease()
            };
            tableModel.addRow(row);
        }
    }

    private void searchPatients() {
        String query = searchField.getText().trim();

        // Clear table
        tableModel.setRowCount(0);

        // Search patients
        List<Patient> patients = patientService.search(query);

        // Add patients to table
        for (Patient patient : patients) {
            Object[] row = {
                    patient.getId(),
                    patient.getName(),
                    patient.getAge(),
                    patient.getContact(),
                    patient.getDisease()
            };
            tableModel.addRow(row);
        }
    }

    private void showAddPatientDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Добавить пациента", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Generate patient ID
        String patientId = patientService.generatePatientId();

        JLabel idLabel = new JLabel("Идентификатор пациента:");
        JTextField idField = new JTextField(patientId);
        idField.setEditable(false);

        JLabel nameLabel = new JLabel("Имя:");
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

        JLabel diseaseLabel = new JLabel("Диагноз:");
        JTextField diseaseField = new JTextField();

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
        formPanel.add(diseaseLabel);
        formPanel.add(diseaseField);

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
                    JOptionPane.showMessageDialog(dialog, "Имя не может быть пустым", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(dialog, "Контакт не может быть пустым", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String email = emailField.getText().trim();
                if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
                    JOptionPane.showMessageDialog(dialog, "Неверный формат электронной почты", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String disease = diseaseField.getText().trim();
                if (disease.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Болезнь/Симптомы не могут быть пустыми", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create patient object
                Patient patient = new Patient(
                        idField.getText(),
                        name,
                        age,
                        contact,
                        email,
                        addressField.getText().trim(),
                        genderComboBox.getSelectedItem().toString(),
                        "", // Blood group
                        "", // Allergies
                        disease
                );

                // Save patient
                if (patientService.add(patient)) {
                    JOptionPane.showMessageDialog(dialog, "Пациент успешно добавлен", "Успешно", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadPatients();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add patient", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showEditPatientDialog() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите пациента для редактирования", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String patientId = (String) patientTable.getValueAt(selectedRow, 0);
        Patient patient = patientService.getById(patientId);

        if (patient == null) {
            JOptionPane.showMessageDialog(this, "Пациент не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Редактировать пациента", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel idLabel = new JLabel("Идентификатор пациента:");
        JTextField idField = new JTextField(patient.getId());
        idField.setEditable(false);

        JLabel nameLabel = new JLabel("Имя:");
        JTextField nameField = new JTextField(patient.getName());

        JLabel ageLabel = new JLabel("Возраст:");
        JTextField ageField = new JTextField(String.valueOf(patient.getAge()));

        JLabel contactLabel = new JLabel("Номер телефона:");
        JTextField contactField = new JTextField(patient.getContact());

        JLabel emailLabel = new JLabel("Электронная почта:");
        JTextField emailField = new JTextField(patient.getEmail());

        JLabel addressLabel = new JLabel("Адрес:");
        JTextField addressField = new JTextField(patient.getAddress());

        JLabel genderLabel = new JLabel("Пол:");
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Мужчина", "Женщина", "Другое"});
        genderComboBox.setSelectedItem(patient.getGender());

        JLabel diseaseLabel = new JLabel("Диагноз:");
        JTextField diseaseField = new JTextField(patient.getDisease());

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
        formPanel.add(diseaseLabel);
        formPanel.add(diseaseField);

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

                String disease = diseaseField.getText().trim();
                if (disease.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Диагноз не может быть пустым", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update patient object
                patient.setName(name);
                patient.setAge(age);
                patient.setContact(contact);
                patient.setEmail(email);
                patient.setAddress(addressField.getText().trim());
                patient.setGender(genderComboBox.getSelectedItem().toString());
                patient.setDisease(disease);

                // Save patient
                if (patientService.update(patient)) {
                    JOptionPane.showMessageDialog(dialog, "Пациент обновлен успешно", "Успешно", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadPatients();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Не удалось обновить пациента", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void deletePatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите пациента для удаления", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String patientId = (String) patientTable.getValueAt(selectedRow, 0);
        String patientName = (String) patientTable.getValueAt(selectedRow, 1);

        int option = JOptionPane.showConfirmDialog(
                this,
                "Вы уверены, что хотите удалить пациента " + patientName + "?",
                "Подтвердить удаление",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            if (patientService.delete(patientId)) {
                JOptionPane.showMessageDialog(this, "Пациент успешно удален", "Успешно", JOptionPane.INFORMATION_MESSAGE);
                loadPatients();
            } else {
                JOptionPane.showMessageDialog(this, "Не удалось удалить пациента", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewPatientDetails() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите пациента для просмотра", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String patientId = (String) patientTable.getValueAt(selectedRow, 0);
        Patient patient = patientService.getById(patientId);

        if (patient == null) {
            JOptionPane.showMessageDialog(this, "Пациент не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Сведения о пациенте", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Patient details
        JLabel titleLabel = new JLabel("Информация для пациентов");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addDetailRow(infoPanel, "Идентификатор пациента:", patient.getId());
        addDetailRow(infoPanel, "Фио:", patient.getName());
        addDetailRow(infoPanel, "Возраст:", String.valueOf(patient.getAge()));
        addDetailRow(infoPanel, "Пол:", patient.getGender());
        addDetailRow(infoPanel, "Номер телефона:", patient.getContact());
        addDetailRow(infoPanel, "Электронная почта:", patient.getEmail());
        addDetailRow(infoPanel, "Адрес:", patient.getAddress());
        addDetailRow(infoPanel, "Группа крови:", patient.getBloodGroup());
        addDetailRow(infoPanel, "Аллергия:", patient.getAllergies());
        addDetailRow(infoPanel, "Диагноз:", patient.getDisease());

        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        detailsPanel.add(infoPanel);

        // Add tabs for medical records, appointments, and billing
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Медицинские записи", createMedicalRecordsPanel(patient));
        tabbedPane.addTab("Назначения", createAppointmentsPanel(patient));
        tabbedPane.addTab("Выставление счета", createBillingPanel(patient));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Закрыть");
        JButton printButton = new JButton("Распечатать отчет");

        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);

        dialog.add(detailsPanel, BorderLayout.NORTH);
        dialog.add(tabbedPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        closeButton.addActionListener(e -> dialog.dispose());
        printButton.addActionListener(e -> generatePatientReport(patient));

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

    private JPanel createMedicalRecordsPanel(Patient patient) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create table for medical records
        String[] columns = {"Идентификатор записи", "Дата", "Доктор", "Диагноз", "Лечение"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Добавить запись");
        JButton viewButton = new JButton("Посмотреть запись");

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> {
            // Show dialog to add medical record
            JOptionPane.showMessageDialog(panel, "Функциональность добавления медицинской карты будет реализована здесь", "Информация", JOptionPane.INFORMATION_MESSAGE);
        });

        viewButton.addActionListener(e -> {
            // Show dialog to view medical record details
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Выберите запись для просмотра", "Нет выбора", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(panel, "Функциональность просмотра медицинской карты будет реализована здесь", "Информация", JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }

    private JPanel createAppointmentsPanel(Patient patient) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create table for appointments
        String[] columns = {"Идентификатор записи", "Дата", "Время", "Доктор", "Статус"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Записаться на прием");
        JButton viewButton = new JButton("Посмотреть детали");

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> {
            // Show dialog to book appointment
            JOptionPane.showMessageDialog(panel, "Функциональность записи на прием будет реализована здесь", "Информация", JOptionPane.INFORMATION_MESSAGE);
        });

        viewButton.addActionListener(e -> {
            // Show dialog to view appointment details
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Выберите встречу для просмотра", "Нет выбора", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(panel, "Функциональность просмотра записи на прием будет реализована здесь", "Информация", JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }

    private JPanel createBillingPanel(Patient patient) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create table for billing
        String[] columns = {"Идентификатор счета", "Дата", "Сумма", "Статус"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Создать счет");
        JButton viewButton = new JButton("Посмотреть счет");

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> {
            // Show dialog to generate bill
            JOptionPane.showMessageDialog(panel, "Функциональность генерации счетов будет реализована здесь", "Информация", JOptionPane.INFORMATION_MESSAGE);
        });

        viewButton.addActionListener(e -> {
            // Show dialog to view bill details
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Выберите счет для просмотра", "Нет выбора", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(panel, "Функциональность просмотра счета будет реализована здесь", "Информация", JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }

    private void generatePatientReport(Patient patient) {
        try {
            String outputPath = "reports/patient_" + patient.getId() + ".pdf";
            java.io.File file = hms.util.PDFGenerator.generatePatientReport(patient, null, outputPath);

            if (file != null && file.exists()) {
                int option = JOptionPane.showConfirmDialog(
                        this,
                        "Отчёт о пациенте успешно создан. Хотите его открыть?",
                        "Отчет создан",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE
                );

                if (option == JOptionPane.YES_OPTION) {
                    // Open the PDF file with the default system viewer
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(file);
                    } else {
                        JOptionPane.showMessageDialog(this, "Не удаётся автоматически открыть PDF-файл. Файл сохранён в: " + file.getAbsolutePath(), "Информация", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Не удалось создать отчет о пациенте", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка создания отчета: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
