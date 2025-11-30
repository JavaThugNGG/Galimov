package hms;

import hms.ui.LoginFrame;

import javax.swing.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        createDataDirectories();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Не удалось установить внешний вид системы: " + e.getMessage());
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new LoginFrame();
                } catch (Exception e) {
                    System.err.println("Ошибка запуска приложения: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "Ошибка запуска приложения: " + e.getMessage(),
                            "Ошибка приложения",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private static void createDataDirectories() {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                boolean created = dataDir.mkdirs();
                if (created) {
                    System.out.println("Созданный каталог данных: " + dataDir.getAbsolutePath());
                } else {
                    System.err.println("Не удалось создать каталог данных: " + dataDir.getAbsolutePath());
                }
            }

            File reportsDir = new File("reports");
            if (!reportsDir.exists()) {
                boolean created = reportsDir.mkdirs();
                if (created) {
                    System.out.println("Созданный каталог отчетов: " + reportsDir.getAbsolutePath());
                } else {
                    System.err.println("Не удалось создать каталог отчетов: " + reportsDir.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка создания каталогов: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
