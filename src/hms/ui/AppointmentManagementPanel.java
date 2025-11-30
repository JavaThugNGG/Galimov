package hms.ui;

import hms.model.Appointment;
import hms.model.Doctor;
import hms.model.Patient;
import hms.service.DoctorService;
import hms.service.PatientService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AppointmentManagementPanel extends JPanel {
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewButton;
    private JButton refreshButton;

    private PatientService patientService;
    private DoctorService doctorService;

    public AppointmentManagementPanel() {
        patientService = new PatientService();
        doctorService = new DoctorService();
        initializeUI();
        loadAppointments();
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
        addButton = new JButton("Записаться на прием");
        editButton = new JButton("Редактировать");
        deleteButton = new JButton("Отменить прием");
        viewButton = new JButton("Посмотреть детали\n");
        refreshButton = new JButton("Обновить");

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(viewButton);
        buttonsPanel.add(refreshButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonsPanel, BorderLayout.EAST);

        // Create table
        String[] columns = {"Идентификатор записи", "Пациент", "Доктор", "Дата", "Время", "Статус"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(appointmentTable);

        // Add components to panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        searchButton.addActionListener(e -> searchAppointments());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchAppointments();
                }
            }
        });

        addButton.addActionListener(e -> showBookAppointmentDialog());
        editButton.addActionListener(e -> showEditAppointmentDialog());
        deleteButton.addActionListener(e -> cancelAppointment());
        viewButton.addActionListener(e -> viewAppointmentDetails());
        refreshButton.addActionListener(e -> loadAppointments());
    }

    private void loadAppointments() {
        // Clear table
        tableModel.setRowCount(0);

        // Add sample data for now
        // In a real implementation, this would load from a service
        addSampleAppointments();
    }

    private void addSampleAppointments() {
        // Add some sample appointments for demonstration
        Object[] appt1 = {"A001", "Большаков Иван Дмитриевич", "Иванов Сергей Петрович", "2025-12-01", "09:00", "ЗАПЛАНИРОВАНО"};
        Object[] appt2 = {"A002", "Соколова Мария Андреевна", "Петрова Анна Викторовна", "2025-12-01", "09:30", "ЗАПЛАНИРОВАНО"};
        Object[] appt3 = {"A003", "Кузнецов Сергей Павлович", "Сидоров Дмитрий Олегович", "2025-12-01", "10:00", "ЗАВЕРШЕНО"};
        Object[] appt4 = {"A004", "Громова Анна Михайловна", "Кузнецова Ольга Михайловна", "2025-12-01", "10:30", "ЗАПЛАНИРОВАНО"};
        Object[] appt5 = {"A005", "Ершов Дмитрий Семёнович", "Васильев Николай Сергеевич", "2025-12-01", "11:00", "ЗАПЛАНИРОВАНО"};
        Object[] appt6 = {"A006", "Орлова Ольга Николаевна", "Белова Татьяна Николаевна", "2025-12-01", "11:30", "ОТМЕНЕНО"};
        Object[] appt7 = {"A007", "Макаров Алексей Игоревич", "Громов Алексей Владимирович", "2025-12-01", "12:00", "ЗАПЛАНИРОВАНО"};
        Object[] appt8 = {"A008", "Сергеевa Екатерина Олеговна", "Миронова Екатерина Сергеевна", "2025-12-01", "12:30", "ЗАВЕРШЕНО"};
        Object[] appt9 = {"A009", "Александров Никита Романович", "Фёдоров Игорь Никитич", "2025-12-01", "13:00", "ЗАВЕРШЕНО"};
        Object[] appt10 = {"A010", "Мельникова Виктория Евгеньевна", "Соколова Мария Павловна", "2025-12-01", "13:30", "ЗАПЛАНИРОВАНО"};
        Object[] appt11 = {"A011", "Филиппов Роман Артёмович", "Романов Артём Денисович", "2025-12-01", "14:00", "ЗАПЛАНИРОВАНО"};
        Object[] appt12 = {"A012", "Савельева Алина Владиславовна", "Зайцева Ирина Юрьевна", "2025-12-01", "14:30", "ЗАВЕРШЕНО"};
        Object[] appt13 = {"A013", "Исламов Тимур Альбертович", "Егоров Максим Степанович", "2025-12-01", "15:00", "ЗАВЕРШЕНО"};
        Object[] appt14 = {"A014", "Кириллова Полина Сергеевна", "Куликова Светлана Аркадьевна", "2025-12-01", "15:30", "ЗАПЛАНИРОВАНО"};
        Object[] appt15 = {"A015", "Громов Георгий Витальевич", "Тихонов Павел Михайлович", "2025-12-01", "16:00", "ЗАПЛАНИРОВАНО"};
        Object[] appt16 = {"A016", "Кожевникова Светлана Петровна", "Ларионова Юлия Константиновна", "2025-12-01", "16:30", "ОТМЕНЕНО"};
        Object[] appt17 = {"A017", "Воронцов Владимир Давидович", "Завьялов Георгий Викторович", "2025-12-01", "17:00", "ЗАПЛАНИРОВАНО"};
        Object[] appt18 = {"A018", "Лебедева Яна Ильинична", "Рябова Ксения Дмитриевна", "2025-12-01", "17:30", "ЗАПЛАНИРОВАНО"};
        Object[] appt19 = {"A019", "Гаврилов Павел Тимофеевич", "Мельников Олег Андреевич", "2025-12-02", "09:00", "ЗАПЛАНИРОВАНО"};
        Object[] appt20 = {"A020", "Фролова Людмила Константиновна", "Чернова Алиса Сергеевна", "2025-12-02", "09:30", "ЗАПЛАНИРОВАНО"};

        tableModel.addRow(appt1);
        tableModel.addRow(appt2);
        tableModel.addRow(appt3);
        tableModel.addRow(appt4);
        tableModel.addRow(appt5);
        tableModel.addRow(appt6);
        tableModel.addRow(appt7);
        tableModel.addRow(appt8);
        tableModel.addRow(appt9);
        tableModel.addRow(appt10);
        tableModel.addRow(appt11);
        tableModel.addRow(appt12);
        tableModel.addRow(appt13);
        tableModel.addRow(appt14);
        tableModel.addRow(appt15);
        tableModel.addRow(appt16);
        tableModel.addRow(appt17);
        tableModel.addRow(appt18);
        tableModel.addRow(appt19);
        tableModel.addRow(appt20);
    }

    private void searchAppointments() {
        String query = searchField.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            loadAppointments();
            return;
        }

        // Clear table
        tableModel.setRowCount(0);

        // In a real implementation, this would search from a service
        // For now, just reload all and filter
        addSampleAppointments();

        // Filter rows
        for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
            boolean match = false;
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                Object value = tableModel.getValueAt(i, j);
                if (value != null && value.toString().toLowerCase().contains(query)) {
                    match = true;
                    break;
                }
            }
            if (!match) {
                tableModel.removeRow(i);
            }
        }
    }

    private void showBookAppointmentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Записаться на прием", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Generate appointment ID
        String appointmentId = "A" + System.currentTimeMillis();

        JLabel idLabel = new JLabel("Идентификатор записи:");
        JTextField idField = new JTextField(appointmentId);
        idField.setEditable(false);

        JLabel patientLabel = new JLabel("Пациент:");
        JComboBox<String> patientComboBox = new JComboBox<>();

        // Load patients
        List<Patient> patients = patientService.getAll();
        for (Patient patient : patients) {
            patientComboBox.addItem(patient.getId() + " - " + patient.getName());
        }

        JLabel doctorLabel = new JLabel("Доктор:");
        JComboBox<String> doctorComboBox = new JComboBox<>();

        // Load doctors
        List<Doctor> doctors = doctorService.getAll();
        for (Doctor doctor : doctors) {
            doctorComboBox.addItem(doctor.getId() + " - " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
        }

        JLabel dateLabel = new JLabel("Дата (ГГГГ-ММ-ДД):");
        JTextField dateField = new JTextField();

        JLabel timeLabel = new JLabel("Время:");
        JComboBox<String> timeComboBox = new JComboBox<>(new String[]{
                "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM",
                "12:00 PM", "12:30 PM", "01:00 PM", "01:30 PM", "02:00 PM", "02:30 PM",
                "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM", "05:00 PM"
        });

        JLabel statusLabel = new JLabel("Статус:");
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"ЗАПЛАНИРОВАНО", "ЗАВЕРШЕНО", "ОТМЕНЕНО"});

        JLabel descriptionLabel = new JLabel("Описание:");
        JTextField descriptionField = new JTextField();

        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(patientLabel);
        formPanel.add(patientComboBox);
        formPanel.add(doctorLabel);
        formPanel.add(doctorComboBox);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(timeLabel);
        formPanel.add(timeComboBox);
        formPanel.add(statusLabel);
        formPanel.add(statusComboBox);
        formPanel.add(descriptionLabel);
        formPanel.add(descriptionField);

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
                if (patientComboBox.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(dialog, "Выберите пациента", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (doctorComboBox.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(dialog, "Выберите врача", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String dateText = dateField.getText().trim();
                if (dateText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Дата не может быть пустой", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate date format
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("гггг-ММ-дд");
                    sdf.setLenient(false);
                    Date date = sdf.parse(dateText);
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(dialog, "Неверный формат даты. Используйте формат ГГГГ-ММ-ДД", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // In a real implementation, this would save to a service
                // For now, just add to the table
                String patientInfo = (String) patientComboBox.getSelectedItem();
                String doctorInfo = (String) doctorComboBox.getSelectedItem();

                String patientName = patientInfo.substring(patientInfo.indexOf(" - ") + 3);
                String doctorName = doctorInfo.substring(doctorInfo.indexOf(" - ") + 3, doctorInfo.indexOf(" ("));

                Object[] row = {
                        idField.getText(),
                        patientName,
                        doctorName,
                        dateField.getText(),
                        timeComboBox.getSelectedItem(),
                        statusComboBox.getSelectedItem()
                };

                tableModel.addRow(row);

                JOptionPane.showMessageDialog(dialog, "Приём успешно забронирован", "Успех", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showEditAppointmentDialog() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите запись для редактирования", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Редактировать запись", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String appointmentId = (String) appointmentTable.getValueAt(selectedRow, 0);
        String patientName = (String) appointmentTable.getValueAt(selectedRow, 1);
        String doctorName = (String) appointmentTable.getValueAt(selectedRow, 2);
        String date = (String) appointmentTable.getValueAt(selectedRow, 3);
        String time = (String) appointmentTable.getValueAt(selectedRow, 4);
        String status = (String) appointmentTable.getValueAt(selectedRow, 5);

        JLabel idLabel = new JLabel("Идентификатор записи:");
        JTextField idField = new JTextField(appointmentId);
        idField.setEditable(false);

        JLabel patientLabel = new JLabel("Пациент:");
        JComboBox<String> patientComboBox = new JComboBox<>();

        // Load patients
        List<Patient> patients = patientService.getAll();
        for (Patient patient : patients) {
            patientComboBox.addItem(patient.getId() + " - " + patient.getName());
            if (patient.getName().equals(patientName)) {
                patientComboBox.setSelectedIndex(patientComboBox.getItemCount() - 1);
            }
        }

        JLabel doctorLabel = new JLabel("Доктор:");
        JComboBox<String> doctorComboBox = new JComboBox<>();

        // Load doctors
        List<Doctor> doctors = doctorService.getAll();
        for (Doctor doctor : doctors) {
            doctorComboBox.addItem(doctor.getId() + " - " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
            if (doctor.getName().equals(doctorName)) {
                doctorComboBox.setSelectedIndex(doctorComboBox.getItemCount() - 1);
            }
        }

        JLabel dateLabel = new JLabel("Дата (ГГГГ-ММ-ДД):");
        JTextField dateField = new JTextField(date);

        JLabel timeLabel = new JLabel("Time:");
        JComboBox<String> timeComboBox = new JComboBox<>(new String[]{
                "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM",
                "12:00 PM", "12:30 PM", "01:00 PM", "01:30 PM", "02:00 PM", "02:30 PM",
                "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM", "05:00 PM"
        });
        timeComboBox.setSelectedItem(time);

        JLabel statusLabel = new JLabel("Статус:");
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"ЗАПЛАНИРОВАНО", "ЗАВЕРШЕНО", "ОТМЕНЕНО"});
        statusComboBox.setSelectedItem(status);

        JLabel descriptionLabel = new JLabel("Описание:");
        JTextField descriptionField = new JTextField();

        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(patientLabel);
        formPanel.add(patientComboBox);
        formPanel.add(doctorLabel);
        formPanel.add(doctorComboBox);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(timeLabel);
        formPanel.add(timeComboBox);
        formPanel.add(statusLabel);
        formPanel.add(statusComboBox);
        formPanel.add(descriptionLabel);
        formPanel.add(descriptionField);

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
                if (patientComboBox.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(dialog, "Выбериет пациента", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (doctorComboBox.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(dialog, "Выберите доктора", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String dateText = dateField.getText().trim();
                if (dateText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Дата не может быть пустой", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate date format
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("гггг-ММ-дд");
                    sdf.setLenient(false);
                    Date date2 = sdf.parse(dateText);
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(dialog, "Неверный формат даты. Используйте формат ГГГГ-ММ-ДД", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // In a real implementation, this would update in a service
                // For now, just update the table
                String patientInfo = (String) patientComboBox.getSelectedItem();
                String doctorInfo = (String) doctorComboBox.getSelectedItem();

                String newPatientName = patientInfo.substring(patientInfo.indexOf(" - ") + 3);
                String newDoctorName = doctorInfo.substring(doctorInfo.indexOf(" - ") + 3, doctorInfo.indexOf(" ("));

                appointmentTable.setValueAt(newPatientName, selectedRow, 1);
                appointmentTable.setValueAt(newDoctorName, selectedRow, 2);
                appointmentTable.setValueAt(dateField.getText(), selectedRow, 3);
                appointmentTable.setValueAt(timeComboBox.getSelectedItem(), selectedRow, 4);
                appointmentTable.setValueAt(statusComboBox.getSelectedItem(), selectedRow, 5);

                JOptionPane.showMessageDialog(dialog, "Запись успешно обновлена", "Успех", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void cancelAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите запись для отмены.", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String appointmentId = (String) appointmentTable.getValueAt(selectedRow, 0);
        String patientName = (String) appointmentTable.getValueAt(selectedRow, 1);
        String doctorName = (String) appointmentTable.getValueAt(selectedRow, 2);
        String date = (String) appointmentTable.getValueAt(selectedRow, 3);

        int option = JOptionPane.showConfirmDialog(
                this,
                "Вы уверены, что хотите отменить прием " + patientName + " с " + doctorName + " на " + date + "?",
                "Подтвердить отмену",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            // In a real implementation, this would update in a service
            // For now, just update the table
            appointmentTable.setValueAt("ОТМЕНЕНО", selectedRow, 5);

            JOptionPane.showMessageDialog(this, "Приём успешно отменен", "Успех", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewAppointmentDetails() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите запись для просмотра", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String appointmentId = (String) appointmentTable.getValueAt(selectedRow, 0);
        String patientName = (String) appointmentTable.getValueAt(selectedRow, 1);
        String doctorName = (String) appointmentTable.getValueAt(selectedRow, 2);
        String date = (String) appointmentTable.getValueAt(selectedRow, 3);
        String time = (String) appointmentTable.getValueAt(selectedRow, 4);
        String status = (String) appointmentTable.getValueAt(selectedRow, 5);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Подробности записи", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Appointment details
        JLabel titleLabel = new JLabel("Информация о записи");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addDetailRow(infoPanel, "Идентификатор встречи:", appointmentId);
        addDetailRow(infoPanel, "Пациент:", patientName);
        addDetailRow(infoPanel, "Доктор:", doctorName);
        addDetailRow(infoPanel, "Дата:", date);
        addDetailRow(infoPanel, "Время:", time);
        addDetailRow(infoPanel, "Статус:", status);

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
