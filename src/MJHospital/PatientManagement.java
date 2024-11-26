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
        patientDetailsPanel.setPatientConditionPanel(patientConditionPanel);

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
        int xValue = 150;
        int yValue = 25;
        int labelWidth = 60;
        int fieldWidth = 200;
        int height = 30;
        int spacing = 40;

        JLabel nameLabel = new JLabel("이름");
        nameLabel.setBounds(xValue, yValue, labelWidth, height);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        add(nameField);

        JLabel idLabel = new JLabel("주민번호");
        idLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(idLabel);

        idField = new NumberTextField(13);
        idField.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        add(idField);

        JLabel genderLabel = new JLabel("성별");
        genderLabel.setBounds(xValue, yValue + 2 * spacing, labelWidth, height);
        add(genderLabel);

        ButtonGroup genderGroup = new ButtonGroup();
        allGender = new JRadioButton("전체");
        allGender.setSelected(true);
        male = new JRadioButton("남");
        female = new JRadioButton("여");

        genderGroup.add(allGender);
        genderGroup.add(male);
        genderGroup.add(female);

        allGender.setBounds(xValue + labelWidth, yValue + 2 * spacing, 50, height);
        male.setBounds(xValue + labelWidth + 50, yValue + 2 * spacing, 50, height);
        female.setBounds(xValue + labelWidth + 100, yValue + 2 * spacing, 50, height);
        add(allGender);
        add(male);
        add(female);

        xValue += labelWidth + fieldWidth + spacing;

        JLabel phoneLabel = new JLabel("전화번호");
        phoneLabel.setBounds(xValue, yValue, labelWidth, height);
        add(phoneLabel);

        phoneField = new NumberTextField(11);
        phoneField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        add(phoneField);

        JLabel bloodTypeLabel = new JLabel("혈액형");
        bloodTypeLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(bloodTypeLabel);

        String[] bloodTypeList = {"전체", "A", "B", "O", "AB"};
        bloodTypeBox = new JComboBox<>(bloodTypeList);
        bloodTypeBox.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        add(bloodTypeBox);

        JLabel addressLabel = new JLabel("주소");
        addressLabel.setBounds(xValue, yValue + 2 * spacing, labelWidth, height);
        add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(xValue + labelWidth, yValue + 2 * spacing, fieldWidth, height);
        add(addressField);

        xValue += labelWidth + fieldWidth + spacing;

        JLabel heightLabel = new JLabel("키");
        heightLabel.setBounds(xValue, yValue, labelWidth, height);
        add(heightLabel);

        heightField = new NumberTextField(3);
        heightField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        add(heightField);

        JLabel weightLabel = new JLabel("몸무게");
        weightLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(weightLabel);

        weightField = new NumberTextField(3);
        weightField.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        add(weightField);

        JButton addButton = new JButton("추가");
        addButton.setBounds(xValue, yValue + 2 * spacing, labelWidth + fieldWidth, height);
        add(addButton);

        xValue += labelWidth + fieldWidth + spacing;

        JButton searchButton = new JButton("검색");
        searchButton.setBounds(xValue, yValue, 80, 110);
        add(searchButton);

        // Add ActionListener to the buttons
        nameField.addActionListener(this);
        idField.addActionListener(this);
        phoneField.addActionListener(this);
        addressField.addActionListener(this);
        heightField.addActionListener(this);
        weightField.addActionListener(this);
        addButton.addActionListener(this);
        searchButton.addActionListener(this);

        searchPatient();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("추가")) {
            new PatientAddWindow(conn, this).setVisible(true);
        } else {
            searchPatient();
        }
    }

    public void searchPatient() {
        String query = "SELECT * FROM patient WHERE 1=1";

        if (!nameField.getText().isEmpty()) query += " AND name LIKE '%" + nameField.getText() + "%'";
        if (!idField.getText().isEmpty()) query += " AND identitynumber LIKE '%" + idField.getText() + "%'";
        if (male.isSelected()) query += " AND gender = '남'";
        if (female.isSelected()) query += " AND gender = '여'";
        if (!phoneField.getText().isEmpty()) query += " AND phone LIKE '%" + phoneField.getText() + "%'";
        if (!bloodTypeBox.getSelectedItem().toString().equals("전체"))
            query += " AND bloodType = '" + bloodTypeBox.getSelectedItem().toString() + "'";
        if (!addressField.getText().isEmpty()) query += " AND address LIKE '%" + addressField.getText() + "%'";
        if (!heightField.getText().isEmpty()) query += " AND height = " + heightField.getText();
        if (!weightField.getText().isEmpty()) query += " AND weight = " + weightField.getText();

        query += " ORDER BY patientid";

        try (ResultSet rs = st.executeQuery(query)) {
            Vector<Vector<String>> dataVector = new Vector<>();
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(String.format("%04d", rs.getInt("patientid")));
                row.add(rs.getString("name"));
                row.add(rs.getString("gender"));
                row.add(rs.getString("identitynumber"));
                dataVector.add(row);
            }

            patientListPanel.setData(dataVector);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "알 수 없는 오류가 발생하였습니다.");
            ex.printStackTrace();
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
        tablePanel.setPreferredSize(new Dimension(300, 600));

        //tableModel 및 Jtable 생성
        String[] columnNames = {"환자id", "환자명", "성별", "주민번호"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        //table 레이아웃 설정
        table.setDefaultEditor(Object.class, null);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(30);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);

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
        String patientId = (String) tableModel.getValueAt(row, 0);
        patientDetailsPanel.setData(patientId);
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
    JComboBox<String> bloodTypeBox;
    JTextField nameField, patientIdField, identityField, phoneField, heightField, weightField, addressField;
    JTextArea cautionArea;
    JRadioButton male, female;
    int patientId;
    Connection conn;
    Statement st;
    PatientListPanel patientListPanel;
    PatientConditionPanel patientConditionPanel;

    public PatientDetailsPanel(Connection conn, Statement st) {
        this.conn = conn;
        this.st = st;

        // 기본 x, y 좌표
        int xValue = 170;
        int yValue = 60;
        int labelWidth = 60;
        int fieldWidth = 200;
        int height = 30;
        int spacing = 60;

// null 레이아웃 설정
        setLayout(null);
        setBorder(BorderFactory.createTitledBorder("환자 정보"));

// 왼쪽 열 - 레이블 및 텍스트 필드
        JLabel nameLabel = new JLabel("이름");
        nameLabel.setBounds(xValue, yValue, labelWidth, height);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(xValue + labelWidth, yValue, fieldWidth - 120, height);
        nameField.setEditable(false);
        add(nameField);


        JLabel patientIdLabel = new JLabel("환자 id");
        patientIdLabel.setBounds(xValue + labelWidth + fieldWidth - 120 + 10, yValue, labelWidth, height);
        add(patientIdLabel);

        patientIdField = new JTextField();
        patientIdField.setBounds(xValue + 2 * labelWidth + fieldWidth - 120, yValue, fieldWidth - 140, height);
        patientIdField.setEditable(false);
        add(patientIdField);

        JLabel idLabel = new JLabel("주민번호");
        idLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(idLabel);

        identityField = new JTextField();
        identityField.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        identityField.setEditable(false);
        add(identityField);

        JLabel genderLabel = new JLabel("성별");
        genderLabel.setBounds(xValue, yValue + 2 * spacing, labelWidth, height);
        add(genderLabel);

        ButtonGroup genderGroup = new ButtonGroup();
        male = new JRadioButton("남");
        male.setSelected(true);
        female = new JRadioButton("여");

        genderGroup.add(male);
        genderGroup.add(female);

        male.setBounds(xValue + labelWidth, yValue + 2 * spacing, 70, height);
        female.setBounds(xValue + labelWidth + 80, yValue + 2 * spacing, 70, height);
        add(male);
        add(female);

        JLabel bloodTypeLabel = new JLabel("혈액형");
        bloodTypeLabel.setBounds(xValue, yValue + 3 * spacing, labelWidth, height);
        add(bloodTypeLabel);

        String[] bloodTypeList = {"선택되지 않음", "A", "B", "O", "AB"};
        bloodTypeBox = new JComboBox<>(bloodTypeList);
        bloodTypeBox.setBounds(xValue + labelWidth, yValue + 3 * spacing, fieldWidth, height);
        add(bloodTypeBox);

        JLabel cautionLabel = new JLabel("주의사항");
        cautionLabel.setBounds(xValue, yValue + 4 * spacing, labelWidth, height);
        add(cautionLabel);

        cautionArea = new JTextArea(15, 15);
        cautionArea.setBounds(xValue + labelWidth, yValue + 4 * spacing, fieldWidth, 3 * height);
        add(cautionArea);

// 오른쪽 열 - 레이블 및 텍스트 필드
        xValue += labelWidth + fieldWidth + 60; // 오른쪽 열로 이동

        JLabel phoneLabel = new JLabel("연락처");
        phoneLabel.setBounds(xValue, yValue, labelWidth, height);
        add(phoneLabel);

        phoneField = new NumberTextField(11);
        phoneField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        add(phoneField);

        JLabel heightLabel = new JLabel("키");
        heightLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(heightLabel);

        heightField = new NumberTextField(3);
        heightField.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        add(heightField);

        JLabel weightLabel = new JLabel("몸무게");
        weightLabel.setBounds(xValue, yValue + 2 * spacing, labelWidth, height);
        add(weightLabel);

        weightField = new NumberTextField(3);
        weightField.setBounds(xValue + labelWidth, yValue + 2 * spacing, fieldWidth, height);
        add(weightField);

        JLabel addressLabel = new JLabel("주소");
        addressLabel.setBounds(xValue, yValue + 3 * spacing, labelWidth, height);
        add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(xValue + labelWidth, yValue + 3 * spacing, fieldWidth, height);
        add(addressField);

        JButton modifyButton = new JButton("수정");
        modifyButton.addActionListener(this);
        modifyButton.setBounds(xValue, yValue + 4 * spacing, 120, height);
        add(modifyButton);

        JButton deleteButton = new JButton("삭제");
        deleteButton.addActionListener(this);
        deleteButton.setBounds(xValue + 140, yValue + 4 * spacing, 120, height);
        add(deleteButton);
    }

    public void setData(String patientId) {
        String query = "SELECT * FROM patient WHERE patientid = " + patientId;
        try (ResultSet rs = st.executeQuery(query)) {
            rs.next();
            this.patientId = rs.getInt("patientid");
            nameField.setText(rs.getString("name"));
            patientIdField.setText(patientId);
            identityField.setText(rs.getString("identitynumber"));
            switch (rs.getString("gender")) {
                case "남":
                    male.setSelected(true);
                    break;
                case "여":
                    female.setSelected(true);
                    break;
            }
            bloodTypeBox.setSelectedItem(rs.getString("bloodtype") == null ? "선택되지 않음" : rs.getString("bloodtype"));
            cautionArea.setText(rs.getString("caution"));
            phoneField.setText(rs.getString("phone"));
            heightField.setText(rs.getString("height"));
            weightField.setText(rs.getString("weight"));
            addressField.setText(rs.getString("address"));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "알 수 없는 오류가 발생하였습니다.");
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //수정 버튼 클릭 시
        if (e.getActionCommand().equals("수정")) {
            if (JOptionPane.showConfirmDialog(this, "수정하시겠습니까?") == 0) {
                String query = "UPDATE patient SET phone = ?, gender = ?, bloodtype = ?, caution = ?, address = ?, height = ?, weight = ? WHERE patientid = ?";
                try {
                    PreparedStatement pstm = conn.prepareStatement(query);
                    pstm.setString(1, phoneField.getText().isEmpty() ? null : phoneField.getText());
                    pstm.setString(2, male.isSelected() ? "남" : "여");
                    pstm.setString(3, bloodTypeBox.getSelectedItem().toString().equals("선택되지 않음") ? null : bloodTypeBox.getSelectedItem().toString());
                    pstm.setString(4, cautionArea.getText().isEmpty() ? null : cautionArea.getText());
                    pstm.setString(5, addressField.getText().isEmpty() ? null : addressField.getText());
                    pstm.setString(6, heightField.getText().isEmpty() ? null : heightField.getText());
                    pstm.setString(7, weightField.getText().isEmpty() ? null : weightField.getText());
                    pstm.setInt(8, patientId);
                    if (pstm.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(this, "수정 성공");
                        patientConditionPanel.searchPatient();
                    } else {
                        JOptionPane.showMessageDialog(this, "수정 실패");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "알 수 없는 오류가 발생하였습니다.");
                    ex.printStackTrace();
                }
            }
        } else if (e.getActionCommand().equals("삭제")) {
            if (JOptionPane.showConfirmDialog(null, nameField.getText() + " 환자를 정말 삭제하시겠습니까?") == 0) {
                String query = "DELETE FROM patient WHERE patientid = ?";
                try {
                    PreparedStatement pstm = conn.prepareStatement(query);
                    pstm.setInt(1, patientId);
                    if (pstm.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(this, "삭제 성공");
                        patientConditionPanel.searchPatient();
                    } else {
                        JOptionPane.showMessageDialog(this, "삭제 실패");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "알 수 없는 오류가 발생하였습니다.");
                    ex.printStackTrace();
                }
            }
        }
    }

    public void setPatientListPanel(PatientListPanel patientListPanel) {
        this.patientListPanel = patientListPanel;
    }

    public void setPatientConditionPanel(PatientConditionPanel patientConditionPanel) {
        this.patientConditionPanel = patientConditionPanel;
    }
}

class PatientAddWindow extends JFrame implements ActionListener {
    Connection conn;
    JTextField nameField, phoneField, idField1, idField2, heightField, weightField, addressField;
    JTextArea cautionArea;
    JRadioButton male, female, A, B, O, AB;
    PatientConditionPanel patientConditionPanel;

    public PatientAddWindow(Connection conn, PatientConditionPanel patientConditionPanel) {
        this.conn = conn;
        this.patientConditionPanel = patientConditionPanel;

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
        int labelWidth = 100;
        int fieldWidth = 150;
        int height = 30;
        int spacing = 50;

        // Left column components
        JLabel nameLabel = new JLabel("*이름");
        nameLabel.setBounds(xValue, yValue, labelWidth, 30);
        mainPanel.add(nameLabel);

        nameField = new JTextField(5);
        nameField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        mainPanel.add(nameField);

        JLabel idLabel = new JLabel("*주민번호");
        idLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        mainPanel.add(idLabel);

        idField1 = new NumberTextField(6);
        idField1.setBounds(xValue + labelWidth, yValue + spacing, 70, height);
        mainPanel.add(idField1);

        JLabel bar = new JLabel("-");
        bar.setBounds(xValue + 173, yValue + spacing, 4, height);
        mainPanel.add(bar);

        idField2 = new NumberTextField(7);
        idField2.setBounds(xValue + 180, yValue + spacing, 70, height);
        mainPanel.add(idField2);

        JLabel genderLabel = new JLabel("*성별");
        genderLabel.setBounds(xValue, yValue + 2 * spacing, labelWidth, height);
        mainPanel.add(genderLabel);

        ButtonGroup genderGroup = new ButtonGroup();
        male = new JRadioButton("남");
        male.setSelected(true);
        female = new JRadioButton("여");

        genderGroup.add(male);
        genderGroup.add(female);

        male.setBounds(xValue + labelWidth, yValue + 2 * spacing, 70, height);
        female.setBounds(xValue + labelWidth + 80, yValue + 100, 70, height);
        mainPanel.add(male);
        mainPanel.add(female);

        JLabel bloodTypeLabel = new JLabel("혈액형");
        bloodTypeLabel.setBounds(xValue, yValue + 3 * spacing, labelWidth, height);
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

        A.setBounds(xValue + labelWidth, yValue + 3 * spacing, 35, height);
        B.setBounds(xValue + labelWidth + 35, yValue + 3 * spacing, 35, height);
        O.setBounds(xValue + labelWidth + 70, yValue + 3 * spacing, 35, height);
        AB.setBounds(xValue + labelWidth + 105, yValue + 3 * spacing, 45, height);
        mainPanel.add(A);
        mainPanel.add(B);
        mainPanel.add(O);
        mainPanel.add(AB);

        JLabel cautionLabel = new JLabel("주의사항");
        cautionLabel.setBounds(xValue, yValue + 4 * spacing, labelWidth, height);
        mainPanel.add(cautionLabel);

        cautionArea = new JTextArea(15, 13);
        cautionArea.setBounds(xValue + labelWidth, yValue + 4 * spacing, fieldWidth, height * 3);
        mainPanel.add(cautionArea);

        xValue += 300;
        // Right column components
        JLabel phoneLabel = new JLabel("연락처");
        phoneLabel.setBounds(xValue, yValue, labelWidth, height);
        mainPanel.add(phoneLabel);

        phoneField = new NumberTextField(11);
        phoneField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        mainPanel.add(phoneField);

        JLabel heightLabel = new JLabel("키");
        heightLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        mainPanel.add(heightLabel);

        heightField = new NumberTextField(3);
        heightField.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        mainPanel.add(heightField);

        JLabel weightLabel = new JLabel("몸무게");
        weightLabel.setBounds(xValue, yValue + 2 * spacing, labelWidth, height);
        mainPanel.add(weightLabel);

        weightField = new NumberTextField(3);
        weightField.setBounds(xValue + labelWidth, yValue + 2 * spacing, fieldWidth, height);
        mainPanel.add(weightField);

        JLabel addressLabel = new JLabel("주소");
        addressLabel.setBounds(xValue, yValue + 3 * spacing, labelWidth, height);
        mainPanel.add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(xValue + labelWidth, yValue + 3 * spacing, fieldWidth, height);
        mainPanel.add(addressField);

        JButton addButton = new JButton("추가");
        addButton.addActionListener(this);
        addButton.setBounds(xValue, yValue + 4 * spacing, labelWidth + fieldWidth, height);
        mainPanel.add(addButton);

        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (JOptionPane.showConfirmDialog(this, nameField.getText() + " 환자를 추가하시겠습니까?") == 0) {
            String query = "INSERT INTO patient(name, phone, identitynumber, caution, address, bloodType, gender, height, weight) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try {
                PreparedStatement pstm = conn.prepareStatement(query);
                if (nameField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "이름을 입력하세요");
                    nameField.requestFocus();
                } else if (idField1.getText().length() != 6 || idField2.getText().length() != 7) {
                    JOptionPane.showMessageDialog(this, "올바른 주민번호를 입력해주세요");
                    idField1.requestFocus();
                } else {
                    pstm.setString(1, nameField.getText());
                    pstm.setString(2, phoneField.getText().isEmpty() ? null : phoneField.getText());
                    pstm.setString(3, idField1.getText() + "-" + idField2.getText());
                    pstm.setString(4, cautionArea.getText().isEmpty() ? null : cautionArea.getText());
                    pstm.setString(5, addressField.getText().isEmpty() ? null : addressField.getText());
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
                    pstm.setString(8, heightField.getText().isEmpty() ? null : heightField.getText());
                    pstm.setString(9, weightField.getText().isEmpty() ? null : weightField.getText());

                    if (pstm.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(this, "추가 성공");
                        patientConditionPanel.searchPatient();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "추가 실패");
                    }
                }
            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "이미 추가된 환자입니다");
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "키와 몸무게는 숫자를 입력해주세요");
            }
        }
    }
}

class NumberTextField extends JTextField implements KeyListener {
    int maxLength;

    public NumberTextField(int maxLength) {
        this.maxLength = maxLength;
        this.addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();

        if (!Character.isDigit(c)) {
            e.consume();
        }

        if (getText().length() >= maxLength) {
            e.consume();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
