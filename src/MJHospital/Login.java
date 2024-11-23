package MJHospital;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Login extends JFrame implements ActionListener {
    JLabel idLabel, passwordLabel, nameLabel, logoLabel, minLogoLabel;
    JTextField idField;
    JPasswordField passwordField;
    JButton loginButton, cancelButton;
    JPanel topPanel, centerPanel, buttonPanel;
    ImageIcon logoIcon, minLogoIcon;

    Login() {
        setTitle("Login");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(200, 200, 200));

        topPanel = new JPanel();
        topPanel.setBackground(new Color(150, 150, 150));
        topPanel.setLayout(null);
        topPanel.setBounds(0, 20, 600, 80);

        minLogoIcon = new ImageIcon("images/mjicon.png");
        minLogoLabel = new JLabel(minLogoIcon);
        minLogoLabel.setBounds(5, 0, 60, 80);
        topPanel.add(minLogoLabel);

        nameLabel = new JLabel("MJ Hospital");
        nameLabel.setFont(new Font("Serif", Font.BOLD, 24));
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setBounds(80, 20, 200, 40);
        topPanel.add(nameLabel);
        add(topPanel);

        centerPanel = new JPanel();
        centerPanel.setBackground(new Color(200, 200, 200));
        centerPanel.setLayout(null);
        centerPanel.setBounds(0, 120, 600, 500);
        add(centerPanel);

        logoIcon = new ImageIcon("images/logo.png");
        logoLabel = new JLabel(logoIcon);
        logoLabel.setBounds(175, 20, 250, 200);
        centerPanel.add(logoLabel);


        idLabel = new JLabel("ID: ");
        idLabel.setBounds(150, 250, 80, 25);
        centerPanel.add(idLabel);

        idField = new JTextField();
        idField.setBounds(240, 250, 200, 25);
        centerPanel.add(idField);

        passwordLabel = new JLabel("PASSWORD:");
        passwordLabel.setBounds(150, 290, 80, 25);
        centerPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(240, 290, 200, 25);
        centerPanel.add(passwordField);

        buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(200, 200, 200));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBounds(150, 330, 300, 40);
        centerPanel.add(buttonPanel);

        loginButton = new JButton("Login");
        buttonPanel.add(loginButton);

        cancelButton = new JButton("Cancel");
        buttonPanel.add(cancelButton);

        loginButton.addActionListener(this);

        cancelButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("Login")) {
            HospitalUI uiFrame = new HospitalUI();
            uiFrame.setVisible(true);
            this.dispose();
        }
        else if (s.equals("Cancel")) {
            idField.setText("");
            passwordField.setText("");
        }
    }
}