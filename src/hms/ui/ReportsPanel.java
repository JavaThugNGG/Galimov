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

        parametersPanel = new JPanel();
        parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.Y_AXIS));
        parametersPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        generateButton = new JButton("Создать отчет");
        buttonPanel.add(generateButton);

        add(topPanel, BorderLayout.NORTH);
        add(parametersPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        reportTypeComboBox.addActionListener(e -> updateParametersPanel());
        generateButton.addActionListener(e -> generateReport());

        updateParametersPanel();
    }

    private void updateParametersPanel() {
        parametersPanel.removeAll();

        String reportType = (String) reportTypeComboBox.getSelectedItem();

        JLabel titleLabel = new JLabel(reportType);
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
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

        JLabel descriptionLabel = new JLabel("Описаение:");
        descriptionLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descriptionArea = new JTextArea(getReportDescription(reportType));
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(getBackground());
        descriptionArea.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
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

        doctorComboBox.addItem("D001 - Иванов Сергей Петрович");
        doctorComboBox.addItem("D002 - Петрова Анна Викторовна");
        doctorComboBox.addItem("D003 - Сидоров Дмитрий Олегович");

        JLabel dateRangeLabel = new JLabel("Диапазон дат:");
        JPanel dateRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JTextField startDateField = new JTextField(10);
        startDateField.setName("startDateField");
        startDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        JLabel toLabel = new JLabel(" для ");

        JTextField endDateField = new JTextField(10);
        endDateField.setName("endDateField");

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
                    printPatientReport();
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

    public void printPatientReport() {
        try {
            String patientName = "Большаков Иван Дмитриевич";
            Document document = new Document();
            File outputDir = new File("reports");
            if (!outputDir.exists()) outputDir.mkdirs();
            String outputPath = "reports/patient_report_" + patientName.replace(" ", "_") + ".pdf";

            PdfWriter.getInstance(document, new java.io.FileOutputStream(outputPath));
            document.open();

            BaseFont bf = BaseFont.createFont("C:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(bf, 16, Font.BOLD);
            Font sectionFont = new Font(bf, 14, Font.BOLD);
            Font normalFont = new Font(bf, 12);

            Paragraph title = new Paragraph("Отчёт по пациенту: " + patientName, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            JCheckBox medicalRecordsCheckbox = findComponentByName(parametersPanel, "medicalRecordsCheckbox");
            JCheckBox appointmentsCheckbox = findComponentByName(parametersPanel, "appointmentsCheckbox");
            JCheckBox billingsCheckbox = findComponentByName(parametersPanel, "billingsCheckbox");

            if (medicalRecordsCheckbox != null && medicalRecordsCheckbox.isSelected()) {
                Paragraph medRecordsTitle = new Paragraph("Медицинские записи", sectionFont);
                document.add(medRecordsTitle);
                document.add(new Paragraph(" "));

                PdfPTable medTable = new PdfPTable(3);
                medTable.setWidthPercentage(100);
                medTable.setWidths(new float[]{2, 3, 5});
                medTable.addCell(new Phrase("Дата", normalFont));
                medTable.addCell(new Phrase("Врач", normalFont));
                medTable.addCell(new Phrase("Описание", normalFont));

                Object[][] medRecords = {
                        {"2025-06-01", "Иванов И.И.", "Общий осмотр, назначены анализы крови"},
                        {"2025-06-05", "Смирнова А.А.", "Повторный осмотр, результаты анализов в норме"}
                };

                for (Object[] record : medRecords) {
                    medTable.addCell(new Phrase(record[0].toString(), normalFont));
                    medTable.addCell(new Phrase(record[1].toString(), normalFont));
                    medTable.addCell(new Phrase(record[2].toString(), normalFont));
                }

                document.add(medTable);
                document.add(new Paragraph(" "));
            }

            if (appointmentsCheckbox != null && appointmentsCheckbox.isSelected()) {
                Paragraph prescriptionsTitle = new Paragraph("Назначения", sectionFont);
                document.add(prescriptionsTitle);
                document.add(new Paragraph(" "));

                PdfPTable prescTable = new PdfPTable(3);
                prescTable.setWidthPercentage(100);
                prescTable.setWidths(new float[]{3, 2, 3});
                prescTable.addCell(new Phrase("Дата", normalFont));
                prescTable.addCell(new Phrase("Название лекарства", normalFont));
                prescTable.addCell(new Phrase("Дозировка/Инструкции", normalFont));

                Object[][] prescriptions = {
                        {"2025-06-01", "Парацетамол", "500мг, 2 раза в день"},
                        {"2025-06-01", "Витамин C", "1 таблетка в день"}
                };

                for (Object[] presc : prescriptions) {
                    prescTable.addCell(new Phrase(presc[0].toString(), normalFont));
                    prescTable.addCell(new Phrase(presc[1].toString(), normalFont));
                    prescTable.addCell(new Phrase(presc[2].toString(), normalFont));
                }

                document.add(prescTable);
                document.add(new Paragraph(" "));
            }

            if (billingsCheckbox != null && billingsCheckbox.isSelected()) {
                Paragraph billsTitle = new Paragraph("Счета", sectionFont);
                document.add(billsTitle);
                document.add(new Paragraph(" "));

                PdfPTable billsTable = new PdfPTable(4);
                billsTable.setWidthPercentage(100);
                billsTable.setWidths(new float[]{2, 2, 2, 2});
                billsTable.addCell(new Phrase("Номер счета", normalFont));
                billsTable.addCell(new Phrase("Дата", normalFont));
                billsTable.addCell(new Phrase("Сумма", normalFont));
                billsTable.addCell(new Phrase("Статус", normalFont));

                Object[][] bills = {
                        {"B001", "2025-06-01", "$150.00", "ОПЛАЧЕН"},
                        {"B011", "2025-06-02", "$200.00", "ОПЛАЧЕН"},
                        {"B013", "2025-06-04", "$170.25", "ОПЛАЧЕН"}
                };

                for (Object[] bill : bills) {
                    billsTable.addCell(new Phrase(bill[0].toString(), normalFont));
                    billsTable.addCell(new Phrase(bill[1].toString(), normalFont));
                    billsTable.addCell(new Phrase(bill[2].toString(), normalFont));
                    billsTable.addCell(new Phrase(bill[3].toString(), normalFont));
                }

                document.add(billsTable);
            }

            document.close();

            File pdfFile = new File(outputPath);
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(pdfFile);

            JOptionPane.showMessageDialog(null, "Отчёт по пациенту " + patientName + " создан и открыт");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка при создании отчёта: " + e.getMessage());
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