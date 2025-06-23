package view;

import app.GuiApp;
import model.Evento;
import model.Palestrante;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EventoDialog extends JDialog {
    private JTextField txtNome, txtLocal, txtCapacidade, txtData;
    private JTextArea txtDescricao;
    private JList<Palestrante> listPalestrantes;
    private Evento eventoAtual;

    public EventoDialog(Frame owner, Evento eventoParaEditar) {
        super(owner, "Detalhes do Evento", true);
        this.eventoAtual = eventoParaEditar;

        // --- ALTERAÇÃO PRINCIPAL NO LAYOUT ---
        // Aumentamos o tamanho da janela para acomodar o novo layout
        setSize(700, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // 1. Painel principal que vai dividir a tela em duas colunas
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 2. Painel da esquerda (formulário de texto)
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtNome = new JTextField();
        formPanel.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; formPanel.add(new JLabel("Local:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        txtLocal = new JTextField();
        formPanel.add(txtLocal, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Capacidade:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        txtCapacidade = new JTextField();
        formPanel.add(txtCapacidade, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Data (AAAA-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        txtData = new JTextField();
        formPanel.add(txtData, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        txtDescricao = new JTextArea(5, 20);
        formPanel.add(new JScrollPane(txtDescricao), gbc);

        // 3. Painel da direita (lista de palestrantes)
        DefaultListModel<Palestrante> listModel = new DefaultListModel<>();
        GuiApp.palestranteService.listarTodos().forEach(listModel::addElement);
        listPalestrantes = new JList<>(listModel);
        
        JPanel palestrantesPanel = new JPanel(new BorderLayout());
        palestrantesPanel.setBorder(BorderFactory.createTitledBorder("Selecione os Palestrantes (use Ctrl para múltiplos)"));
        palestrantesPanel.add(new JScrollPane(listPalestrantes), BorderLayout.CENTER);

        // 4. Adiciona os painéis esquerdo e direito ao painel de conteúdo
        contentPanel.add(formPanel);
        contentPanel.add(palestrantesPanel);

        // 5. Painel de botões (continua no sul)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);

        // 6. Adiciona os painéis principais ao diálogo
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Lógica para preencher o formulário (sem alteração)
        if (eventoAtual != null) {
            setTitle("Editar Evento");
            preencherFormulario();
        } else {
            setTitle("Novo Evento");
        }

        // Ações dos botões (sem alteração)
        btnSalvar.addActionListener(e -> salvarEvento());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void preencherFormulario() {
        txtNome.setText(eventoAtual.getNome());
        txtLocal.setText(eventoAtual.getLocal());
        txtCapacidade.setText(String.valueOf(eventoAtual.getCapacidade()));
        txtData.setText(eventoAtual.getData().format(DateTimeFormatter.ISO_LOCAL_DATE));
        txtDescricao.setText(eventoAtual.getDescricao());

        List<Palestrante> palestrantesVinculados = eventoAtual.getPalestrantes();
        ListModel<Palestrante> model = listPalestrantes.getModel();
        int[] indicesParaSelecionar = new int[palestrantesVinculados.size()];
        int count = 0;
        for (int i = 0; i < model.getSize(); i++) {
            for (Palestrante vinculado : palestrantesVinculados) {
                if (model.getElementAt(i).getId() == vinculado.getId()) {
                    indicesParaSelecionar[count++] = i;
                    break;
                }
            }
        }
        listPalestrantes.setSelectedIndices(indicesParaSelecionar);
    }

    private void salvarEvento() {
        try {
            String nome = txtNome.getText();
            String local = txtLocal.getText();
            String desc = txtDescricao.getText();
            int capacidade = Integer.parseInt(txtCapacidade.getText());
            LocalDate data = LocalDate.parse(txtData.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
            List<Palestrante> selecionados = listPalestrantes.getSelectedValuesList();

            if (eventoAtual == null && data.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "A data do novo evento não pode ser no passado.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (nome.trim().isEmpty() || local.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e Local são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (eventoAtual == null) {
                GuiApp.eventoService.criar(nome, desc, data, local, capacidade, selecionados);
                JOptionPane.showMessageDialog(this, "Evento criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                GuiApp.eventoService.atualizar(eventoAtual.getId(), nome, desc, data, local, capacidade, selecionados);
                JOptionPane.showMessageDialog(this, "Evento atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Capacidade deve ser um número.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Data em formato inválido. Use AAAA-MM-DD.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
}