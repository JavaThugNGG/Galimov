package hms.interfaces;

import java.io.File;

public interface ReportGenerator<T> {
    String generateReport(T data);

    boolean generateAndSaveReport(T data, String outputPath);

    File generatePdfReport(T data, String outputPath);
}
