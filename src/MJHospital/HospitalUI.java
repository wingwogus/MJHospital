package MJHospital;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;

class HospitalUI extends JFrame {

    private String currentStaffId;

    public HospitalUI(String i, String n, String r, Connection c) {
        this.currentStaffId = i;

        setTitle("Main");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setResizable(false);
        setLocationRelativeTo(null);

        String id = i;
        String name = n;
        String role = r;

        Connection connection = c;

        // 상단 패널 생성
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.setPreferredSize(new Dimension(getWidth(), 70));


        // 병원 로고 및 이름 레이블
        JLabel hospitalLabel = new JLabel("MJ HOSPITAL", new ImageIcon("images/mjicon.png"), JLabel.LEFT);
        hospitalLabel.setFont(new Font("Serif", Font.BOLD, 30));
        topPanel.add(hospitalLabel, BorderLayout.WEST);

        //유저 정보 패널
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
        Consultation consultation = new Consultation(currentStaffId);
        tabbedPane.addTab("진료", consultation);
        tabbedPane.addTab("환자 관리", new PatientManagement());
        tabbedPane.addTab("진료 관리", new ConsultationManagement());
        tabbedPane.addTab("정보 관리", new StaffManagement(connection, id, role));
        tabbedPane.addTab("예약 관리", new ReservationManagement(consultation));

        // 프레임에 패널 추가
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() { //창 종료시 커넥션 클로즈
            @Override
            public void windowClosing(WindowEvent e) {
                if (connection != null) {
                    try {
                        connection.close();
                        JOptionPane.showMessageDialog(HospitalUI.this, "데이터베이스 연결 해제", "성공", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
}
