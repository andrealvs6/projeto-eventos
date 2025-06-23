package view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Sistema de Gerenciamento de Eventos");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Bem-vindo! Acesse como:", SwingConstants.CENTER);
        JButton btnOrganizador = new JButton("Organizador");
        JButton btnParticipante = new JButton("Participante");

        panel.add(label);
        panel.add(btnOrganizador);
        panel.add(btnParticipante);

        add(panel);

        btnOrganizador.addActionListener(e -> {
            OrganizadorFrame organizadorFrame = new OrganizadorFrame();
            organizadorFrame.setVisible(true);
        });

        btnParticipante.addActionListener(e -> {
            ParticipanteLoginDialog loginDialog = new ParticipanteLoginDialog(this);
            loginDialog.setVisible(true);
        });
    }
}