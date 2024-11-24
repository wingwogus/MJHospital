package MJHospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

class StaffManagement extends JPanel {
    StaffInfo staffInfo = new StaffInfo();
    StaffList staffList = new StaffList();

    public StaffManagement() {
        setLayout(new BorderLayout());
        add(staffList, BorderLayout.WEST);
        add(staffInfo, BorderLayout.CENTER);
    }
}

class StaffList extends JPanel implements ActionListener {
    private Vector<String> columnNames;
    private Vector<Vector<String>> data;
    private DefaultTableModel model;
    private JTable table;

    public StaffList() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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

        // 추가 버튼
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("추가");
        buttonPanel.add(addButton);

        addButton.addActionListener(this);

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("추가")) {
            AddStaff addStaff = new AddStaff();
            addStaff.setVisible(true);
        }
        else if (s.equals("검색")){

        }
    }
}

class StaffInfo extends JPanel implements ActionListener {
    public StaffInfo() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Color backgroundColor = new Color(200, 200, 200);

        // 타이틀
        JLabel topLabel = new JLabel(" STAFF INFO");
        topLabel.setFont(new Font("Arial", Font.BOLD, 30));

        // 상단 패널
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.setPreferredSize(new Dimension(getWidth(), 50));
        topPanel.add(topLabel);

        JPanel centerPanel = new JPanel(new GridLayout(5, 2));
        centerPanel.setBackground(backgroundColor);

        JLabel nameLabel = new JLabel("이름");
        nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
        JLabel IDLabel = new JLabel("ID");
        IDLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
        JLabel passwdLabel = new JLabel("PASSWORD");
        passwdLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
        JLabel idNumLabel = new JLabel("주민번호");
        idNumLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
        JLabel titleLabel = new JLabel("직급");
        titleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
        JLabel phoneNumLabel = new JLabel("연락처");
        phoneNumLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
        JLabel majorLabel = new JLabel("전공");
        majorLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
        JLabel offDayLabel = new JLabel("휴뮤일");
        offDayLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
        JLabel addressLabel = new JLabel("주소");
        addressLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));

        JTextField nameField = new JTextField(20);
        JTextField IDField = new JTextField(20);
        JTextField passwdField = new JTextField(20);
        JTextField idNumField = new JTextField(20);
        JTextField titleField = new JTextField(20);
        JTextField phoneNumField = new JTextField(20);
        JTextField majorField = new JTextField(20);
        JTextField offDayField = new JTextField(20);
        JTextField addressField = new JTextField(20);

        JPanel namePanel = new JPanel(new BorderLayout());
        JPanel IDPanel = new JPanel(new BorderLayout());
        JPanel passwdPanel = new JPanel(new BorderLayout());
        JPanel idNumPanel = new JPanel(new BorderLayout());
        JPanel titlePanel = new JPanel(new BorderLayout());
        JPanel phoneNumPanel = new JPanel(new BorderLayout());
        JPanel majorPanel = new JPanel(new BorderLayout());
        JPanel offDayPanel = new JPanel(new BorderLayout());
        JPanel addressPanel = new JPanel(new BorderLayout());

        namePanel.setBackground(backgroundColor);
        namePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        IDPanel.setBackground(backgroundColor);
        IDPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        passwdPanel.setBackground(backgroundColor);
        passwdPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        idNumPanel.setBackground(backgroundColor);
        idNumPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        titlePanel.setBackground(backgroundColor);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        phoneNumPanel.setBackground(backgroundColor);
        phoneNumPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        majorPanel.setBackground(backgroundColor);
        majorPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        offDayPanel.setBackground(backgroundColor);
        offDayPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        addressPanel.setBackground(backgroundColor);
        addressPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        namePanel.add(nameLabel, BorderLayout.CENTER);
        namePanel.add(nameField, BorderLayout.EAST);
        IDPanel.add(IDLabel, BorderLayout.WEST);
        IDPanel.add(IDField, BorderLayout.EAST);
        passwdPanel.add(passwdLabel, BorderLayout.WEST);
        passwdPanel.add(passwdField, BorderLayout.EAST);
        idNumPanel.add(idNumLabel, BorderLayout.WEST);
        idNumPanel.add(idNumField, BorderLayout.EAST);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(titleField, BorderLayout.EAST);
        phoneNumPanel.add(phoneNumLabel, BorderLayout.WEST);
        phoneNumPanel.add(phoneNumField, BorderLayout.EAST);
        majorPanel.add(majorLabel, BorderLayout.WEST);
        majorPanel.add(majorField, BorderLayout.EAST);
        offDayPanel.add(offDayLabel, BorderLayout.WEST);
        offDayPanel.add(offDayField, BorderLayout.EAST);
        addressPanel.add(addressLabel, BorderLayout.WEST);
        addressPanel.add(addressField, BorderLayout.EAST);

        centerPanel.add(namePanel);
        centerPanel.add(phoneNumPanel);
        centerPanel.add(IDPanel);
        centerPanel.add(majorPanel);
        centerPanel.add(passwdPanel);
        centerPanel.add(offDayPanel);
        centerPanel.add(idNumPanel);
        centerPanel.add(addressPanel);
        centerPanel.add(titlePanel);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("수정");
        JButton inactivationButton = new JButton("비활성화");
        buttonPanel.add(editButton);
        buttonPanel.add(inactivationButton);

        editButton.addActionListener(this);
        inactivationButton.addActionListener(this);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {

    }

}

class AddStaff extends JFrame {
    public AddStaff() {
        setTitle("의료진 추가");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(200, 200, 200));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.setPreferredSize(new Dimension(getWidth(), 50));

        JLabel topLabel = new JLabel("의료진 추가");
        topLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        topPanel.add(topLabel);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(8, 2, 10, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(200, 200, 200));

        JLabel nameLabel = new JLabel("이름");
        nameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
        JLabel IDLabel = new JLabel("ID");
        IDLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
        JLabel passwdLabel = new JLabel("PASSWORD");
        passwdLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
        JLabel idNumLabel = new JLabel("주민번호");
        idNumLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
        JLabel phoneNumLabel = new JLabel("연락처");
        phoneNumLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
        JLabel majorLabel = new JLabel("전공");
        majorLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
        JLabel offDayLabel = new JLabel("휴뮤일");
        offDayLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));
        JLabel addressLabel = new JLabel("주소");
        addressLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 25));

        JTextField nameField = new JTextField(20);
        JTextField IDField = new JTextField(20);
        JTextField passwdField = new JTextField(20);
        JTextField idNumField = new JTextField(20);
        JTextField phoneNumField = new JTextField(20);
        JTextField majorField = new JTextField(20);
        JTextField offDayField = new JTextField(20);
        JTextField addressField = new JTextField(20);

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
        mainPanel.add(addressField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(200, 200, 200));
        JButton addButton = new JButton("추가");
        buttonPanel.add(addButton);

        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}

