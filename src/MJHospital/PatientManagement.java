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
    JTextField nameField, idField, phoneField, addressField, heightField, weightField;
    JRadioButton allGender, male, female;
    JComboBox<String> bloodTypeBox;

    public PatientConditionPanel(PatientListPanel patientListPanel, Connection conn, Statement st) {
        this.patientListPanel = patientListPanel;
        this.conn = conn;
        this.st = st;

        setLayout(null);
        setBorder(BorderFactory.createTitledBorder("환자 조건"));
        setPreferredSize(new Dimension(1000, 150));
        int xValue = 250;
        int yValue = 20;
        int labelWidth = 60;
        int fieldWidth = 130;
        int height = 30;
        int spacing = 40;

        // Create labels and text fields
        JLabel nameLabel = new JLabel("이름:");
        nameLabel.setBounds(xValue, yValue, labelWidth, height);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        add(nameField);

        JLabel idLabel = new JLabel("주민번호:");
        idLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(idLabel);

        idField = new JTextField();
        idField.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        add(idField);

        JLabel genderLabel = new JLabel("성별:");
        genderLabel.setBounds(xValue, yValue + 2 * spacing, labelWidth, height);
        add(genderLabel);

        ButtonGroup genderGroup = new ButtonGroup();
        allGender  = new JRadioButton("전체");
        allGender.setSelected(true);
        male = new JRadioButton("남");
        female = new JRadioButton("여");

        genderGroup.add(allGender);
        genderGroup.add(male);
        genderGroup.add(female);

        allGender.setBounds(xValue + 60, yValue + 2 * spacing, 50, height);
        male.setBounds(xValue + 110, yValue + 2 * spacing, 40, height);
        female.setBounds(xValue + 150, yValue + 2 * spacing, 40, height);
        add(allGender);
        add(male);
        add(female);

        xValue += 220;

        JLabel phoneLabel = new JLabel("전화번호:");
        phoneLabel.setBounds(xValue, yValue, labelWidth, height);
        add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        add(phoneField);

        JLabel bloodTypeLabel = new JLabel("혈액형:");
        bloodTypeLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(bloodTypeLabel);

        String[] bloodTypeList = {"전체", "A", "B", "O", "AB"};
        bloodTypeBox = new JComboBox<>(bloodTypeList);
        bloodTypeBox.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        add(bloodTypeBox);

        JLabel addressLabel = new JLabel("주소:");
        addressLabel.setBounds(xValue, yValue + 2 * spacing, labelWidth, height);
        add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(xValue + labelWidth, yValue + 2 * spacing, fieldWidth, height);
        add(addressField);

        xValue += 220;

        JLabel heightLabel = new JLabel("키:");
        heightLabel.setBounds(xValue, yValue, labelWidth, height);
        add(heightLabel);

        heightField = new JTextField();
        heightField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        add(heightField);

        JLabel weightLabel = new JLabel("몸무게:");
        weightLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(weightLabel);

        weightField = new JTextField();
        weightField.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        add(weightField);

        JButton addButton = new JButton("추가");
        addButton.setBounds(xValue, yValue + 2 * spacing, 190, height);
        add(addButton);

        JButton searchButton = new JButton("검색");
        searchButton.setBounds(xValue + 220, yValue, 80, 110);
        add(searchButton);

        // Add ActionListener to the buttons
        addButton.addActionListener(this);
        searchButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("검색")) {
            String name = nameField.getText();
            String id = idField.getText();
            String phone = phoneField.getText();
            String bloodType = bloodTypeBox.getSelectedItem().toString();
            String address = addressField.getText();
            String height = heightField.getText();
            String weight = weightField.getText();

            String query = "SELECT name, identitynumber FROM patient WHERE 1=1";

            if (!name.isEmpty()) query += " AND name LIKE '%" + name + "%'";
            if (!id.isEmpty()) query += " AND identitynumber LIKE '%" + id + "%'";
            if (male.isSelected()) query += " AND gender = '남'";
            if (female.isSelected()) query +=" AND gender = '여'";
            if (!phone.isEmpty()) query += " AND phone LIKE '%" + phone + "%'";
            if (!bloodType.equals("전체")) query += " AND bloodType LIKE '%" + bloodType + "%'";
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

class PatientDetailsPanel extends JPanel implements ActionListener {
    JTextField nameField, idField, genderField, bloodTypeField, phoneField, heightField, weightField, addressField;
    JTextArea cautionArea;
    JRadioButton A, B, O, AB;
    PatientListPanel patientListPanel;
    int patientId;
    Connection conn;
    Statement st;

    public PatientDetailsPanel(Connection conn, Statement st) {
        this.conn = conn;
        this.st = st;

        // 기본 x, y 좌표
        int xValue = 300;
        int yValue = 60;
        int labelWidth = 60;
        int fieldWidth = 130;
        int height = 30;
        int spacing = 60;

// null 레이아웃 설정
        setLayout(null);
        setBorder(BorderFactory.createTitledBorder("환자 정보"));

// 왼쪽 열 - 레이블 및 텍스트 필드
        JLabel nameLabel = new JLabel("이름:");
        nameLabel.setBounds(xValue, yValue, labelWidth, height);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        nameField.setEditable(false);
        add(nameField);

        JLabel idLabel = new JLabel("주민번호:");
        idLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(idLabel);

        idField = new JTextField();
        idField.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        idField.setEditable(false);
        add(idField);

        JLabel genderLabel = new JLabel("성별:");
        genderLabel.setBounds(xValue, yValue + 2 * spacing, labelWidth, height);
        add(genderLabel);

        genderField = new JTextField();
        genderField.setBounds(xValue + labelWidth, yValue + 2 * spacing, fieldWidth, height);
        genderField.setEditable(false);
        add(genderField);

        JLabel bloodTypeLabel = new JLabel("혈액형:");
        bloodTypeLabel.setBounds(xValue, yValue + 3 * spacing, labelWidth, height);
        add(bloodTypeLabel);

        ButtonGroup bloodGroup = new ButtonGroup();
        A = new JRadioButton("A");
        B = new JRadioButton("B");
        O = new JRadioButton("O");
        AB = new JRadioButton("AB");
        bloodGroup.add(A);
        bloodGroup.add(B);
        bloodGroup.add(O);
        bloodGroup.add(AB);

        A.setBounds(xValue + labelWidth, yValue + 3 * spacing, 35, 30);
        B.setBounds(xValue + labelWidth + 35, yValue + 3 * spacing, 35, 30);
        O.setBounds(xValue + labelWidth + 70, yValue + 3 * spacing, 35, 30);
        AB.setBounds(xValue + labelWidth + 105, yValue + 3 * spacing, 45, 30);
        add(A);
        add(B);
        add(O);
        add(AB);

        JLabel cautionLabel = new JLabel("주의사항:");
        cautionLabel.setBounds(xValue, yValue + 4 * spacing, labelWidth, height);
        add(cautionLabel);

        cautionArea = new JTextArea(15, 15);
        cautionArea.setBounds(xValue + labelWidth, yValue + 4 * spacing, fieldWidth, 3 * height);
        add(cautionArea);

// 오른쪽 열 - 레이블 및 텍스트 필드
        xValue += labelWidth + fieldWidth + 60; // 오른쪽 열로 이동

        JLabel phoneLabel = new JLabel("연락처:");
        phoneLabel.setBounds(xValue, yValue, labelWidth, height);
        add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        add(phoneField);

        JLabel heightLabel = new JLabel("키:");
        heightLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(heightLabel);

        heightField = new JTextField();
        heightField.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        add(heightField);

        JLabel weightLabel = new JLabel("몸무게:");
        weightLabel.setBounds(xValue, yValue + 2 * spacing, labelWidth, height);
        add(weightLabel);

        weightField = new JTextField();
        weightField.setBounds(xValue + labelWidth, yValue + 2 * spacing, fieldWidth, height);
        add(weightField);

        JLabel addressLabel = new JLabel("주소:");
        addressLabel.setBounds(xValue, yValue + 3 * spacing, labelWidth, height);
        add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(xValue + labelWidth, yValue + 3 * spacing, fieldWidth, height);
        add(addressField);

        JButton modifyButton = new JButton("수정");
        modifyButton.addActionListener(this);
        modifyButton.setBounds(xValue, yValue + 4 * spacing, labelWidth + fieldWidth, height);
        add(modifyButton);
    }

    public void setData(String id) {
        String query = "SELECT * FROM patient WHERE identitynumber = '" + id + "'";
        try(ResultSet rs = st.executeQuery(query)) {
            rs.next();
            patientId = rs.getInt("patientid");
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("수정")) {
            String query = "UPDATE patient SET phone = ?, gender = ?, bloodtype = ?, caution = ?, address = ?, height = ?, weight = ? WHERE patientid = ? ";
            try {
                PreparedStatement pstm = conn.prepareStatement(query);
                pstm.setString(1, phoneField.getText());
                pstm.setString(2, genderField.getText());
                pstm.setString(3, bloodTypeField.getText());
                pstm.setString(4, cautionArea.getText());
                pstm.setString(5, addressField.getText());
                pstm.setInt(6, Integer.parseInt(heightField.getText()));
                pstm.setInt(7, Integer.parseInt(weightField.getText()));
                pstm.setInt(8, patientId);

                if (pstm.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(null, "수정 성공");
                } else {
                    JOptionPane.showMessageDialog(null, "수정 실패");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

class PatientAddWindow extends JFrame implements ActionListener{
    Connection conn;
    JTextField nameField, phoneField, idField1, idField2, heightField, weightField, addressField;
    JTextArea cautionArea;
    JRadioButton male, female, A, B, O, AB;

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

        int xValue = 20;
        int yValue = 40;
        // Left column components
        JLabel nameLabel = new JLabel("*이름");
        nameLabel.setBounds(xValue, yValue, 80, 30);
        mainPanel.add(nameLabel);

        nameField = new JTextField(5);
        nameField.setBounds(xValue + 100, yValue, 150, 30);
        mainPanel.add(nameField);

        JLabel idLabel = new JLabel("*주민번호");
        idLabel.setBounds(xValue, yValue + 50, 80, 30);
        mainPanel.add(idLabel);

        idField1 = new JTextField(6);
        idField1.setBounds(xValue + 100, yValue + 50, 70, 30);
        mainPanel.add(idField1);

        JLabel bar = new JLabel("-");
        bar.setBounds(xValue + 173, yValue + 50, 4, 30);
        mainPanel.add(bar);

        idField2 = new JTextField(7);
        idField2.setBounds(xValue + 180, yValue + 50, 70, 30);
        mainPanel.add(idField2);

        JLabel genderLabel = new JLabel("*성별");
        genderLabel.setBounds(xValue, yValue + 100, 80, 30);
        mainPanel.add(genderLabel);

        ButtonGroup genderGroup = new ButtonGroup();
        male = new JRadioButton("남");
        male.setSelected(true);
        female = new JRadioButton("여");

        genderGroup.add(male);
        genderGroup.add(female);

        male.setBounds(xValue + 100, yValue + 100, 70, 30);
        female.setBounds(xValue + 180, yValue + 100, 70, 30);
        mainPanel.add(male);
        mainPanel.add(female);

        JLabel bloodTypeLabel = new JLabel("혈액형");
        bloodTypeLabel.setBounds(xValue, yValue + 150, 80, 30);
        mainPanel.add(bloodTypeLabel);

        ButtonGroup bloodGroup = new ButtonGroup();
        A = new JRadioButton("A");
        B = new JRadioButton("B");
        O = new JRadioButton("O");
        AB = new JRadioButton("AB");
        bloodGroup.add(A);
        bloodGroup.add(B);
        bloodGroup.add(O);
        bloodGroup.add(AB);

        A.setBounds(xValue + 100, yValue + 150, 35, 30);
        B.setBounds(xValue + 135, yValue + 150, 35, 30);
        O.setBounds(xValue + 170, yValue + 150, 35, 30);
        AB.setBounds(xValue + 205, yValue + 150, 45, 30);
        mainPanel.add(A);
        mainPanel.add(B);
        mainPanel.add(O);
        mainPanel.add(AB);

        JLabel cautionLabel = new JLabel("주의사항");
        cautionLabel.setBounds(xValue, yValue + 200, 80, 30);
        mainPanel.add(cautionLabel);

        cautionArea = new JTextArea(15, 13);
        cautionArea.setBounds(xValue + 100, yValue + 200, 150, 100);
        mainPanel.add(cautionArea);

        xValue += 300;
        // Right column components
        JLabel phoneLabel = new JLabel("연락처");
        phoneLabel.setBounds(xValue, yValue, 80, 30);
        mainPanel.add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(xValue + 100, yValue, 150, 30);
        mainPanel.add(phoneField);

        JLabel heightLabel = new JLabel("키");
        heightLabel.setBounds(xValue, yValue + 50, 80, 30);
        mainPanel.add(heightLabel);

        heightField = new JTextField();
        heightField.setBounds(xValue + 100, yValue + 50, 150, 30);
        mainPanel.add(heightField);

        JLabel weightLabel = new JLabel("몸무게");
        weightLabel.setBounds(xValue, yValue + 100, 80, 30);
        mainPanel.add(weightLabel);

        weightField = new JTextField();
        weightField.setBounds(xValue + 100, yValue + 100, 150, 30);
        mainPanel.add(weightField);

        JLabel addressLabel = new JLabel("주소");
        addressLabel.setBounds(xValue, yValue + 150, 80, 30);
        mainPanel.add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(xValue + 100, yValue + 150, 150, 30);
        mainPanel.add(addressField);

        JButton addButton = new JButton("추가");
        addButton.addActionListener(this);
        addButton.setBounds(xValue, yValue + 200, 250, 30);
        mainPanel.add(addButton);

        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String query = "INSERT INTO patient(name, phone, identitynumber, caution, address, bloodType, gender, height, weight) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try {
            PreparedStatement pstm = conn.prepareStatement(query);
            if (nameField.getText().isEmpty() || idField1.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "필수 사항은 입력하셔야 합니다.");
            } else {
                pstm.setString(1, nameField.getText());
                pstm.setString(2, phoneField.getText());
                pstm.setString(3, idField1.getText() + "-" + idField2.getText());
                pstm.setString(4, cautionArea.getText());
                pstm.setString(5, addressField.getText());
                if (A.isSelected()) {
                    pstm.setString(6, "A");
                } else if (B.isSelected()) {
                    pstm.setString(6, "B");
                } else if (O.isSelected()) {
                    pstm.setString(6, "O");
                } else if (AB.isSelected()) {
                    pstm.setString(6, "AB");
                } else {
                    pstm.setString(6, null);
                }
                pstm.setString(7, male.isSelected() ? "남" : "여");
                pstm.setInt(8, heightField.getText().isEmpty() ? null : Integer.parseInt(heightField.getText().trim()));
                pstm.setInt(9, weightField.getText().isEmpty() ? null : Integer.parseInt(weightField.getText().trim()));

                if (pstm.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(null, "추가 성공");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "추가 실패");
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "키와 몸무게는 숫자를 입력해주세요");
        }
    }
}

