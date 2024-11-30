package MJHospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

class StaffManagement extends JPanel {
    Connection connection;
    StaffList staffList;
    StaffInfo staffInfo;
    String role, id;

    public StaffManagement(Connection c, String i, String r) {
        setLayout(new BorderLayout());
        connection = c;
        id = i;
        role = r;

        staffInfo = new StaffInfo(connection, id, role);
        add(staffInfo, BorderLayout.CENTER);
        staffList = new StaffList(connection, id, role);
        add(staffList, BorderLayout.WEST);
    }

}

class StaffList extends JPanel implements ActionListener, MouseListener {
    Connection connection;

    String id, role;

    Vector<String> columnNames;
    Vector<Vector<String>> data;
    DefaultTableModel model;
    JTable table;

    JTextField searchField;
    JButton searchButton;
    ButtonGroup group1, group2;
    JRadioButton radioBtnA, radioBtnD, radioBtnN, radioBtnAA, radioBtnO, radioBtnX;

    public StaffList(Connection c, String i,  String r) {
        connection = c;
        id = i;
        role = r;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("의료진 검색"));
        setPreferredSize(new Dimension(300, getHeight()));

        // 검색 패널
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.LIGHT_GRAY);

        // 검색 필드와 버튼을 담을 패널
        JPanel searchFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchFieldPanel.setBackground(Color.LIGHT_GRAY);

        JLabel searchLabel = new JLabel("이름 :");
        searchLabel.setFont(new Font("Gothic", Font.PLAIN, 15));
        searchLabel.setForeground(Color.BLACK);

        searchField = new JTextField(13);
        searchButton = new JButton("검색");

        searchFieldPanel.add(searchLabel);
        searchFieldPanel.add(searchField);
        searchFieldPanel.add(searchButton);
        searchFieldPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));

        searchButton.addActionListener(this);

        //라디오 버튼 패널
        JPanel radioPanel = new JPanel(new GridLayout(2, 3));
        radioPanel.setBackground(Color.LIGHT_GRAY);

        group1 = new ButtonGroup();
        group2 = new ButtonGroup();
        radioBtnA = new JRadioButton("전체", true);
        radioBtnD = new JRadioButton("의사");
        radioBtnN = new JRadioButton("간호사");
        radioBtnAA = new JRadioButton("전체",  true);
        radioBtnO = new JRadioButton("재직중");
        radioBtnX = new JRadioButton("퇴사");

        group1.add(radioBtnA);
        group1.add(radioBtnD);
        group1.add(radioBtnN);
        group2.add(radioBtnAA);
        group2.add(radioBtnO);
        group2.add(radioBtnX);
        radioPanel.add(radioBtnA);
        radioPanel.add(radioBtnD);
        radioPanel.add(radioBtnN);
        radioPanel.add(radioBtnAA);
        radioPanel.add(radioBtnO);
        radioPanel.add(radioBtnX);
        radioPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 3, 0));

        //admin이 아니라면 검색 패널의 컴포넌트 비활성화
        if (!role.equals("admin")){
            searchField.setEditable(false);
            searchButton.setEnabled(false);
            radioBtnA.setEnabled(false);
            radioBtnD.setEnabled(false);
            radioBtnN.setEnabled(false);
            radioBtnAA.setEnabled(false);
            radioBtnO.setEnabled(false);
            radioBtnX.setEnabled(false);
        }

        // 검색 패널에 컴포넌트 추가
        searchPanel.add(searchFieldPanel, BorderLayout.NORTH);
        searchPanel.add(radioPanel, BorderLayout.CENTER);

        // 테이블 설정
        columnNames = new Vector<String>();
        columnNames.add("ID");
        columnNames.add("직급");
        columnNames.add("이름");
        columnNames.add("재직 여부");

        data = new Vector<Vector<String>>();
        model = new DefaultTableModel(data, columnNames);
        table = new JTable(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(20);
        table.addMouseListener(this);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            searchStaff();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + ex.getMessage(),
                    "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void searchStaff() throws SQLException{
        String getName = searchField.getText();

        //라디오버튼 선택 확인
        ButtonModel selectedModel1 = group1.getSelection();
        ButtonModel selectedModel2 = group2.getSelection();
        String radioRole = getText(group1, selectedModel1);
        String radioActive = getText(group2, selectedModel2);

        //쿼리문 생성
        String selectQuery = setQuery(radioActive, radioRole, getName);

        //테이블 만들기
        setTable(selectQuery);
    }

    //라디오 버튼에서 텍스트 가져오기
    private String getText(ButtonGroup group, ButtonModel selectedModel) {
        if (selectedModel != null) {
            for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();
                if (button.getModel() == selectedModel) {
                    return button.getText();
                }
            }
        }
        return null;
    }

    //쿼리문 만들기
    private String setQuery(String radioActive, String radioRole, String getName) {
        String query = "SELECT s.staffid, r.rolename, s.name, s.is_active FROM staff s INNER JOIN role r ON s.roleid = r.roleid  WHERE s.name LIKE '%" + getName + "%'";
        if (radioActive.equals("재직중")) {
            query += " AND s.is_active = 1";
        }
        else if (radioActive.equals("퇴사")) {
            query += " AND s.is_active = 0";
        }
        if (radioRole.equals("의사")) {
            query += " AND r.rolename = 'doctor'";
        }
        else if (radioRole.equals("간호사")) {
            query += " AND r.rolename = 'nurse'";
        }
        query += " AND r.rolename != 'admin'";
        return query;
    }

    //테이블 생성
    private void setTable(String selectQuery) throws SQLException {
        Statement statement;
        ResultSet resultSet;
        statement = connection.createStatement();

        data.clear();
        resultSet = statement.executeQuery(selectQuery);

        while(resultSet.next()) {
            Vector <String> text = new Vector<String>();
            text.add(resultSet.getString("staffid"));
            text.add(resultSet.getString("rolename"));
            text.add(resultSet.getString("name"));
            if(resultSet.getString("is_active").equals("1")){
                text.add("재직중");
            }
            else {
                text.add("퇴사자");
            }
            data.add(text);
        }

        table.setModel(new DefaultTableModel(data, columnNames));
        table.updateUI();

        statement.close();
        resultSet.close();
    }

    //테이블 클릭시 staffInfo의 setInfo 실행
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        String userID = (String) model.getValueAt(row, 0);
        try {
            StaffInfo.setInfo(userID);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + ex.getMessage(),
                    "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}

class StaffInfo extends JPanel implements ActionListener {
    static Connection connection;

    String id, role;

    static JTextField nameField, IDField, passwdField, idNumField, titleField, phoneNumField, majorField;
    static JComboBox offDayBox;
    static JTextArea addressArea;
    JButton addButton, editButton;
    static JButton inactivationButton;

    static {
        inactivationButton = new JButton("비활성화"); //이유는 모르겠는데 초기화가 안된대서 초기화
    }

    public StaffInfo(Connection c, String i, String r) {
        Color backgroundColor = new Color(200, 200, 200);

        connection = c;

        id = i;
        role = r;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("의료진 정보"));

        // 타이틀
        JLabel topLabel = new JLabel(" STAFF INFO");
        topLabel.setFont(new Font("Arial", Font.BOLD, 30));

        // 상단 패널
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.setPreferredSize(new Dimension(getWidth(), 50));
        topPanel.add(topLabel);

        // 중앙 패널
        JPanel centerPanel = new JPanel(null);
        centerPanel.setBackground(backgroundColor);

        //중앙 패널 컴포넌트 생성
        JLabel nameLabel = new JLabel("이름");
        nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        JLabel IDLabel = new JLabel("ID");
        IDLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        JLabel passwdLabel = new JLabel("PASSWORD");
        passwdLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        JLabel idNumLabel = new JLabel("주민번호");
        idNumLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        JLabel titleLabel = new JLabel("직급");
        titleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        JLabel phoneNumLabel = new JLabel("연락처");
        phoneNumLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        JLabel majorLabel = new JLabel("전공");
        majorLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        JLabel offDayLabel = new JLabel("휴무일");
        offDayLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        JLabel addressLabel = new JLabel("주소");
        addressLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 20));

        nameField = new JTextField();
        nameField.setEditable(false);
        IDField = new JTextField();
        IDField.setEditable(false);
        passwdField = new JTextField();
        idNumField = new JTextField();
        idNumField.setEditable(false);
        titleField = new JTextField();
        titleField.setEditable(false);
        phoneNumField = new JTextField();
        majorField = new JTextField();
        String[] offStr = {"", "월", "화", "수", "목", "금", "토"};
        offDayBox = new JComboBox(offStr);
        addressArea = new JTextArea();
        addressArea.setLineWrap(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);

        //텍스트필드 채워넣기
        try {
            setInfo();
        }catch (Exception e) {
            JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + e.getMessage(),
                    "오류", JOptionPane.ERROR_MESSAGE);
        }

        int xLabel1 = 200;
        int xField1 = 350;

        int xLabel2 = 500;
        int xField2 = 650;

        // 라벨 위치 설정
        nameLabel.setBounds(xLabel1, 70, 100, 30);
        IDLabel.setBounds(xLabel1, 140, 100, 30);
        passwdLabel.setBounds(xLabel1, 210, 150, 30);
        idNumLabel.setBounds(xLabel1, 280, 100, 30);
        titleLabel.setBounds(xLabel1, 350, 100, 30);
        phoneNumLabel.setBounds(xLabel2, 70, 100, 30);
        majorLabel.setBounds(xLabel2, 140, 100, 30);
        offDayLabel.setBounds(xLabel2, 210, 100, 30);
        addressLabel.setBounds(xLabel2, 280, 100, 30);

        // 텍스트필드 위치 설정
        nameField.setBounds(xField1, 70, 110, 30);
        IDField.setBounds(xField1, 140, 110, 30);
        passwdField.setBounds(xField1, 210, 110, 30);
        idNumField.setBounds(xField1, 280, 110, 30);
        titleField.setBounds(xField1, 350, 110, 30);
        phoneNumField.setBounds(xField2, 70, 110, 30);
        majorField.setBounds(xField2, 140, 110, 30);
        offDayBox.setBounds(xField2, 210, 110, 30);
        addressScroll.setBounds(570, 280, 200, 105);

        centerPanel.add(nameLabel);
        centerPanel.add(nameField);
        centerPanel.add(IDLabel);
        centerPanel.add(IDField);
        centerPanel.add(passwdLabel);
        centerPanel.add(passwdField);
        centerPanel.add(idNumLabel);
        centerPanel.add(idNumField);
        centerPanel.add(titleLabel);
        centerPanel.add(titleField);
        centerPanel.add(phoneNumLabel);
        centerPanel.add(phoneNumField);
        centerPanel.add(majorLabel);
        centerPanel.add(majorField);
        centerPanel.add(offDayLabel);
        centerPanel.add(offDayBox);
        centerPanel.add(addressLabel);
        centerPanel.add(addressScroll);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        addButton = new JButton("추가");
        editButton = new JButton("수정");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(inactivationButton);

        //admin이 아니라면 컴포넌트 비활성화
        if (!role.equals("admin")){
            addButton.setEnabled(false);
            inactivationButton.setEnabled(false);
        }

        addButton.addActionListener(this);
        editButton.addActionListener(this);
        inactivationButton.addActionListener(this);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            AddStaff addStaff = new AddStaff(connection);
            addStaff.setVisible(true);
        }
        else if (e.getSource() == editButton) {
            try {
                editInfo();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + ex.getMessage(),
                        "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (e.getSource() == inactivationButton) {
            try {
                inactivationUser();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + ex.getMessage(),
                        "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //활성화/비활성화 버튼 클릭시 실행
    public void inactivationUser() throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(activeQuery())) { //try with resources사용 자동으로 자원을 닫아줌
            if(resultSet.next()) {
                if (resultSet.getString("is_active").equals("1")) {
                    int rowAffected = statement.executeUpdate(buildUpdateActivationQuery(1));
                    if (rowAffected > 0) { //계정이 비활성화 되었다면 메세지 출력
                        JOptionPane.showMessageDialog(this, "계정이 비활성화 되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                else {
                    int rowAffected = statement.executeUpdate(buildUpdateActivationQuery(0));
                    if (rowAffected > 0) { //계정이 활성화 되었다면 메세지 출력
                        JOptionPane.showMessageDialog(this, "계정이 활성화 되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(activeQuery())) {
            if(resultSet.next()) {
                editBtn(resultSet); //활성화, 비활성화 버튼 수정
            }
        }
    }

    //활성화/비활성화 상태 확인
    public String activeQuery() {
        String getID = IDField.getText();
        return "SELECT is_active FROM staff WHERE staffid = '" + getID + "'";
    }
    //활성화/비활성화 상태 수정 쿼리문 작성
    public String buildUpdateActivationQuery(int actState) {
        String getID = IDField.getText();
        if (actState == 0) {
            return "UPDATE staff SET is_active = 1 WHERE staffid = '" + getID + "'";
        }
        else {
            return "UPDATE staff SET is_active = 0 WHERE staffid = '" + getID + "'";
        }
    }

    //수정 버튼 클릭시 실행
    public void editInfo() throws SQLException {
        try (Statement statement = connection.createStatement()) { //try with resources사용 자동으로 자원을 닫아줌
            int rowAffected = statement.executeUpdate(buildUpdateQuery());
            if (rowAffected > 0) { //수정된 정보가 있다면 메세지 출력
                JOptionPane.showMessageDialog(this, "정보가 성공적으로 수정되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "수정할 정보가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    //UPDATE 쿼리문 작성
    public String buildUpdateQuery() {
        String getID = IDField.getText();
        String getPasswd = passwdField.getText();
        String getPhone = phoneNumField.getText();
        String getMajor = majorField.getText();
        String getOff = offDayBox.getSelectedItem().toString();
        String getAddress = addressArea.getText();
        int offDay = AddStaff.offCode(getOff);
        if (offDay == 0) {
            return "UPDATE staff SET password = '" + getPasswd + "', phone = '" + getPhone + "', major = '" + getMajor
                    + "', off = null, address = '" + getAddress + "' WHERE staffid = '" + getID + "'";
        }
        else {
            return "UPDATE staff SET password = '" + getPasswd + "', phone = '" + getPhone + "', major = '" + getMajor
                    + "', off = '" + offDay + "', address = '" + getAddress + "' WHERE staffid = '" + getID + "'";
        }
    }

    public void setInfo() throws SQLException {
        setInfo(this.id);
    }

    //메소드 오버로딩
    public static void setInfo(String userid) throws SQLException {
        try (Statement statement = connection.createStatement(); //try with resources사용 자동으로 자원을 닫아줌
             ResultSet resultSet = statement.executeQuery(buildSelectQuery(userid))) {
            if (resultSet.next()) {
                setFields(resultSet);
                editBtn(resultSet);
            }
        }
    }

    //활성화, 비활성화 버튼 수정하기
    private static void editBtn(ResultSet resultSet) throws SQLException {
        if (resultSet.getInt("is_active") != 1 ) {
            inactivationButton.setText("활성화");
        }
        else {
            inactivationButton.setText("비활성화");
        }
    }

    //쿼리문 생성
    private static String buildSelectQuery(String userid) {
        return "SELECT s.staffid, s.password, s.is_active, " +
                "r.rolename, s.name, s.off, s.phone, s.identitynumber, s.major, s.address " +
                "FROM staff s INNER JOIN role r ON s.roleid = r.roleid " +
                "WHERE s.staffid = '" + userid + "'";
    }

    //textField에 문자 입력
    private static void setFields(ResultSet resultSet) throws SQLException {
        nameField.setText(resultSet.getString("name"));
        IDField.setText(resultSet.getString("staffid"));
        passwdField.setText(resultSet.getString("password"));
        setIDNum(resultSet.getString("identitynumber"));
        titleField.setText(resultSet.getString("rolename"));
        phoneNumField.setText(resultSet.getString("phone"));
        majorField.setText(resultSet.getString("major"));
        setOffDayBox(resultSet.getString("off"));
        addressArea.setText(resultSet.getString("address"));
    }

    //주민번호 뒷자리 숨기기
    private static void setIDNum(String idNum) {
        String maskedIdNum = idNum.replaceAll("-.*", "-*******"); //replaceAll은 원본 문자열 변경하지 않음
        idNumField.setText(maskedIdNum);
    }

    //쉬는날 설정
    private static void setOffDayBox(String off) {
        if (off == null) {
            offDayBox.setSelectedIndex(0);
            return;
        }
        int index = switch (off) {
            case "1", "2", "3", "4", "5", "6" -> Integer.parseInt(off);
            default -> 0;
        };
        offDayBox.setSelectedIndex(index);
    }

}

class AddStaff extends JFrame implements ActionListener{
    JTextField nameField, IDField, passwdField, idNumField, phoneNumField, majorField;
    JTextArea addressArea;
    JButton addButton, cancelButton;
    JComboBox roleBox, offDayBox;

    Connection connection;
    public AddStaff(Connection c) {
        connection = c;

        setTitle("의료진 추가");
        setSize(500, 500);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(200, 200, 200));

        //상단 패널
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.setPreferredSize(new Dimension(getWidth(), 50));

        //타이틀 레이블
        JLabel topLabel = new JLabel("의료진 추가");
        topLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        topPanel.add(topLabel);

        //메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(200, 200, 200));

        //메인패널 컴포넌트
        JLabel nameLabel = new JLabel("이름*");
        nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        JLabel IDLabel = new JLabel("ID*");
        IDLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        JLabel passwdLabel = new JLabel("PASSWORD*");
        passwdLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        JLabel idNumLabel = new JLabel("주민번호*");
        idNumLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        JLabel phoneNumLabel = new JLabel("연락처");
        phoneNumLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        JLabel majorLabel = new JLabel("전공*");
        majorLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        JLabel offDayLabel = new JLabel("휴무일");
        offDayLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        JLabel addressLabel = new JLabel("주소");
        addressLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        JLabel titleLabel = new JLabel("직급*");
        titleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));

        nameField = new JTextField();
        IDField = new JTextField();
        passwdField = new JTextField();
        idNumField = new JTextField();
        phoneNumField = new JTextField();
        majorField = new JTextField();
        addressArea = new JTextArea();

        String[] offStr = {"", "월", "화", "수", "목", "금", "토"};
        offDayBox = new JComboBox(offStr);

        String[] roleStr = {"doctor", "nurse"};
        roleBox = new JComboBox(roleStr);

        addressArea.setLineWrap(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);

        int xLabel1 = 30;
        int xLabel2 = 250;
        int xField1 = 120;
        int xField2 = 340;

        // 라벨 위치 설정
        nameLabel.setBounds(xLabel1, 50, 100, 30);
        IDLabel.setBounds(xLabel1, 120, 100, 30);
        passwdLabel.setBounds(xLabel1, 190, 150, 30);
        idNumLabel.setBounds(xLabel1, 260, 100, 30);
        titleLabel.setBounds(xLabel1, 330, 100, 30);
        phoneNumLabel.setBounds(xLabel2, 50, 100, 30);
        majorLabel.setBounds(xLabel2, 120, 100, 30);
        offDayLabel.setBounds(xLabel2, 190, 100, 30);
        addressLabel.setBounds(xLabel2, 260, 100, 30);

        // 텍스트필드 위치 설정
        nameField.setBounds(xField1, 50, 110, 30);
        IDField.setBounds(xField1, 120, 110, 30);
        passwdField.setBounds(xField1, 190, 110, 30);
        idNumField.setBounds(xField1, 260, 110, 30);
        roleBox.setBounds(xField1, 330, 110, 30);
        phoneNumField.setBounds(xField2, 50, 110, 30);
        majorField.setBounds(xField2, 120, 110, 30);
        offDayBox.setBounds(xField2, 190, 110, 30);
        addressScroll.setBounds(290, 260, 160, 80);

        mainPanel.add(nameLabel);
        mainPanel.add(nameField);
        mainPanel.add(IDLabel);
        mainPanel.add(IDField);
        mainPanel.add(passwdLabel);
        mainPanel.add(passwdField);
        mainPanel.add(idNumLabel);
        mainPanel.add(idNumField);
        mainPanel.add(titleLabel);
        mainPanel.add(roleBox);
        mainPanel.add(phoneNumLabel);
        mainPanel.add(phoneNumField);
        mainPanel.add(majorLabel);
        mainPanel.add(majorField);
        mainPanel.add(offDayLabel);
        mainPanel.add(offDayBox);
        mainPanel.add(addressLabel);
        mainPanel.add(addressScroll);

        //버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(null);
        buttonPanel.setPreferredSize(new Dimension(getWidth(), 50));
        buttonPanel.setBackground(new Color(200, 200, 200));

        //버튼 컴포넌트
        addButton = new JButton("추가");
        cancelButton = new JButton("취소");

        addButton.addActionListener(this);
        cancelButton.addActionListener(this);

        addButton.setBounds(280, 0, 80, 30);
        cancelButton.setBounds(380, 0, 80, 30);

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == addButton) {
                addStaff();
                dispose();
            }
            else if (e.getSource() == cancelButton) {
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + ex.getMessage(),
                "오류", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "입력 오류", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "예상치 못한 오류: " + ex.getMessage(),
                "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addStaff() throws SQLException, IllegalArgumentException{
        Statement statement;
        ResultSet resultSet;

        String selectQuery = "SELECT staffID, identitynumber FROM staff";

        String getName, getID, getPasswd, getIDNum, getRole, getPhone, getMajor, getOff, getAddress;
        getName = nameField.getText();
        getID = IDField.getText();
        getPasswd = passwdField.getText();
        getIDNum = idNumField.getText();
        getRole = roleBox.getSelectedItem().toString();
        getPhone = phoneNumField.getText();
        getMajor = majorField.getText();
        getOff = offDayBox.getSelectedItem().toString();
        getAddress = addressArea.getText();

        statement = connection.createStatement();
        resultSet = statement.executeQuery(selectQuery);

        // null 체크
        if (Arrays.asList(getName, getID, getPasswd, getIDNum, getMajor).contains("")) {
            throw new IllegalArgumentException("필수 입력 항목이 누락되었습니다.");
        }

        //주민번호 형식 확인
        if (!getIDNum.contains("-")) {
            throw new IllegalArgumentException("주민번호는 -를 포함한 14자리 정수를 입력해주세요.");
        }
        String[] splitNum = getIDNum.split("-");
        if(splitNum.length != 2) {
            throw new IllegalArgumentException("-는 한번만 입력하세요.");
        }
        else if(!is_Ok(splitNum[0], 6) || !is_Ok(splitNum[1], 7)) {
            throw new IllegalArgumentException("주민번호의 형식이 올바르지 않습니다.");
        }

        // 중복 ID, 주민번호 발견 시 예외처리
        while (resultSet.next()) {
            if (getID.equals(resultSet.getString("staffID"))) {
                throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
            }
            else if (getIDNum.equals(resultSet.getString("identitynumber"))) {
                throw new IllegalArgumentException("이미 등록된 의료진입니다.");
            }
        }

        //INSERT 쿼리문 작성 및 실행
        resultSet = statement.executeQuery("SELECT roleid FROM role WHERE rolename = '" + getRole + "'");
        resultSet.next();
        int roleID = resultSet.getInt("roleid");
        int offDay;

        offDay = offCode(getOff);
        String insertQuery = "Insert INTO staff values ('" + getID + "', '" + getPasswd + "', 1, '"
                + roleID + "', '" + getName + "', '" + offDay + "', '" + getPhone + "', '" + getIDNum + "', '"
                + getMajor + "', '" + getAddress + "')";
        if (offDay == 0){
            insertQuery = "Insert INTO staff values ('" + getID + "', '" + getPasswd + "', 1, '"
                    + roleID + "', '" + getName + "', null, '" + getPhone + "', '" + getIDNum + "', '"
                    + getMajor + "', '" + getAddress + "')";
        }
        statement.executeUpdate(insertQuery);
        JOptionPane.showMessageDialog(this, "의료진이 성공적으로 추가되었습니다.", "확인", JOptionPane.INFORMATION_MESSAGE);
        statement.close();
        resultSet.close();
    }

    //주민번호 형식 확인 메소드
    private boolean is_Ok(String str, int length) {
        if (str.length() != length) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //콤보박스 값 토대로 휴무일 코드 반환
    static int offCode(String off) {
        switch (off) {
            case "월" -> {
                return 1;
            }
            case "화" -> {
                return 2;
            }
            case "수" -> {
                return 3;
            }
            case "목" -> {
                return 4;
            }
            case "금" -> {
                return 5;
            }
            case "토" -> {
                return 6;
            }
        }
        return 0;
    }
}

