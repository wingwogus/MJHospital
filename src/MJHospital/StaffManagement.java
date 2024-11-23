package MJHospital;

import javax.swing.*;
import java.awt.*;

class StaffManagement extends JPanel {
    Staffinfo staffinfo = new Staffinfo();
    StaffList staffList = new StaffList();

    public StaffManagement() {
        setLayout(new BorderLayout());
        add(staffList, BorderLayout.WEST);
        add(staffinfo, BorderLayout.CENTER);
    }
}

class StaffList extends JPanel {
    public StaffList() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(300, getHeight()));

        // 검색 패널
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.LIGHT_GRAY);
        System.out.println(searchPanel.getPreferredSize());

        // 검색 필드와 버튼을 담을 패널
        JPanel searchFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchFieldPanel.setBackground(Color.LIGHT_GRAY);
        JLabel searchLabel = new JLabel("이름 :");
        searchLabel.setFont(new Font("Gothic", Font.PLAIN, 15));
        searchLabel.setForeground(Color.BLACK);
        JTextField searchField = new JTextField(13);

        searchFieldPanel.add(searchLabel);
        searchFieldPanel.add(searchField);


        JPanel searchButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        searchButtonPanel.setBackground(Color.LIGHT_GRAY);
        JButton searchButton = new JButton("검색");
        searchButtonPanel.add(searchButton);

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

        // 검색 패널에 컴포넌트 추가
        searchPanel.add(searchFieldPanel, BorderLayout.NORTH);
        searchPanel.add(radioPanel, BorderLayout.CENTER);
        searchPanel.add(searchButtonPanel, BorderLayout.EAST);

        // 테이블 패널
        String[] columnNames = {"직급", "이름"};
        Object[][] data = { };
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        // 추가 버튼
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("추가");
        buttonPanel.add(addButton);

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}

class Staffinfo extends JPanel {
    public Staffinfo() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 타이틀
        JLabel titleLabel = new JLabel("STAFF INFO");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // 정보 입력 패널
        JPanel infoPanel = new JPanel(new GridLayout(5, 4, 20, 10));

        // 왼쪽 정보
        infoPanel.add(new JLabel("이름"));
        infoPanel.add(createTextField("성시환", true));
        infoPanel.add(new JLabel("연락처"));
        infoPanel.add(createTextField("010-1234-1234", true));

        infoPanel.add(new JLabel("ID"));
        infoPanel.add(createTextField("sihwan", true));
        infoPanel.add(new JLabel("전공"));
        infoPanel.add(createTextField("정신과", true));

        infoPanel.add(new JLabel("PASSWORD"));
        infoPanel.add(createTextField("abc1234", true));
        infoPanel.add(new JLabel("휴무일"));
        infoPanel.add(createTextField("월", true));

        infoPanel.add(new JLabel("주민번호"));
        infoPanel.add(createTextField("021001-000000", true));
        infoPanel.add(new JLabel("주소"));
        infoPanel.add(createTextField("-", true));

        infoPanel.add(new JLabel("직급"));
        infoPanel.add(createTextField("의사", true));
        infoPanel.add(new JLabel(""));
        infoPanel.add(new JLabel(""));

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("수정");
        JButton pwdButton = new JButton("비밀번호");
        buttonPanel.add(editButton);
        buttonPanel.add(pwdButton);

        add(titleLabel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTextField createTextField(String text, boolean editable) {
        JTextField field = new JTextField(text);
        field.setEditable(false);
        field.setBackground(Color.WHITE);
        return field;
    }
}

