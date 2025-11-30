package hms.ui;

import hms.model.User;
import hms.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private UserService userService;

    public LoginFrame() {
        userService = new UserService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Медицинская информационная система — Вход");
        setSize(600, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(500, 100));
        headerPanel.setLayout(new BorderLayout());

        JPanel logoTitlePanel = new JPanel();
        logoTitlePanel.setLayout(new BoxLayout(logoTitlePanel, BoxLayout.Y_AXIS));
        logoTitlePanel.setBackground(new Color(70, 130, 180));

        ImageIcon logoIcon = loadLogo();
        if (logoIcon != null) {
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoTitlePanel.add(logoLabel);
            logoTitlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        JLabel titleLabel = new JLabel("Око");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoTitlePanel.add(titleLabel);

        headerPanel.add(logoTitlePanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBackground(new Color(240, 248, 255));

        JLabel usernameLabel = new JLabel("Имя пользователя:");
        usernameLabel.setBounds(50, 30, 150, 25);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(200, 30, 200, 25);
        formPanel.add(usernameField);

        JLabel passwordLabel = new JLabel("Пароль:");
        passwordLabel.setBounds(50, 70, 100, 25);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(200, 70, 200, 25);
        formPanel.add(passwordField);

        loginButton = new JButton("Вход");
        loginButton.setBounds(200, 120, 100, 30);
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        formPanel.add(loginButton);

        exitButton = new JButton("Выход");
        exitButton.setBounds(310, 120, 90, 30);
        exitButton.setBackground(new Color(220, 20, 60));
        exitButton.setForeground(Color.BLACK);
        exitButton.setFocusPainted(false);
        formPanel.add(exitButton);

        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(70, 130, 180));
        footerPanel.setPreferredSize(new Dimension(500, 40));

        JLabel footerLabel = new JLabel("© 2025 Око");
        footerLabel.setForeground(Color.WHITE);
        footerPanel.add(footerLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login();
                }
            }
        });

        setVisible(true);
    }

    private ImageIcon loadLogo() {
        String[] possiblePaths = {
                "logo.png",
                "./logo.png",
                "src/logo.png",
                "resources/logo.png",
                "images/logo.png",
                System.getProperty("user.dir") + "/logo.png"
        };

        for (String path : possiblePaths) {
            try {
                File logoFile = new File(path);
                System.out.println("Пробуем путь к логотипу: " + logoFile.getAbsolutePath());
                System.out.println("Файл существует: " + logoFile.exists());

                if (logoFile.exists()) {
                    ImageIcon originalIcon = new ImageIcon(path);
                    if (originalIcon.getIconWidth() > 0) {
                        Image img = originalIcon.getImage();
                        Image scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                        System.out.println("Логотип успешно загружен с: " + path);
                        return new ImageIcon(scaledImg);
                    }
                }
            } catch (Exception e) {
                System.out.println("Не удалось загрузить логотип из " + path + ": " + e.getMessage());
            }
        }

        try {
            java.net.URL logoURL = getClass().getClassLoader().getResource("logo.png");
            if (logoURL != null) {
                ImageIcon originalIcon = new ImageIcon(logoURL);
                Image img = originalIcon.getImage();
                Image scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                System.out.println("Логотип загружен из classpath");
                return new ImageIcon(scaledImg);
            }
        } catch (Exception e) {
            System.out.println("Не удалось загрузить логотип из classpath: " + e.getMessage());
        }

        System.out.println("Логотип не найден ни в одном месте. Текущий рабочий каталог.: " + System.getProperty("user.dir"));
        return null;
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Имя пользователя и пароль не могут быть пустыми",
                    "Ошибка входа",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userService.authenticate(username, password);

        if (user != null) {
            new DashboardFrame(user);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Неверное имя пользователя или пароль",
                    "Ошибка входа",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame();
            }
        });
    }
}
