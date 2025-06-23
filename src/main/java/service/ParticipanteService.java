package service;

import dao.DatabaseManager;
import model.Participante;

import java.sql.*;
import java.util.Optional;

public class ParticipanteService {

    public Participante cadastrar(String nome, String email) {
        if (buscarPorEmail(email).isPresent()) {
            return null;
        }
        String sql = "INSERT INTO participantes(nome, email) VALUES(?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return new Participante(rs.getInt(1), nome, email);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Optional<Participante> buscarPorEmail(String email) {
        String sql = "SELECT * FROM participantes WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Participante(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}