package MJHospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

class PatientManagement extends JPanel {
    Connection conn;
    Statement st;
    PatientListPanel patientListPanel;
    PatientConditionPanel patientConditionPanel;
    PatientDetailsPanel patientDetailsPanel;

    public PatientManagement() {
        setLayout(new BorderLayout());
        String url = "jdbc:mysql://hyunsql.cjwqee8gsrhn.ap-southeast-2.rds.amazonaws.com:3306/mjhospital";
        String userName = "ljh";
        String password = "1234";
        try {
            conn = DriverManager.getConnection(url, userName, password);
            st = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        patientDetailsPanel = new PatientDetailsPanel(conn, st);
        patientListPanel = new PatientListPanel(patientDetailsPanel);
        patientConditionPanel = new PatientConditionPanel(patientListPanel, conn, st);

        patientDetailsPanel.setPatientListPanel(patientListPanel);

        // 환자 조건 패널 생성
        add(patientConditionPanel, BorderLayout.NORTH);

        // 환자 목록 패널 생성
        add(patientListPanel, BorderLayout.WEST);

        // 환자 상세 정보 패널 생성
        add(patientDetailsPanel, BorderLayout.CENTER);

    }
}

class PatientConditionPanel extends JPanel implements ActionListener {
    Connection conn;
    Statement st;
    PatientListPanel patientListPanel;
    JTextField nameField, idField, genderField, phoneField, bloodTypeField, addressField, heightField, weightField;
    public PatientConditionPanel(PatientListPanel patientListPanel, Connection conn, Statement st) {
        this.patientListPanel = patientListPanel;
        this.conn = conn;
        this.st = st;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("환자 조건"));

        // 환자 정보 입력 패널
        JPanel patientInfoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // 첫 번째 열 (이름, 주민번호, 성별)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        patientInfoPanel.add(new JLabel("이름:"), gbc);

        gbc.gridy++;
        patientInfoPanel.add(new JLabel("주민번호:"), gbc);

        gbc.gridy++;
        patientInfoPanel.add(new JLabel("성별:"), gbc);

        // 두 번째 열 (이름 입력 필드, 주민번호 입력 필드, 성별 입력 필드)
        gbc.gridx = 1;
        gbc.gridy = 0;
        nameField = new JTextField(10);
        patientInfoPanel.add(nameField, gbc);

        gbc.gridy++;
        idField = new JTextField(10);
        patientInfoPanel.add(idField, gbc);

        gbc.gridy++;
        genderField = new JTextField(10);
        patientInfoPanel.add(genderField, gbc);

        // 세 번째 열 (전화번호, 혈액형, 주소)
        gbc.gridx = 2;
        gbc.gridy = 0;
        patientInfoPanel.add(new JLabel("전화번호:"), gbc);

        gbc.gridy++;
        patientInfoPanel.add(new JLabel("혈액형:"), gbc);

        gbc.gridy++;
        patientInfoPanel.add(new JLabel("주소:"), gbc);

        // 네 번째 열 (전화번호 입력 필드, 혈액형 입력 필드, 주소 입력 필드)
        gbc.gridx = 3;
        gbc.gridy = 0;
        phoneField = new JTextField(10);
        patientInfoPanel.add(phoneField, gbc);

        gbc.gridy++;
        bloodTypeField = new JTextField(10);
        patientInfoPanel.add(bloodTypeField, gbc);

        gbc.gridy++;
        addressField = new JTextField(10);
        patientInfoPanel.add(addressField, gbc);

        // 다섯 번째 열 (키, 몸무게, 추가 버튼)
        gbc.gridx = 4;
        gbc.gridy = 0;
        patientInfoPanel.add(new JLabel("키:"), gbc);

        gbc.gridy++;
        patientInfoPanel.add(new JLabel("몸무게:"), gbc);

        gbc.gridy++;
        gbc.gridwidth = 2; // 추가 버튼을 두 칸으로 확장
        gbc.fill = GridBagConstraints.BOTH;
        JButton addButton = new JButton("추가");
        addButton.addActionListener(this);
        patientInfoPanel.add(addButton, gbc);

        // 여섯 번째 열 (키 입력 필드, 몸무게 입력 필드)
        gbc.gridx = 5;
        gbc.gridy = 0;
        heightField = new JTextField(10);
        patientInfoPanel.add(heightField, gbc);

        gbc.gridy++;
        weightField = new JTextField(10);
        patientInfoPanel.add(weightField, gbc);

        // 여섯 번째 열 (검색 버튼) - 검색 버튼이 다른 요소와 겹치지 않도록 설정
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.gridheight = 3; // 검색 버튼이 세 줄을 차지하도록 설정
        gbc.fill = GridBagConstraints.VERTICAL; // 버튼의 높이가 세 줄을 차지하도록 설정
        JButton searchButton = new JButton("검색");
        searchButton.addActionListener(this);
        patientInfoPanel.add(searchButton, gbc);

        add(patientInfoPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("검색")) {
            String name = nameField.getText();
            String id = idField.getText();
            String gender = genderField.getText();
            String phone = phoneField.getText();
            String bloodType = bloodTypeField.getText();
            String address = addressField.getText();
            String height = heightField.getText();
            String weight = weightField.getText();

            String query = "SELECT name, identitynumber FROM patient WHERE 1=1";

            if (!name.isEmpty()) query += " AND name LIKE '%" + name + "%'";
            if (!id.isEmpty()) query += " AND identitynumber LIKE '%" + id + "%'";
            if (!gender.isEmpty()) query += " AND gender LIKE '%" + gender + "%'";
            if (!phone.isEmpty()) query += " AND phone LIKE '%" + phone + "%'";
            if (!bloodType.isEmpty()) query += " AND bloodType LIKE '%" + bloodType + "%'";
            if (!address.isEmpty()) query += " AND address LIKE '%" + address + "%'";
            if (!height.isEmpty()) query += " AND height LIKE '%" + height + "%'";
            if (!weight.isEmpty()) query += " AND weight LIKE '%" + weight + "%'";

            try (ResultSet rs = st.executeQuery(query)) {
                Vector<Vector<String>> dataVector = new Vector<>();
                while (rs.next()) {
                    Vector<String> row = new Vector<>();
                    row.add(rs.getString("name"));
                    row.add(rs.getString("identitynumber"));
                    dataVector.add(row);
                }

                patientListPanel.setData(dataVector);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (e.getActionCommand().equals("추가")) {
            new PatientAddWindow(conn).setVisible(true);
        }
    }
}

class PatientListPanel extends JPanel implements MouseListener {
    JTable table;
    DefaultTableModel tableModel;
    PatientDetailsPanel patientDetailsPanel;

    public PatientListPanel(PatientDetailsPanel patientDetailspanel) {
        this.patientDetailsPanel = patientDetailspanel;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("환자 목록"));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setPreferredSize(new Dimension(200, 600));

        //tableModel 및 Jtable 생성
        String[] columnNames = {"환자명", "주민번호"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        //table 레이아웃 설정
        table.setDefaultEditor(Object.class, null);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane);
        add(tablePanel, BorderLayout.CENTER);

        table.addMouseListener(this);
    }

    public void setData(Vector<Vector<String>> inputData) {
        tableModel.setRowCount(0);
        for (Vector<String> row : inputData) {
            tableModel.addRow(row);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        String id = (String)tableModel.getValueAt(row, 1);
        patientDetailsPanel.setData(id);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}

class PatientDetailsPanel extends JPanel {
    JTextField nameField, idField, genderField, bloodTypeField, phoneField, heightField, weightField, addressField;
    JTextArea cautionArea;
    PatientListPanel patientListPanel;
    Connection conn;
    Statement st;

    public PatientDetailsPanel(Connection conn, Statement st) {
        this.conn = conn;
        this.st = st;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("환자 상세 정보"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // 왼쪽 열 - 레이블 및 텍스트 필드
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("이름:"), gbc);

        gbc.gridy++;
        add(new JLabel("주민번호:"), gbc);

        gbc.gridy++;
        add(new JLabel("성별:"), gbc);

        gbc.gridy++;
        add(new JLabel("혈액형:"), gbc);

        gbc.gridy++;
        add(new JLabel("주의사항:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        nameField = new JTextField(15);
        nameField.setEditable(false);
        add(nameField, gbc);

        gbc.gridy++;
        idField = new JTextField(15);
        idField.setEditable(false);
        add(idField, gbc);

        gbc.gridy++;
        genderField = new JTextField(15);
        genderField.setEditable(false);
        add(genderField, gbc);

        gbc.gridy++;
        bloodTypeField = new JTextField(15);
        bloodTypeField.setEditable(false);
        add(bloodTypeField, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        cautionArea = new JTextArea(3, 15);
        add(new JScrollPane(cautionArea), gbc);

        // 오른쪽 열 - 레이블 및 텍스트 필드
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("연락처:"), gbc);

        gbc.gridy++;
        add(new JLabel("키:"), gbc);

        gbc.gridy++;
        add(new JLabel("몸무게:"), gbc);

        gbc.gridy++;
        add(new JLabel("주소:"), gbc);

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(new JButton("수정"), gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        phoneField = new JTextField(15);
        add(phoneField, gbc);

        gbc.gridy++;
        heightField = new JTextField(15);
        add(heightField, gbc);

        gbc.gridy++;
        weightField = new JTextField(15);
        add(weightField, gbc);

        gbc.gridy++;
        addressField = new JTextField(15);
        add(addressField, gbc);
    }

    public void setData(String id) {
        String query = "SELECT * FROM patient WHERE identitynumber = '" + id + "'";
        try(ResultSet rs = st.executeQuery(query)) {
            rs.next();
            nameField.setText(rs.getString("name"));
            idField.setText(rs.getString("identitynumber"));
            genderField.setText(rs.getString("gender"));
            bloodTypeField.setText(rs.getString("bloodtype"));
            cautionArea.setText(rs.getString("caution"));
            phoneField.setText(rs.getString("phone"));
            heightField.setText(rs.getString("height"));
            weightField.setText(rs.getString("weight"));
            addressField.setText(rs.getString("address"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPatientListPanel(PatientListPanel patientListPanel) {
        this.patientListPanel = patientListPanel;
    }
}

class PatientAddWindow extends JFrame implements ActionListener{
    Connection conn;
    JTextField nameField, phoneField, idField1, idField2, heightField, genderField, weightField, bloodTypeField, addressField;
    JTextArea cautionArea;

    public PatientAddWindow(Connection conn) {
        this.conn = conn;

        setTitle("환자 추가");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(Color.LIGHT_GRAY);

        JLabel titleLabel = new JLabel("환자 추가");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        titlePanel.add(titleLabel, BorderLayout.NORTH);

        add(titlePanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);

        // Left column components
        JLabel nameLabel = new JLabel("*이름");
        nameLabel.setBounds(20, 80, 80, 30);
        mainPanel.add(nameLabel);

        nameField = new JTextField(5);
        nameField.setBounds(120, 80, 150, 30);
        mainPanel.add(nameField);

        JLabel idLabel = new JLabel("*주민번호");
        idLabel.setBounds(20, 130, 80, 30);
        mainPanel.add(idLabel);

        idField1 = new JTextField(6);
        idField1.setBounds(120, 130, 70, 30);
        mainPanel.add(idField1);

        JLabel bar = new JLabel("-");
        bar.setBounds(193, 130, 4, 30);
        mainPanel.add(bar);

        idField2 = new JTextField(7);
        idField2.setBounds(200, 130, 70, 30);
        mainPanel.add(idField2);

        JLabel genderLabel = new JLabel("*성별");
        genderLabel.setBounds(20, 180, 80, 30);
        mainPanel.add(genderLabel);

        genderField = new JTextField(3);
        genderField.setBounds(120, 180, 150, 30);
        mainPanel.add(genderField);

        JLabel bloodTypeLabel = new JLabel("혈액형");
        bloodTypeLabel.setBounds(20, 230, 80, 30);
        mainPanel.add(bloodTypeLabel);

        bloodTypeField = new JTextField();
        bloodTypeField.setBounds(120, 230, 150, 30);
        mainPanel.add(bloodTypeField);

        JLabel cautionLabel = new JLabel("주의사항");
        cautionLabel.setBounds(20, 280, 80, 30);
        mainPanel.add(cautionLabel);

        cautionArea = new JTextArea(15, 20);
        cautionArea.setBounds(120, 280, 150, 100);
        mainPanel.add(cautionArea);

        // Right column components
        JLabel phoneLabel = new JLabel("연락처");
        phoneLabel.setBounds(320, 80, 80, 30);
        mainPanel.add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(420, 80, 150, 30);
        mainPanel.add(phoneField);

        JLabel heightLabel = new JLabel("키");
        heightLabel.setBounds(320, 130, 80, 30);
        mainPanel.add(heightLabel);

        heightField = new JTextField();
        heightField.setBounds(420, 130, 150, 30);
        mainPanel.add(heightField);

        JLabel weightLabel = new JLabel("몸무게");
        weightLabel.setBounds(320, 180, 80, 30);
        mainPanel.add(weightLabel);

        weightField = new JTextField();
        weightField.setBounds(420, 180, 150, 30);
        mainPanel.add(weightField);

        JLabel addressLabel = new JLabel("주소");
        addressLabel.setBounds(320, 230, 80, 30);
        mainPanel.add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(420, 230, 150, 30);
        mainPanel.add(addressField);

        JButton addButton = new JButton("추가");
        addButton.addActionListener(this);
        addButton.setBounds(320, 280, 150, 30);
        mainPanel.add(addButton);

        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String query = "INSERT INTO patient(name, tel1, identitynumber, caution, address, bloodType, gender, height, weight) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try {
            PreparedStatement pstm = conn.prepareStatement(query);
            if (nameField.getText().isEmpty() || idField1.getText().isEmpty() || genderField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "필수 사항은 입력하셔야 합니다.");
            } else {
                pstm.setString(1, nameField.getText());
                pstm.setString(2, phoneField.getText());
                pstm.setString(3, idField1.getText() + "-" + idField2.getText());
                pstm.setString(4, cautionArea.getText());
                pstm.setString(5, addressField.getText());
                pstm.setString(6, bloodTypeField.getText());
                pstm.setString(7, genderField.getText());
                pstm.setInt(8, Integer.parseInt(heightField.getText()));
                pstm.setInt(9, Integer.parseInt(weightField.getText()));

                if (pstm.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(null, "삽입 성공");
                } else {
                    JOptionPane.showMessageDialog(null, "실패");
                }
            }

        } catch (SQLException ex) {
            System.out.println("오류");
        }
    }
}

