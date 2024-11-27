package MJHospital;

import javax.swing.*;
import java.awt.*;

class HospitalUI extends JFrame {

    public HospitalUI(String n, String r) {
        setTitle("Main");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setResizable(false);

        String name = n;
        String role = r;

        // 상단 패널 생성
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.setPreferredSize(new Dimension(getWidth(), 70));


        // 병원 로고 및 이름 레이블
        JLabel hospitalLabel = new JLabel("MJ HOSPITAL", new ImageIcon("images/mjicon.png"), JLabel.LEFT);
        hospitalLabel.setFont(new Font("Serif", Font.BOLD, 30));
        topPanel.add(hospitalLabel, BorderLayout.WEST);

        JPanel userPanel = new JPanel();
        userPanel.setBackground(Color.LIGHT_GRAY);
        userPanel.setPreferredSize(new Dimension(200, 40));
        JLabel nameLabel =  new JLabel("이름: " + name);
        JLabel roleLabel = new JLabel("직급: " + role);
        nameLabel.setFont(new Font("Gothic", Font.PLAIN, 20));
        roleLabel.setFont(new Font("Gothic", Font.PLAIN, 20));
        userPanel.add(nameLabel);
        userPanel.add(roleLabel);
        topPanel.add(userPanel, BorderLayout.EAST);

        // 탭 패널 생성
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("진료", new Consultation());
        tabbedPane.addTab("환자 관리", new PatientManagement());
        tabbedPane.addTab("진료 관리", new ConsultationManagement());
        tabbedPane.addTab("예약 관리", new ReservationManagement());
        tabbedPane.addTab("정보 관리", new StaffManagement());

        // 프레임에 패널 추가
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
}
