package MJHospital;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Login extends JFrame implements ActionListener {
    JTextField loginField, passwdField;
    public Login() {
        setTitle("로그인");
        setSize(800,800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BorderLayout());
        logoPanel.setBackground(Color.LIGHT_GRAY);

        JLabel hospitalLabel = new JLabel("MJ HOSPITAL", new ImageIcon("src/img/mjicon.png"), JLabel.LEFT);
        hospitalLabel.setFont(new Font("Serif", Font.BOLD, 30));
        logoPanel.add(hospitalLabel, BorderLayout.WEST);

        add(logoPanel, BorderLayout.NORTH);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);

        JLabel imgLabel = new JLabel(new ImageIcon("src/img/mjicon.png"), JLabel.CENTER);
        JLabel titleLabel = new JLabel("MJ HOSPITAL", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        JLabel loginLabel = new JLabel("Login");
        loginField = new JTextField(20);
        JLabel passwdLabel = new JLabel("Password");
        passwdField = new JPasswordField(20);
        JButton loginButton = new JButton("로그인");

        imgLabel.setBounds(250,150,300,100);
        titleLabel.setBounds(250,250,300,100);
        loginLabel.setBounds(250, 500, 100, 30);
        loginField.setBounds(350, 500, 200, 30);
        passwdLabel.setBounds(250, 550, 100, 30);
        passwdField.setBounds(350, 550, 200, 30);
        loginButton.setBounds(250, 600, 140, 30);

        passwdField.addActionListener(this);
        loginButton.addActionListener(this);

        loginPanel.add(imgLabel);
        loginPanel.add(titleLabel);
        loginPanel.add(loginLabel);
        loginPanel.add(loginField);
        loginPanel.add(passwdLabel);
        loginPanel.add(passwdField);
        loginPanel.add(loginButton);

        add(loginPanel, BorderLayout.CENTER);
    }

    public void checkLogin(String id, String pass) {
        if (id.equals("doctor") && pass.equals("1234")) {
            new HospitalUI().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(null, "로그인 실패");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        checkLogin(loginField.getText(), passwdField.getText());
    }
}
