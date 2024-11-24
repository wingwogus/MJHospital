package MJHospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
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

        reservationDetailsPanel.setReservationListPanel(reservationListPanel);
        reservationDetailsPanel.setReservationConditionPanel(reservationConditionPanel);

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

        //레이아웃 설정
        int xValue = 150;
        int yValue = 40;
        int labelWidth = 60;
        int fieldWidth = 200;
        int height = 30;
        int spacing = 50;

        JLabel nameLabel = new JLabel("환자 이름");
        nameLabel.setBounds(xValue, yValue, labelWidth, height);
        add(nameLabel);

        patientField = new JTextField();
        patientField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        add(patientField);

        JLabel idLabel = new JLabel("주민번호");
        idLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(idLabel);

        idField = new NumberTextField(13);
        idField.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        add(idField);

        xValue += labelWidth + fieldWidth + spacing;

        JLabel addressLabel = new JLabel("담당의");
        addressLabel.setBounds(xValue, yValue, labelWidth, height);
        add(addressLabel);

        doctorField = new JTextField();
        doctorField.setBounds(xValue + labelWidth, yValue, fieldWidth, height);
        add(doctorField);

        JLabel phoneLabel = new JLabel("전화번호");
        phoneLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(phoneLabel);

        phoneField = new NumberTextField(11);
        phoneField.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        add(phoneField);

        xValue += labelWidth + fieldWidth + spacing;

        //날짜 설정
        Integer[] years = new Integer[100];

        for (int i = 0; i < 100; i++) {
            years[i] = now.getYear() - 50 + i;
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

        year.setSelectedItem(now.getYear());
        year2.setSelectedItem(now.plusDays(7).getYear());
        month.setSelectedItem(now.getMonthValue());
        month2.setSelectedItem(now.plusDays(7).getMonthValue());
        day.setSelectedItem(now.getDayOfMonth());
        day2.setSelectedItem(now.plusDays(7).getDayOfMonth());
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
            new ReservationAddWindow(conn, this).setVisible(true);
        } else if (e.getSource() == year || e.getSource() == month) {
            setDate(year, month, day);
        } else if (e.getSource() == year2 || e.getSource() == month2) {
            setDate(year2, month2, day2);
        } else {
            searchReservation();
        }
    }

    //년도와 월 선택에 따라 일수가 달라지는 기능
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

    //조건에 맞는 예약을 리스트에 띄우는 기능
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
                    row.add(rs.getString("patient.patientid"));
                    dataVector.add(row);
                }
                isFirst(dataVector);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "알 수 없는 오류가 발생하였습니다.");
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public void isFirst(Vector<Vector<String>> dataVector) {
        for (Vector<String> row : dataVector) {
            String query = "SELECT * FROM consultation WHERE patientid = " + row.elementAt(4);
            try (ResultSet rs = st.executeQuery(query)) {
                if (rs.next()) row.setElementAt("재진", 4);
                else row.setElementAt("초진", 4);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        reservationListPanel.setData(dataVector);
    }
}

class ReservationListPanel extends JPanel implements MouseListener {
    JTable table;
    DefaultTableModel tableModel;
    ReservationDetailPanel reservationDetailPanel;

    public ReservationListPanel(ReservationDetailPanel reservationDetailPanel) {
        this.reservationDetailPanel = reservationDetailPanel;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("예약 목록"));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setPreferredSize(new Dimension(400, 600));

        String[] columnNames = {"예약 날짜", "예약 시간", "환자명", "담당의", "재진 여부"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        table.setDefaultEditor(Object.class, null);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);
        table.getColumnModel().getColumn(3).setPreferredWidth(50);
        table.getColumnModel().getColumn(4).setPreferredWidth(50);

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane);
        add(tablePanel, BorderLayout.CENTER);

        table.addMouseListener(this);
    }

    //표 데이터 설정하는 기능
    public void setData(Vector<Vector<String>> inputData) {
        tableModel.setRowCount(0);
        for (Vector<String> row : inputData) {
            tableModel.addRow(row);
        }
    }

    //클릭 시 상세 정보 창에 정보 띄우기
    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        LocalDate date = LocalDate.parse(tableModel.getValueAt(row, 0).toString());
        LocalTime time = LocalTime.parse(tableModel.getValueAt(row, 1).toString());
        String patientName = tableModel.getValueAt(row, 2).toString();
        String doctorName = tableModel.getValueAt(row, 3).toString();
        reservationDetailPanel.setData(date, time, patientName, doctorName);
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

class ReservationDetailPanel extends JPanel implements ActionListener, MouseListener {
    JTextField patientField, patientIdField, phoneField, doctorField;
    JTextArea noteArea;
    JComboBox<Integer> year, month, day, hour, minute;
    int reservationId;
    Connection conn;
    Statement st;
    ReservationListPanel reservationListPanel;
    ReservationConditionPanel reservationConditionPanel;
    JTable table;
    DefaultTableModel tableModel;

    public ReservationDetailPanel(Connection conn, Statement st) {
        this.conn = conn;
        this.st = st;

        int xValue = 130;
        int yValue = 60;
        int labelWidth = 60;
        int fieldWidth = 200;
        int height = 30;
        int spacing = 60;

        setLayout(null);
        setBorder(BorderFactory.createTitledBorder("예약 정보"));

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

        phoneField = new NumberTextField(11);
        phoneField.setBounds(xValue + labelWidth, yValue + 2 * spacing, fieldWidth, height);
        add(phoneField);

        JLabel doctorLabel = new JLabel("담당의");
        doctorLabel.setBounds(xValue, yValue + 3 * spacing, labelWidth, height);
        add(doctorLabel);

        doctorField = new JTextField();
        doctorField.setBounds(xValue + labelWidth, yValue + 3 * spacing, fieldWidth - 70, height);
        add(doctorField);

        JButton searchButton = new JButton("검색");
        searchButton.addActionListener(this);
        searchButton.setBounds(xValue + labelWidth + fieldWidth - 60, yValue + 3 * spacing, 60, height);
        add(searchButton);

        String[] columnNames = {"의사 이름", "전공", "주민번호"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.addMouseListener(this);

        table.setDefaultEditor(Object.class, null);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(xValue, yValue + 4 * spacing, labelWidth + fieldWidth, 3 * height);
        add(scrollPane);

        xValue += labelWidth + fieldWidth + spacing; // 오른쪽 열로 이동

        JLabel dateLabel = new JLabel("날짜");
        dateLabel.setBounds(xValue, yValue, labelWidth, height);
        add(dateLabel);

        LocalDate now = LocalDate.now();

        Integer[] years = new Integer[100];

        for (int i = 0; i < 100; i++) {
            years[i] = now.getYear() - 50 + i;
        }

        Integer[] months = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

        Integer[] days = new Integer[31];
        for (int i = 0; i < 31; i++) {
            days[i] = i + 1;
        }

        year = new JComboBox<>(years);
        month = new JComboBox<>(months);
        day = new JComboBox<>(days);
        year.setSelectedItem(now.getYear());
        month.setSelectedItem(now.getMonthValue());
        day.setSelectedItem(now.getDayOfMonth());

        year.setBounds(xValue + labelWidth, yValue, 60, height);
        month.setBounds(xValue + labelWidth + 60, yValue, 40, height);
        day.setBounds(xValue + labelWidth + 100, yValue, 40, height);

        add(year);
        add(month);
        add(day);

        JLabel timeLabel = new JLabel("시간");
        timeLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        add(timeLabel);

        Integer[] hours = new Integer[9];
        for (int i = 0; i < hours.length; i++) {
            hours[i] = i + 9;
        }

        Integer[] minutes = new Integer[6];
        for (int i = 0; i < minutes.length; i++) {
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

        noteArea = new JTextArea(15, 15);
        noteArea.setBounds(xValue + labelWidth, yValue + 2 * spacing, fieldWidth, 3 * height);
        add(noteArea);

        JButton modifyButton = new JButton("수정");
        modifyButton.setBounds(xValue, yValue + 4 * spacing, 120, height);
        add(modifyButton);

        JButton deleteButton = new JButton("삭제");
        deleteButton.setBounds(xValue + 140, yValue + 4 * spacing, 120, height);
        add(deleteButton);

        modifyButton.addActionListener(this);
        deleteButton.addActionListener(this);
        year.addActionListener(this);
        month.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getActionCommand().equals("수정")) modifyReservation();
            else if (e.getActionCommand().equals("삭제")) deleteReservation();
            else if (e.getActionCommand().equals("검색")) searchDoctor(doctorField.getText());
            else if (e.getSource() == year || e.getSource() == month) setDate(year, month, day);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "알 수 없는 오류가 발생하였습니다.");
            ex.printStackTrace();
        }
    }

    //의사 검색 시 이름에 맞는 목록 출력
    public void searchDoctor(String name) {
        String query = "SELECT name, major, identitynumber FROM staff WHERE roleid = 1 AND is_active = 1 AND name LIKE '%" + name + "%'";
        Vector<Vector<String>> dataVector = new Vector<>();

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("name"));
                row.add(rs.getString("major"));
                row.add(rs.getString("identitynumber"));
                dataVector.add(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "알 수 없는 오류가 발생하였습니다.");
            ex.printStackTrace();
        }

        tableModel.setRowCount(0);
        for (Vector<String> row : dataVector) {
            tableModel.addRow(row);
        }
    }

    //상세 정보 세팅 기능
    public void setData(LocalDate date, LocalTime time, String patientName, String doctorName) {
        String query = "SELECT * FROM reservation JOIN patient ON reservation.patientid = patient.patientid JOIN staff ON reservation.staffid = staff.staffid" +
                " WHERE patient.name = '" + patientName + "' AND staff.name = '" + doctorName + "' AND reservationdate = '" + date + "' AND reservationtime = '" + time + "'";

        try (ResultSet rs = st.executeQuery(query)) {
            rs.next();
            reservationId = rs.getInt("reservationid");
            patientField.setText(rs.getString("patient.name"));
            patientIdField.setText(rs.getString("patient.identitynumber"));
            doctorField.setText(rs.getString("staff.name"));
            noteArea.setText(rs.getString("note"));
            phoneField.setText(rs.getString("patient.phone"));
            year.setSelectedItem(date.getYear());
            month.setSelectedItem(date.getMonthValue());
            day.setSelectedItem(date.getDayOfMonth());
            hour.setSelectedItem(time.getHour());
            minute.setSelectedItem(time.getMinute());
            tableModel.setRowCount(0);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "알 수 없는 오류가 발생하였습니다.");
            e.printStackTrace();
        }
    }

    //의존성 주입
    public void setReservationListPanel(ReservationListPanel reservationListPanel) {
        this.reservationListPanel = reservationListPanel;
    }

    //년도와 월 선택 시 날짜 일수 설정 기능
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

    //예약 수정 기능
    public void modifyReservation() throws SQLException {
        if (JOptionPane.showConfirmDialog(this, "수정하시겠습니까?") == 0) {
            String query = "UPDATE reservation SET reservationdate = ?, reservationtime = ?, staffid = (SELECT staffid FROM staff WHERE name = ?), " +
                    "note = ? WHERE reservationid = ?";
            PreparedStatement pstm = conn.prepareStatement(query);

            int reservationYear = Integer.parseInt(year.getSelectedItem().toString());
            int reservationMonth = Integer.parseInt(month.getSelectedItem().toString());
            int reservationDay = Integer.parseInt(day.getSelectedItem().toString());
            LocalDate reservationDate = LocalDate.of(reservationYear, reservationMonth, reservationDay);

            int reservationHour = Integer.parseInt(hour.getSelectedItem().toString());
            int reservationMin = Integer.parseInt(minute.getSelectedItem().toString());
            LocalTime reservationTime = LocalTime.of(reservationHour, reservationMin);

            pstm.setString(1, reservationDate.toString());
            pstm.setString(2, reservationTime.toString());
            pstm.setString(3, doctorField.getText());
            pstm.setString(4, noteArea.getText().isEmpty() ? null : noteArea.getText());
            pstm.setInt(5, reservationId);
            if (pstm.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "수정 성공");
                reservationConditionPanel.searchReservation();
            } else {
                JOptionPane.showMessageDialog(this, "수정 실패");
            }
        }
    }

    //예약 삭제 기능
    public void deleteReservation() throws SQLException {
        if (JOptionPane.showConfirmDialog(null, patientField.getText() + " 예약을 정말 취소하시겠습니까?") == 0) {
            String query = "DELETE FROM reservation WHERE reservationid = ?";
            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setInt(1, reservationId);
            if (pstm.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "예약 취소 성공");
                reservationConditionPanel.searchReservation();
            } else {
                JOptionPane.showMessageDialog(this, "예약 취소 실패");
            }
        }
    }

    public void setReservationConditionPanel(ReservationConditionPanel reservationConditionPanel) {
        this.reservationConditionPanel = reservationConditionPanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        doctorField.setText(tableModel.getValueAt(row, 0).toString());

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

class ReservationAddWindow extends JFrame implements ActionListener, MouseListener {
    Connection conn;
    JTextField patientField, idField, doctorField;
    JTextArea noteArea;
    JTable table;
    DefaultTableModel tableModel;
    JComboBox<Integer> year, month, day, hour, minute;
    JButton patientSearchButton, doctorSearchButton;
    JPanel mainPanel;
    JScrollPane scrollPane;
    String doctorId;
    ReservationConditionPanel reservationConditionPanel;
    boolean patientTurn = true;

    public ReservationAddWindow(Connection conn, ReservationConditionPanel reservationConditionPanel) {
        this.conn = conn;
        this.reservationConditionPanel = reservationConditionPanel;

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

        mainPanel = new JPanel();
        mainPanel.setLayout(null);

        int xValue = 20;
        int yValue = 40;
        int labelWidth = 100;
        int fieldWidth = 150;
        int height = 30;
        int spacing = 50;

        JLabel nameLabel = new JLabel("*이름");
        nameLabel.setBounds(xValue, yValue, labelWidth, 30);
        mainPanel.add(nameLabel);

        patientField = new JTextField(5);
        patientField.setBounds(xValue + labelWidth, yValue, fieldWidth - 70, height);
        mainPanel.add(patientField);

        patientSearchButton = new JButton("검색");
        patientSearchButton.setBounds(xValue + labelWidth + fieldWidth - 60, yValue, 60, 30);
        mainPanel.add(patientSearchButton);

        JLabel idLabel = new JLabel("*주민번호");
        idLabel.setBounds(xValue, yValue + spacing, labelWidth, height);
        mainPanel.add(idLabel);

        idField = new JTextField();
        idField.setEditable(false);
        idField.setBounds(xValue + labelWidth, yValue + spacing, fieldWidth, height);
        mainPanel.add(idField);

        JLabel doctorLabel = new JLabel("담당의");
        doctorLabel.setBounds(xValue, yValue + 2 * spacing, labelWidth, height);
        mainPanel.add(doctorLabel);

        doctorField = new JTextField();
        doctorField.setBounds(xValue + labelWidth, yValue + 2 * spacing, fieldWidth - 70, height);
        mainPanel.add(doctorField);

        doctorSearchButton = new JButton("검색");
        doctorSearchButton.setBounds(xValue + labelWidth + fieldWidth - 60, yValue + 2 * spacing, 60, 30);
        mainPanel.add(doctorSearchButton);

        JLabel dateLabel = new JLabel("날짜");
        dateLabel.setBounds(xValue, yValue + 3 * spacing, labelWidth, height);
        mainPanel.add(dateLabel);

        LocalDate now = LocalDate.now();

        Integer[] years = new Integer[50];
        // 현재 년도
        for (int i = 0; i < years.length; i++) {
            years[i] = now.getYear() + i;
        }

        Integer[] months = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

        Integer[] days = new Integer[31];
        for (int i = 0; i < days.length; i++) {
            days[i] = i + 1;
        }

        year = new JComboBox<>(years);
        month = new JComboBox<>(months);
        day = new JComboBox<>(days);

        year.setSelectedItem(now.getYear());
        month.setSelectedItem(now.getMonthValue());
        day.setSelectedItem(now.getDayOfMonth());

        year.setBounds(xValue + labelWidth, yValue + 3 * spacing, 60, height);
        month.setBounds(xValue + labelWidth + 60, yValue + 3 * spacing, 40, height);
        day.setBounds(xValue + labelWidth + 100, yValue + 3 * spacing, 40, height);

        mainPanel.add(year);
        mainPanel.add(month);
        mainPanel.add(day);

        JLabel timeLabel = new JLabel("시간");
        timeLabel.setBounds(xValue, yValue + 4 * spacing, labelWidth, height);
        mainPanel.add(timeLabel);

        Integer[] hours = new Integer[9];
        for (int i = 0; i < hours.length; i++) {
            hours[i] = i + 9;
        }

        Integer[] minutes = new Integer[6];
        for (int i = 0; i < minutes.length; i++) {
            minutes[i] = i * 10;
        }

        hour = new JComboBox<>(hours);
        minute = new JComboBox<>(minutes);
        hour.setBounds(xValue + labelWidth, yValue + 4 * spacing, 50, height);
        minute.setBounds(xValue + labelWidth + 50, yValue + 4 * spacing, 50, height);

        mainPanel.add(hour);
        mainPanel.add(minute);

        JLabel cautionLabel = new JLabel("내원목적");
        cautionLabel.setBounds(xValue, yValue + 5 * spacing, labelWidth, height);
        mainPanel.add(cautionLabel);

        noteArea = new JTextArea(15, 13);
        noteArea.setBounds(xValue + labelWidth, yValue + 5 * spacing, fieldWidth, height * 3);
        mainPanel.add(noteArea);

        xValue += labelWidth + fieldWidth + spacing;

        scrollPane = new JScrollPane();
        scrollPane.setBounds(xValue, yValue, labelWidth + fieldWidth, 8 * height);
        mainPanel.add(scrollPane);

        JButton addButton = new JButton("추가");
        addButton.addActionListener(this);
        addButton.setBounds(xValue, yValue + 5 * spacing, labelWidth + fieldWidth, height);
        mainPanel.add(addButton);

        patientSearchButton.addActionListener(this);
        doctorSearchButton.addActionListener(this);
        year.addActionListener(this);
        month.addActionListener(this);
        patientField.addActionListener(this);
        doctorField.addActionListener(this);

        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("추가")) {
            if (JOptionPane.showConfirmDialog(this, patientField.getText() + " 환자 예약을 추가하시겠습니까?") == 0) {
                addReservation();
            }
        } else if (e.getSource() == patientSearchButton || e.getSource() == patientField) {
            setPatientData(patientField.getText());
            patientTurn = true;
        } else if (e.getSource() == doctorSearchButton || e.getSource() == doctorField) {
            setDoctorData(doctorField.getText());
            patientTurn = false;
        } else if(e.getSource() == year || e.getSource() == month) {
            setDate(year, month, day);
        }
    }

    //예약 추가 기능
    public void addReservation() {
        int reservationYear = Integer.parseInt(year.getSelectedItem().toString());
        int reservationMonth = Integer.parseInt(month.getSelectedItem().toString());
        int reservationDay = Integer.parseInt(day.getSelectedItem().toString());
        LocalDate reservationDate = LocalDate.of(reservationYear, reservationMonth, reservationDay);
        int reservationHour = Integer.parseInt(hour.getSelectedItem().toString());
        int reservationMin = Integer.parseInt(minute.getSelectedItem().toString());
        LocalTime reservationTime = LocalTime.of(reservationHour, reservationMin);

        String query = "INSERT INTO reservation(patientid, staffid, note, reservationdate, reservationtime) " +
                "VALUES ((SELECT patientid FROM patient WHERE identitynumber = ?), (SELECT staffid FROM staff WHERE identitynumber = ?), ?, ?, ?)";
        try {
            PreparedStatement pstm = conn.prepareStatement(query);
            if (patientField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "환자 이름을 입력하세요");
                patientField.requestFocus();
            } else if (doctorField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "담당의 이름을 입력하세요");
                doctorField.requestFocus();
            } else if (LocalDate.now().isAfter(reservationDate)) {
                JOptionPane.showMessageDialog(this, "오늘 이후의 날짜를 선택해주세요");
            } else {
                pstm.setString(1, idField.getText());
                pstm.setString(2, doctorId);
                pstm.setString(3, noteArea.getText().isEmpty() ? null : noteArea.getText());
                pstm.setString(4, reservationDate.toString());
                pstm.setString(5, reservationTime.toString());
                if (pstm.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "추가 성공");
                    reservationConditionPanel.searchReservation();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "추가 실패");
                }
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "해당 날짜와 시간에는 이미 예약이 있습니다");
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "키와 몸무게는 숫자를 입력해주세요");
        }

    }

    public void setPatientData(String name) {
        String query = "SELECT * FROM patient WHERE name LIKE '%" + name + "%'";
        Vector<Vector<String>> dataVector = new Vector<>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("name") + "[" + String.format("%04d", rs.getInt("patientid")) + "]");
                row.add(rs.getString("gender"));
                row.add(rs.getString("identitynumber"));
                dataVector.add(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "알 수 없는 오류가 발생하였습니다.");
            ex.printStackTrace();
        }

        String[] columnNames = {"환자 이름", "성별", "주민번호"};
        setTable(columnNames, dataVector);
    }

    public void setDoctorData(String name) {
        String query = "SELECT name, major, identitynumber FROM staff WHERE roleid = 1 AND is_active = 1 AND name LIKE '%" + name + "%'";
        Vector<Vector<String>> dataVector = new Vector<>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("name"));
                row.add(rs.getString("major"));
                row.add(rs.getString("identitynumber"));
                dataVector.add(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "알 수 없는 오류가 발생하였습니다.");
            ex.printStackTrace();
        }

        String[] columnNames = {"의사 이름", "전공", "주민번호"};
        setTable(columnNames, dataVector);
    }

    public void setTable(String[] columnNames, Vector<Vector<String>> dataVector) {
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.addMouseListener(this);

        for (Vector<String> row : dataVector) {
            tableModel.addRow(row);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(30);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.setDefaultEditor(Object.class, null);

        scrollPane.setViewportView(table);
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

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        if (patientTurn) {
            String name = tableModel.getValueAt(row, 0).toString();
            patientField.setText(name.substring(0, name.length() - 6));
            idField.setText(tableModel.getValueAt(row, 2).toString());
        } else {
            doctorField.setText(tableModel.getValueAt(row, 0).toString());
            doctorId = (String) tableModel.getValueAt(row, 2);
        }
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