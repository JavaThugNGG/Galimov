package hms.ui;

import hms.model.Patient;
import hms.service.PatientService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Object[][] bills = {
                {"B001", "Большаков Иван Дмитриевич", "2025-06-01", "$150.00", "ОПЛАЧЕН"},
                {"B002", "Соколова Мария Андреевна", "2025-06-10", "$200.50", "НЕ ОПЛАЧЕН"},
                {"B003", "Кузнецов Сергей Павлович", "2025-06-05", "$320.75", "ОПЛАЧЕН"},
                {"B004", "Громова Анна Михайловна", "2025-06-01", "$95.25", "НЕ ОПЛАЧЕН"},
                {"B005", "Ершов Дмитрий Семёнович", "2025-06-12", "$120.00", "ОПЛАЧЕН"},
                {"B006", "Орлова Ольга Николаевна", "2025-06-03", "$180.50", "ОПЛАЧЕН"},
                {"B007", "Макаров Алексей Игоревич", "2025-06-08", "$250.00", "НЕ ОПЛАЧЕН"},
                {"B008", "Сергеевa Екатерина Олеговна", "2025-06-09", "$130.25", "ОПЛАЧЕН"},
                {"B009", "Александров Никита Романович", "2025-06-11", "$90.00", "НЕ ОПЛАЧЕН"},
                {"B010", "Мельникова Виктория Евгеньевна", "2025-06-07", "$160.75", "ОПЛАЧЕН"},
                {"B011", "Филиппов Роман Артёмович", "2025-06-02", "$200.00", "ОПЛАЧЕН"},
                {"B012", "Савельева Алина Владиславовна", "2025-06-06", "$140.50", "НЕ ОПЛАЧЕН"},
                {"B013", "Исламов Тимур Альбертович", "2025-06-04", "$170.25", "ОПЛАЧЕН"},
                {"B014", "Кириллова Полина Сергеевна", "2025-06-05", "$155.00", "НЕ ОПЛАЧЕН"},
                {"B015", "Громов Георгий Витальевич", "2025-06-03", "$210.75", "ОПЛАЧЕН"},
                {"B016", "Кожевникова Светлана Петровна", "2025-06-10", "$125.50", "ОПЛАЧЕН"},
                {"B017", "Воронцов Владимир Давидович", "2025-06-08", "$300.00", "НЕ ОПЛАЧЕН"},
                {"B018", "Лебедева Яна Ильинична", "2025-06-12", "$115.25", "ОПЛАЧЕН"},
                {"B019", "Гаврилов Павел Тимофеевич", "2025-06-06", "$220.50", "ОПЛАЧЕН"},
                {"B020", "Фролова Людмила Константиновна", "2025-06-09", "$190.75", "НЕ ОПЛАЧЕН"},
                {"B021", "Ермаков Константин Львович", "2025-06-07", "$130.00", "ОПЛАЧЕН"},
                {"B022", "Щукина Елена Валерьевна", "2025-06-05", "$145.50", "НЕ ОПЛАЧЕН"},
                {"B023", "Астахов Максим Андреевич", "2025-06-04", "$160.25", "ОПЛАЧЕН"},
                {"B024", "Смирнова Татьяна Вячеславовна", "2025-06-03", "$210.00", "НЕ ОПЛАЧЕН"},
                {"B025", "Журавлёв Игорь Никитич", "2025-06-08", "$230.50", "ОПЛАЧЕН"},
                {"B026", "Белова Юлия Михайловна", "2025-06-06", "$125.75", "ОПЛАЧЕН"},
                {"B027", "Шестаков Степан Арсеньевич", "2025-06-10", "$95.00", "НЕ ОПЛАЧЕН"},
                {"B028", "Панфилова Алёна Георгиевна", "2025-06-09", "$135.50", "ОПЛАЧЕН"},
                {"B029", "Рожков Глеб Денисович", "2025-06-07", "$180.25", "ОПЛАЧЕН"},
                {"B030", "Волкова Ксения Александровна", "2025-06-05", "$155.50", "НЕ ОПЛАЧЕН"}
        };

        for (Object[] row : bills) {
            tableModel.addRow(row);
        }
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
        itemsLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));

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
        JTextField subtotalField = new JTextField("$0.00");
        subtotalField.setEditable(false);

        JLabel discountLabel = new JLabel("Скидка (%):");
        JTextField discountField = new JTextField("0");

        JLabel taxLabel = new JLabel("Налош (%):");
        JTextField taxField = new JTextField("0");

        JLabel totalLabel = new JLabel("Итог:");
        JTextField totalField = new JTextField("$0.00");
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

        JLabel methodLabel = new JLabel("Payment Method:");
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
        JTextField amountField = new JTextField("$0.00");
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
                    amountField.setText(String.format("$%.2f", amount));
                } catch (NumberFormatException ex) {
                    amountField.setText("$0.00");
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
                        String.format("$%.2f", price),
                        String.format("$%.2f", amount)
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

        subtotalField.setText(String.format("$%.2f", subtotal));
        totalField.setText(String.format("$%.2f", total));
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
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
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
        itemsLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
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

        Object[] item1 = {"Плата за консультацию", 1, "$50.00", "$50.00"};
        Object[] item2 = {"Лекарство - Парацетамол", 2, "$5.99", "$11.98"};
        Object[] item3 = {"Анализ крови", 1, "$35.00", "$35.00"};

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

    public void printBill() {
        try {
            String billId = "B001";
            String date = "2025-11-30";
            String patientName = "Большаков Иван Дмитриевич";
            String status = "ОПЛАЧЕН";

            Document document = new Document();
            File outputDir = new File("reports");
            if (!outputDir.exists()) outputDir.mkdirs();
            String outputPath = "reports/bill_" + billId + ".pdf";

            PdfWriter.getInstance(document, new java.io.FileOutputStream(outputPath));
            document.open();

            BaseFont bf = BaseFont.createFont("C:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(bf, 12);

            Paragraph title = new Paragraph("Счет №" + billId, new Font(bf, 16, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Дата: " + date, font));
            document.add(new Paragraph("Пациент: " + patientName, font));
            document.add(new Paragraph("Статус: " + status, font));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 1, 2, 2});
            table.addCell(new Phrase("Описание", font));
            table.addCell(new Phrase("Кол-во", font));
            table.addCell(new Phrase("Цена", font));
            table.addCell(new Phrase("Сумма", font));

            Object[][] items = {
                    {"Консультация", 1, "$50.00", "$50.00"},
                    {"Парацетамол", 2, "$5.99", "$11.98"},
                    {"Анализ крови", 1, "$35.00", "$35.00"}
            };

            double subtotal = 0.0;
            for (Object[] item : items) {
                table.addCell(new Phrase(item[0].toString(), font));
                table.addCell(new Phrase(item[1].toString(), font));
                table.addCell(new Phrase(item[2].toString(), font));
                table.addCell(new Phrase(item[3].toString(), font));

                subtotal += Double.parseDouble(item[3].toString().replace("$", "").replace(",", "."));
            }

            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(String.format("Промежуточный итог: $%.2f", subtotal), font));
            document.add(new Paragraph(String.format("Итог: $%.2f", subtotal), font));

            document.close();

            File pdfFile = new File(outputPath);
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(pdfFile);

            JOptionPane.showMessageDialog(null, "Счет для Ивана Большакова создан и открыт");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка при создании счета: " + e.getMessage());
        }
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));

        JLabel valueComponent = new JLabel(value != null && !value.isEmpty() ? value : "N/A");
        valueComponent.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));

        panel.add(labelComponent);
        panel.add(valueComponent);
    }
}
