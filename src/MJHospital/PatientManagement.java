package MJHospital;

import javax.swing.*;
import java.awt.*;

class PatientManagement extends JPanel {
    public PatientManagement() {
        setLayout(new BorderLayout());

        // 환자 조건 패널 생성
        JPanel patientConditionPanel = new PatientConditionPanel();
        add(patientConditionPanel, BorderLayout.NORTH);

        // 환자 목록 패널 생성
        JPanel patientListPanel = new PatientListPanel();
        add(patientListPanel, BorderLayout.WEST);

        // 환자 상세 정보 패널 생성
        JPanel patientDetailsPanel = new PatientDetailsPanel();
        add(patientDetailsPanel, BorderLayout.CENTER);
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
        patientInfoPanel.add(new JTextField(10), gbc);

        gbc.gridy++;
        patientInfoPanel.add(new JTextField(10), gbc);

        gbc.gridy++;
        patientInfoPanel.add(new JTextField(10), gbc);

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
        patientInfoPanel.add(new JTextField(10), gbc);

        gbc.gridy++;
        patientInfoPanel.add(new JTextField(10), gbc);

        gbc.gridy++;
        patientInfoPanel.add(new JTextField(10), gbc);

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
        patientInfoPanel.add(new JTextField(10), gbc);

        gbc.gridy++;
        patientInfoPanel.add(new JTextField(10), gbc);

// 여섯 번째 열 (검색 버튼) - 검색 버튼이 다른 요소와 겹치지 않도록 설정
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.gridheight = 3; // 검색 버튼이 세 줄을 차지하도록 설정
        gbc.fill = GridBagConstraints.VERTICAL; // 버튼의 높이가 세 줄을 차지하도록 설정
        patientInfoPanel.add(new JButton("검색"), gbc);

        add(patientInfoPanel, BorderLayout.CENTER);
    }
}

class PatientListPanel extends JPanel {
    public PatientListPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("환자 목록"));

        String[] columnNames = {"번호", "주민번호"};
        Object[][] data = {
                {"1", "000000-0000000"},
                {"2", "000000-0000000"},
                {"3", "000000-0000000"},
                {"4", "000000-0000000"},
                {"5", "000000-0000000"}
        };

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
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
