/*
package MonsterCard.mtcg.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import MonsterCard.database.DatabaseService;
import MonsterCard.mtcg.User;

public class UserManager {

    private static UserManager single_instance = null;

    public static UserManager getInstance() {
        if (single_instance == null) {
            single_instance = new UserManager();
        }
        return single_instance;
    }

    public User authorizeUser(String token) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseService.getInstance().getConnection();
            ps = conn.prepareStatement("SELECT username, name, bio, image, coins, games, wins, elo FROM users WHERE token = ? AND logged = TRUE;");
            ps.setString(1, token);
            rs = ps.executeQuery();
    
            if (!rs.next()) {
                return null;
            }
            
            User user = new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8));
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean isAdmin(String token) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(username) FROM users WHERE token = ? AND admin = TRUE AND logged = TRUE;");
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            ps.close();
            if (!rs.next() || rs.getInt(1) != 1) {
                rs.close();
                conn.close();
                return false;
            }
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerUser(String username, String pwd) {
    String token = "Basic " + username + "-mtcgToken";
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
        conn = DatabaseService.getInstance().getConnection();
        ps = conn.prepareStatement("SELECT COUNT(username) FROM users WHERE username = ?;");
        ps.setString(1, username);
        rs = ps.executeQuery();

        if (!rs.next() || rs.getInt(1) > 0) {
            return false;
        }

        if (username.equals("admin")) {
            ps.close(); // Close the previous PreparedStatement
            ps = conn.prepareStatement("INSERT INTO users(username, pwd, token, admin) VALUES(?,?,?,TRUE);");
        } else {
            ps.close(); // Close the previous PreparedStatement
            ps = conn.prepareStatement("INSERT INTO users(username, pwd, token) VALUES(?,?,?);");
        }
        
        ps.setString(1, username);
        ps.setString(2, pwd);
        ps.setString(3, token);
        int affectedRows = ps.executeUpdate();
        return affectedRows != 0;
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    return false;
}

    public boolean loginUser(String username, String pwd) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET logged = TRUE WHERE username = ? AND pwd = ?;");
            ps.setString(1, username);
            ps.setString(2, pwd);
            int affectedRows = ps.executeUpdate();
            ps.close();
            conn.close();
            if (affectedRows == 1) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean logoutUser(String username, String pwd) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET logged = FALSE WHERE username = ? AND pwd = ?;");
            ps.setString(1, username);
            ps.setString(2, pwd);
            int affectedRows = ps.executeUpdate();
            ps.close();
            conn.close();
            if (affectedRows == 1) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}

*/


package MonsterCardGame.mtcg.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import MonsterCardGame.database.DatabaseService;
import MonsterCardGame.mtcg.User;

public class UserManager {

    private static UserManager single_instance = null;

    public static UserManager getInstance() {
        if (single_instance == null) {
            single_instance = new UserManager();
        }
        return single_instance;
    }

    private Connection getConnection() throws SQLException {
        return DatabaseService.getInstance().getConnection();
    }

    public User authorizeUser(String token) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT username, name, bio, image, coins, games, wins, elo FROM users WHERE token = ? AND logged = TRUE;")) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return null;
            }

            return new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isAdmin(String token) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(username) FROM users WHERE token = ? AND admin = TRUE AND logged = TRUE;")) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (!rs.next() || rs.getInt(1) != 1) {
                return false;
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerUser(String username, String pwd) {
        String token = "Basic " + username + "-mtcgToken";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(username) FROM users WHERE username = ?;")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next() || rs.getInt(1) > 0) {
                return false;
            }

            String insertSql = username.equals("admin") ?
                    "INSERT INTO users(username, pwd, token, admin) VALUES(?,?,?,TRUE);" :
                    "INSERT INTO users(username, pwd, token) VALUES(?,?,?);";

            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                insertPs.setString(1, username);
                insertPs.setString(2, pwd);
                insertPs.setString(3, token);
                int affectedRows = insertPs.executeUpdate();
                return affectedRows != 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean loginUser(String username, String pwd) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE users SET logged = TRUE WHERE username = ? AND pwd = ?;")) {
            ps.setString(1, username);
            ps.setString(2, pwd);
            int affectedRows = ps.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean logoutUser(String username, String pwd) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE users SET logged = FALSE WHERE username = ? AND pwd = ?;")) {
            ps.setString(1, username);
            ps.setString(2, pwd);
            int affectedRows = ps.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
