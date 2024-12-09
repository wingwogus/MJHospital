package MJHospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Vector;

public class Consultation extends JPanel {
    private JList<String> patientList;// 환자 목록 표시
    private DefaultListModel<String> patientListModel; //환자 목록 데이터 관리
    private JComboBox<String> datePickComboBox, diseaseComboBox; // 질병, 날짜 콤보박스
    private JTextArea symptomsArea, doctorNoteArea, prescriptionArea; //진료 작성
    private JTextArea diseaseArea, NsymptomsArea, NdoctorNoteArea, NprescriptionArea; //진료 기록 출력

    private Connection connection;
    public String currentStaffId; // 로그인한 사용자 ID

    public Consultation(String staffId, Connection connection) {
        setLayout(new BorderLayout());

        // 데이터베이스 연결
        this.currentStaffId = staffId;
        this.connection = connection;

        setSize(1000, 600);
        setLayout(new BorderLayout());

        // 좌측 p1 : 환자 리스트
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setBorder(BorderFactory.createTitledBorder("환자 목록"));


        patientListModel = new DefaultListModel<>();
        patientList = new JList<>(patientListModel);
        patientList.setCellRenderer(new PatientListCellRenderer());
        loadPatientList();
        patientList.addListSelectionListener(e -> loadPatientDateRecords()); //선택 이벤트 처리
        leftPanel.add(new JScrollPane(patientList), BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);


        // 중앙 p2 : 진료 기록
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("진료 기록"));

        //날짜 선택
        JPanel datePickPanel = new JPanel(new BorderLayout());
        datePickPanel.setBorder(BorderFactory.createTitledBorder("날짜 선택"));
        datePickComboBox = new JComboBox<>();
        datePickComboBox.addActionListener(e -> loadPatientConsultationRecord()); //날짜 선택시, 날짜에 맞는 환자의 진료기록 출력 이벤트
        datePickPanel.add(datePickComboBox, BorderLayout.CENTER);

        //진료 기록 출력
        diseaseArea = new JTextArea(2, 20);
        NsymptomsArea = new JTextArea(4, 20);
        NdoctorNoteArea = new JTextArea(4, 20);
        NprescriptionArea = new JTextArea(4, 20);

        diseaseArea.setEditable(false);
        NsymptomsArea.setEditable(false);
        NdoctorNoteArea.setEditable(false);
        NprescriptionArea.setEditable(false);

        JPanel PrintPanel = new JPanel();
        PrintPanel.setLayout(new BoxLayout(PrintPanel, BoxLayout.Y_AXIS));
        PrintPanel.add(createLabeledPanel("병명 :", diseaseArea));
        PrintPanel.add(createLabeledPanel("증상 :", NsymptomsArea));
        PrintPanel.add(createLabeledPanel("의사 소견 :", NdoctorNoteArea));
        PrintPanel.add(createLabeledPanel("처방 :", NprescriptionArea));

        centerPanel.add(datePickPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(PrintPanel), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);


        // 우측 p3 : 진료 작성 창
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(300, 0));
        rightPanel.setBorder(BorderFactory.createTitledBorder("진료 기록 작성"));

        // 질병 선택
        JPanel diseasePanel = new JPanel(new BorderLayout());
        diseasePanel.setBorder(BorderFactory.createTitledBorder("질병"));
        diseaseComboBox = new JComboBox<>();
        loadDiseaseList();
        diseasePanel.add(diseaseComboBox, BorderLayout.CENTER);

        // 텍스트 필드 (증상, 의사 소견, 처방)
        symptomsArea = new JTextArea(4, 20);
        doctorNoteArea = new JTextArea(4, 20);
        prescriptionArea = new JTextArea(4, 20);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.add(createLabeledPanel("증상:", symptomsArea));
        formPanel.add(createLabeledPanel("의사 소견:", doctorNoteArea));
        formPanel.add(createLabeledPanel("처방:", prescriptionArea));

        // 저장 버튼
        JButton saveButton = new JButton("저장");
        saveButton.addActionListener(e -> saveMedicalRecord()); // 저장 버튼 누를시 이벤트 발생

        // 구성 추가
        rightPanel.add(diseasePanel, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        rightPanel.add(saveButton, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);

        setVisible(true);
    }


    // 환자 목록 로드
    public void loadPatientList() {
        try {
            patientListModel.clear(); //기존 목록 초기화
            LocalDate today = LocalDate.now(); //오늘 날짜로 지정

            // 오늘 날짜로 예약된 환자 목록 조회 SQL 쿼리문
            String query = """
                        SELECT r.patientid, r.reservationtime, p.name, r.note, r.status, r.reservationdate
                        FROM reservation r
                        JOIN patient p ON r.patientid = p.patientid
                        WHERE r.reservationdate = ? AND staffid = ?
                        ORDER BY r.reservationtime ASC
                    """;

            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, today.toString());
            pstmt.setString(2, currentStaffId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // 환자 정보를 가져와서 환자 목록에 추가
                String entry = rs.getInt("patientid") + " - " + rs.getString("name") +
                        " (" + rs.getString("reservationtime") + ", " + rs.getString("note") + ")";
                // reservation 테이블에서 status = 1일 경우 (완료)표시 추가
                patientListModel.addElement(entry + (rs.getInt("status") == 1 ? " (완료)" : ""));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "환자 목록 로드 실패: " + e.getMessage());
        }
    }


// 환자의 진료날짜 기록 로드
    private void loadPatientDateRecords() {
        try {
            datePickComboBox.removeAllItems(); // 기존 데이터 초기화
            String selected = patientList.getSelectedValue(); //리스트에서 하나의 환자를 선택할 경우 해당 환자의 데이터 가져옴
            if (selected == null) return;

            int patientId = Integer.parseInt(selected.split(" - ")[0]); //선택된 환자의 id 추출

            String query = """
                SELECT c.consultationdate
                FROM consultation c
                WHERE c.patientid = ?
                ORDER BY c.consultationdate DESC
            """;

            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, patientId); // sql 쿼리문의 첫번째 매개변수에 추출한 id 값을 바인딩
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                datePickComboBox.addItem(rs.getDate("consultationdate").toString());
            }

            // 날짜가 없을 경우 알림
            if (datePickComboBox.getItemCount() == 0) {
                diseaseArea.setText("");
                NsymptomsArea.setText("");
                NdoctorNoteArea.setText("");
                NprescriptionArea.setText("");
                JOptionPane.showMessageDialog(this, "해당 환자의 진료 날짜가 없습니다.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "진료 날짜 로드 실패: " + e.getMessage());
        }
    }

    // 콤보박스로 선택된 날짜의 환자 진료기록 로드
    private void loadPatientConsultationRecord() {
        try {
            String selected = patientList.getSelectedValue();
            if (selected == null) return;

            String selectedDate = (String) datePickComboBox.getSelectedItem();
            if (selectedDate == null) return;

            int patientId = Integer.parseInt(selected.split(" - ")[0]);

            String query = """
                SELECT d.diseasename, c.diagnosis, c.opinion, c.prescription
                FROM consultation c
                JOIN disease d ON c.diseaseid = d.diseaseid
                WHERE c.patientid = ? AND c.consultationdate = ?
            """;

            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, patientId);
            pstmt.setDate(2, java.sql.Date.valueOf(selectedDate));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                diseaseArea.setText(rs.getString("diseasename"));
                NsymptomsArea.setText(rs.getString("diagnosis"));
                NdoctorNoteArea.setText(rs.getString("opinion"));
                NprescriptionArea.setText(rs.getString("prescription"));
            } else {
                diseaseArea.setText("");
                NsymptomsArea.setText("");
                NdoctorNoteArea.setText("");
                NprescriptionArea.setText("");
                JOptionPane.showMessageDialog(this, "해당 날짜에 진료 기록이 없습니다.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "진료 기록 로드 실패: " + e.getMessage());
        }
    }

    // 질병리스트 로드
    private void loadDiseaseList() {
        try {
            String query = "SELECT diseaseid, diseasename FROM disease";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                diseaseComboBox.addItem(rs.getString("diseaseid") + " - " + rs.getString("diseasename"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "질병 목록 로드 실패: " + e.getMessage());
        }
    }

    // 작성된 진료 기록 저장
    private void saveMedicalRecord() {
        try {
            String selectedPatient = patientList.getSelectedValue();
            if (selectedPatient == null) {
                JOptionPane.showMessageDialog(this, "환자를 선택하세요!");
                return;
            }
            int patientId = Integer.parseInt(selectedPatient.split(" - ")[0]);

            String disease = (String) diseaseComboBox.getSelectedItem();
            if (disease == null) {
                JOptionPane.showMessageDialog(this, "질병을 선택하세요!");
                return;
            }
            String[] diseaseParts = disease.split(" - ");
            String diseaseCode = diseaseParts[0];

            String symptoms = symptomsArea.getText();
            String doctorNote = doctorNoteArea.getText();
            String prescription = prescriptionArea.getText();

            String query = """
                        INSERT INTO consultation (patientid, staffid, consultationdate, consultationtime, diseaseid, diagnosis, opinion, prescription)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, patientId);
            pstmt.setString(2, currentStaffId);
            pstmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            pstmt.setTime(4, new java.sql.Time(System.currentTimeMillis()));
            pstmt.setString(5, diseaseCode);
            pstmt.setString(6, symptoms);
            pstmt.setString(7, doctorNote);
            pstmt.setString(8, prescription);
            pstmt.executeUpdate();

            // 상태 업데이트
            query = "UPDATE reservation SET status = 1 WHERE patientid = ?";
            pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, patientId);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "진료 기록이 저장되었습니다.");

            symptomsArea.setText("");
            doctorNoteArea.setText("");
            prescriptionArea.setText("");

            loadPatientList();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "진료 기록 저장 실패: " + e.getMessage());
        }
    }

    private JPanel createLabeledPanel(String label, JTextArea textArea) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(label), BorderLayout.NORTH);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        return panel;
    }

    private static class PatientListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JTextArea textArea = new JTextArea(value.toString());
            textArea.setWrapStyleWord(true); // 단어 단위로 줄바꿈
            textArea.setLineWrap(true); // 자동 줄바꿈 활성화
            textArea.setOpaque(true); // 배경색 보이도록 설정
            textArea.setFont(list.getFont()); // 리스트의 폰트에 맞춤
            textArea.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            textArea.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 패딩 추가

            // 진료가 완료된 항목의 색상을 회색으로 변경
            String text = value.toString();
            if (text.contains("(완료)")) {
                textArea.setForeground(Color.GRAY); // 회색 처리
            }

            return textArea;
        }
    }

}