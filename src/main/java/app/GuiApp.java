package app;

import dao.DatabaseManager;
import model.Palestrante;
import service.EventoService;
import service.PalestranteService;
import service.ParticipanteService;
import view.MainFrame;
import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

public class GuiApp {

    public static final EventoService eventoService = new EventoService();
    public static final PalestranteService palestranteService = new PalestranteService();
    public static final ParticipanteService participanteService = new ParticipanteService();

    public static void main(String[] args) {
        // Passo 1: Inicializa o banco de dados e cria as tabelas se não existirem.
        DatabaseManager.initializeDatabase();

        // Passo 2: Define o Look and Feel para o padrão do sistema operacional.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Passo 3: Inicia a interface gráfica na thread de eventos do Swing.
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}