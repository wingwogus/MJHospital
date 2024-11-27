package MJHospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

class StaffManagement extends JPanel {
    Connection connection;
    Statement statement;
    StaffList staffList = new StaffList(connection, statement);
    StaffInfo staffInfo = new StaffInfo(connection, statement);

    public StaffManagement() {
        setLayout(new BorderLayout());
        add(staffList, BorderLayout.WEST);
        add(staffInfo, BorderLayout.CENTER);
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://hyunsql.cjwqee8gsrhn.ap-southeast-2.rds.amazonaws.com:3306/mjhospital",
                    "hyeni", "0705"
            );
            statement = connection.createStatement();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB 연결 실패: " + e.getMessage());
        }
    }
}

class StaffList extends JPanel implements ActionListener {
    Vector<String> columnNames;
    Vector<Vector<String>> data;
    DefaultTableModel model;
    JTable table;
    Connection connection;
    Statement statement;

    public StaffList(Connection c, Statement s) {
        connection = c;
        statement = s;
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

        // 라디오 버튼 패널
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
    Statement statement;
    public StaffInfo(Connection c, Statement s) {
        Color backgroundColor = new Color(200, 200, 200);

        connection = c;
        statement = s;

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
        addressScroll.setBounds(570, 280, 220, 110);

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
            AddStaff addStaff = new AddStaff(connection, statement);
            addStaff.setVisible(true);
        }
    }

}

class AddStaff extends JFrame {
    Connection connection;
    Statement statement;
    public AddStaff(Connection c, Statement s) {
        connection = c;
        statement = s;

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
        JLabel nameLabel = new JLabel("이름");
        nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        JLabel IDLabel = new JLabel("ID");
        IDLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        JLabel passwdLabel = new JLabel("PASSWORD");
        passwdLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        JLabel idNumLabel = new JLabel("주민번호");
        idNumLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        JLabel phoneNumLabel = new JLabel("연락처");
        phoneNumLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        JLabel majorLabel = new JLabel("전공");
        majorLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        JLabel offDayLabel = new JLabel("휴무일");
        offDayLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        JLabel addressLabel = new JLabel("주소");
        addressLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));

        JTextField nameField = new JTextField();
        JTextField IDField = new JTextField();
        JTextField passwdField = new JTextField();
        JTextField idNumField = new JTextField();
        JTextField phoneNumField = new JTextField();
        JTextField majorField = new JTextField();
        JTextField offDayField = new JTextField();
        JTextArea addressArea = new JTextArea();

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
        phoneNumLabel.setBounds(xLabel2, 50, 100, 30);
        majorLabel.setBounds(xLabel2, 120, 100, 30);
        offDayLabel.setBounds(xLabel2, 190, 100, 30);
        addressLabel.setBounds(xLabel2, 260, 100, 30);

        // 텍스트필드 위치 설정
        nameField.setBounds(xField1, 50, 110, 30);
        IDField.setBounds(xField1, 120, 110, 30);
        passwdField.setBounds(xField1, 190, 110, 30);
        idNumField.setBounds(xField1, 260, 110, 30);
        phoneNumField.setBounds(xField2, 50, 110, 30);
        majorField.setBounds(xField2, 120, 110, 30);
        offDayField.setBounds(xField2, 190, 110, 30);
        addressScroll.setBounds(290, 260, 170, 50);

        mainPanel.add(nameLabel);
        mainPanel.add(nameField);
        mainPanel.add(IDLabel);
        mainPanel.add(IDField);
        mainPanel.add(passwdLabel);
        mainPanel.add(passwdField);
        mainPanel.add(idNumLabel);
        mainPanel.add(idNumField);
        mainPanel.add(phoneNumLabel);
        mainPanel.add(phoneNumField);
        mainPanel.add(majorLabel);
        mainPanel.add(majorField);
        mainPanel.add(offDayLabel);
        mainPanel.add(offDayField);
        mainPanel.add(addressLabel);
        mainPanel.add(addressScroll);

        //버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(null);
        buttonPanel.setPreferredSize(new Dimension(getWidth(), 70));
        buttonPanel.setBackground(new Color(200, 200, 200));

        //버튼 컴포넌트
        JButton addButton = new JButton("추가");
        JButton cancelButton = new JButton("취소");

        addButton.setBounds(150, 0, 80, 30);
        cancelButton.setBounds(250, 0, 80, 30);

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}

