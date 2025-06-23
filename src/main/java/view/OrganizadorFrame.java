package view;

import app.GuiApp;
import model.Evento;
import model.Palestrante;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizadorFrame extends JFrame {
    private JTable eventosTable;
    private JTable palestrantesTable;
    private EventoTableModel eventoTableModel;
    private PalestranteTableModel palestranteTableModel;

    public OrganizadorFrame() {
        setTitle("Painel do Organizador");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Cria um painel principal para adicionar margens internas (padding)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Cria e configura o painel de abas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Gerenciar Eventos", createEventosPanel());
        tabbedPane.addTab("Gerenciar Palestrantes", createPalestrantesPanel());

        // Adiciona o painel de abas ao painel principal
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Define o painel principal como o contêiner da janela
        setContentPane(mainPanel);
    }
    
    private JPanel createEventosPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        eventoTableModel = new EventoTableModel(GuiApp.eventoService.listarTodos());
        eventosTable = new JTable(eventoTableModel);
        panel.add(new JScrollPane(eventosTable), BorderLayout.CENTER);

        // Painel com todos os botões de ação para eventos
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnNovo = new JButton("Novo Evento");
        JButton btnEditar = new JButton("Editar Evento");
        JButton btnVerInscritos = new JButton("Ver Inscritos");
        JButton btnCancelar = new JButton("Cancelar Evento");
        
        buttonsPanel.add(btnNovo);
        buttonsPanel.add(btnEditar);
        buttonsPanel.add(btnVerInscritos);
        buttonsPanel.add(btnCancelar);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Ação para o botão "Novo Evento"
        btnNovo.addActionListener(e -> {
            EventoDialog dialog = new EventoDialog(this, null);
            dialog.setVisible(true);
            refreshEventosTable();
        });
        
        // Ação para o botão "Editar Evento"
        btnEditar.addActionListener(e -> {
            int selectedRow = eventosTable.getSelectedRow();
            if (selectedRow >= 0) {
                int eventoId = (int) eventoTableModel.getValueAt(selectedRow, 0);
                Evento eventoParaEditar = GuiApp.eventoService.buscarPorId(eventoId);
                
                EventoDialog dialog = new EventoDialog(this, eventoParaEditar);
                dialog.setVisible(true);
                refreshEventosTable();
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um evento na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Ação para o botão "Ver Inscritos"
        btnVerInscritos.addActionListener(e -> {
            int selectedRow = eventosTable.getSelectedRow();
            if (selectedRow >= 0) {
                int eventoId = (int) eventoTableModel.getValueAt(selectedRow, 0);
                Evento evento = GuiApp.eventoService.buscarPorId(eventoId);
                if (evento != null) {
                    InscritosDialog dialog = new InscritosDialog(this, evento);
                    dialog.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um evento na tabela para ver os inscritos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Ação para o botão "Cancelar Evento"
        btnCancelar.addActionListener(e -> {
            int selectedRow = eventosTable.getSelectedRow();
            if (selectedRow >= 0) {
                int eventoId = (int) eventoTableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Tem certeza que deseja cancelar este evento?", "Confirmação",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    GuiApp.eventoService.cancelar(eventoId);
                    refreshEventosTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um evento na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createPalestrantesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        palestranteTableModel = new PalestranteTableModel(GuiApp.palestranteService.listarTodos());
        palestrantesTable = new JTable(palestranteTableModel);
        panel.add(new JScrollPane(palestrantesTable), BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnNovo = new JButton("Novo Palestrante");
        JButton btnExcluir = new JButton("Excluir Palestrante");
        buttonsPanel.add(btnNovo);
        buttonsPanel.add(btnExcluir);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        btnNovo.addActionListener(e -> {
            PalestranteDialog dialog = new PalestranteDialog(this);
            dialog.setVisible(true);
            refreshPalestrantesTable();
        });
        
        btnExcluir.addActionListener(e -> {
            int selectedRow = palestrantesTable.getSelectedRow();
            if (selectedRow >= 0) {
                int palestranteId = (int) palestranteTableModel.getValueAt(selectedRow, 0);
                 int confirm = JOptionPane.showConfirmDialog(this,
                        "Tem certeza que deseja excluir este palestrante?\n(Isso o desvinculará de todos os eventos)", "Confirmação",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (!GuiApp.palestranteService.excluir(palestranteId)) {
                        JOptionPane.showMessageDialog(this, "Erro ao excluir o palestrante.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                    refreshPalestrantesTable();
                    refreshEventosTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um palestrante na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        return panel;
    }

    private void refreshEventosTable() {
        eventoTableModel.updateData(GuiApp.eventoService.listarTodos());
    }

    private void refreshPalestrantesTable() {
        palestranteTableModel.updateData(GuiApp.palestranteService.listarTodos());
    }
}

// Classe interna ou separada para o modelo da tabela de Eventos
class EventoTableModel extends AbstractTableModel {
    private List<Evento> eventos;
    private final String[] columnNames = {"ID", "Nome", "Data", "Local", "Vagas", "Palestrantes"};

    public EventoTableModel(List<Evento> eventos) {
        this.eventos = eventos;
    }

    public void updateData(List<Evento> eventos) {
        this.eventos = eventos;
        fireTableDataChanged();
    }

    @Override public int getRowCount() { return eventos.size(); }
    @Override public int getColumnCount() { return columnNames.length; }
    @Override public String getColumnName(int column) { return columnNames[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Evento evento = eventos.get(rowIndex);
        switch (columnIndex) {
            case 0: return evento.getId();
            case 1: return evento.getNome();
            case 2: return evento.getData();
            case 3: return evento.getLocal();
            case 4: return evento.getVagasDisponiveis() + "/" + evento.getCapacidade();
            case 5:
                return evento.getPalestrantes().stream()
                        .map(Palestrante::getNome)
                        .collect(Collectors.joining(", "));
            default: return null;
        }
    }
}

// Classe interna ou separada para o modelo da tabela de Palestrantes
class PalestranteTableModel extends AbstractTableModel {
    private List<Palestrante> palestrantes;
    private final String[] columnNames = {"ID", "Nome", "Área de Atuação"};
    
    public PalestranteTableModel(List<Palestrante> palestrantes) {
        this.palestrantes = new ArrayList<>(palestrantes);
    }
    
    public void updateData(List<Palestrante> palestrantes) {
        this.palestrantes = new ArrayList<>(palestrantes);
        fireTableDataChanged();
    }
    
    @Override public int getRowCount() { return palestrantes.size(); }
    @Override public int getColumnCount() { return columnNames.length; }
    @Override public String getColumnName(int column) { return columnNames[column]; }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Palestrante p = palestrantes.get(rowIndex);
        switch (columnIndex) {
            case 0: return p.getId();
            case 1: return p.getNome();
            case 2: return p.getAreaDeAtuacao();
            default: return null;
        }
    }
}