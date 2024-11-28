package MJHospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Vector;

class StaffManagement extends JPanel {
    Connection connection;
    StaffList staffList;
    StaffInfo staffInfo;

    public StaffManagement(Connection c) {
        setLayout(new BorderLayout());
        connection = c;
        staffList = new StaffList(connection);
        staffInfo = new StaffInfo(connection);
        add(staffList, BorderLayout.WEST);
        add(staffInfo, BorderLayout.CENTER);
    }

}

class StaffList extends JPanel implements ActionListener {
    Vector<String> columnNames;
    Vector<Vector<String>> data;
    DefaultTableModel model;
    JTable table;
    Connection connection;

    public StaffList(Connection c) {
        connection = c;
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
        JTextField searchField = new JTextField(13);
        JButton searchButton = new JButton("검색");
        searchFieldPanel.add(searchLabel);
        searchFieldPanel.add(searchField);
        searchFieldPanel.add(searchButton);
        searchFieldPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));

        searchButton.addActionListener(this);

        //라디오 버튼 패널
        JPanel radioPanel = new JPanel(new GridLayout(2, 3));
        radioPanel.setBackground(Color.LIGHT_GRAY);
        ButtonGroup group1 = new ButtonGroup();
        ButtonGroup group2 = new ButtonGroup();
        JRadioButton r1 = new JRadioButton("전체");
        JRadioButton r2 = new JRadioButton("의사");
        JRadioButton r3 = new JRadioButton("간호사");
        JRadioButton r4 = new JRadioButton("재직중");
        JRadioButton r5 = new JRadioButton("퇴사");

        group1.add(r1);
        group1.add(r2);
        group1.add(r3);
        group2.add(r4);
        group2.add(r5);
        radioPanel.add(r1);
        radioPanel.add(r2);
        radioPanel.add(r3);
        radioPanel.add(r4);
        radioPanel.add(r5);
        radioPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 3, 0));

        // 검색 패널에 컴포넌트 추가
        searchPanel.add(searchFieldPanel, BorderLayout.NORTH);
        searchPanel.add(radioPanel, BorderLayout.CENTER);

        // 테이블 설정
        columnNames = new Vector<String>();
        columnNames.add("직급");
        columnNames.add("이름");

        data = new Vector<Vector<String>>();
        model = new DefaultTableModel(data, columnNames);
        table = new JTable(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(20);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("검색")){

        }
    }
}

class StaffInfo extends JPanel implements ActionListener {
    Connection connection;
    public StaffInfo(Connection c) {
        Color backgroundColor = new Color(200, 200, 200);

        connection = c;

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

        JTextField nameField = new JTextField();
        nameField.setEditable(false);
        JTextField IDField = new JTextField();
        IDField.setEditable(false);
        JTextField passwdField = new JTextField();
        JTextField idNumField = new JTextField();
        idNumField.setEditable(false);
        JTextField titleField = new JTextField();
        titleField.setEditable(false);
        JTextField phoneNumField = new JTextField();
        JTextField majorField = new JTextField();
        JTextField offDayField = new JTextField();
        JTextArea addressArea = new JTextArea();
        addressArea.setLineWrap(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);

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
        offDayField.setBounds(xField2, 210, 110, 30);
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
        centerPanel.add(offDayField);
        centerPanel.add(addressLabel);
        centerPanel.add(addressScroll);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        JButton addButton = new JButton("추가");
        JButton editButton = new JButton("수정");
        JButton inactivationButton = new JButton("비활성화");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(inactivationButton);

        addButton.addActionListener(this);
        editButton.addActionListener(this);
        inactivationButton.addActionListener(this);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("추가")) {
            AddStaff addStaff = new AddStaff(connection);
            addStaff.setVisible(true);
        }
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

        String[] roleStr = {"의사", "간호사"};
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
        int roleID;
        String offDay;
        if(getRole.equals("의사")) {
            roleID = 1;
        }
        else {
            roleID = 2;
        }
        offDay = offCode(getOff);
        String insertQuery = "Insert INTO staff values ('" + getID + "', '" + getPasswd + "', 1, '"
                + roleID + "', '" + getName + "', '" + offDay + "', '" + getPhone + "', '" + getIDNum + "', '"
                + getMajor + "', '" + getAddress + "')";
        statement.executeUpdate(insertQuery);
        JOptionPane.showMessageDialog(this, "의료진이 성공적으로 추가되었습니다.", "확인", JOptionPane.INFORMATION_MESSAGE);
        statement.close();
        resultSet.close();
    }

    //주민번호 형식 확인 메소드
    static boolean is_Ok(String str, int length) {
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
    static String offCode(String off) {
        switch (off) {
            case "월" -> {
                return "1";
            }
            case "화" -> {
                return "2";
            }
            case "수" -> {
                return "3";
            }
            case "목" -> {
                return "4";
            }
            case "금" -> {
                return "5";
            }
            case "토" -> {
                return "6";
            }
        }
        return null;
    }
}

