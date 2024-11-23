    package MJHospital;

    import javax.swing.*;
    import javax.swing.table.DefaultTableModel;
    import java.awt.*;
    import java.awt.event.*;
    import java.sql.*;
    import java.util.Vector;

    public class Consultation extends JFrame {
        private JList<String> patientList;
        private DefaultTableModel recordTableModel;
        private JTable recordTable;
        private JComboBox<String> diseaseComboBox;
        private JTextArea symptomsArea, doctorNoteArea, prescriptionArea;

        private Connection connection;
        public String currentStaffId; // id를 로그인한 사용자 별로 동적으로 가져옴

        public Consultation(String staffId) {
            // 데이터베이스 연결
            this.currentStaffId = staffId;
            connectToDatabase();

            setTitle("MJ Hospital Management");
            setSize(1000, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            // 좌측 p1 : 환자 리스트
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.setPreferredSize(new Dimension(200, 0));
            leftPanel.setBorder(BorderFactory.createTitledBorder("환자 목록"));
            patientList = new JList<>();
            loadPatientList();
            patientList.addListSelectionListener(e -> loadPatientRecords());
            leftPanel.add(new JScrollPane(patientList), BorderLayout.CENTER);
            add(leftPanel, BorderLayout.WEST);

            // 중앙 p2 : 진료 기록
            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.setBorder(BorderFactory.createTitledBorder("진료 기록"));
            recordTableModel = new DefaultTableModel(new String[]{"날짜", "질병명", "증상"}, 0);
            recordTable = new JTable(recordTableModel);
            centerPanel.add(new JScrollPane(recordTable), BorderLayout.CENTER);
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
            saveButton.addActionListener(e -> saveMedicalRecord());

            // 구성 추가
            rightPanel.add(diseasePanel, BorderLayout.NORTH);
            rightPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
            rightPanel.add(saveButton, BorderLayout.SOUTH);
            add(rightPanel, BorderLayout.EAST);

            setVisible(true);
        }

        private void connectToDatabase() {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://hyunsql.cjwqee8gsrhn.ap-southeast-2.rds.amazonaws.com:3306/mjhospital", "ssk08078", "tjdtlghks11");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "DB 연결 실패: " + e.getMessage());
            }
        }

        private void loadPatientList() {
            try {
                DefaultListModel<String> model = new DefaultListModel<>();
                String query = "SELECT r.patientid, r.reservationtime, p.name, r.note FROM reservation r " + "JOIN patient p ON r.patientid = p.patientid";
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    String entry = rs.getInt("patientid") + " - " + rs.getString("name") +
                            " (" + rs.getString("reservationtime") + ", " + rs.getString("note") + ")";
                    model.addElement(entry);
                }
                patientList.setModel(model);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "환자 목록 로드 실패: " + e.getMessage());
            }
        }

        private void loadPatientRecords() {
            try {
                recordTableModel.setRowCount(0); // 기존 데이터 초기화
                String selected = patientList.getSelectedValue();
                if (selected == null) return;

                int patientId = Integer.parseInt(selected.split(" - ")[0]);
                String query = "SELECT c.consultationdate, d.diseasename, c.diagnosis " +
                        "FROM consultation c " +
                        "JOIN disease d ON c.diseaseid = d.diseaseid " +
                        "WHERE c.patientid = ? ORDER BY c.consultationdate DESC";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, patientId);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    recordTableModel.addRow(new Object[]{
                            rs.getDate("consultationdate"),
                            rs.getString("diseasename"),
                            rs.getString("diagnosis")
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "진료 기록 로드 실패: " + e.getMessage());
            }
        }

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

        private void saveMedicalRecord() {
            try {
                // 환자 선택 여부 확인
                String selectedPatient = patientList.getSelectedValue();
                if (selectedPatient == null) {
                    JOptionPane.showMessageDialog(this, "환자를 선택하세요!");
                    return;
                }
                int patientId = Integer.parseInt(selectedPatient.split(" - ")[0]);

                // 질병 정보 가져오기
                String disease = (String) diseaseComboBox.getSelectedItem();
                if (disease == null) {
                    JOptionPane.showMessageDialog(this, "질병을 선택하세요!");
                    return;
                }
                String[] diseaseParts = disease.split(" - ");
                String diseaseCode = diseaseParts[0]; // 질병 코드 (diseaseid)

                // 입력된 진단 정보 가져오기
                String symptoms = symptomsArea.getText();
                String doctorNote = doctorNoteArea.getText();
                String prescription = prescriptionArea.getText();

                // INSERT 쿼리
                String query = "INSERT INTO consultation (patientid, staffid, consultationdate, consultationtime, diseaseid, diagnosis, opinion, prescription) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(query);

                // PreparedStatement에 값 바인딩
                pstmt.setInt(1, patientId); // patientid
                pstmt.setString(2, currentStaffId); // staffid
                pstmt.setDate(3, new java.sql.Date(System.currentTimeMillis())); // consultationdate
                pstmt.setTime(4, new java.sql.Time(System.currentTimeMillis())); // consultationtime
                pstmt.setString(5, diseaseCode); // diseaseid
                pstmt.setString(6, symptoms); // diagnosis
                pstmt.setString(7, doctorNote); // opinion
                pstmt.setString(8, prescription); // prescription

                // 쿼리 실행
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "진료 기록이 저장되었습니다.");

                // 진료 기록 갱신
                loadPatientRecords();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "진료 기록 저장 실패: " + e.getMessage());
            }
        }

        private JPanel createLabeledPanel(String label, JTextArea textArea) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JLabel(label), BorderLayout.NORTH);
            panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
            return panel;
        }

        public static void main(String[] args) {

            String staffId = "doctor";
            SwingUtilities.invokeLater(() -> new Consultation(staffId));
        }
    }