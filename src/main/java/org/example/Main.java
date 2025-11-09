package org.example;
import java.sql.*;
public class Main {
    static void main(String[] args) {
        String url = "jdbc:sqlite:students.db";

        try (Connection conn = DriverManager.getConnection(url)) {

            if (conn == null) {
                System.out.println("Brak połączenia z bazą.");
                return;
            }


            printAllStudents(conn);
            addJavaSubject(conn);
            printStudentsWithAvgAtLeast3(conn);

        } catch (SQLException e) {

        }
    }


    private static void printAllStudents(Connection conn) throws SQLException {
        String sql = "SELECT id, first_name FROM students";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("=== Wszyscy studenci ===");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("first_name");
                System.out.println(id + " | " + name);
            }
        }
    }
    private static void addJavaSubject(Connection conn) throws SQLException {

        String checkSql = "SELECT COUNT(*) FROM courses WHERE name = ?";
        try (PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setString(1, "Java");
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Przedmiot 'Java' już istnieje w tabeli subjects.");
                    return;
                }
            }
        }

        String insertSql = "INSERT INTO courses(name) VALUES (?)";
        try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
            insert.setString(1, "Java");
            int rows = insert.executeUpdate();
            System.out.println("Dodano przedmiot 'Java' (wierszy: " + rows + ").");
        }
    }
    private static void printStudentsWithAvgAtLeast3(Connection conn) throws SQLException {
        String sql =
                "SELECT s.id, s.first_name, AVG(g.grade) AS avg_grade " +
                        "FROM students s " +
                        "JOIN grades g ON g.student_id = s.id " +
                        "GROUP BY s.id, s.first_name " +
                        "HAVING AVG(g.grade) >= 3";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
                System.out.println("=== Studenci ze średnią ocen >= 3 ===");
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("first_name");
                    double avg = rs.getDouble("avg_grade");
                    System.out.println(id + " | " + name + " | " + avg);
                }
            }
    }
}
