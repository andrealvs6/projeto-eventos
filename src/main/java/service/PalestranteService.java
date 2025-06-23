package service;

import dao.DatabaseManager;
import model.Palestrante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PalestranteService {
    
    public Palestrante cadastrar(String nome, String curriculo, String area) {
        String sql = "INSERT INTO palestrantes(nome, curriculo, area_de_atuacao) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, curriculo);
            pstmt.setString(3, area);
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return new Palestrante(rs.getInt(1), nome, curriculo, area);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Palestrante buscarPorId(int id) {
        String sql = "SELECT * FROM palestrantes WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Palestrante(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("curriculo"),
                        rs.getString("area_de_atuacao")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Palestrante> listarTodos() {
        List<Palestrante> palestrantes = new ArrayList<>();
        String sql = "SELECT * FROM palestrantes ORDER BY nome";
        try (Connection conn = DatabaseManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                palestrantes.add(new Palestrante(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("curriculo"),
                    rs.getString("area_de_atuacao")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return palestrantes;
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM palestrantes WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Se a exclusão falhar por restrição de chave estrangeira, o SQLite lançará uma exceção.
            e.printStackTrace();
        }
        return false;
    }
}