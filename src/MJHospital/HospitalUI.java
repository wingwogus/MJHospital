package MJHospital;

import javax.swing.*;
import java.awt.*;

class HospitalUI extends JFrame {

    public HospitalUI() {
        setTitle("Main");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720); // 필요한 크기로 설정
        setResizable(false);

        // 상단 패널 생성
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.LIGHT_GRAY); // 배경색 설정 (선택사항)

        // 병원 로고 및 이름 레이블
        JLabel hospitalLabel = new JLabel("MJ HOSPITAL", new ImageIcon("src/img/mjicon.png"), JLabel.LEFT);
        hospitalLabel.setFont(new Font("Serif", Font.BOLD, 30));
        topPanel.add(hospitalLabel, BorderLayout.WEST);

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
