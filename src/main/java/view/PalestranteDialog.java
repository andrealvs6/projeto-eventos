package view;

import app.GuiApp;
import javax.swing.*;
import java.awt.*;

public class PalestranteDialog extends JDialog {
    private JTextField txtNome;
    private JTextArea txtCurriculo;
    private JTextField txtAreaDeAtuacao;

    public PalestranteDialog(Frame owner) {
        super(owner, "Novo Palestrante", true);
        setSize(450, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtNome = new JTextField();
        formPanel.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Área de Atuação:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        txtAreaDeAtuacao = new JTextField();
        formPanel.add(txtAreaDeAtuacao, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(new JLabel("Currículo/Bio:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        txtCurriculo = new JTextArea(5, 20);
        formPanel.add(new JScrollPane(txtCurriculo), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        btnSalvar.addActionListener(e -> salvarPalestrante());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void salvarPalestrante() {
        String nome = txtNome.getText();
        String curriculo = txtCurriculo.getText();
        String area = txtAreaDeAtuacao.getText();

        if (nome.trim().isEmpty() || area.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e Área de Atuação são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        GuiApp.palestranteService.cadastrar(nome, curriculo, area);
        JOptionPane.showMessageDialog(this, "Palestrante cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}