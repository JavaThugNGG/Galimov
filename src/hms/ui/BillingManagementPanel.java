package hms.ui;

import hms.model.Billing;
import hms.model.Patient;
import hms.service.PatientService;
import hms.util.PDFGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BillingManagementPanel extends JPanel {
    private JTable billingTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewButton;
    private JButton refreshButton;
    private JButton printButton;

    private PatientService patientService;

    public BillingManagementPanel() {
        patientService = new PatientService();
        initializeUI();
        loadBillings();
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
        addButton = new JButton("Создать счет");
        editButton = new JButton("Редактировать");
        deleteButton = new JButton("Удалить");
        viewButton = new JButton("Посмотреть детали");
        printButton = new JButton("Распечатать счет");
        refreshButton = new JButton("Обновить");

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(viewButton);
        buttonsPanel.add(printButton);
        buttonsPanel.add(refreshButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonsPanel, BorderLayout.EAST);

        String[] columns = {"Идентификатор счета", "Пациент", "Дата", "Сумма", "Статус"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        billingTable = new JTable(tableModel);
        billingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billingTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(billingTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> searchBillings());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchBillings();
                }
            }
        });

        addButton.addActionListener(e -> showGenerateBillDialog());
        editButton.addActionListener(e -> showEditBillDialog());
        deleteButton.addActionListener(e -> deleteBill());
        viewButton.addActionListener(e -> viewBillDetails());
        printButton.addActionListener(e -> printBill());
        refreshButton.addActionListener(e -> loadBillings());
    }

    private void loadBillings() {
        tableModel.setRowCount(0);

        addSampleBillings();
    }

    private void addSampleBillings() {
        Object[] row2 = {"B002",  "Соколова Мария Андреевна", "2024-06-10", "3200.50", "НЕ ОПЛАЧЕНО"};
        Object[] row3 = {"B003",  "Кузнецов Сергей Павлович", "2024-06-05", "7850.75", "ОПЛАЧЕНО"};
        Object[] row4 = {"B004",  "Громова Анна Михайловна", "2024-06-01", "2950.00", "НЕ ОПЛАЧЕНО"};
        Object[] row5 = {"B005",  "Ершов Дмитрий Семёнович", "2024-06-15", "1750.25", "ОПЛАЧЕНО"};
        Object[] row6 = {"B006",  "Орлова Ольга Николаевна", "2024-06-12", "5300.00", "ОПЛАЧЕНО"};
        Object[] row7 = {"B007",  "Макаров Алексей Игоревич", "2024-06-08", "9500.00", "НЕ ОПЛАЧЕНО"};
        Object[] row8 = {"B008",  "Сергеева Екатерина Олеговна", "2024-06-18", "3100.00", "ОПЛАЧЕНО"};
        Object[] row9 = {"B009",  "Александров Никита Романович", "2024-06-03", "2450.50", "НЕ ОПЛАЧЕНО"};
        Object[] row10 = {"B010",  "Мельникова Виктория Евгеньевна", "2024-06-14", "4200.00", "ОПЛАЧЕНО"};

        tableModel.addRow(row2);
        tableModel.addRow(row3);
        tableModel.addRow(row4);
        tableModel.addRow(row5);
        tableModel.addRow(row6);
        tableModel.addRow(row7);
        tableModel.addRow(row8);
        tableModel.addRow(row9);
        tableModel.addRow(row10);
    }

    private void searchBillings() {
        String query = searchField.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            loadBillings();
            return;
        }

        tableModel.setRowCount(0);

        addSampleBillings();

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

    private void showGenerateBillDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Создать счет", true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        String billId = "B" + System.currentTimeMillis();

        JLabel idLabel = new JLabel("Идентификатор счета:");
        JTextField idField = new JTextField(billId);
        idField.setEditable(false);

        JLabel dateLabel = new JLabel("Дата:");
        JTextField dateField = new JTextField(new SimpleDateFormat("гггг-ММ-дд").format(new Date()));
        dateField.setEditable(false);

        JLabel patientLabel = new JLabel("Пациент:");
        JComboBox<String> patientComboBox = new JComboBox<>();

        java.util.List<Patient> patients = patientService.getAll();
        for (Patient patient : patients) {
            patientComboBox.addItem(patient.getId() + " - " + patient.getName());
        }

        headerPanel.add(idLabel);
        headerPanel.add(idField);
        headerPanel.add(dateLabel);
        headerPanel.add(dateField);
        headerPanel.add(patientLabel);
        headerPanel.add(patientComboBox);

        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel itemsLabel = new JLabel("Элементы счета:");
        itemsLabel.setFont(new Font("Arial", Font.BOLD, 14));

        String[] columns = {"Описание", "Количество", "Цена за единицу", "Сумма"};
        DefaultTableModel itemsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 3; // Amount is calculated
            }
        };

        JTable itemsTable = new JTable(itemsModel);
        JScrollPane itemsScrollPane = new JScrollPane(itemsTable);

        JPanel itemButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addItemButton = new JButton("Добавить элемент");
        JButton removeItemButton = new JButton("Удалить элемент");

        itemButtonsPanel.add(addItemButton);
        itemButtonsPanel.add(removeItemButton);

        itemsPanel.add(itemsLabel, BorderLayout.NORTH);
        itemsPanel.add(itemsScrollPane, BorderLayout.CENTER);
        itemsPanel.add(itemButtonsPanel, BorderLayout.SOUTH);

        JPanel summaryPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        summaryPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel subtotalLabel = new JLabel("Промежуточный итог:");
        JTextField subtotalField = new JTextField("0.00");
        subtotalField.setEditable(false);

        JLabel discountLabel = new JLabel("Скидка (%):");
        JTextField discountField = new JTextField("0");

        JLabel taxLabel = new JLabel("Налог (%):");
        JTextField taxField = new JTextField("0");

        JLabel totalLabel = new JLabel("Итог:");
        JTextField totalField = new JTextField("0.00");
        totalField.setEditable(false);

        summaryPanel.add(subtotalLabel);
        summaryPanel.add(subtotalField);
        summaryPanel.add(discountLabel);
        summaryPanel.add(discountField);
        summaryPanel.add(taxLabel);
        summaryPanel.add(taxField);
        summaryPanel.add(totalLabel);
        summaryPanel.add(totalField);

        JPanel paymentPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        paymentPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel statusLabel = new JLabel("Статус платежа:");
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"ОПЛАЧЕН", "НЕ ОПЛАЧЕН"});

        JLabel methodLabel = new JLabel("Метод оплаты:");
        JComboBox<String> methodComboBox = new JComboBox<>(new String[]{"Наличные", "Кредитная карта", "Дебетовая карта", "Страхование", "Другое"});

        paymentPanel.add(statusLabel);
        paymentPanel.add(statusComboBox);
        paymentPanel.add(methodLabel);
        paymentPanel.add(methodComboBox);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(itemsPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(summaryPanel, BorderLayout.NORTH);
        bottomPanel.add(paymentPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Сохранить счет");
        JButton cancelButton = new JButton("Отменить");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        addItemButton.addActionListener(e -> {
            showAddItemDialog(dialog, itemsModel, subtotalField, totalField, discountField, taxField);
        });

        removeItemButton.addActionListener(e -> {
            int selectedRow = itemsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Выберите элемент для удаления", "Нет выбора", JOptionPane.WARNING_MESSAGE);
                return;
            }

            itemsModel.removeRow(selectedRow);
            updateBillTotal(itemsModel, subtotalField, totalField, discountField, taxField);
        });

        discountField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateBillTotal(itemsModel, subtotalField, totalField, discountField, taxField);
            }
        });

        taxField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateBillTotal(itemsModel, subtotalField, totalField, discountField, taxField);
            }
        });

        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (patientComboBox.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(dialog, "Выберите пациента", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (itemsModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(dialog, "Выберите хотя бы один элемент", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String patientInfo = (String) patientComboBox.getSelectedItem();
                String patientName = patientInfo.substring(patientInfo.indexOf(" - ") + 3);

                Object[] row = {
                        idField.getText(),
                        patientName,
                        dateField.getText(),
                        totalField.getText(),
                        statusComboBox.getSelectedItem()
                };

                tableModel.addRow(row);

                JOptionPane.showMessageDialog(dialog, "Счет сгенерирован успешно", "Успех", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showAddItemDialog(JDialog parentDialog, DefaultTableModel itemsModel,
                                   JTextField subtotalField, JTextField totalField,
                                   JTextField discountField, JTextField taxField) {
        JDialog dialog = new JDialog(parentDialog, "Добавить элемент", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(parentDialog);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel descriptionLabel = new JLabel("Описание:");
        JTextField descriptionField = new JTextField();

        JLabel quantityLabel = new JLabel("Количество:");
        JTextField quantityField = new JTextField("1");

        JLabel priceLabel = new JLabel("Цена за единицу:");
        JTextField priceField = new JTextField();

        JLabel amountLabel = new JLabel("Сумма:");
        JTextField amountField = new JTextField("0.00");
        amountField.setEditable(false);

        formPanel.add(descriptionLabel);
        formPanel.add(descriptionField);
        formPanel.add(quantityLabel);
        formPanel.add(quantityField);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(amountLabel);
        formPanel.add(amountField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Добавить");
        JButton cancelButton = new JButton("Отменить");

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        FocusListener calculateAmount = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    double price = Double.parseDouble(priceField.getText().trim());
                    double amount = quantity * price;
                    amountField.setText(String.format("%.2f", amount));
                } catch (NumberFormatException ex) {
                    amountField.setText("0.00");
                }
            }
        };

        quantityField.addFocusListener(calculateAmount);
        priceField.addFocusListener(calculateAmount);

        addButton.addActionListener(e -> {
            try {
                String description = descriptionField.getText().trim();
                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Описание не может быть пустым", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int quantity;
                try {
                    quantity = Integer.parseInt(quantityField.getText().trim());
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Количество должно быть больше 0", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Количество должно быть числом", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
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

                double amount = quantity * price;

                Object[] row = {
                        description,
                        quantity,
                        String.format("%.2f", price),
                        String.format("%.2f", amount)
                };

                itemsModel.addRow(row);

                updateBillTotal(itemsModel, subtotalField, totalField, discountField, taxField);

                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void updateBillTotal(DefaultTableModel itemsModel, JTextField subtotalField,
                                 JTextField totalField, JTextField discountField, JTextField taxField) {
        double subtotal = 0.0;

        for (int i = 0; i < itemsModel.getRowCount(); i++) {
            String amountStr = (String) itemsModel.getValueAt(i, 3);
            double amount = Double.parseDouble(amountStr.substring(1));
            subtotal += amount;
        }

        double discount = 0.0;
        try {
            discount = Double.parseDouble(discountField.getText().trim());
        } catch (NumberFormatException e) {
            discountField.setText("0");
        }

        double tax = 0.0;
        try {
            tax = Double.parseDouble(taxField.getText().trim());
        } catch (NumberFormatException e) {
            taxField.setText("0");
        }

        double discountAmount = subtotal * (discount / 100.0);
        double taxAmount = subtotal * (tax / 100.0);
        double total = subtotal - discountAmount + taxAmount;

        subtotalField.setText(String.format("%.2f", subtotal));
        totalField.setText(String.format("$.2f", total));
    }

    private void showEditBillDialog() {
        int selectedRow = billingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите счет для редактирования", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Функциональность редактирования счета будет реализована здесь", "Информация", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteBill() {
        int selectedRow = billingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите счет для удаления", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String billId = (String) billingTable.getValueAt(selectedRow, 0);
        String patientName = (String) billingTable.getValueAt(selectedRow, 1);

        int option = JOptionPane.showConfirmDialog(
                this,
                "Вы уверены, что хотите удалить счет " + billId + " для " + patientName + "?",
                "Подтвердить удаление",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);

            JOptionPane.showMessageDialog(this, "Счет успешно удален", "Успех", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewBillDetails() {
        int selectedRow = billingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите счет для просмотра", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String billId = (String) billingTable.getValueAt(selectedRow, 0);
        String patientName = (String) billingTable.getValueAt(selectedRow, 1);
        String date = (String) billingTable.getValueAt(selectedRow, 2);
        String amount = (String) billingTable.getValueAt(selectedRow, 3);
        String status = (String) billingTable.getValueAt(selectedRow, 4);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Подробности счета", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Информация по счета");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addDetailRow(infoPanel, "Идентификатор счета:", billId);
        addDetailRow(infoPanel, "Пациент:", patientName);
        addDetailRow(infoPanel, "Дата:", date);
        addDetailRow(infoPanel, "Сумма:", amount);
        addDetailRow(infoPanel, "Статус:", status);

        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        detailsPanel.add(infoPanel);

        JLabel itemsLabel = new JLabel("Элемента счета");
        itemsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        itemsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] columns = {"Описание", "Количество", "Цена за единицу", "Сумма"};
        DefaultTableModel itemsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable itemsTable = new JTable(itemsModel);
        JScrollPane itemsScrollPane = new JScrollPane(itemsTable);
        itemsScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        itemsScrollPane.setPreferredSize(new Dimension(550, 200));

        Object[] item1 = {"Плата за консультацию", 1, "5000", "5000"};
        Object[] item2 = {"Лекарство - Парацетамол", 2, "599", "1198"};
        Object[] item3 = {"Анализ крови", 1, "3500", "3500"};

        itemsModel.addRow(item1);
        itemsModel.addRow(item2);
        itemsModel.addRow(item3);

        detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        detailsPanel.add(itemsLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailsPanel.add(itemsScrollPane);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton printButton = new JButton("Распечатать счет");
        JButton closeButton = new JButton("Закрыть");

        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);

        dialog.add(detailsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        printButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Счет напечатан успешно", "Успех", JOptionPane.INFORMATION_MESSAGE);
        });

        closeButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void printBill() {
        int selectedRow = billingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите счет для печати", "Нет выбора", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String billId = (String) billingTable.getValueAt(selectedRow, 0);
        String patientName = (String) billingTable.getValueAt(selectedRow, 1);

        try {
            Billing billing = new Billing(billId, "P001", new Date());
            billing.setTotalAmount(Double.parseDouble(((String) billingTable.getValueAt(selectedRow, 3)).substring(1)));
            billing.setPaymentStatus((String) billingTable.getValueAt(selectedRow, 4));

            Patient patient = new Patient("P001", patientName, 35, "123-456-7890", "Высокая температура");

            billing.addItem(new Billing.BillItem("Плата за консультацию", 5000, 1));
            billing.addItem(new Billing.BillItem("Лекарство - Парацетамол", 599, 2));
            billing.addItem(new Billing.BillItem("Анализ крови", 350, 1));

            String outputPath = "reports/bill_" + billId + ".pdf";
            File file = PDFGenerator.generateBillingReport(billing, patient, outputPath);

            if (file != null && file.exists()) {
                int option = JOptionPane.showConfirmDialog(
                        this,
                        "Счёт успешно сформирован. Хотите его открыть??",
                        "Счет создан",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE
                );

                if (option == JOptionPane.YES_OPTION) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(file);
                    } else {
                        JOptionPane.showMessageDialog(this, "Не удаётся автоматически открыть PDF-файл. Файл сохранён в: " + file.getAbsolutePath(), "Информация", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Не удалось создать счет", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка создания счета: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
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
