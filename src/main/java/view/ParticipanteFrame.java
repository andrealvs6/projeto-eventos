package view;

import app.GuiApp;
import model.Participante;
import javax.swing.*;
import java.awt.*;

public class ParticipanteFrame extends JFrame {
    private final Participante participante;

    // Teremos duas tabelas e dois modelos, um para cada aba
    private JTable eventosDisponiveisTable;
    private EventoTableModel eventosDisponiveisTableModel;
    private JTable minhasInscricoesTable;
    private EventoTableModel minhasInscricoesTableModel;

    public ParticipanteFrame(Participante participante) {
        this.participante = participante;

        setTitle("Painel do Participante");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Painel Superior com boas-vindas
        JLabel welcomeLabel = new JLabel("Bem-vindo(a), " + participante.getNome() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(welcomeLabel, BorderLayout.NORTH);

        // Cria o painel de abas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Eventos Disponíveis", createEventosDisponiveisPanel());
        tabbedPane.addTab("Minhas Inscrições", createMinhasInscricoesPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // Painel para a aba "Eventos Disponíveis"
    private JPanel createEventosDisponiveisPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        eventosDisponiveisTableModel = new EventoTableModel(GuiApp.eventoService.listarEventosFuturos());
        eventosDisponiveisTable = new JTable(eventosDisponiveisTableModel);
        eventosDisponiveisTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(eventosDisponiveisTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnInscrever = new JButton("Inscrever-se no Evento Selecionado");
        buttonPanel.add(btnInscrever);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        btnInscrever.addActionListener(e -> inscrever());

        return panel;
    }

    // Painel para a nova aba "Minhas Inscrições"
    private JPanel createMinhasInscricoesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        minhasInscricoesTableModel = new EventoTableModel(GuiApp.eventoService.listarEventosPorParticipante(participante.getId()));
        minhasInscricoesTable = new JTable(minhasInscricoesTableModel);
        minhasInscricoesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(minhasInscricoesTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnCancelar = new JButton("Cancelar Inscrição");
        JButton btnCertificado = new JButton("Emitir Certificado");
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnCertificado);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> cancelarInscricao());
        btnCertificado.addActionListener(e -> emitirCertificado());

        return panel;
    }

    private void inscrever() {
        int selectedRow = eventosDisponiveisTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um evento na tabela de 'Eventos Disponíveis'.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int eventoId = (int) eventosDisponiveisTableModel.getValueAt(selectedRow, 0);
        String resultado = GuiApp.eventoService.inscreverParticipante(eventoId, this.participante);
        JOptionPane.showMessageDialog(this, resultado);
        refreshTables(); // Atualiza ambas as tabelas
    }
    
    private void cancelarInscricao() {
        int selectedRow = minhasInscricoesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um evento na tabela de 'Minhas Inscrições'.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int eventoId = (int) minhasInscricoesTableModel.getValueAt(selectedRow, 0);
        if (GuiApp.eventoService.cancelarInscricao(eventoId, this.participante)) {
            JOptionPane.showMessageDialog(this, "Inscrição cancelada com sucesso!");
        } else {
            JOptionPane.showMessageDialog(this, "Não foi possível cancelar a inscrição.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
        refreshTables(); // Atualiza ambas as tabelas
    }
    
    private void emitirCertificado() {
        int selectedRow = minhasInscricoesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um evento na tabela de 'Minhas Inscrições'.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int eventoId = (int) minhasInscricoesTableModel.getValueAt(selectedRow, 0);
        String certificado = GuiApp.eventoService.emitirCertificado(eventoId, this.participante);
        
        JTextArea textArea = new JTextArea(certificado);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 200));
        JOptionPane.showMessageDialog(this, scrollPane, "Certificado de Participação", JOptionPane.INFORMATION_MESSAGE);
    }

    // Método único para atualizar os dados de ambas as tabelas
    private void refreshTables() {
        eventosDisponiveisTableModel.updateData(GuiApp.eventoService.listarEventosFuturos());
        minhasInscricoesTableModel.updateData(GuiApp.eventoService.listarEventosPorParticipante(participante.getId()));
    }
}