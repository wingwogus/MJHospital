package MJHospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDayChooser;
import java.util.Calendar;

public class ConsultationManagement extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel residentNumberLabel, visitDateLabel, heightWeightLabel, addressLabel, symptomsLabel, opinionLabel, prescriptionLabel, diseaseCodeLabel;
    private JCalendar calendar;
    private Connection connection;
    private Statement statement;

    public ConsultationManagement() {
        setLayout(null);

        // DB 연결
        connectToDatabase();

        // 테이블 초기화
        tableModel = new DefaultTableModel(new Object[]{"날짜", "이름", "환자아이디"}, 0);
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBounds(400, 10, 380, 510); // 테이블의 위치 및 크기 설정

        // 달력 추가
        calendar = new JCalendar();
        calendar.setBounds(10, 10, 380, 300); // 달력 크기와 위치 조정
        calendar.setTodayButtonVisible(true);
        calendar.setWeekOfYearVisible(false);
        calendar.setTodayButtonText("MOMENT IN DATE");

        // 달력 색상 설정 호출
        setCalendarColors();

        // 년도 또는 월 변경 시 이벤트 처리
        calendar.getMonthChooser().addPropertyChangeListener("month", e -> setCalendarColors());
        calendar.getYearChooser().addPropertyChangeListener("year", e -> setCalendarColors());

        // 진단서 패널
        JPanel diagnosisPanel = initializeDiagnosisPanel();
        diagnosisPanel.setBounds(800, 10, 400, 510);

        // 컴포넌트 추가
        add(calendar);
        add(diagnosisPanel);
        add(tableScrollPane);

        // 달력 클릭 이벤트
        calendar.getDayChooser().addPropertyChangeListener("day", e -> {
            Date selectedDate = calendar.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = sdf.format(selectedDate);
            loadAppointmentsForDate(dateString);
        });

        // Table 클릭 이벤트
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    loadPatientDetails(selectedRow);
                }
            }
        });
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://hyunsql.cjwqee8gsrhn.ap-southeast-2.rds.amazonaws.com:3306/mjhospital",
                    "sjso1142", "1142"
            );
            statement = connection.createStatement();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB 연결 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JPanel initializeDiagnosisPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("진단서", JLabel.CENTER);
        titleLabel.setBounds(10, 10, 380, 30);
        panel.add(titleLabel);

        residentNumberLabel = addLabel(panel, "주민번호:", 10, 100);
        visitDateLabel = addLabel(panel, "내원일:", 10, 140);
        heightWeightLabel = addLabel(panel, "키 / 몸무게:", 10, 60);
        addressLabel = addLabel(panel, "주소:", 10, 180);
        symptomsLabel = addLabel(panel, "증상:", 10, 220);
        diseaseCodeLabel = addLabel(panel, "한국질병관리번호:", 10, 260);
        opinionLabel = addLabel(panel, "의사 소견:", 10, 300);
        prescriptionLabel = addLabel(panel, "처방:", 10, 360);

        return panel;
    }

    private JLabel addLabel(JPanel panel, String text, int x, int y) {
        JLabel labelTitle = new JLabel(text);
        labelTitle.setBounds(x, y, 150, 25);
        panel.add(labelTitle);

        JLabel label = new JLabel();
        label.setBounds(x + 150, y, 200, 25);
        panel.add(label);

        return label;
    }

    private void setCalendarColors() {
        JDayChooser dayChooser = calendar.getDayChooser();
        JPanel dayPanel = dayChooser.getDayPanel();
        Component[] days = dayPanel.getComponents();

        Calendar cal = Calendar.getInstance();

        for (Component component : days) {
            if (component instanceof JButton button) {
                String buttonText = button.getText();
                if (!buttonText.isEmpty()) { // 날짜가 있는 버튼인지 확인
                    try {
                        int day = Integer.parseInt(buttonText);

                        // 현재 월과 년도로 설정
                        cal.set(Calendar.DAY_OF_MONTH, day);
                        cal.set(Calendar.MONTH, calendar.getMonthChooser().getMonth());
                        cal.set(Calendar.YEAR, calendar.getYearChooser().getYear());

                        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                        if (dayOfWeek == Calendar.SUNDAY) {
                            button.setForeground(Color.RED);
                        } else if (dayOfWeek == Calendar.SATURDAY) {
                            button.setForeground(Color.BLUE);
                        } else {
                            button.setForeground(Color.BLACK);
                        }
                    } catch (NumberFormatException e) {
                        // 무시하거나, 로그에 기록할 수 있습니다.
                        System.err.println("Invalid day format: " + buttonText);
                    }
                }
            }
        }
    }

    private void loadAppointmentsForDate(String date) {
        List<Object[]> appointments = new ArrayList<>();
        ResultSet rs = null;
        try {
            String query = "SELECT consultationdate, patientid FROM consultation WHERE consultationdate = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, date);
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String consultationDate = rs.getString("consultationdate");
                String patientId = rs.getString("patientid");
                String patientName = getPatientNameById(patientId);

                appointments.add(new Object[]{
                        consultationDate,
                        patientName,
                        patientId
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "데이터 로드 실패: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        loadDataIntoTable(appointments);
    }

    private String getPatientNameById(String patientId) {
        String patientName = "";
        ResultSet rs = null;
        try {
            String query = "SELECT name FROM patient WHERE patientid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, patientId);
            rs = preparedStatement.executeQuery();

            if (rs.next()) {
                patientName = rs.getString("name");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "환자 이름 로드 실패: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return patientName;
    }

    private void loadPatientDetails(int selectedRow) {
        String patientId = (String) table.getValueAt(selectedRow, 2);
        String consultationDate = (String) table.getValueAt(selectedRow, 0);

        ResultSet rs = null;
        try {
            // patient 테이블에서 데이터를 가져옴
            String patientQuery = "SELECT identitynumber, height, weight, address FROM patient WHERE patientid = ?";
            PreparedStatement patientPreparedStatement = connection.prepareStatement(patientQuery);
            patientPreparedStatement.setString(1, patientId);
            rs = patientPreparedStatement.executeQuery();

            if (rs.next()) {
                residentNumberLabel.setText(rs.getString("identitynumber"));
                heightWeightLabel.setText(rs.getString("height") + "cm / " + rs.getString("weight") + "kg");
                addressLabel.setText(rs.getString("address"));
            } else {
                JOptionPane.showMessageDialog(this, "해당 환자의 데이터를 찾을 수 없습니다.");
                return; // 데이터를 찾지 못했을 경우 이후 코드를 실행하지 않음
            }
            rs.close();

            // consultation 테이블에서 데이터를 가져옴
            String consultationQuery = "SELECT diagnosis, opinion, prescription, diseaseid FROM consultation WHERE patientid = ? AND consultationdate = ?";
            PreparedStatement consultationPreparedStatement = connection.prepareStatement(consultationQuery);
            consultationPreparedStatement.setString(1, patientId);
            consultationPreparedStatement.setString(2, consultationDate);
            rs = consultationPreparedStatement.executeQuery();

            if (rs.next()) {
                symptomsLabel.setText(rs.getString("diagnosis"));
                opinionLabel.setText(rs.getString("opinion"));
                prescriptionLabel.setText(rs.getString("prescription"));
                String diseaseId = rs.getString("diseaseid");

                // disease 테이블에서 한국질병관리번호를 가져옴
                String diseaseQuery = "SELECT diseaseid FROM disease WHERE diseaseid = ?";
                PreparedStatement diseasePreparedStatement = connection.prepareStatement(diseaseQuery);
                diseasePreparedStatement.setString(1, diseaseId);
                ResultSet diseaseRs = diseasePreparedStatement.executeQuery();

                if (diseaseRs.next()) {
                    diseaseCodeLabel.setText(diseaseRs.getString("diseaseid"));
                } else {
                    diseaseCodeLabel.setText("해당 질병 코드를 찾을 수 없습니다.");
                }
                diseaseRs.close();
            } else {
                JOptionPane.showMessageDialog(this, "해당 진단 데이터를 찾을 수 없습니다.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "데이터 로드 실패: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        visitDateLabel.setText(consultationDate); // consultation 테이블에서 가져온 날짜
    }

    private void loadDataIntoTable(List<Object[]> data) {
        tableModel.setRowCount(0); // 기존 데이터 삭제
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }
}