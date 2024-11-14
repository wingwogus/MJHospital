import javax.swing.*;
import java.awt.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        HospitalUI frame = new HospitalUI();
        frame.setVisible(true);
        System.out.println("이건 feature login");
    }

    static class HospitalUI extends JFrame {

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
            ImageIcon icon1 = new ImageIcon("img.png");
            JLabel hospitalLabel = new JLabel("MJ HOSPITAL", icon1, JLabel.LEFT);
            hospitalLabel.setFont(new Font("Serif", Font.BOLD, 30));
            topPanel.add(hospitalLabel, BorderLayout.WEST);

            // 탭 패널 생성
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("진료", new JPanel());
            tabbedPane.addTab("환자 관리", new JPanel());
            tabbedPane.addTab("진료 관리", new JPanel());
            tabbedPane.addTab("예약 관리", new JPanel());
            tabbedPane.addTab("정보 관리", new JPanel());

            // 프레임에 패널 추가
            add(topPanel, BorderLayout.NORTH);
            add(tabbedPane, BorderLayout.CENTER);
        }
    }
}