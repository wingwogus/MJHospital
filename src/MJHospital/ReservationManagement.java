package MJHospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.*;
import java.util.Vector;

public class ReservationManagement extends JPanel {
    Connection conn;
    Statement st;
    ReservationListPanel reservationListPanel;
    ReservationConditionPanel reservationConditionPanel;
    ReservationDetailPanel reservationDetailsPanel;

    public ReservationManagement() {
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
        reservationDetailsPanel = new ReservationDetailPanel(conn, st);
        reservationListPanel = new ReservationListPanel(reservationDetailsPanel);
        reservationConditionPanel = new ReservationConditionPanel(reservationListPanel, conn, st);

        reservationDetailsPanel.setPatientListPanel(reservationListPanel);

        // 환자 조건 패널 생성
        add(reservationConditionPanel, BorderLayout.NORTH);

        // 환자 목록 패널 생성
        add(reservationListPanel, BorderLayout.WEST);

        // 환자 상세 정보 패널 생성
        add(reservationDetailsPanel, BorderLayout.CENTER);
    }
}

class ReservationConditionPanel extends JPanel implements ActionListener {
    Connection conn;
    Statement st;
    ReservationListPanel reservationListPanel;
    JTextField patientField, idField, phoneField, doctorField;
    JComboBox<Integer> year, year2, month, month2, day, day2;

    public ReservationConditionPanel(ReservationListPanel reservationListPanel, Connection conn, Statement st) {
        this.reservationListPanel = reservationListPanel;
        this.conn = conn;
        this.st = st;

        setLayout(null);
        setBorder(BorderFactory.createTitledBorder("예약 조건"));
        setPreferredSize(new Dimension(1000, 150));
        int xValue = 150;
        int yValue = 50;
        int labelWidth = 60;
        int fieldWidth = 200;
        int height = 30;
        int spacing = 40;

        // Create labels and text fields
        JLabel nameLabel = new JLabel("환자 이름:");
        nameLabel.setBounds(xValue, yValue, labelWidth, height);
        add(nameLabel);

        patientField = new JTextField();
        patientField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        add(patientField);

        JLabel idLabel = new JLabel("주민번호:");
        idLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(idLabel);

        idField = new JTextField();
        idField.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        add(idField);

        xValue += labelWidth + fieldWidth + spacing;

        JLabel addressLabel = new JLabel("담당의:");
        addressLabel.setBounds(xValue, yValue, labelWidth, height);
        add(addressLabel);

        doctorField = new JTextField();
        doctorField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        add(doctorField);

        JLabel phoneLabel = new JLabel("전화번호:");
        phoneLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        add(phoneField);

        xValue += labelWidth + fieldWidth + spacing;

        Integer[] years = new Integer[100];
        int currentYear = 2024; // 현재 년도
        for (int i = 0; i < 100; i++) {
            years[i] = currentYear - 50 + i;
        }

        Integer[] months = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

        Integer[] days = new Integer[31];
        for (int i = 0; i < 31; i++) {
            days[i] = i + 1;
        }

        year = new JComboBox<>(years);
        month = new JComboBox<>(months);
        day = new JComboBox<>(days);
        year2 = new JComboBox<>(years);
        month2 = new JComboBox<>(months);
        day2 = new JComboBox<>(days);
        JLabel wave = new JLabel("~");

        year.setBounds(xValue, yValue, 60, height);
        month.setBounds(xValue + 60, yValue, 40, height);
        day.setBounds(xValue + 100, yValue, 40, height);
        wave.setBounds(xValue + 150, yValue, 10, height);
        year2.setBounds(xValue + 170, yValue, 60, height);
        month2.setBounds(xValue + 230, yValue, 40, height);
        day2.setBounds(xValue + 270, yValue, 40, height);

        add(year);
        add(month);
        add(day);
        add(wave);
        add(year2);
        add(month2);
        add(day2);

        JButton addButton = new JButton("추가");
        addButton.setBounds(xValue, yValue + spacing, 140, height);
        add(addButton);

        JButton searchButton = new JButton("검색");
        searchButton.setBounds(xValue + 170, yValue + spacing, 140, height);
        add(searchButton);

        // Add ActionListener to the buttons
        patientField.addActionListener(this);
        idField.addActionListener(this);
        phoneField.addActionListener(this);
        doctorField.addActionListener(this);
        addButton.addActionListener(this);
        searchButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("추가")) {
            new ReservationAddWindow(conn).setVisible(true);
        } else {
            String patientName = patientField.getText();
            String id = idField.getText();
            String phone = phoneField.getText();
            String doctorName = doctorField.getText();


            String query = "SELECT * FROM reservation JOIN patient ON reservation.patientid = patient.patientid JOIN staff ON reservation.staffid = staff.staffid WHERE 1=1";

            if (!patientName.isEmpty()) query += " AND patient.name LIKE '%" + patientName + "%'";
            if (!id.isEmpty()) query += " AND patient.identitynumber LIKE '%" + id + "%'";
            if (!phone.isEmpty()) query += " AND patient.phone LIKE '%" + phone + "%'";
            if (!doctorName.isEmpty()) query += " AND staff.name LIKE '%" + doctorName + "%'";

            query += " ORDER BY patient.name";

            try (ResultSet rs = st.executeQuery(query)) {
                Vector<Vector<String>> dataVector = new Vector<>();
                while (rs.next()) {
                    Vector<String> row = new Vector<>();
                    row.add(rs.getString("reservationdate"));
                    row.add(rs.getString("reservationtime"));
                    row.add(rs.getString("patient.name"));
                    row.add(rs.getString("staff.name"));
                    dataVector.add(row);
                }

                reservationListPanel.setData(dataVector);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "알 수 없는 오류가 발생하였습니다.");
                ex.printStackTrace();
            }
        }
    }
}

class ReservationListPanel extends JPanel implements MouseListener {
    JTable table;
    DefaultTableModel tableModel;
    ReservationDetailPanel reservationDetailPanel;

    public ReservationListPanel(ReservationDetailPanel reservationDetailPanel) {
        this.reservationDetailPanel = reservationDetailPanel;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("환자 목록"));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setPreferredSize(new Dimension(400, 600));

        //tableModel 및 Jtable 생성
        String[] columnNames = {"예약 날짜", "예약 시간", "환자명", "담당의"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        //table 레이아웃 설정
        table.setDefaultEditor(Object.class, null);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);
        table.getColumnModel().getColumn(3).setPreferredWidth(50);

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
        String date = (String) tableModel.getValueAt(row, 0);
        String time = (String) tableModel.getValueAt(row, 1);
        reservationDetailPanel.setData(date, time);
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

class ReservationDetailPanel extends JPanel implements ActionListener {
    JTextField patientField, patientIdField, phoneField, doctorField, dateField, timeField;
    JTextArea cautionArea;
    int reservationId;
    Connection conn;
    Statement st;
    ReservationListPanel reservationListPanel;

    public ReservationDetailPanel(Connection conn, Statement st) {
        this.conn = conn;
        this.st = st;

        // 기본 x, y 좌표
        int xValue = 130;
        int yValue = 60;
        int labelWidth = 60;
        int fieldWidth = 200;
        int height = 30;
        int spacing = 80;

// null 레이아웃 설정
        setLayout(null);
        setBorder(BorderFactory.createTitledBorder("환자 정보"));

// 왼쪽 열 - 레이블 및 텍스트 필드
        JLabel patientLabel = new JLabel("이름");
        patientLabel.setBounds(xValue, yValue, labelWidth, height);
        add(patientLabel);

        patientField = new JTextField();
        patientField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        patientField.setEditable(false);
        add(patientField);

        JLabel idLabel = new JLabel("주민번호");
        idLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(idLabel);

        patientIdField = new JTextField();
        patientIdField.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        patientIdField.setEditable(false);
        add(patientIdField);

        JLabel phoneLabel = new JLabel("연락처");
        phoneLabel.setBounds(xValue, yValue + 2 * spacing, labelWidth, height);
        add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(xValue + labelWidth, yValue + 2 * spacing, fieldWidth, height);
        add(phoneField);

        JLabel doctorLabel = new JLabel("담당의");
        doctorLabel.setBounds(xValue, yValue + 3 * spacing, labelWidth, height);
        add(doctorLabel);

        doctorField = new JTextField();
        doctorField.setBounds(xValue + labelWidth, yValue + 3 * spacing, fieldWidth, height);
        add(doctorField);

// 오른쪽 열 - 레이블 및 텍스트 필드
        xValue += labelWidth + fieldWidth + spacing; // 오른쪽 열로 이동

        JLabel dateLabel = new JLabel("날짜");
        dateLabel.setBounds(xValue, yValue, labelWidth, height);
        add(dateLabel);

        dateField = new JTextField();
        dateField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        add(dateField);

        JLabel timeLabel = new JLabel("시간");
        timeLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(timeLabel);

        timeField = new JTextField();
        timeField.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        add(timeField);

        JLabel cautionLabel = new JLabel("내원 목적");
        cautionLabel.setBounds(xValue, yValue + 2 * spacing, labelWidth, height);
        add(cautionLabel);

        cautionArea = new JTextArea(15, 15);
        cautionArea.setBounds(xValue + labelWidth, yValue + 2 * spacing, fieldWidth, 2 * height);
        add(cautionArea);

        JButton modifyButton = new JButton("수정");
        modifyButton.addActionListener(this);
        modifyButton.setBounds(xValue, yValue + 3 * spacing, 120, height);
        add(modifyButton);

        JButton deleteButton = new JButton("삭제");
        deleteButton.addActionListener(this);
        deleteButton.setBounds(xValue + 140, yValue + 3 * spacing, 120, height);
        add(deleteButton);
    }

    public void setData(String date, String time) {
        String query = "SELECT * FROM reservation JOIN patient ON reservation.patientid = patient.patientid JOIN staff ON reservation.staffid = staff.staffid" +
                " WHERE reservationdate = '" + date + "' AND reservationtime = '" + time + "'";
        try (ResultSet rs = st.executeQuery(query)) {
            rs.next();
            reservationId = rs.getInt("reservationid");
            patientField.setText(rs.getString("patient.name"));
            patientIdField.setText(rs.getString("patient.identitynumber"));
            cautionArea.setText(rs.getString("note"));
            phoneField.setText(rs.getString("patient.phone"));
            dateField.setText(rs.getString("reservationdate"));
            timeField.setText(rs.getString("reservationtime"));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "알 수 없는 오류가 발생하였습니다.");
            e.printStackTrace();
        }
    }

    public void setPatientListPanel(ReservationListPanel reservationListPanel) {
        this.reservationListPanel = reservationListPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //수정 버튼 클릭 시
        if (e.getActionCommand().equals("수정")) {
            if (JOptionPane.showConfirmDialog(this, "수정하시겠습니까?") == 0) {
                String query = "UPDATE reservation SET reservationdate = ?, reservationtime = ?, phone = ?, note = ? WHERE reservationid = ?";
                try {
                    PreparedStatement pstm = conn.prepareStatement(query);
                    pstm.setString(1, dateField.getText().isEmpty() ? null : dateField.getText());
                    pstm.setString(2, timeField.getText().isEmpty() ? null : timeField.getText());
                    pstm.setString(3, phoneField.getText().isEmpty() ? null : phoneField.getText());
                    pstm.setString(4, cautionArea.getText().isEmpty() ? null : cautionArea.getText());
                    pstm.setInt(5, reservationId);
                    if (pstm.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(this, "수정 성공");
                    } else {
                        JOptionPane.showMessageDialog(this, "수정 실패");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "알 수 없는 오류가 발생하였습니다.");
                    ex.printStackTrace();
                }
            }
        } else if (e.getActionCommand().equals("삭제")) {
            if (JOptionPane.showConfirmDialog(null, patientField.getText() + " 예약을 정말 취소하시겠습니까?") == 0) {
                String query = "DELETE FROM reservation WHERE reservationid = ?";
                try {
                    PreparedStatement pstm = conn.prepareStatement(query);
                    pstm.setInt(1, reservationId);
                    if (pstm.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(this, "예약 취소");
                    } else {
                        JOptionPane.showMessageDialog(this, "예약 취소 실패");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "알 수 없는 오류가 발생하였습니다.");
                    ex.printStackTrace();
                }
            }
        }
    }
}

class ReservationAddWindow extends JFrame implements ActionListener {
    Connection conn;
    JTextField nameField, phoneField, idField1, idField2, heightField, weightField, addressField;
    JTextArea cautionArea;
    JRadioButton male, female, A, B, O, AB;

    public ReservationAddWindow(Connection conn) {
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
        int labelWidth = 100;
        int fieldWidth = 150;
        int height = 30;

        // Left column components
        JLabel nameLabel = new JLabel("*이름");
        nameLabel.setBounds(xValue, yValue, labelWidth, 30);
        mainPanel.add(nameLabel);

        nameField = new JTextField(5);
        nameField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        mainPanel.add(nameField);

        JLabel idLabel = new JLabel("*주민번호");
        idLabel.setBounds(xValue, yValue + 50, labelWidth, height);
        mainPanel.add(idLabel);

        idField1 = new JTextField(6);
        idField1.setBounds(xValue + labelWidth, yValue + 50, 70, height);
        mainPanel.add(idField1);

        JLabel bar = new JLabel("-");
        bar.setBounds(xValue + 173, yValue + 50, 4, height);
        mainPanel.add(bar);

        idField2 = new JTextField(7);
        idField2.setBounds(xValue + 180, yValue + 50, 70, height);
        mainPanel.add(idField2);

        JLabel genderLabel = new JLabel("*성별");
        genderLabel.setBounds(xValue, yValue + 100, labelWidth, height);
        mainPanel.add(genderLabel);

        ButtonGroup genderGroup = new ButtonGroup();
        male = new JRadioButton("남");
        male.setSelected(true);
        female = new JRadioButton("여");

        genderGroup.add(male);
        genderGroup.add(female);

        male.setBounds(xValue + labelWidth, yValue + 100, 70, height);
        female.setBounds(xValue + labelWidth + 80, yValue + 100, 70, height);
        mainPanel.add(male);
        mainPanel.add(female);

        JLabel bloodTypeLabel = new JLabel("혈액형");
        bloodTypeLabel.setBounds(xValue, yValue + 150, labelWidth, height);
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

        A.setBounds(xValue + labelWidth, yValue + 150, 35, height);
        B.setBounds(xValue + labelWidth + 35, yValue + 150, 35, height);
        O.setBounds(xValue + labelWidth + 70, yValue + 150, 35, height);
        AB.setBounds(xValue + labelWidth + 105, yValue + 150, 45, height);
        mainPanel.add(A);
        mainPanel.add(B);
        mainPanel.add(O);
        mainPanel.add(AB);

        JLabel cautionLabel = new JLabel("주의사항");
        cautionLabel.setBounds(xValue, yValue + 200, labelWidth, height);
        mainPanel.add(cautionLabel);

        cautionArea = new JTextArea(15, 13);
        cautionArea.setBounds(xValue + labelWidth, yValue + 200, fieldWidth, height * 3);
        mainPanel.add(cautionArea);

        xValue += 300;
        // Right column components
        JLabel phoneLabel = new JLabel("연락처");
        phoneLabel.setBounds(xValue, yValue, labelWidth, height);
        mainPanel.add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        mainPanel.add(phoneField);

        JLabel heightLabel = new JLabel("키");
        heightLabel.setBounds(xValue, yValue + 50, labelWidth, height);
        mainPanel.add(heightLabel);

        heightField = new JTextField();
        heightField.setBounds(xValue + labelWidth, yValue + 50, fieldWidth, height);
        mainPanel.add(heightField);

        JLabel weightLabel = new JLabel("몸무게");
        weightLabel.setBounds(xValue, yValue + 100, labelWidth, height);
        mainPanel.add(weightLabel);

        weightField = new JTextField();
        weightField.setBounds(xValue + labelWidth, yValue + 100, fieldWidth, height);
        mainPanel.add(weightField);

        JLabel addressLabel = new JLabel("주소");
        addressLabel.setBounds(xValue, yValue + 150, labelWidth, height);
        mainPanel.add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(xValue + labelWidth, yValue + 150, fieldWidth, height);
        mainPanel.add(addressField);

        JButton addButton = new JButton("추가");
        addButton.addActionListener(this);
        addButton.setBounds(xValue, yValue + 200, labelWidth + fieldWidth, height);
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

