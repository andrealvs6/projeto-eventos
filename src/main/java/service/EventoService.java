package service;

import dao.DatabaseManager;
import model.Evento;
import model.Palestrante;
import model.Participante;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventoService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    // --- Métodos de Evento ---
    public Evento criar(String nome, String desc, LocalDate data, String local, int cap, List<Palestrante> palestrantes) {
        String sql = "INSERT INTO eventos(nome, descricao, data, local, capacidade) VALUES(?,?,?,?,?)";
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false); // Inicia transação

            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, nome);
                pstmt.setString(2, desc);
                pstmt.setString(3, data.format(formatter));
                pstmt.setString(4, local);
                pstmt.setInt(5, cap);

                if (pstmt.executeUpdate() > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int eventoId = rs.getInt(1);
                            for (Palestrante p : palestrantes) {
                                vincularPalestrante(conn, eventoId, p.getId());
                            }
                            conn.commit(); // Finaliza transação
                            return buscarPorId(eventoId);
                        }
                    }
                }
            }
            conn.rollback(); // Desfaz transação em caso de falha
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException ex) { ex.printStackTrace(); }
        }
        return null;
    }

    public boolean atualizar(int id, String nome, String desc, LocalDate data, String local, int cap, List<Palestrante> palestrantes) {
        String sql = "UPDATE eventos SET nome = ?, descricao = ?, data = ?, local = ?, capacidade = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nome);
                pstmt.setString(2, desc);
                pstmt.setString(3, data.format(formatter));
                pstmt.setString(4, local);
                pstmt.setInt(5, cap);
                pstmt.setInt(6, id);
                pstmt.executeUpdate();
            }

            desvincularTodosPalestrantes(conn, id);
            for (Palestrante p : palestrantes) {
                vincularPalestrante(conn, id, p.getId());
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException ex) { ex.printStackTrace(); }
        }
        return false;
    }
    
    public Evento buscarPorId(int id) {
        String sql = "SELECT * FROM eventos WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return buildEventoFromResultSet(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Evento> listarTodos() {
        return findEventos("SELECT * FROM eventos ORDER BY data DESC", null);
    }
    
    public List<Evento> listarEventosFuturos() {
        return findEventos("SELECT * FROM eventos WHERE data >= ? ORDER BY data ASC", LocalDate.now().format(formatter));
    }

    public List<Evento> listarEventosPorParticipante(int participanteId) {
        String sql = "SELECT e.* FROM eventos e " +
                     "JOIN evento_participante ep ON e.id = ep.evento_id " +
                     "WHERE ep.participante_id = ? ORDER BY e.data DESC";
        return findEventos(sql, participanteId);
    }
    
    public boolean cancelar(int id) {
        String sql = "DELETE FROM eventos WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    
    // --- Métodos de Vinculação (Palestrante) ---
    private void vincularPalestrante(Connection conn, int eventoId, int palestranteId) throws SQLException {
        String sql = "INSERT INTO evento_palestrante(evento_id, palestrante_id) VALUES(?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventoId);
            pstmt.setInt(2, palestranteId);
            pstmt.executeUpdate();
        }
    }
    
    private void desvincularTodosPalestrantes(Connection conn, int eventoId) throws SQLException {
        String sql = "DELETE FROM evento_palestrante WHERE evento_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventoId);
            pstmt.executeUpdate();
        }
    }
    
    private List<Palestrante> getPalestrantesDoEvento(int eventoId) {
        List<Palestrante> palestrantes = new ArrayList<>();
        String sql = "SELECT p.* FROM palestrantes p JOIN evento_palestrante ep ON p.id = ep.palestrante_id WHERE ep.evento_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    palestrantes.add(new Palestrante(rs.getInt("id"), rs.getString("nome"), rs.getString("curriculo"), rs.getString("area_de_atuacao")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return palestrantes;
    }

    // --- Métodos de Vinculação (Participante) ---
    public String inscreverParticipante(int idEvento, Participante participante) {
        Evento evento = buscarPorId(idEvento);
        if (evento == null) return "Evento não encontrado.";
        if (evento.getParticipantesInscritos().contains(participante)) return "Participante já está inscrito neste evento.";
        if (evento.getVagasDisponiveis() <= 0) return "Inscrição não realizada. O evento está lotado.";
        
        String sql = "INSERT INTO evento_participante(evento_id, participante_id) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEvento);
            pstmt.setInt(2, participante.getId());
            if (pstmt.executeUpdate() > 0) return "Inscrição realizada com sucesso!";
        } catch (SQLException e) { e.printStackTrace(); }
        return "Ocorreu um erro ao realizar a inscrição.";
    }

    public boolean cancelarInscricao(int idEvento, Participante participante) {
        String sql = "DELETE FROM evento_participante WHERE evento_id = ? AND participante_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEvento);
            pstmt.setInt(2, participante.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public String emitirCertificado(int idEvento, Participante participante) {
        Evento evento = buscarPorId(idEvento);
        if (evento == null) return "Evento não encontrado.";
        if (!evento.getParticipantesInscritos().contains(participante)) return "O participante não está inscrito neste evento.";
        if (evento.getData().isAfter(LocalDate.now())) return "Certificado não pode ser emitido antes da data do evento.";
        
        return "--- CERTIFICADO DE PARTICIPAÇÃO ---\n" +
               "Certificamos que " + participante.getNome() + " (" + participante.getEmail() + ")\n" +
               "participou do evento '" + evento.getNome() + "'\n" +
               "realizado em " + evento.getData() + ".\n" +
               "------------------------------------";
    }

    // --- Métodos Auxiliares ---
    private List<Participante> getParticipantesDoEvento(int eventoId) {
        List<Participante> participantes = new ArrayList<>();
        String sql = "SELECT p.* FROM participantes p JOIN evento_participante ep ON p.id = ep.participante_id WHERE ep.evento_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    participantes.add(new Participante(rs.getInt("id"), rs.getString("nome"), rs.getString("email")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return participantes;
    }

    private Evento buildEventoFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        Evento evento = new Evento(
            id,
            rs.getString("nome"),
            rs.getString("descricao"),
            LocalDate.parse(rs.getString("data"), formatter),
            rs.getString("local"),
            rs.getInt("capacidade")
        );
        evento.setPalestrantes(getPalestrantesDoEvento(id));
        evento.setParticipantesInscritos(getParticipantesDoEvento(id));
        return evento;
    }

    private List<Evento> findEventos(String sql, Object parameter) {
        List<Evento> eventos = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (parameter != null) {
                if (parameter instanceof String) {
                    pstmt.setString(1, (String) parameter);
                } else if (parameter instanceof Integer) {
                    pstmt.setInt(1, (Integer) parameter);
                }
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    eventos.add(buildEventoFromResultSet(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return eventos;
    }
}