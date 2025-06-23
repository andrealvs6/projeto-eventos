package view;

import app.GuiApp;
import model.Participante;
import javax.swing.*;
import java.awt.*;

public class ParticipanteLoginDialog extends JDialog {
    private JTextField txtEmail;

    public ParticipanteLoginDialog(Frame owner) {
        super(owner, "Acesso do Participante", true);
        // Ajustando o tamanho para uma melhor proporção
        setSize(450, 200);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));


        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento entre componentes

        // 2. Configurações para o JLabel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.WEST; 
        JLabel label = new JLabel("Digite seu e-mail para entrar ou se cadastrar:");
        panel.add(label, gbc);

        // 3. Configurações para o JTextField
        gbc.gridy = 1; // Próxima linha
        gbc.weightx = 1.0; // Permite que o campo cresça horizontalmente
        gbc.fill = GridBagConstraints.HORIZONTAL; // Preenche o espaço horizontal
        txtEmail = new JTextField();
        panel.add(txtEmail, gbc);


        JButton btnAcessar = new JButton("Acessar");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(btnAcessar);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        btnAcessar.addActionListener(e -> acessar());
    }

    private void acessar() {
        String email = txtEmail.getText().trim();
        if (email.isEmpty() || !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um e-mail válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Participante participante = GuiApp.participanteService.buscarPorEmail(email).orElse(null);

        if (participante == null) {
            int confirm = JOptionPane.showConfirmDialog(this, "E-mail não encontrado. Deseja se cadastrar?", "Novo Participante", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String nome = JOptionPane.showInputDialog(this, "Digite seu nome completo:");
                if (nome != null && !nome.trim().isEmpty()) {
                    participante = GuiApp.participanteService.cadastrar(nome, email);
                    JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso, " + nome + "!");
                } else {
                    return; 
                }
            } else {
                return;
            }
        }
        
        if (participante != null) {
            dispose();
            ParticipanteFrame participanteFrame = new ParticipanteFrame(participante);
            participanteFrame.setVisible(true);
        }
    }
}