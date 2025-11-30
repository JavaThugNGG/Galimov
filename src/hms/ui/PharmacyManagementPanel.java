package hms.ui;

import hms.model.Medicine;
import hms.service.MedicineService;
import hms.util.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PharmacyManagementPanel extends JPanel {
    private MedicineService medicineService;
    private JTable medicineTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewButton;
    private JButton refreshButton;
    private JButton dispenseMedicineButton;

    private boolean samplesAdded = false; // Add this as a class field

    public PharmacyManagementPanel() {
        medicineService = new MedicineService();
        initializeUI();
        loadMedicines();
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
        addButton = new JButton("Добавить лекарство");
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
        String[] columns = {"Идентификатор", "Имя", "Производитель", "Категория", "Цена", "Количество"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        medicineTable = new JTable(tableModel);
        medicineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medicineTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(medicineTable);

        // Add components to panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        searchButton.addActionListener(e -> searchMedicines());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchMedicines();
                }
            }
        });

        addButton.addActionListener(e -> showAddMedicineDialog());
        editButton.addActionListener(e -> showEditMedicineDialog());
        deleteButton.addActionListener(e -> deleteMedicine());
        viewButton.addActionListener(e -> viewMedicineDetails());
        refreshButton.addActionListener(e -> loadMedicines());
    }

    // Fix the sample data loading to prevent recursion
    private void loadMedicines() {
        // Clear table
        tableModel.setRowCount(0);

        try {
            // Load medicines from service
            List<Medicine> medicines = medicineService.getAll();
            System.out.println("Загруженные " + medicines.size() + " лекарства из службы");

            // If no medicines exist, add samples first
            if (medicines.isEmpty() && !samplesAdded) {
                System.out.println("Лекарства не найдены, добавляем образцы данных...");
                addSampleMedicinesDirectly();
                medicines = medicineService.getAll(); // Reload after adding samples
                System.out.println("После добавления образцов: " + medicines.size() + " лекарства");
            }

            // Add medicines to table
            for (Medicine medicine : medicines) {
                try {
                    Object[] row = {
                            medicine.getMedicineId(),
                            medicine.getName(),
                            medicine.getManufacturer() != null ? medicine.getManufacturer() : "N/A",
                            medicine.getCategory() != null ? medicine.getCategory() : "N/A",
                            String.format("%.2f", medicine.getPrice()),
                            medicine.getQuantity()
                    };
                    tableModel.addRow(row);
                } catch (Exception e) {
                    System.err.println("Ошибка добавления лекарства в таблицу: " + medicine.getMedicineId());
                    e.printStackTrace();
                }
            }

            System.out.println("Успешно загружены " + tableModel.getRowCount() + " лекарства в таблицу");

        } catch (Exception e) {
            System.err.println("Ошибка при загрузке лекарств:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Ошибка загрузки лекарств: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSampleMedicinesDirectly() {
        if (samplesAdded) return;
        samplesAdded = true;

        // Add sample medicines directly to service
        Medicine m1 = new Medicine("M001", "Парацетамол", "ABC Pharma", 5.99, 100);
        m1.setCategory("Облегчение боли");
        m1.setDescription("Обезболивающее лекарство");

        Medicine m2 = new Medicine("M002", "Амоксициллин", "XYZ Pharma", 12.50, 50);
        m2.setCategory("Антибиотики");
        m2.setDescription("Прием антибиотиков");

        Medicine m3 = new Medicine("M003", "Ибупрофен", "MNO Pharma", 7.25, 75);
        m3.setCategory("Облегчение боли");
        m3.setDescription("Противовоспалительные препараты");

        Medicine m4 = new Medicine("M004", "Цетиризин", "PQR Pharma", 8.99, 60);
        m4.setCategory("Антигистаминный препарат");
        m4.setDescription("Лекарства от аллергии");

        // Add to service
        medicineService.add(m1);
        medicineService.add(m2);
        medicineService.add(m3);
        medicineService.add(m4);
    }

    private void searchMedicines() {
        String query = searchField.getText().trim();

        // Clear table
        tableModel.setRowCount(0);

        // Search medicines
        List<Medicine> medicines = medicineService.search(query);

        // Add medicines to table
        for (Medicine medicine : medicines) {
            Object[] row = {
                    medicine.getMedicineId(),
                    medicine.getName(),
                    medicine.getManufacturer() != null ? medicine.getManufacturer() : "N/A",
                    medicine.getCategory() != null ? medicine.getCategory() : "N/A",
                    String.format("%.2f", medicine.getPrice()),
                    medicine.getQuantity()
            };
            tableModel.addRow(row);
        }
    }

    private void showAddMedicineDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Добавить лекарство", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Generate medicine ID
        String medicineId = medicineService.generateMedicineId();

        JLabel idLabel = new JLabel("Идентификатор лекарства:");
        JTextField idField = new JTextField(medicineId);
        idField.setEditable(false);

        JLabel nameLabel = new JLabel("Название:");
        JTextField nameField = new JTextField();

        JLabel manufacturerLabel = new JLabel("Производитель:");
        JTextField manufacturerField = new JTextField();

        JLabel categoryLabel = new JLabel("Категория:");
        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{
                "Облегчение боли", "Антибиотики", "Антигистаминный препарат", "Сердечно-сосудистая система",
                "Желудочно-кишечный", "Дыхательная система", "Дерматологический", "Другое"
        });

        JLabel priceLabel = new JLabel("Цена:");
        JTextField priceField = new JTextField();

        JLabel quantityLabel = new JLabel("Количество:");
        JTextField quantityField = new JTextField();

        JLabel descriptionLabel = new JLabel("Описание:");
        JTextField descriptionField = new JTextField();

        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(manufacturerLabel);
        formPanel.add(manufacturerField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryComboBox);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(quantityLabel);
        formPanel.add(quantityField);
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
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Название не может быть пустым", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String manufacturer = manufacturerField.getText().trim();
                if (manufacturer.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Производитель не может быть пустым", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double price;
                try {
                    price = Double.parseDouble(priceField.getText().trim());
                    if (price < 0) {
                        JOptionPane.showMessageDialog(dialog, "Цена не может быть отрицательной", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Цена должна быть числом", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int quantity;
                try {
                    quantity = Integer.parseInt(quantityField.getText().trim());
                    if (quantity < 0) {
                        JOptionPane.showMessageDialog(dialog, "Количество не может быть отрицательным", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Количество должно быть числом", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create medicine object
                Medicine medicine = new Medicine(
                        idField.getText(),
                        name,
                        manufacturer,
                        price,
                        quantity
                );

                medicine.setCategory((String) categoryComboBox.getSelectedItem());
                medicine.setDescription(descriptionField.getText().trim());

                // Save medicine
                if (medicineService.add(medicine)) {
                    JOptionPane.showMessageDialog(dialog, "Лекарство успешно добавлено", "Успешно", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadMedicines();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Не удалось добавить лекарство", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showEditMedicineDialog() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите лекарство для редактирования.", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String medicineId = (String) medicineTable.getValueAt(selectedRow, 0);
        Medicine medicine = medicineService.getById(medicineId);

        if (medicine == null) {
            JOptionPane.showMessageDialog(this, "Лекарство не найдено", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Редактировать лекарство", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel idLabel = new JLabel("Идентификатор лекарства:");
        JTextField idField = new JTextField(medicine.getMedicineId());
        idField.setEditable(false);

        JLabel nameLabel = new JLabel("Название:");
        JTextField nameField = new JTextField(medicine.getName());

        JLabel manufacturerLabel = new JLabel("Производитель:");
        JTextField manufacturerField = new JTextField(medicine.getManufacturer());

        JLabel categoryLabel = new JLabel("Категория:");
        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{
                "Облегчение боли", "Антибиотики", "Антигистаминный", "Сердечно-сосудистые",
                "Желудочно-кишечный", "Респираторный", "Дерматологический", "Другое"
        });
        categoryComboBox.setSelectedItem(medicine.getCategory());

        JLabel priceLabel = new JLabel("Цена:");
        JTextField priceField = new JTextField(String.valueOf(medicine.getPrice()));

        JLabel quantityLabel = new JLabel("Количество:");
        JTextField quantityField = new JTextField(String.valueOf(medicine.getQuantity()));

        JLabel descriptionLabel = new JLabel("Описание:");
        JTextField descriptionField = new JTextField(medicine.getDescription());

        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(manufacturerLabel);
        formPanel.add(manufacturerField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryComboBox);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(quantityLabel);
        formPanel.add(quantityField);
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
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Название не может быть пустым", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String manufacturer = manufacturerField.getText().trim();
                if (manufacturer.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Производитель не может быть пустым", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double price;
                try {
                    price = Double.parseDouble(priceField.getText().trim());
                    if (price < 0) {
                        JOptionPane.showMessageDialog(dialog, "Цена не может быть отрицательной", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Цена должна быть числом", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int quantity;
                try {
                    quantity = Integer.parseInt(quantityField.getText().trim());
                    if (quantity < 0) {
                        JOptionPane.showMessageDialog(dialog, "Количество не может быть отрицательным", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Количество должно быть числом", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update medicine object
                medicine.setName(name);
                medicine.setManufacturer(manufacturer);
                medicine.setCategory((String) categoryComboBox.getSelectedItem());
                medicine.setPrice(price);
                medicine.setQuantity(quantity);
                medicine.setDescription(descriptionField.getText().trim());

                // Save medicine
                if (medicineService.update(medicine)) {
                    JOptionPane.showMessageDialog(dialog, "Лекарство успешно обновлено", "Успешно", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadMedicines();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Не удалось обновить лекарство", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void deleteMedicine() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите лекарство для удаления", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String medicineId = (String) medicineTable.getValueAt(selectedRow, 0);
        String medicineName = (String) medicineTable.getValueAt(selectedRow, 1);

        int option = JOptionPane.showConfirmDialog(
                this,
                "Вы уверены, что хотите удалить лекарство " + medicineName + "?",
                "Подтвердить удаление",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            if (medicineService.delete(medicineId)) {
                JOptionPane.showMessageDialog(this, "Лекарство успешно удалено", "Успешно", JOptionPane.INFORMATION_MESSAGE);
                loadMedicines();
            } else {
                JOptionPane.showMessageDialog(this, "Не удалось удалить лекарство", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewMedicineDetails() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите лекарство для просмотра", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String medicineId = (String) medicineTable.getValueAt(selectedRow, 0);
        Medicine medicine = medicineService.getById(medicineId);

        if (medicine == null) {
            JOptionPane.showMessageDialog(this, "Лекарство не найдено по идентификатору: " + medicineId, "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Подробности о лекарстве", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Medicine details
        JLabel titleLabel = new JLabel("Информация о лекарствах");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addDetailRow(infoPanel, "Идентификатор лекарства:", medicine.getMedicineId());
        addDetailRow(infoPanel, "Название:", medicine.getName());
        addDetailRow(infoPanel, "Производитель:", medicine.getManufacturer());
        addDetailRow(infoPanel, "Категория:", medicine.getCategory());
        addDetailRow(infoPanel, "Цена:", String.format("$%.2f", medicine.getPrice()));
        addDetailRow(infoPanel, "Количество:", String.valueOf(medicine.getQuantity()));
        addDetailRow(infoPanel, "Описание:", medicine.getDescription());

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


    private void updateTotal(DefaultTableModel model, JLabel totalLabel) {
        double total = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {
            String priceStr = (String) model.getValueAt(i, 6);
            double price = Double.parseDouble(priceStr.substring(1));

            int quantity = 1;
            try {
                Object quantityObj = model.getValueAt(i, 2);
                if (quantityObj != null) {
                    quantity = Integer.parseInt(quantityObj.toString());
                }
            } catch (NumberFormatException ex) {
                // Use default quantity of 1
            }

            total += price * quantity;
        }

        totalLabel.setText(String.format("Всего: $%.2f", total));
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));

        // Fix null pointer issue and handle empty strings
        String displayValue = "N/A";
        if (value != null && !value.trim().isEmpty() && !value.equals("null")) {
            displayValue = value;
        }

        JLabel valueComponent = new JLabel(displayValue);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(labelComponent);
        panel.add(valueComponent);
    }
}
