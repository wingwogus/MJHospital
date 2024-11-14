package MJHospital;

import javax.swing.*;
import java.awt.*;

public class PatientManagement extends JPanel {
    public PatientManagement() {
        setLayout(new BorderLayout());
        add(new JLabel("환자 관리", SwingConstants.CENTER), BorderLayout.NORTH);
    }
}
