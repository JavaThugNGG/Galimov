package hms.ui;

import hms.model.Patient;
import hms.service.PatientService;
import hms.util.PDFGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportsPanel extends JPanel {
    private JComboBox<String> reportTypeComboBox;
    private JPanel parametersPanel;
    private JButton generateButton;

    private PatientService patientService;

    public ReportsPanel() {
        patientService = new PatientService();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create top panel with report type selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel reportTypeLabel = new JLabel("Тип отчета:");
        reportTypeComboBox = new JComboBox<>(new String[]{
                "Отчет пациента",
                "Расписание врачей",
                "Описание назначения",
                "Сводка по счетам",
                "Состояние запасов",
                "Отчет о доходах"
        });

        topPanel.add(reportTypeLabel);
        topPanel.add(reportTypeComboBox);

        // Create parameters panel
        parametersPanel = new JPanel();
        parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.Y_AXIS));
        parametersPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        generateButton = new JButton("Создать отчет");
        buttonPanel.add(generateButton);

        // Add components to panel
        add(topPanel, BorderLayout.NORTH);
        add(parametersPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        reportTypeComboBox.addActionListener(e -> updateParametersPanel());
        generateButton.addActionListener(e -> generateReport());

        // Initialize parameters panel
        updateParametersPanel();
    }

    private void updateParametersPanel() {
        parametersPanel.removeAll();

        String reportType = (String) reportTypeComboBox.getSelectedItem();

        JLabel titleLabel = new JLabel(reportType);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        parametersPanel.add(titleLabel);
        parametersPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        switch (reportType) {
            case "Отчет пациента":
                setupPatientReportParameters(formPanel);
                break;
            case "Расписание врачей":
                setupDoctorScheduleParameters(formPanel);
                break;
            case "Описание назначения":
                setupAppointmentSummaryParameters(formPanel);
                break;
            case "Сводка по счетам":
                setupBillingSummaryParameters(formPanel);
                break;
            case "Состояние запасов":
                setupInventoryStatusParameters(formPanel);
                break;
            case "Отчет о доходах":
                setupRevenueReportParameters(formPanel);
                break;
        }

        parametersPanel.add(formPanel);

        // Add description
        JLabel descriptionLabel = new JLabel("Описаение:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descriptionArea = new JTextArea(getReportDescription(reportType));
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(getBackground());
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        parametersPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        parametersPanel.add(descriptionLabel);
        parametersPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        parametersPanel.add(descriptionArea);

        parametersPanel.revalidate();
        parametersPanel.repaint();
    }

    private void setupPatientReportParameters(JPanel formPanel) {
        JLabel patientLabel = new JLabel("Пациент:");
        JComboBox<String> patientComboBox = new JComboBox<>();
        patientComboBox.setName("patientComboBox");

        // Load patients
        java.util.List<Patient> patients = patientService.getAll();
        for (Patient patient : patients) {
            patientComboBox.addItem(patient.getId() + " - " + patient.getName());
        }

        JLabel includeLabel = new JLabel("Включает:");
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JCheckBox medicalRecordsCheckbox = new JCheckBox("Медицинские записи");
        medicalRecordsCheckbox.setName("medicalRecordsCheckbox");
        medicalRecordsCheckbox.setSelected(true);

        JCheckBox appointmentsCheckbox = new JCheckBox("Назначения");
        appointmentsCheckbox.setName("appointmentsCheckbox");
        appointmentsCheckbox.setSelected(true);

        JCheckBox billingsCheckbox = new JCheckBox("Счета");
        billingsCheckbox.setName("billingsCheckbox");
        billingsCheckbox.setSelected(true);

        checkboxPanel.add(medicalRecordsCheckbox);
        checkboxPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        checkboxPanel.add(appointmentsCheckbox);
        checkboxPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        checkboxPanel.add(billingsCheckbox);

        formPanel.add(patientLabel);
        formPanel.add(patientComboBox);
        formPanel.add(includeLabel);
        formPanel.add(checkboxPanel);
    }

    private void setupDoctorScheduleParameters(JPanel formPanel) {
        JLabel doctorLabel = new JLabel("Доктор:");
        JComboBox<String> doctorComboBox = new JComboBox<>();
        doctorComboBox.setName("doctorComboBox");

        // Add sample doctors
        doctorComboBox.addItem("D001 - Dr. Smith");
        doctorComboBox.addItem("D002 - Dr. Johnson");
        doctorComboBox.addItem("D003 - Dr. Williams");

        JLabel dateRangeLabel = new JLabel("Диапазон дат:");
        JPanel dateRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JTextField startDateField = new JTextField(10);
        startDateField.setName("startDateField");
        startDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        JLabel toLabel = new JLabel(" для ");

        JTextField endDateField = new JTextField(10);
        endDateField.setName("endDateField");
        // Set end date to 7 days from now
        Date endDate = new Date();
        endDate.setTime(endDate.getTime() + 7 * 24 * 60 * 60 * 1000);
        endDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(endDate));

        dateRangePanel.add(startDateField);
        dateRangePanel.add(toLabel);
        dateRangePanel.add(endDateField);

        formPanel.add(doctorLabel);
        formPanel.add(doctorComboBox);
        formPanel.add(dateRangeLabel);
        formPanel.add(dateRangePanel);
    }

    private void setupAppointmentSummaryParameters(JPanel formPanel) {
        JLabel dateRangeLabel = new JLabel("Диапазон дат:");
        JPanel dateRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JTextField startDateField = new JTextField(10);
        startDateField.setName("startDateField");
        // Set start date to 30 days ago
        Date startDate = new Date();
        startDate.setTime(startDate.getTime() - 30 * 24 * 60 * 60 * 1000);
        startDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(startDate));

        JLabel toLabel = new JLabel(" для ");

        JTextField endDateField = new JTextField(10);
        endDateField.setName("endDateField");
        endDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        dateRangePanel.add(startDateField);
        dateRangePanel.add(toLabel);
        dateRangePanel.add(endDateField);

        JLabel statusLabel = new JLabel("Статус:");
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{
                "Все", "ЗАПЛАНИРОВАНО", "ЗАВЕРШЕНО", "ОТМЕНЕНО"
        });
        statusComboBox.setName("statusComboBox");

        JLabel groupByLabel = new JLabel("Группировать по:");
        JComboBox<String> groupByComboBox = new JComboBox<>(new String[]{
                "Дата", "Доктор", "Статус"
        });
        groupByComboBox.setName("groupByComboBox");

        formPanel.add(dateRangeLabel);
        formPanel.add(dateRangePanel);
        formPanel.add(statusLabel);
        formPanel.add(statusComboBox);
        formPanel.add(groupByLabel);
        formPanel.add(groupByComboBox);
    }

    private void setupBillingSummaryParameters(JPanel formPanel) {
        JLabel dateRangeLabel = new JLabel("Диапазон дат:");
        JPanel dateRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JTextField startDateField = new JTextField(10);
        startDateField.setName("startDateField");
        // Set start date to 30 days ago
        Date startDate = new Date();
        startDate.setTime(startDate.getTime() - 30 * 24 * 60 * 60 * 1000);
        startDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(startDate));

        JLabel toLabel = new JLabel(" to ");

        JTextField endDateField = new JTextField(10);
        endDateField.setName("endDateField");
        endDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        dateRangePanel.add(startDateField);
        dateRangePanel.add(toLabel);
        dateRangePanel.add(endDateField);

        JLabel statusLabel = new JLabel("Статус оплаты:");
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{
                "Все", "ОПЛАЧЕНО", "НЕ ОПЛАЧЕНО"
        });
        statusComboBox.setName("statusComboBox");

        JLabel groupByLabel = new JLabel("Группировать по:");
        JComboBox<String> groupByComboBox = new JComboBox<>(new String[]{
                "Дата", "Пациент", "Статус"
        });
        groupByComboBox.setName("groupByComboBox");

        formPanel.add(dateRangeLabel);
        formPanel.add(dateRangePanel);
        formPanel.add(statusLabel);
        formPanel.add(statusComboBox);
        formPanel.add(groupByLabel);
        formPanel.add(groupByComboBox);
    }

    private void setupInventoryStatusParameters(JPanel formPanel) {
        JLabel categoryLabel = new JLabel("Категория:");
        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{
                "Все", "Облегчение боли", "Антибиотики", "Антигистаминный", "Сердечно-сосудистые",
                "Желудочно-кишечный", "Респираторный", "Дерматологический", "Другое"
        });
        categoryComboBox.setName("categoryComboBox");

        JLabel stockStatusLabel = new JLabel("Состояние запасов:");
        JComboBox<String> stockStatusComboBox = new JComboBox<>(new String[]{
                "Все", "В наличии", "Низкий запас", "Распродано"
        });
        stockStatusComboBox.setName("stockStatusComboBox");

        JLabel sortByLabel = new JLabel("Сортировать по:");
        JComboBox<String> sortByComboBox = new JComboBox<>(new String[]{
                "Название", "Категория", "Количество", "Цена"
        });
        sortByComboBox.setName("sortByComboBox");

        formPanel.add(categoryLabel);
        formPanel.add(categoryComboBox);
        formPanel.add(stockStatusLabel);
        formPanel.add(stockStatusComboBox);
        formPanel.add(sortByLabel);
        formPanel.add(sortByComboBox);
    }

    private void setupRevenueReportParameters(JPanel formPanel) {
        JLabel dateRangeLabel = new JLabel("Диапазон дат:");
        JPanel dateRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JTextField startDateField = new JTextField(10);
        startDateField.setName("startDateField");
        // Set start date to first day of current month
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentMonth = sdf.format(now).substring(0, 8) + "01";
        startDateField.setText(currentMonth);

        JLabel toLabel = new JLabel(" для ");

        JTextField endDateField = new JTextField(10);
        endDateField.setName("endDateField");
        endDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(now));

        dateRangePanel.add(startDateField);
        dateRangePanel.add(toLabel);
        dateRangePanel.add(endDateField);

        JLabel groupByLabel = new JLabel("Группировать по:");
        JComboBox<String> groupByComboBox = new JComboBox<>(new String[]{
                "День", "Неделя", "Месяц"
        });
        groupByComboBox.setName("groupByComboBox");

        JLabel includeLabel = new JLabel("Включает:");
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JCheckBox consultationsCheckbox = new JCheckBox("Консультации");
        consultationsCheckbox.setName("consultationsCheckbox");
        consultationsCheckbox.setSelected(true);

        JCheckBox medicinesCheckbox = new JCheckBox("Лекарства");
        medicinesCheckbox.setName("medicinesCheckbox");
        medicinesCheckbox.setSelected(true);

        JCheckBox testsCheckbox = new JCheckBox("Тесты");
        testsCheckbox.setName("testsCheckbox");
        testsCheckbox.setSelected(true);

        checkboxPanel.add(consultationsCheckbox);
        checkboxPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        checkboxPanel.add(medicinesCheckbox);
        checkboxPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        checkboxPanel.add(testsCheckbox);

        formPanel.add(dateRangeLabel);
        formPanel.add(dateRangePanel);
        formPanel.add(groupByLabel);
        formPanel.add(groupByComboBox);
        formPanel.add(includeLabel);
        formPanel.add(checkboxPanel);
    }

    private String getReportDescription(String reportType) {
        switch (reportType) {
            case "Отчет пациента":
                return "Создает подробный отчет по конкретному пациенту, включая персональные данные, историю болезни, назначения и платежную информацию.";
            case "Расписание врачей":
                return "Отображает расписание приема конкретного врача на указанный диапазон дат, включая все приемы, доступные интервалы и часы работы.";
            case "Описание назначения":
                return "Предоставляет сводку всех встреч в указанном диапазоне дат с возможностью фильтрации по статусу и группировки по различным критериям.";
            case "Сводка по счетам":
                return "Формирует сводку всех платежных операций за указанный диапазон дат с возможностью фильтрации по статусу платежа и группировки по различным критериям.";
            case "Состояние запасов":
                return "Отображает текущий статус запасов медикаментов с возможностью фильтрации по категории и состоянию запасов, а также сортировки по различным критериям.";
            case "Отчет о доходах":
                return "Предоставляет подробный анализ доходов, полученных за указанный диапазон дат, с возможностью группировки по периодам времени и включения различных источников доходов.";
            default:
                return "";
        }
    }

    private void generateReport() {
        String reportType = (String) reportTypeComboBox.getSelectedItem();

        try {
            switch (reportType) {
                case "Отчет пациента":
                    generatePatientReport();
                    break;
                case "Расписание врачей":
                case "Описание назначения":
                case "Сводка по счетам":
                case "Состояние запасов":
                case "Отчет о доходах":
                    JOptionPane.showMessageDialog(this,
                            reportType + " генерация будет реализована в будущем",
                            "Информация",
                            JOptionPane.INFORMATION_MESSAGE);
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка создания отчета: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void generatePatientReport() {
        // Get selected patient
        JComboBox<?> patientComboBox = findComponentByName(parametersPanel, "patientComboBox");
        if (patientComboBox == null || patientComboBox.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Выберите пациента", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String patientInfo = (String) patientComboBox.getSelectedItem();
        String patientId = patientInfo.substring(0, patientInfo.indexOf(" - "));

        // Get patient object
        Patient patient = patientService.getById(patientId);
        if (patient == null) {
            JOptionPane.showMessageDialog(this, "Пациент не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String outputPath = "reports/patient_" + patient.getId() + ".pdf";
            File file = PDFGenerator.generatePatientReport(patient, null, outputPath);

            if (file != null && file.exists()) {
                int option = JOptionPane.showConfirmDialog(
                        this,
                        "Отчёт о пациенте успешно сформирован. Хотите его открыть??",
                        "Отчет создан",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE
                );

                if (option == JOptionPane.YES_OPTION) {
                    // Open the PDF file with the default system viewer
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(file);
                    } else {
                        JOptionPane.showMessageDialog(this, "Не удаётся автоматически открыть PDF-файл. Файл сохранён в: " + file.getAbsolutePath(), "Information", JOptionPane.INFORMATION_MESSAGE);
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

    private <T extends Component> T findComponentByName(Container container, String name) {
        for (Component component : container.getComponents()) {
            if (name.equals(component.getName())) {
                return (T) component;
            }

            if (component instanceof Container) {
                T found = findComponentByName((Container) component, name);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }
}