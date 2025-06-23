package view;

import model.Participante;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ParticipanteTableModel extends AbstractTableModel {
    private List<Participante> participantes;
    private final String[] columnNames = {"ID", "Nome", "E-mail"};

    public ParticipanteTableModel(List<Participante> participantes) {
        this.participantes = participantes;
    }

    // --- NOVO MÉTODO ---
    // Este método permite atualizar a lista de participantes e notificar a tabela
    public void updateData(List<Participante> novosParticipantes) {
        this.participantes = novosParticipantes;
        fireTableDataChanged(); // Essencial para atualizar a JTable visualmente
    }
    
    // --- NOVO MÉTODO AUXILIAR ---
    public Participante getParticipanteAt(int rowIndex) {
        return participantes.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return participantes.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Participante participante = participantes.get(rowIndex);
        switch (columnIndex) {
            case 0: return participante.getId();
            case 1: return participante.getNome();
            case 2: return participante.getEmail();
            default: return null;
        }
    }
}