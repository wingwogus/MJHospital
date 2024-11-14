package MJHospital;

import javax.swing.*;
import java.awt.*;

public class Consultation extends JPanel {
    public Consultation() {
        setLayout(new BorderLayout());
        add(new JLabel("진료", SwingConstants.CENTER), BorderLayout.NORTH);
    }
}
