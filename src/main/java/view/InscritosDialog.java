package view;

import app.GuiApp;
import model.Evento;
import model.Participante;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InscritosDialog extends JDialog {
    
    // Precisamos de referências para estes componentes nos métodos
    private final Evento evento;
    private JTable table;
    private ParticipanteTableModel tableModel;

    public InscritosDialog(Frame owner, Evento evento) {
        super(owner, "Inscritos no Evento", true);
        this.evento = evento; // Armazena o evento

        setTitle("Inscritos: " + evento.getNome());
        setSize(600, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        
        List<Participante> inscritos = evento.getParticipantesInscritos();

        if (inscritos.isEmpty()) {
            add(new JLabel("Ainda não há participantes inscritos neste evento.", SwingConstants.CENTER), BorderLayout.CENTER);
        } else {
            // Cria a tabela e a armazena na variável de instância
            tableModel = new ParticipanteTableModel(inscritos);
            table = new JTable(tableModel);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            add(new JScrollPane(table), BorderLayout.CENTER);
        }

        // --- ALTERAÇÃO AQUI: Adicionando o novo botão ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton btnRemover = new JButton("Remover Participante Selecionado");
        JButton btnFechar = new JButton("Fechar");

        // Só adiciona o botão de remover se a tabela existir (ou seja, se houver inscritos)
        if (table != null) {
            buttonPanel.add(btnRemover);
        }
        buttonPanel.add(btnFechar);
        add(buttonPanel, BorderLayout.SOUTH);

        // Ação para o novo botão
        btnRemover.addActionListener(e -> removerParticipante());
        
        btnFechar.addActionListener(e -> dispose());
    }

    // --- NOVO MÉTODO PARA A LÓGICA DE REMOÇÃO ---
    private void removerParticipante() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um participante na lista para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Participante participanteParaRemover = tableModel.getParticipanteAt(selectedRow);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente remover a inscrição de '" + participanteParaRemover.getNome() + "'?",
                "Confirmar Remoção",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Reutilizamos o método do service!
            boolean sucesso = GuiApp.eventoService.cancelarInscricao(this.evento.getId(), participanteParaRemover);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Participante removido com sucesso!");
                // Atualiza a lista para refletir a remoção
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Ocorreu um erro ao remover o participante.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- NOVO MÉTODO PARA ATUALIZAR A TABELA ---
    private void refreshTable() {
        // Busca a lista atualizada de participantes do banco de dados
        Evento eventoAtualizado = GuiApp.eventoService.buscarPorId(this.evento.getId());
        if (eventoAtualizado != null) {
            tableModel.updateData(eventoAtualizado.getParticipantesInscritos());
        }
    }
}