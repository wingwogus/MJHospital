package MJHospital;

import javax.swing.*;
import java.awt.*;

public class Consultation extends JPanel {

    private JPanel mainPanel, p1Panel, p2Panel, p3Panel;
    private JList<String> patientList;
    private JTextArea recordDetails, symptomsArea, doctorAdviceArea, prescriptionArea;
    private JButton saveButton;
    private DefaultListModel<String> patientModel;

    public Consultation() {
        setLayout(new BorderLayout());
        add(new JLabel("진료", SwingConstants.CENTER), BorderLayout.NORTH);

        // 메인 레이아웃 설정
        setSize(1000, 600);
        setLayout(new BorderLayout());

        // 패널 초기화
        initializeP1Panel();
        initializeP2Panel();
        initializeP3Panel();

        // 메인 패널에 각 패널 추가
        mainPanel = new JPanel(new GridLayout(1, 3));
        mainPanel.add(p1Panel);
        mainPanel.add(p2Panel);
        mainPanel.add(p3Panel);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }
    private void initializeP1Panel() {
        // P1 패널 설정
        p1Panel = new JPanel(new BorderLayout());
        p1Panel.setBorder(BorderFactory.createTitledBorder("Today's Patients"));

        patientModel = new DefaultListModel<>();
        // 예시 데이터 추가
        patientModel.addElement("이재현 - 병명");
        patientModel.addElement("서준원 - 병명");
        patientModel.addElement("김경준 - 병명");
        patientModel.addElement("김건우 - 병명");

        patientList = new JList<>(patientModel);

        p1Panel.add(new JScrollPane(patientList), BorderLayout.CENTER);
    }

    private void initializeP2Panel() {
        // P2 패널 설정
        p2Panel = new JPanel(new BorderLayout());
        p2Panel.setBorder(BorderFactory.createTitledBorder("Patient Details"));

        recordDetails = new JTextArea("과거 진료 기록이 여기에 표시됩니다.");
        recordDetails.setEditable(false);
        p2Panel.add(new JScrollPane(recordDetails), BorderLayout.CENTER);
    }

    private void initializeP3Panel() {
        // P3 패널 설정
        p3Panel = new JPanel(new BorderLayout());
        p3Panel.setBorder(BorderFactory.createTitledBorder("Consultation"));

        JLabel symptoms = new JLabel("증상");
        JLabel doctorAdvice = new JLabel("의사 소견");
        JLabel presLb = new JLabel("처방");

        JPanel formPanel = new JPanel(new GridLayout(3, 1));
        symptomsArea = new JTextArea("증상");
        doctorAdviceArea = new JTextArea("의사 소견");
        prescriptionArea = new JTextArea("처방");

        formPanel.add(new JScrollPane(symptomsArea));
        formPanel.add(new JScrollPane(doctorAdviceArea));
        formPanel.add(new JScrollPane(prescriptionArea));


        saveButton = new JButton("저장");

        p3Panel.add(formPanel, BorderLayout.CENTER);
        p3Panel.add(saveButton, BorderLayout.SOUTH);
    }
}
