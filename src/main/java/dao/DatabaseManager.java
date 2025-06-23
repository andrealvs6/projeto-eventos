package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    
    // ALTERAÇÃO 1: A URL agora é uma variável estática que pode ser alterada.
    // Ela continua com o valor padrão para a aplicação principal.
    private static String databaseUrl = "jdbc:sqlite:eventos.db";

    /**
     * ALTERAÇÃO 2: Novo método público para permitir que os testes
     * configurem um banco de dados diferente (ex: "jdbc:sqlite:eventos_test.db").
     * @param url A nova URL do banco de dados a ser usada.
     */
    public static void setDatabaseUrl(String url) {
        databaseUrl = url;
    }

    /**
     * Retorna uma conexão com o banco de dados usando a URL configurada.
     */
    public static Connection getConnection() throws SQLException {
        // ALTERAÇÃO 3: Adicionado o carregamento explícito do driver.
        // É uma boa prática para garantir que a classe do driver seja encontrada.
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC do SQLite não encontrado. Verifique se o JAR está no classpath.");
            throw new SQLException("SQLite JDBC driver not found", e);
        }
        return DriverManager.getConnection(databaseUrl);
    }

    /**
     * Cria todas as tabelas do sistema se elas ainda não existirem.
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // ALTERAÇÃO 4: Comando PRAGMA para garantir que as Chaves Estrangeiras
            // e regras como ON DELETE CASCADE sejam efetivamente aplicadas pelo SQLite.
            stmt.execute("PRAGMA foreign_keys = ON;");

            stmt.execute("CREATE TABLE IF NOT EXISTS palestrantes (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "nome TEXT NOT NULL," +
                         "curriculo TEXT," +
                         "area_de_atuacao TEXT NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS participantes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "nome TEXT NOT NULL," +
                        "email TEXT NOT NULL UNIQUE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS eventos (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "nome TEXT NOT NULL," +
                        "descricao TEXT," +
                         "data TEXT NOT NULL," + // Armazenaremos data como TEXT no formato ISO (YYYY-MM-DD)
                        "local TEXT NOT NULL," +
                        "capacidade INTEGER NOT NULL)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS evento_palestrante (" +
                        "evento_id INTEGER NOT NULL," +
                        "palestrante_id INTEGER NOT NULL," +
                        "PRIMARY KEY (evento_id, palestrante_id)," +
                        "FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE," +
                        "FOREIGN KEY (palestrante_id) REFERENCES palestrantes(id) ON DELETE CASCADE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS evento_participante (" +
                        "evento_id INTEGER NOT NULL," +
                        "participante_id INTEGER NOT NULL," +
                        "PRIMARY KEY (evento_id, participante_id)," +
                        "FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE," +
                        "FOREIGN KEY (participante_id) REFERENCES participantes(id) ON DELETE CASCADE)");

        } catch (SQLException e) {
            System.err.println("Erro crítico ao inicializar o banco de dados: " + e.getMessage());
            throw new RuntimeException(e); // Lança uma exceção para parar a aplicação se o DB falhar
        }
    }
}