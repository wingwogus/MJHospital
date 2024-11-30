package MJHospital;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

class Login extends JFrame implements ActionListener {
    JLabel idLabel, passwordLabel, nameLabel, logoLabel, minLogoLabel;
    JTextField idField;
    JPasswordField passwordField;
    JButton loginButton, cancelButton;
    JPanel topPanel, centerPanel, buttonPanel;
    ImageIcon logoIcon, minLogoIcon;
    Connection connection;
    Statement statement;
    ResultSet resultSet;

    public Login() {
        connectToDatabase();
        setTitle("Login");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(200, 200, 200));

        //탑 패널 생성
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

        //중앙 패널 생성
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

        //버튼 패널 생성
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
        idField.addActionListener(this);
        passwordField.addActionListener(this);

        addWindowListener(new WindowAdapter() { //창 종료시 커넥션 클로즈
            @Override
            public void windowClosing(WindowEvent e) {
                if (connection != null) {
                    try {
                        connection.close();
                        JOptionPane.showMessageDialog(Login.this, "데이터베이스 연결 해제", "성공", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    //DB 연결
    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://hyunsql.cjwqee8gsrhn.ap-southeast-2.rds.amazonaws.com:3306/mjhospital",
                    "hyeni", "0705"
            );
            statement = connection.createStatement();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB 연결 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //엔터 or 로그인버튼, 취소버튼 클릭 시 이벤트
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton || e.getSource() == idField || e.getSource() == passwordField) {
            check();
        }
        else if (e.getSource() == cancelButton) {
            idField.setText("");
            passwordField.setText("");
        }
    }

    //엔터 or 로그인 버튼 클릭 시 아이디 비밀번호 체크
    public void check() {
        String id = idField.getText();
        String password = passwordField.getText();
        String idQuery = "SELECT s.password, s.is_active, s.name, r.rolename " +
                "FROM staff s INNER JOIN role r ON s.roleid = r.roleid " +
                "WHERE staffid = '" + id + "'";

        try {
            resultSet = statement.executeQuery(idQuery);
            if (resultSet.next()) {
                int isActive = resultSet.getInt("is_active");
                String storedPassword = resultSet.getString("password");
                //활성화 여부 확인
                if (isActive != 0) {
                    //비밀번호 확인
                    if (password.equals(storedPassword)) {
                        //이름과 직급 저장 후 HospitalUI 클래스 열기
                        String name = resultSet.getString("name");
                        String role = resultSet.getString("rolename");
                        statement.close();
                        resultSet.close();
                        HospitalUI uiFrame = new HospitalUI(id, name, role, connection);
                        uiFrame.setVisible(true);
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다",
                                "Message", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "비활성화된 계정입니다. 관리자에게 문의하세요",
                            "Message", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "사용자를 찾을 수 없습니다",
                        "Message", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "알 수 없는 오류가 발생하였습니다.");
            e.printStackTrace();
        }

    }
}