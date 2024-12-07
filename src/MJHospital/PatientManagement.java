package MJHospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.StringTokenizer;
import java.util.Vector;

class PatientManagement extends JPanel {
    Connection conn;
    PatientListPanel patientListPanel;
    PatientConditionPanel patientConditionPanel;
    PatientDetailsPanel patientDetailsPanel;

    public PatientManagement(Connection conn) {
        setLayout(new BorderLayout());
        this.conn = conn;

        patientDetailsPanel = new PatientDetailsPanel(conn);
        patientListPanel = new PatientListPanel(patientDetailsPanel);
        patientConditionPanel = new PatientConditionPanel(patientListPanel, conn);

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
    PatientListPanel patientListPanel;
    JTextField nameField, idField, phoneField, addressField, heightField, weightField;
    JRadioButton allGender, male, female;
    JComboBox<String> bloodTypeBox, address1, address2;
    String[][] districts = {
            {},
            // 서울특별시
            {"", "강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구", "도봉구",
                    "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", "양천구", "영등포구",
                    "용산구", "은평구", "종로구", "중구", "중랑구"},
            // 부산광역시
            {"", "강서구", "금정구", "기장군", "남구", "동구", "동래구", "부산진구", "북구", "사상구", "사하구", "서구",
                    "수영구", "연제구", "영도구", "중구", "해운대구"},
            // 대구광역시
            {"", "남구", "달서구", "달성군", "동구", "북구", "서구", "수성구", "중구"},
            // 인천광역시
            {"", "강화군", "계양구", "미추홀구", "남동구", "동구", "부평구", "서구", "연수구", "옹진군", "중구"},
            // 광주광역시
            {"", "광산구", "남구", "동구", "북구", "서구"},
            // 대전광역시
            {"", "대덕구", "동구", "서구", "유성구", "중구"},
            // 울산광역시
            {"", "남구", "동구", "북구", "울주군", "중구"},
            // 세종특별자치시 (세종시는 구가 없음)
            {},
            // 경기도
            {"", "가평군", "고양시", "과천시", "광명시", "광주시", "구리시", "군포시", "김포시", "남양주시", "동두천시",
                    "부천시", "성남시", "수원시", "시흥시", "안산시", "안성시", "안양시", "양주시", "양평군", "여주시",
                    "연천군", "오산시", "용인시", "의왕시", "의정부시", "이천시", "파주시", "평택시", "포천시", "하남시",
                    "화성시"},
            // 강원도
            {"", "강릉시", "고성군", "동해시", "삼척시", "속초시", "양구군", "양양군", "영월군", "원주시", "인제군",
                    "정선군", "철원군", "춘천시", "태백시", "평창군", "홍천군", "화천군", "횡성군"},
            // 충청북도
            {"", "괴산군", "단양군", "보은군", "영동군", "옥천군", "음성군", "제천시", "증평군", "진천군", "청주시",
                    "충주시"},
            // 충청남도
            {"", "계룡시", "공주시", "금산군", "논산시", "당진시", "보령시", "부여군", "서산시", "서천군", "아산시",
                    "연기군", "예산군", "천안시", "청양군", "태안군", "홍성군"},
            // 전라북도
            {"", "고창군", "군산시", "김제시", "남원시", "무주군", "부안군", "순창군", "완주군", "익산시", "임실군",
                    "장수군", "전주시", "정읍시", "진안군"},
            // 전라남도
            {"", "강진군", "고흥군", "곡성군", "광양시", "구례군", "나주시", "담양군", "목포시", "무안군", "보성군",
                    "순천시", "신안군", "여수시", "영광군", "영암군", "완도군", "장성군", "장흥군", "진도군", "함평군",
                    "해남군", "화순군"},
            // 경상북도
            {"", "경산시", "경주시", "고령군", "구미시", "군위군", "김천시", "문경시", "봉화군", "상주시", "성주군",
                    "안동시", "영덕군", "영양군", "영주시", "영천시", "예천군", "울릉군", "울진군", "의성군", "청도군",
                    "청송군", "칠곡군", "포항시"},
            // 경상남도
            {"", "거제시", "거창군", "고성군", "김해시", "남해군", "밀양시", "사천시", "산청군", "양산시", "의령군",
                    "진주시", "창녕군", "창원시", "통영시", "하동군", "함안군", "함양군", "합천군"},
            // 제주특별자치도
            {"", "서귀포시", "제주시"}
    };

    public PatientConditionPanel(PatientListPanel patientListPanel, Connection conn) {
        this.patientListPanel = patientListPanel;
        this.conn = conn;

        setLayout(null);
        setBorder(BorderFactory.createTitledBorder("환자 조건"));
        setPreferredSize(new Dimension(1000, 175));

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

        String[] cities = {
                "전체", "서울특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시", "대전광역시", "울산광역시",
                "세종특별자치시", "경기도", "강원도", "충청북도", "충청남도", "전라북도", "전라남도", "경상북도",
                "경상남도", "제주특별자치도"
        };
        address1 = new JComboBox<>(cities);
        address1.setBounds(xValue + labelWidth, yValue + 2 * spacing, fieldWidth / 2, height);
        add(address1);
        address1.addActionListener(this);

        address2 = new JComboBox<>();
        address2.setBounds(xValue + labelWidth + 100, yValue + 2 * spacing, fieldWidth / 2, height);
        add(address2);

        addressField = new JTextField();
        addressField.setBounds(xValue + labelWidth, yValue + 2 * spacing + height, fieldWidth, height);
        add(addressField);

        setAddress();

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

        nameField.addActionListener(this);
        idField.addActionListener(this);
        phoneField.addActionListener(this);
        addressField.addActionListener(this);
        heightField.addActionListener(this);
        weightField.addActionListener(this);
        addButton.addActionListener(this);
        searchButton.addActionListener(this);

        searchButton.doClick();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getActionCommand().equals("추가")) {
                new PatientAddWindow(conn, this).setVisible(true);
            } else if (e.getActionCommand().equals("검색")) {
                searchPatient();
            } else if (e.getSource() == address1) {
                setAddress();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void setAddress() {
        int selectedIndex = address1.getSelectedIndex();

        if (selectedIndex == 0) addressField.setEditable(false);
        else addressField.setEditable(true);

        address2.removeAllItems();

        for (String district : districts[selectedIndex]) {
            address2.addItem(district);
        }
    }

    //환자 찾기
    public void searchPatient() throws SQLException {
        String query = "SELECT * FROM patient WHERE 1=1";

        //작성한 조건 필드가 있다면 WHERE 절에 조건을 붙인다
        if (!nameField.getText().isEmpty()) query += " AND name LIKE '%" + nameField.getText() + "%'";
        if (!idField.getText().isEmpty()) query += " AND identitynumber LIKE '%" + idField.getText() + "%'";
        if (male.isSelected()) query += " AND gender = '남'";
        if (female.isSelected()) query += " AND gender = '여'";
        if (!phoneField.getText().isEmpty()) query += " AND phone LIKE '%" + phoneField.getText() + "%'";
        if (!bloodTypeBox.getSelectedItem().toString().equals("전체"))
            query += " AND bloodType = '" + bloodTypeBox.getSelectedItem().toString() + "'";
        if (!address1.getSelectedItem().toString().equals("전체"))
            query += " AND address LIKE '%" + (address1.getSelectedItem().toString() +
                    " " + address2.getSelectedItem().toString() +
                    " " + addressField.getText()).trim() + "%'";
        if (!heightField.getText().isEmpty()) query += " AND height = " + heightField.getText();
        if (!weightField.getText().isEmpty()) query += " AND weight = " + weightField.getText();

        //환자 id 순으로 정렬
        query += " ORDER BY patientid";

        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        Vector<Vector<String>> dataVector = new Vector<>();

        while (rs.next()) {
            //환자 id, 이름, 성, 주민번호를 벡터값에 넣고 표로 보내기
            Vector<String> row = new Vector<>();
            row.add(String.format("%04d", rs.getInt("patientid")));
            row.add(rs.getString("name"));
            row.add(rs.getString("gender"));
            row.add(rs.getString("identitynumber"));
            dataVector.add(row);
        }

        patientListPanel.setData(dataVector);

        st.close();
        rs.close();
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

        String[] columnNames = {"환자id", "환자명", "성별", "주민번호"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        //테이블 수정이 안 되도록 에디터 설정
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
        //선택한 행에서 환자 id를 가져와서 저장
        int row = table.getSelectedRow();
        String patientId = tableModel.getValueAt(row, 0).toString();
        try {
            patientDetailsPanel.setData(patientId);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
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



class PatientDetailsPanel extends JPanel implements ActionListener {
    Connection conn;
    JComboBox<String> bloodTypeBox, address1, address2;
    JTextField nameField, patientIdField, identityField, phoneField, heightField, weightField, addressField;
    JTextArea cautionArea;
    JRadioButton male, female;
    int patientId;
    PatientListPanel patientListPanel;
    PatientConditionPanel patientConditionPanel;
    String[][] districts = {
            {},
            // 서울특별시
            {"강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구", "도봉구",
                    "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", "양천구", "영등포구",
                    "용산구", "은평구", "종로구", "중구", "중랑구"},
            // 부산광역시
            {"강서구", "금정구", "기장군", "남구", "동구", "동래구", "부산진구", "북구", "사상구", "사하구", "서구",
                    "수영구", "연제구", "영도구", "중구", "해운대구"},
            // 대구광역시
            {"남구", "달서구", "달성군", "동구", "북구", "서구", "수성구", "중구"},
            // 인천광역시
            {"강화군", "계양구", "미추홀구", "남동구", "동구", "부평구", "서구", "연수구", "옹진군", "중구"},
            // 광주광역시
            {"광산구", "남구", "동구", "북구", "서구"},
            // 대전광역시
            {"대덕구", "동구", "서구", "유성구", "중구"},
            // 울산광역시
            {"남구", "동구", "북구", "울주군", "중구"},
            // 세종특별자치시 (세종시는 구가 없음)
            {},
            // 경기도
            {"가평군", "고양시", "과천시", "광명시", "광주시", "구리시", "군포시", "김포시", "남양주시", "동두천시",
                    "부천시", "성남시", "수원시", "시흥시", "안산시", "안성시", "안양시", "양주시", "양평군", "여주시",
                    "연천군", "오산시", "용인시", "의왕시", "의정부시", "이천시", "파주시", "평택시", "포천시", "하남시",
                    "화성시"},
            // 강원도
            {"강릉시", "고성군", "동해시", "삼척시", "속초시", "양구군", "양양군", "영월군", "원주시", "인제군",
                    "정선군", "철원군", "춘천시", "태백시", "평창군", "홍천군", "화천군", "횡성군"},
            // 충청북도
            {"괴산군", "단양군", "보은군", "영동군", "옥천군", "음성군", "제천시", "증평군", "진천군", "청주시",
                    "충주시"},
            // 충청남도
            {"계룡시", "공주시", "금산군", "논산시", "당진시", "보령시", "부여군", "서산시", "서천군", "아산시",
                    "연기군", "예산군", "천안시", "청양군", "태안군", "홍성군"},
            // 전라북도
            {"고창군", "군산시", "김제시", "남원시", "무주군", "부안군", "순창군", "완주군", "익산시", "임실군",
                    "장수군", "전주시", "정읍시", "진안군"},
            // 전라남도
            {"강진군", "고흥군", "곡성군", "광양시", "구례군", "나주시", "담양군", "목포시", "무안군", "보성군",
                    "순천시", "신안군", "여수시", "영광군", "영암군", "완도군", "장성군", "장흥군", "진도군", "함평군",
                    "해남군", "화순군"},
            // 경상북도
            {"경산시", "경주시", "고령군", "구미시", "군위군", "김천시", "문경시", "봉화군", "상주시", "성주군",
                    "안동시", "영덕군", "영양군", "영주시", "영천시", "예천군", "울릉군", "울진군", "의성군", "청도군",
                    "청송군", "칠곡군", "포항시"},
            // 경상남도
            {"거제시", "거창군", "고성군", "김해시", "남해군", "밀양시", "사천시", "산청군", "양산시", "의령군",
                    "진주시", "창녕군", "창원시", "통영시", "하동군", "함안군", "함양군", "합천군"},
            // 제주특별자치도
            {"서귀포시", "제주시"}
    };

    public PatientDetailsPanel(Connection conn) {
        this.conn = conn;

        int xValue = 170;
        int yValue = 40;
        int labelWidth = 60;
        int fieldWidth = 200;
        int height = 30;
        int spacing = 60;

        setLayout(null);
        setBorder(BorderFactory.createTitledBorder("환자 정보"));

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
        cautionArea.setLineWrap(true);
        cautionArea.setBounds(xValue + labelWidth, yValue + 4 * spacing, fieldWidth, 3 * height);
        add(cautionArea);

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

        String[] cities = {
                "", "서울특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시", "대전광역시", "울산광역시",
                "세종특별자치시", "경기도", "강원도", "충청북도", "충청남도", "전라북도", "전라남도", "경상북도",
                "경상남도", "제주특별자치도"
        };

        address1 = new JComboBox<>(cities);
        address1.setBounds(xValue + labelWidth, yValue + 3 * spacing, fieldWidth / 2, height);
        add(address1);
        address1.addActionListener(this);

        address2 = new JComboBox<>();
        address2.setBounds(xValue + labelWidth + 100, yValue + 3 * spacing, fieldWidth / 2, height);
        add(address2);

        addressField = new JTextField();
        addressField.setBounds(xValue + labelWidth, yValue + 3 * spacing + height, fieldWidth, height);
        add(addressField);


        JButton modifyButton = new JButton("수정");
        modifyButton.addActionListener(this);
        modifyButton.setBounds(xValue, yValue + 4 * spacing + height, labelWidth + fieldWidth, height);
        add(modifyButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            //수정 버튼 클릭 시
            if (e.getActionCommand().equals("수정")) {
                modifyPatient();
            } else if (e.getSource() == address1) {
                setAddress();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void setData(String patientId) throws SQLException {

        //환자 id로 환자 검색
        String query = "SELECT * FROM patient WHERE patientid = " + patientId;
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);

        //환자 정보 패널에 세팅
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
        String address = rs.getString("address");

        if (address == null) {
            address1.setSelectedIndex(0);
            setAddress();
        } else {
            StringTokenizer tk = new StringTokenizer(address);
            address1.setSelectedItem(tk.nextToken());
            setAddress();
            address2.setSelectedItem(tk.nextToken());

            //상세 주소
            StringBuilder sb = new StringBuilder();
            while(tk.hasMoreTokens()) {
                sb.append(tk.nextToken()).append(" ");
            }

            addressField.setText(sb.toString());
        }


        st.close();
        rs.close();
    }

    private void setAddress() {
        int selectedIndex = address1.getSelectedIndex();

        if (selectedIndex == 0) addressField.setEditable(false);
        else addressField.setEditable(true);

        address2.removeAllItems();

        for (String district : districts[selectedIndex]) {
            address2.addItem(district);
        }
    }

    private void modifyPatient() throws SQLException {
        //팝업을 띄워서 수정할 지 되묻기
        if (JOptionPane.showConfirmDialog(null, "수정하시겠습니까?") == 0) {
            String query = "UPDATE patient SET phone = ?, gender = ?, bloodtype = ?, caution = ?, address = ?, height = ?, weight = ? WHERE patientid = ?";
            PreparedStatement pstm = conn.prepareStatement(query);

            //값을 입력하지 않았다면 빈 칸이 들어가지 않도록 null 삽입
            pstm.setString(1, phoneField.getText().isEmpty() ? null : phoneField.getText());
            pstm.setString(2, male.isSelected() ? "남" : "여");
            pstm.setString(3, bloodTypeBox.getSelectedItem().toString().equals("선택되지 않음") ?
                    null : bloodTypeBox.getSelectedItem().toString());
            pstm.setString(4, cautionArea.getText().isEmpty() ? null : cautionArea.getText());
            pstm.setString(5, address1.getSelectedItem().toString().isEmpty() ?
                    null : (address1.getSelectedItem().toString() + " " + address2.getSelectedItem().toString() +
                    " " + addressField.getText()).trim());
            pstm.setString(6, heightField.getText().isEmpty() ? null : heightField.getText());
            pstm.setString(7, weightField.getText().isEmpty() ? null : weightField.getText());
            pstm.setInt(8, patientId);

            if (pstm.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "수정 성공");

                //환자 목록 다시 검색하여 새로고침
                patientConditionPanel.searchPatient();
            } else {
                JOptionPane.showMessageDialog(null, "수정 실패");
            }

            pstm.close();
        }
    }

    //의존성 설정
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
    JComboBox<String> address1, address2;
    String[][] districts = {
            {},
            // 서울특별시
            {"강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구", "도봉구",
                    "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", "양천구", "영등포구",
                    "용산구", "은평구", "종로구", "중구", "중랑구"},
            // 부산광역시
            {"강서구", "금정구", "기장군", "남구", "동구", "동래구", "부산진구", "북구", "사상구", "사하구", "서구",
                    "수영구", "연제구", "영도구", "중구", "해운대구"},
            // 대구광역시
            {"남구", "달서구", "달성군", "동구", "북구", "서구", "수성구", "중구"},
            // 인천광역시
            {"강화군", "계양구", "미추홀구", "남동구", "동구", "부평구", "서구", "연수구", "옹진군", "중구"},
            // 광주광역시
            {"광산구", "남구", "동구", "북구", "서구"},
            // 대전광역시
            {"대덕구", "동구", "서구", "유성구", "중구"},
            // 울산광역시
            {"남구", "동구", "북구", "울주군", "중구"},
            // 세종특별자치시 (세종시는 구가 없음)
            {},
            // 경기도
            {"가평군", "고양시", "과천시", "광명시", "광주시", "구리시", "군포시", "김포시", "남양주시", "동두천시",
                    "부천시", "성남시", "수원시", "시흥시", "안산시", "안성시", "안양시", "양주시", "양평군", "여주시",
                    "연천군", "오산시", "용인시", "의왕시", "의정부시", "이천시", "파주시", "평택시", "포천시", "하남시",
                    "화성시"},
            // 강원도
            {"강릉시", "고성군", "동해시", "삼척시", "속초시", "양구군", "양양군", "영월군", "원주시", "인제군",
                    "정선군", "철원군", "춘천시", "태백시", "평창군", "홍천군", "화천군", "횡성군"},
            // 충청북도
            {"괴산군", "단양군", "보은군", "영동군", "옥천군", "음성군", "제천시", "증평군", "진천군", "청주시",
                    "충주시"},
            // 충청남도
            {"계룡시", "공주시", "금산군", "논산시", "당진시", "보령시", "부여군", "서산시", "서천군", "아산시",
                    "연기군", "예산군", "천안시", "청양군", "태안군", "홍성군"},
            // 전라북도
            {"고창군", "군산시", "김제시", "남원시", "무주군", "부안군", "순창군", "완주군", "익산시", "임실군",
                    "장수군", "전주시", "정읍시", "진안군"},
            // 전라남도
            {"강진군", "고흥군", "곡성군", "광양시", "구례군", "나주시", "담양군", "목포시", "무안군", "보성군",
                    "순천시", "신안군", "여수시", "영광군", "영암군", "완도군", "장성군", "장흥군", "진도군", "함평군",
                    "해남군", "화순군"},
            // 경상북도
            {"경산시", "경주시", "고령군", "구미시", "군위군", "김천시", "문경시", "봉화군", "상주시", "성주군",
                    "안동시", "영덕군", "영양군", "영주시", "영천시", "예천군", "울릉군", "울진군", "의성군", "청도군",
                    "청송군", "칠곡군", "포항시"},
            // 경상남도
            {"거제시", "거창군", "고성군", "김해시", "남해군", "밀양시", "사천시", "산청군", "양산시", "의령군",
                    "진주시", "창녕군", "창원시", "통영시", "하동군", "함안군", "함양군", "합천군"},
            // 제주특별자치도
            {"서귀포시", "제주시"}
    };

    public PatientAddWindow(Connection conn, PatientConditionPanel patientConditionPanel) {
        this.conn = conn;
        this.patientConditionPanel = patientConditionPanel;

        setTitle("환자 추가");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(Color.LIGHT_GRAY);

        JLabel titleLabel = new JLabel("환자 추가");
        titleLabel.setFont(new Font("Gothic", Font.BOLD, 30));
        titlePanel.add(titleLabel, BorderLayout.NORTH);

        add(titlePanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);

        int xValue = 50;
        int yValue = 40;
        int labelWidth = 60;
        int fieldWidth = 200;
        int height = 30;
        int spacing = 60;

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
        idField1.setBounds(xValue + labelWidth, yValue + spacing, 90, height);
        mainPanel.add(idField1);

        JLabel bar = new JLabel("-");
        bar.setBounds(xValue + labelWidth + 95 , yValue + spacing, 5, height);
        mainPanel.add(bar);

        idField2 = new NumberTextField(7);
        idField2.setBounds(xValue + labelWidth + 105, yValue + spacing, 95, height);
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
        female.setBounds(xValue + labelWidth + 80, yValue + 2 * spacing, 70, height);
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

        int buttonWidth = 50;

        A.setBounds(xValue + labelWidth, yValue + 3 * spacing, buttonWidth, height);
        B.setBounds(xValue + labelWidth + buttonWidth, yValue + 3 * spacing, buttonWidth, height);
        O.setBounds(xValue + labelWidth + 2 * buttonWidth, yValue + 3 * spacing, buttonWidth, height);
        AB.setBounds(xValue + labelWidth + 3 * buttonWidth, yValue + 3 * spacing, buttonWidth, height);
        mainPanel.add(A);
        mainPanel.add(B);
        mainPanel.add(O);
        mainPanel.add(AB);

        JLabel cautionLabel = new JLabel("주의사항");
        cautionLabel.setBounds(xValue, yValue + 4 * spacing, labelWidth, height);
        mainPanel.add(cautionLabel);

        cautionArea = new JTextArea();
        cautionArea.setLineWrap(true);
        cautionArea.setBounds(xValue + labelWidth, yValue + 4 * spacing, fieldWidth, height * 3);
        mainPanel.add(cautionArea);

        xValue += 280;

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

        String[] cities = {
                "", "서울특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시", "대전광역시", "울산광역시",
                "세종특별자치시", "경기도", "강원도", "충청북도", "충청남도", "전라북도", "전라남도", "경상북도",
                "경상남도", "제주특별자치도"
        };

        address1 = new JComboBox<>(cities);
        address1.setBounds(xValue + labelWidth, yValue + 3 * spacing, fieldWidth / 2, height);
        mainPanel.add(address1);
        address1.addActionListener(this);

        address2 = new JComboBox<>();
        address2.setBounds(xValue + labelWidth + 100, yValue + 3 * spacing, fieldWidth / 2, height);
        mainPanel.add(address2);

        addressField = new JTextField();
        addressField.setBounds(xValue + labelWidth, yValue + 3 * spacing + height, fieldWidth, height);
        addressField.setEditable(false);
        mainPanel.add(addressField);

        JButton addButton = new JButton("추가");
        addButton.addActionListener(this);
        addButton.setBounds(xValue, yValue + 4 * spacing + height, labelWidth + fieldWidth, height);
        mainPanel.add(addButton);

        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("추가")) addPatient();
        else if (e.getSource() == address1) {
            setAddress();
        }
    }

    //환자 추가 로직
    private void addPatient() {
        //팝업을 띄워 되묻고 확인 시 로직 실행
        if (JOptionPane.showConfirmDialog(this, nameField.getText() + " 환자를 추가하시겠습니까?") == 0) {
            String query = "INSERT INTO patient(name, phone, identitynumber, caution, address, bloodType, gender, height, weight) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement pstm = null;

            try {
                pstm = conn.prepareStatement(query);
                //이름을 입력했는지, 주민번호 길이가 맞는지 체크
                if (checkName() && checkIdLength()) {
                    pstm.setString(1, nameField.getText());
                    pstm.setString(2, phoneField.getText().isEmpty() ? null : phoneField.getText());
                    pstm.setString(3, idField1.getText() + "-" + idField2.getText());
                    pstm.setString(4, cautionArea.getText().isEmpty() ? null : cautionArea.getText());
                    pstm.setString(5, address1.getSelectedItem().toString().isEmpty() ?
                            null : (address1.getSelectedItem().toString() + " " + address2.getSelectedItem().toString() +
                            " " + addressField.getText()).trim());
                    pstm.setString(6, getBloodType());
                    pstm.setString(7, male.isSelected() ? "남" : "여");
                    pstm.setString(8, heightField.getText().isEmpty() ? null : heightField.getText());
                    pstm.setString(9, weightField.getText().isEmpty() ? null : weightField.getText());

                    if (pstm.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(null, "추가 성공");
                        patientConditionPanel.searchPatient();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "추가 실패");
                    }
                }
            } catch (SQLIntegrityConstraintViolationException ex) {
                //같은 주민번호가 이미 있을 때
                JOptionPane.showMessageDialog(null, "이미 추가된 환자입니다");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "키와 몸무게는 숫자를 입력해주세요");
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            } finally {
                try {
                    pstm.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void setAddress() {
        int selectedIndex = address1.getSelectedIndex();

        if (selectedIndex == 0) addressField.setEditable(false);
        else addressField.setEditable(true);

        address2.removeAllItems();

        for (String district : districts[selectedIndex]) {
            address2.addItem(district);
        }
    }

    //이름 입력했는지 확인
    private boolean checkName() {
        if (nameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "이름을 입력하세요");
            nameField.requestFocus();
            return false;
        }

        return true;
    }

    //주민번호 앞자리와 뒷자리의 길이 확인
    private boolean checkIdLength() {
        if (idField1.getText().length() != 6 || idField2.getText().length() != 7) {
            JOptionPane.showMessageDialog(null, "주민번호를 올바르게 입력해주세요");
            idField1.requestFocus();
            return false;
        }

        return true;
    }

    //선택한 혈액형에 따라 문자열 반환
    public String getBloodType() {
        if (A.isSelected()) {
            return "A";
        } else if (B.isSelected()) {
            return "B";
        } else if (O.isSelected()) {
            return "O";
        } else if (AB.isSelected()) {
            return "AB";
        }

        //아무것도 선택하지 않았다면 null 반환
        return null;
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
        //입력한 키의 종류를 char로 받아옴
        char c = e.getKeyChar();

        //숫자가 아니라면 입력 중지
        if (!Character.isDigit(c)) {
            e.consume();
        }

        //최대 숫자를 넘었다면 입력 중지
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
