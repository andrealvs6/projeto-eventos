package service;

import dao.DatabaseManager;
import model.Evento;
import model.Palestrante;
import model.Participante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventoServiceTest {
    private EventoService eventoService;
    private PalestranteService palestranteService;
    private ParticipanteService participanteService;

    @BeforeEach
    void setUp() {
        DatabaseManager.setDatabaseUrl("jdbc:sqlite:eventos_test.db");
        DatabaseManager.initializeDatabase();
        limparTabelas();
        eventoService = new EventoService();
        palestranteService = new PalestranteService();
        participanteService = new ParticipanteService();
    }

    private void limparTabelas() {
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM evento_participante");
            stmt.execute("DELETE FROM evento_palestrante");
            stmt.execute("DELETE FROM eventos");
            stmt.execute("DELETE FROM participantes");
            stmt.execute("DELETE FROM palestrantes");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Test
    @DisplayName("Deve inscrever participante com sucesso em evento com vagas")
    void deveInscreverParticipanteComSucesso() {
        Palestrante palestrante = palestranteService.cadastrar("Teste", "Bio", "Java");
        Evento evento = eventoService.criar("Evento Vago", "Desc", LocalDate.now().plusDays(1), "Online", 2, List.of(palestrante));
        Participante p = participanteService.cadastrar("Fulano", "fulano@teste.com");

        String resultado = eventoService.inscreverParticipante(evento.getId(), p);
        
        assertEquals("Inscrição realizada com sucesso!", resultado);
        Evento eAtualizado = eventoService.buscarPorId(evento.getId());
        assertEquals(1, eAtualizado.getParticipantesInscritos().size());
        assertTrue(eAtualizado.getParticipantesInscritos().stream().anyMatch(pi -> pi.getId() == p.getId()));
    }

    @Test
    @DisplayName("Não deve inscrever participante em evento lotado")
    void naoDeveInscreverEmEventoLotado() {
        Evento e = eventoService.criar("Evento Lotado", "D", LocalDate.now().plusDays(1), "L", 1, Collections.emptyList());
        Participante p1 = participanteService.cadastrar("Fulano", "fulano@teste.com");
        Participante p2 = participanteService.cadastrar("Ciclano", "ciclano@teste.com");
        eventoService.inscreverParticipante(e.getId(), p1);

        String resultado = eventoService.inscreverParticipante(e.getId(), p2);

        assertEquals("Inscrição não realizada. O evento está lotado.", resultado);
    }

    @Test
    @DisplayName("Não deve inscrever o mesmo participante duas vezes")
    void naoDeveInscreverParticipanteDuasVezes() {
        Evento e = eventoService.criar("Evento Teste", "D", LocalDate.now().plusDays(1), "L", 5, Collections.emptyList());
        Participante p = participanteService.cadastrar("Beltrano", "beltrano@teste.com");
        eventoService.inscreverParticipante(e.getId(), p);

        String resultado = eventoService.inscreverParticipante(e.getId(), p);

        assertEquals("Participante já está inscrito neste evento.", resultado);
    }
}