package MJHospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

class PatientManagement extends JPanel {
    Connection con;
    Statement st;
    JPanel patientConditionPanel = new PatientConditionPanel();
    JPanel patientListPanel = new PatientListPanel();
    JPanel patientDetailsPanel = new PatientDetailsPanel();

    public PatientManagement() {
        setLayout(new BorderLayout());

        // 환자 조건 패널 생성
        add(patientConditionPanel, BorderLayout.NORTH);

        // 환자 목록 패널 생성
        add(patientListPanel, BorderLayout.WEST);

        // 환자 상세 정보 패널 생성
        add(patientDetailsPanel, BorderLayout.CENTER);

        String url = "jdbc:mysql://hyunsql.cjwqee8gsrhn.ap-southeast-2.rds.amazonaws.com:3306/mjhospital";
        String userName = "ljh";
        String password = "1234";
        try {
            con = DriverManager.getConnection(url, userName, password);
            st = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    class PatientConditionPanel extends JPanel {
        public PatientConditionPanel() {
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
            JTextField nameField = new JTextField(10);
            patientInfoPanel.add(nameField, gbc);

            gbc.gridy++;
            JTextField idField = new JTextField(10);
            patientInfoPanel.add(idField, gbc);

            gbc.gridy++;
            JTextField genderField = new JTextField(10);
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
            JTextField phoneField = new JTextField(10);
            patientInfoPanel.add(phoneField, gbc);

            gbc.gridy++;
            JTextField bloodTypeField = new JTextField(10);
            patientInfoPanel.add(bloodTypeField, gbc);

            gbc.gridy++;
            JTextField addressField = new JTextField(10);
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
            patientInfoPanel.add(new JButton("추가"), gbc);

            // 여섯 번째 열 (키 입력 필드, 몸무게 입력 필드)
            gbc.gridx = 5;
            gbc.gridy = 0;
            JTextField heightField = new JTextField(10);
            patientInfoPanel.add(heightField, gbc);

            gbc.gridy++;
            JTextField weightField = new JTextField(10);
            patientInfoPanel.add(weightField, gbc);

            // 여섯 번째 열 (검색 버튼) - 검색 버튼이 다른 요소와 겹치지 않도록 설정
            gbc.gridx = 7;
            gbc.gridy = 0;
            gbc.gridheight = 3; // 검색 버튼이 세 줄을 차지하도록 설정
            gbc.fill = GridBagConstraints.VERTICAL; // 버튼의 높이가 세 줄을 차지하도록 설정
            JButton searchButton = new JButton("검색");
            patientInfoPanel.add(searchButton, gbc);

            add(patientInfoPanel, BorderLayout.CENTER);

            searchButton.addActionListener(e -> {
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
                if (!phone.isEmpty()) query += " AND tel3 LIKE '%" + phone + "%'";
                if (!bloodType.isEmpty()) query += " AND bloodType LIKE '%" + bloodType + "%'";
                if (!address.isEmpty()) query += " AND address LIKE '%" + address + "%'";
                if (!height.isEmpty()) query += " AND height LIKE '%" + height + "%'";
                if (!weight.isEmpty()) query += " AND weight LIKE '%" + weight + "%'";

                try (ResultSet rs = st.executeQuery(query)) {
                    ArrayList<Object[]> tempList = new ArrayList<>();
                    while (rs.next()) {
                        Object[] row = new Object[2];
                        row[0] = rs.getString("name");
                        row[1] = rs.getString("identitynumber");
                        tempList.add(row);
                    }

                    Object[][] resultData = new Object[tempList.size()][2];
                    for (int i = 0; i < tempList.size(); i++) {
                        resultData[i] = tempList.get(i);
                    }

                    PatientListPanel.setData(resultData);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
    class PatientListPanel extends JPanel {
        static JTable table;
        static DefaultTableModel tableModel;

        public PatientListPanel() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createTitledBorder("환자 목록"));

            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setPreferredSize(new Dimension(200, 600));

            String[] columnNames = {"환자명", "주민번호"};
            tableModel = new DefaultTableModel(columnNames, 0);
            table = new JTable(tableModel);
            table.setDefaultEditor(Object.class, null);
            table.getColumnModel().getColumn(0).setPreferredWidth(50);
            table.getColumnModel().getColumn(1).setPreferredWidth(150);

            JScrollPane scrollPane = new JScrollPane(table);
            tablePanel.add(scrollPane);
            add(tablePanel, BorderLayout.CENTER);
        }

        public static void setData(Object[][] inputData) {
            tableModel.setRowCount(0);
            for (Object[] row : inputData) {
                tableModel.addRow(row);
            }
            table.setModel(tableModel);
        }
    }

    class PatientDetailsPanel extends JPanel {
        public PatientDetailsPanel() {
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
            add(new JTextField(15), gbc);

            gbc.gridy++;
            add(new JTextField(15), gbc);

            gbc.gridy++;
            add(new JTextField(15), gbc);

            gbc.gridy++;
            add(new JTextField(15), gbc);

            gbc.gridy++;
            gbc.fill = GridBagConstraints.BOTH;
            add(new JScrollPane(new JTextArea(3, 15)), gbc);

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
            add(new JTextField(15), gbc);

            gbc.gridy++;
            add(new JTextField(15), gbc);

            gbc.gridy++;
            add(new JTextField(15), gbc);

            gbc.gridy++;
            add(new JTextField(15), gbc);
        }
    }
}


