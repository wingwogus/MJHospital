package MJHospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
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
        LocalDate now = LocalDate.now();
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
        int currentYear = now.getYear(); // 현재 년도
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

        year.setSelectedItem(currentYear);
        year2.setSelectedItem(currentYear);
        month.setSelectedItem(now.getMonthValue());
        month2.setSelectedItem(now.getMonthValue());
        day.setSelectedItem(now.getDayOfMonth());
        day2.setSelectedItem(now.getDayOfMonth());
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
        year.addActionListener(this);
        month.addActionListener(this);
        year2.addActionListener(this);
        month2.addActionListener(this);

        searchReservation();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("추가")) {
            new ReservationAddWindow(conn).setVisible(true);
        } else if (e.getSource() == year || e.getSource() == month) {
            setDate(year, month, day);
        } else if (e.getSource() == year2 || e.getSource() == month2) {
            setDate(year2, month2, day2);
        } else {
            searchReservation();
        }
    }

    public void setDate(JComboBox<Integer> year, JComboBox<Integer> month, JComboBox<Integer> day) {
        LocalDate date = LocalDate.of(Integer.parseInt(year.getSelectedItem().toString()), Integer.parseInt(month.getSelectedItem().toString()), 1);
        LocalDate afterDate = date.plusMonths(1);
        Vector<Integer> days = new Vector<>();
        while (date.isBefore(afterDate)) {
            days.add(date.getDayOfMonth());
            date = date.plusDays(1);
        }
        day.setModel(new DefaultComboBoxModel<>(days));
    }

    public void searchReservation() {
        String patientName = patientField.getText();
        String id = idField.getText();
        String phone = phoneField.getText();
        String doctorName = doctorField.getText();
        LocalDate startDay = LocalDate.of(Integer.parseInt(year.getSelectedItem().toString()), Integer.parseInt(month.getSelectedItem().toString()), Integer.parseInt(day.getSelectedItem().toString()));
        LocalDate endDay = LocalDate.of(Integer.parseInt(year2.getSelectedItem().toString()), Integer.parseInt(month2.getSelectedItem().toString()), Integer.parseInt(day2.getSelectedItem().toString()));

        if (startDay.isAfter(endDay)) {
            JOptionPane.showMessageDialog(this, "검색하려는 날짜의 범위를 올바르게 지정해주세요");
        } else {
            String query = "SELECT * FROM reservation JOIN patient ON reservation.patientid = patient.patientid JOIN staff ON reservation.staffid = staff.staffid" +
                    " WHERE reservationdate >= '" + startDay + "' and reservationdate <= '" + endDay + "'";

            if (!patientName.isEmpty()) query += " AND patient.name LIKE '%" + patientName + "%'";
            if (!id.isEmpty()) query += " AND patient.identitynumber LIKE '%" + id + "%'";
            if (!phone.isEmpty()) query += " AND patient.phone LIKE '%" + phone + "%'";
            if (!doctorName.isEmpty()) query += " AND staff.name LIKE '%" + doctorName + "%'";

            query += " ORDER BY reservationdate, reservationtime";

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
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }


    }
}

class ReservationListPanel extends JPanel implements MouseListener{
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
        LocalDate date = LocalDate.parse(tableModel.getValueAt(row, 0).toString());
        LocalTime time = LocalTime.parse(tableModel.getValueAt(row, 1).toString());
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
    JComboBox<Integer> year, month, day, hour, minute;
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

        LocalDate now = LocalDate.now();

        Integer[] years = new Integer[100];
        int currentYear = now.getYear(); // 현재 년도
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

        year.setBounds(xValue + labelWidth, yValue, 60, height);
        month.setBounds(xValue + labelWidth + 60, yValue, 40, height);
        day.setBounds(xValue + labelWidth + 100, yValue, 40, height);

        add(year);
        add(month);
        add(day);

        JLabel timeLabel = new JLabel("시간");
        timeLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(timeLabel);

        Integer[] hours = new Integer[10];
        for (int i = 0; i < hours.length; i++) {
            hours[i] = i + 9;
        }

        Integer[] minutes = new Integer[6];
        for (int i = 0; i < 6; i++) {
            minutes[i] = i * 10;
        }

        hour = new JComboBox<>(hours);
        minute = new JComboBox<>(minutes);
        hour.setBounds(xValue + labelWidth, yValue + spacing, 50, height);
        minute.setBounds(xValue + labelWidth + 50, yValue + spacing, 50, height);

        add(hour);
        add(minute);

        JLabel cautionLabel = new JLabel("내원 목적");
        cautionLabel.setBounds(xValue, yValue + 2 * spacing, labelWidth, height);
        add(cautionLabel);

        cautionArea = new JTextArea(15, 15);
        cautionArea.setBounds(xValue + labelWidth, yValue + 2 * spacing, fieldWidth, 2 * height);
        add(cautionArea);

        JButton modifyButton = new JButton("수정");
        modifyButton.setBounds(xValue, yValue + 3 * spacing, 120, height);
        add(modifyButton);

        JButton deleteButton = new JButton("삭제");
        deleteButton.setBounds(xValue + 140, yValue + 3 * spacing, 120, height);
        add(deleteButton);

        modifyButton.addActionListener(this);
        deleteButton.addActionListener(this);
        year.addActionListener(this);
        month.addActionListener(this);
    }

    public void setData(LocalDate date, LocalTime time){
        String query = "SELECT * FROM reservation JOIN patient ON reservation.patientid = patient.patientid JOIN staff ON reservation.staffid = staff.staffid" +
                " WHERE reservationdate = '" + date + "' AND reservationtime = '" + time + "'";
        try (ResultSet rs = st.executeQuery(query)) {
            rs.next();
            reservationId = rs.getInt("reservationid");
            patientField.setText(rs.getString("patient.name"));
            patientIdField.setText(rs.getString("patient.identitynumber"));
            doctorField.setText(rs.getString("staff.name"));
            cautionArea.setText(rs.getString("note"));
            phoneField.setText(rs.getString("patient.phone"));
            year.setSelectedItem(date.getYear());
            month.setSelectedItem(date.getMonthValue());
            day.setSelectedItem(date.getDayOfMonth());
            hour.setSelectedItem(time.getHour());
            minute.setSelectedItem(time.getMinute());
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
        try {
            if (e.getActionCommand().equals("수정")) modifyReservation();
            else if (e.getActionCommand().equals("삭제")) deleteReservation();
            else if (e.getSource() == year || e.getSource() == month) setDate(year, month, day);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "알 수 없는 오류가 발생하였습니다.");
            ex.printStackTrace();
        }
    }

    private void setDate(JComboBox<Integer> year, JComboBox<Integer> month, JComboBox<Integer> day) {
        LocalDate date = LocalDate.of(Integer.parseInt(year.getSelectedItem().toString()), Integer.parseInt(month.getSelectedItem().toString()), 1);
        LocalDate afterDate = date.plusMonths(1);
        Vector<Integer> days = new Vector<>();
        while (date.isBefore(afterDate)) {
            days.add(date.getDayOfMonth());
            date = date.plusDays(1);
        }
        day.setModel(new DefaultComboBoxModel<>(days));
    }

    public void modifyReservation() throws SQLException {
        if (JOptionPane.showConfirmDialog(this, "수정하시겠습니까?") == 0) {
            String query = "UPDATE reservation SET reservationdate = ?, reservationtime = ?, phone = ?, note = ? WHERE reservationid = ?";
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
        }
    }

    public void deleteReservation() throws SQLException {
        if (JOptionPane.showConfirmDialog(null, patientField.getText() + " 예약을 정말 취소하시겠습니까?") == 0) {
            String query = "DELETE FROM reservation WHERE reservationid = ?";
            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setInt(1, reservationId);
            if (pstm.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "예약 취소");
            } else {
                JOptionPane.showMessageDialog(this, "예약 취소 실패");
            }
        }
    }
}

class ReservationAddWindow extends JFrame implements ActionListener{
    Connection conn;
    JTextField nameField, idField, doctorField;
    JTextArea cautionArea;

    public ReservationAddWindow(Connection conn){
        this.conn = conn;

        setTitle("예약 추가");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(Color.LIGHT_GRAY);

        JLabel titleLabel = new JLabel("예약 추가");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        titlePanel.add(titleLabel, BorderLayout.NORTH);

        add(titlePanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);

        int xValue = 20;
        int yValue = 40;
        int labelWidth = 100;
        int fieldWidth = 200;
        int height = 30;
        int spacing = 50;

        // Left column components
        JLabel nameLabel = new JLabel("*이름");
        nameLabel.setBounds(xValue, yValue, labelWidth, 30);
        mainPanel.add(nameLabel);

        nameField = new JTextField(5);
        nameField.setBounds(xValue + labelWidth, yValue, fieldWidth - 50, height);
        mainPanel.add(nameField);

        JLabel idLabel = new JLabel("*주민번호");
        idLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        mainPanel.add(idLabel);

        idField = new JTextField(6);
        idField.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        mainPanel.add(idField);

        JLabel doctorLabel = new JLabel("담당의");
        doctorLabel.setBounds(xValue, yValue + 2 * spacing, labelWidth, height);
        mainPanel.add(doctorLabel);

        doctorField = new JTextField();
        doctorField.setBounds(xValue + labelWidth, yValue + 2 * spacing, fieldWidth, height);
        mainPanel.add(doctorField);

        JLabel dateLabel = new JLabel("날짜");
        dateLabel.setBounds(xValue, yValue + 3 * spacing, labelWidth, height);
        mainPanel.add(dateLabel);

        JLabel timeLabel = new JLabel("시간");
        timeLabel.setBounds(xValue, yValue + 4 * spacing, labelWidth, height);
        mainPanel.add(timeLabel);

        JLabel cautionLabel = new JLabel("내원목적");
        cautionLabel.setBounds(xValue, yValue + 200, labelWidth, height);
        mainPanel.add(cautionLabel);

        cautionArea = new JTextArea(15, 13);
        cautionArea.setBounds(xValue + labelWidth, yValue + 200, fieldWidth, height * 3);
        mainPanel.add(cautionArea);



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
                } else {
                    pstm.setString(1, nameField.getText());
                    pstm.setString(4, cautionArea.getText().isEmpty() ? null : cautionArea.getText());
                    pstm.setString(8, doctorField.getText().isEmpty() ? null : doctorField.getText());
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

