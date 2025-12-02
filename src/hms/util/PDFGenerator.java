package hms.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import hms.model.*;

public class PDFGenerator {

    private static Font createRussianFont(float size, int style) {
        try {
            String fontPath = "/fonts/arialmt.ttf";
            BaseFont baseFont = BaseFont.createFont(
                    PDFGenerator.class.getResource(fontPath).getPath(),
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );
            return new Font(baseFont, size, style);
        } catch (Exception e) {
            try {
                String systemFontPath = "C:/Windows/Fonts/arial.ttf";
                BaseFont baseFont = BaseFont.createFont(
                        systemFontPath,
                        BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED
                );
                return new Font(baseFont, size, style);
            } catch (Exception ex) {
                ex.printStackTrace();
                return new Font(Font.FontFamily.HELVETICA, size, style);
            }
        }
    }

    private static final Font TITLE_FONT = createRussianFont(18, Font.BOLD);
    private static final Font SUBTITLE_FONT = createRussianFont(14, Font.BOLD);
    private static final Font NORMAL_FONT = createRussianFont(12, Font.NORMAL);
    private static final Font SMALL_FONT = createRussianFont(10, Font.NORMAL);

    public static File generatePatientReport(Patient patient, List<MedicalRecord> records, String outputPath) {
        if (patient == null || outputPath == null || outputPath.trim().isEmpty()) {
            return null;
        }

        Document document = new Document();
        File file = new File(outputPath);

        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            addHospitalHeader(document);

            document.add(new Paragraph("ОТЧЕТ ПАЦИЕНТА", TITLE_FONT));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Данные пациента:", SUBTITLE_FONT));
            document.add(new Paragraph("Идентификатор: " + safeString(patient.getId()), NORMAL_FONT));
            document.add(new Paragraph("ФИО: " + safeString(patient.getName()), NORMAL_FONT));
            document.add(new Paragraph("Возраст: " + patient.getAge(), NORMAL_FONT));
            document.add(new Paragraph("Номер телефона: " + safeString(patient.getContact()), NORMAL_FONT));
            document.add(new Paragraph("Электронная почта: " + safeString(patient.getEmail()), NORMAL_FONT));
            document.add(new Paragraph("Адрес: " + safeString(patient.getAddress()), NORMAL_FONT));
            document.add(new Paragraph("Пол: " + safeString(patient.getGender()), NORMAL_FONT));
            document.add(new Paragraph("Болезнь: " + safeString(patient.getDisease()), NORMAL_FONT));
            document.add(new Paragraph("Группа крови: " + safeString(patient.getBloodGroup()), NORMAL_FONT));
            document.add(new Paragraph("Аллергии: " + safeString(patient.getAllergies()), NORMAL_FONT));
            document.add(new Paragraph(" "));

            if (records != null && !records.isEmpty()) {
                document.add(new Paragraph("История болезни:", SUBTITLE_FONT));
                document.add(new Paragraph(" "));

                for (MedicalRecord record : records) {
                    if (record != null) {
                        document.add(new Paragraph("Дата: " + formatDate(record.getRecordDate()), NORMAL_FONT));
                        document.add(new Paragraph("Доктор: " + safeString(record.getDoctorId()), NORMAL_FONT));
                        document.add(new Paragraph("Диагноз: " + safeString(record.getDiagnosis()), NORMAL_FONT));
                        document.add(new Paragraph("Уход: " + safeString(record.getTreatment()), NORMAL_FONT));
                        document.add(new Paragraph("Примечания: " + safeString(record.getNotes()), NORMAL_FONT));
                        document.add(new Paragraph(" "));
                    }
                }
            } else {
                document.add(new Paragraph("Медицинских записей не найдено.", NORMAL_FONT));
            }

            addFooter(document);

            document.close();
            return file;

        } catch (Exception e) {
            e.printStackTrace();
            if (document.isOpen()) {
                document.close();
            }
            return null;
        }
    }

    public static File generateBillingReport(Billing billing, Patient patient, String outputPath) {
        if (billing == null || patient == null || outputPath == null || outputPath.trim().isEmpty()) {
            return null;
        }

        Document document = new Document();
        File file = new File(outputPath);

        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            addHospitalHeader(document);

            document.add(new Paragraph("СЧЕТ", TITLE_FONT));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Счет #: " + safeString(billing.getBillId()), NORMAL_FONT));
            document.add(new Paragraph("Дата: " + formatDate(billing.getBillDate()), NORMAL_FONT));
            document.add(new Paragraph("Статус платежа: " + safeString(billing.getPaymentStatus()), NORMAL_FONT));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Данные пациента:", SUBTITLE_FONT));
            document.add(new Paragraph("Идентификатор: " + safeString(patient.getId()), NORMAL_FONT));
            document.add(new Paragraph("ФИО: " + safeString(patient.getName()), NORMAL_FONT));
            document.add(new Paragraph("Номер телефона: " + safeString(patient.getContact()), NORMAL_FONT));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Платежные реквизиты:", SUBTITLE_FONT));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            addTableHeader(table, new String[]{"Описаение", "Количество", "Цена за единицу", "Количество"});

            double subtotal = 0;
            List<Billing.BillItem> items = billing.getItems();
            if (items != null && !items.isEmpty()) {
                for (Billing.BillItem item : items) {
                    if (item != null) {
                        table.addCell(new Phrase(safeString(item.getDescription()), SMALL_FONT));
                        table.addCell(new Phrase(String.valueOf(item.getQuantity()), SMALL_FONT));
                        table.addCell(new Phrase(String.format("%.2f", item.getUnitPrice()), SMALL_FONT));
                        table.addCell(new Phrase(String.format("%.2f", item.getAmount()), SMALL_FONT));
                        subtotal += item.getAmount();
                    }
                }
            } else {
                table.addCell(new Phrase("Плата за консультацию", SMALL_FONT));
                table.addCell(new Phrase("1", SMALL_FONT));
                table.addCell(new Phrase(String.format("%.2f", billing.getTotalAmount()), SMALL_FONT));
                table.addCell(new Phrase(String.format("%.2f", billing.getTotalAmount()), SMALL_FONT));
                subtotal = billing.getTotalAmount();
            }

            document.add(table);
            document.add(new Paragraph(" "));

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(50);
            summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

            summaryTable.addCell(new Phrase("Итого:", NORMAL_FONT));
            summaryTable.addCell(new Phrase(String.format("%.2f", subtotal), NORMAL_FONT));

            double discountAmount = subtotal * (billing.getDiscount() / 100.0);
            summaryTable.addCell(new Phrase("Скидка (" + billing.getDiscount() + "%):", NORMAL_FONT));
            summaryTable.addCell(new Phrase(String.format("%.2f", discountAmount), NORMAL_FONT));

            double taxAmount = subtotal * (billing.getTax() / 100.0);
            summaryTable.addCell(new Phrase("Налог (" + billing.getTax() + "%):", NORMAL_FONT));
            summaryTable.addCell(new Phrase(String.format("%.2f", taxAmount), NORMAL_FONT));

            PdfPCell totalCell = new PdfPCell(new Phrase("Всего:", SUBTITLE_FONT));
            totalCell.setBorder(0);
            summaryTable.addCell(totalCell);

            PdfPCell totalAmountCell = new PdfPCell(new Phrase(String.format("%.2f", billing.getTotalAmount()), SUBTITLE_FONT));
            totalAmountCell.setBorder(0);
            summaryTable.addCell(totalAmountCell);

            document.add(summaryTable);
            document.add(new Paragraph(" "));

            if (billing.getPaymentMethod() != null && !billing.getPaymentMethod().isEmpty()) {
                document.add(new Paragraph("Способ оплаты: " + billing.getPaymentMethod(), NORMAL_FONT));
            }

            addFooter(document);

            document.close();
            return file;

        } catch (Exception e) {
            e.printStackTrace();
            if (document.isOpen()) {
                document.close();
            }
            return null;
        }
    }

    public static File generatePrescription(Prescription prescription, Patient patient, Doctor doctor, String outputPath) {
        Document document = new Document();
        File file = new File(outputPath);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            addHospitalHeader(document);

            document.add(new Paragraph("РЕЦЕПТ", TITLE_FONT));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Рецепт #: " + prescription.getPrescriptionId(), NORMAL_FONT));
            document.add(new Paragraph("Дата: " + formatDate(prescription.getIssueDate()), NORMAL_FONT));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Данные пациента:", SUBTITLE_FONT));
            document.add(new Paragraph("ФИО: " + patient.getName(), NORMAL_FONT));
            document.add(new Paragraph("Идентификатор: " + patient.getId(), NORMAL_FONT));
            document.add(new Paragraph("Возраст: " + patient.getAge(), NORMAL_FONT));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Данные врача:", SUBTITLE_FONT));
            document.add(new Paragraph("ФИО: " + doctor.getName(), NORMAL_FONT));
            document.add(new Paragraph("Специализация: " + doctor.getSpecialization(), NORMAL_FONT));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Лекарства:", SUBTITLE_FONT));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            addTableHeader(table, new String[]{"Лекарство", "Дозировка", "Частота", "Продолжительность"});

            for (Medicine medicine : prescription.getMedicines()) {
                table.addCell(new Phrase(medicine.getName(), SMALL_FONT));
                table.addCell(new Phrase(medicine.getDosage(), SMALL_FONT));
                table.addCell(new Phrase(medicine.getFrequency(), SMALL_FONT));
                table.addCell(new Phrase(medicine.getDuration(), SMALL_FONT));
            }

            document.add(table);
            document.add(new Paragraph(" "));

            if (prescription.getNotes() != null && !prescription.getNotes().isEmpty()) {
                document.add(new Paragraph("Заметки:", SUBTITLE_FONT));
                document.add(new Paragraph(prescription.getNotes(), NORMAL_FONT));
                document.add(new Paragraph(" "));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("____________________", NORMAL_FONT));
            document.add(new Paragraph("Подпись врача", NORMAL_FONT));

            addFooter(document);

            document.close();
            return file;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void addHospitalHeader(Document document) throws DocumentException {
        Paragraph header = new Paragraph("Око", TITLE_FONT);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        Paragraph address = new Paragraph("ул. Рихарда Зорге, 67к1, Уфа, Респ. Башкортостан", NORMAL_FONT);
        address.setAlignment(Element.ALIGN_CENTER);
        document.add(address);

        Paragraph contact = new Paragraph("Телефон: 8 (347) 293-42-07 | Электронная почта: help@alloplant.ru", NORMAL_FONT);
        contact.setAlignment(Element.ALIGN_CENTER);
        document.add(contact);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("------------------------------------------------------------", NORMAL_FONT));
        document.add(new Paragraph(" "));
    }

    private static void addFooter(Document document) throws DocumentException {
        document.add(new Paragraph(" "));
        document.add(new Paragraph("------------------------------------------------------------", NORMAL_FONT));
        document.add(new Paragraph(" "));

        Paragraph footer = new Paragraph("Это документ, созданный на компьютере. Подпись не требуется.", SMALL_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        Paragraph date = new Paragraph("Сгенерировано на: " + formatDate(new Date()), SMALL_FONT);
        date.setAlignment(Element.ALIGN_CENTER);
        document.add(date);
    }

    private static void addTableHeader(PdfPTable table, String[] headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(safeString(header), SUBTITLE_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }
    }

    private static String formatDate(Date date) {
        if (date == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(date);
    }

    private static String safeString(String str) {
        return str != null && !str.trim().isEmpty() ? str : "N/A";
    }
}
