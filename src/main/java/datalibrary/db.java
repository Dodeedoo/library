package datalibrary;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.logging.Level;

public class db {
    public static String host;
    public static String name;
    public static String user;
    public static String pass;

    public static void tell(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }
    public static void log(final String message) {
        tell(Bukkit.getConsoleSender(), "[" + datalibrary.getPlugin().getName() + "] " + message);
    }
    public static String colorize(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void setcreds(String hostc, String namec, String userc, String passc) {
        host = hostc;
        name = namec;
        user = userc;
        pass = passc;
    }
    public static Connection conn() throws SQLException { return initMySQLDataSource().getConnection(); }
    public static DataSource initMySQLDataSource() throws SQLException {
        MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
        //all the details to connect to the database
        dataSource.setServerName(host);
        dataSource.setPortNumber(3306);
        dataSource.setDatabaseName(name);
        dataSource.setUser(user);
        dataSource.setPassword(pass);
        testDataSource(dataSource);
        return dataSource;
    }

    public static void testDataSource(DataSource dataSource) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(1000)) {
                throw new SQLException("Could not establish database connection. (did you set creds?)");
            }
        }
    }


    public static ArrayList<String> setuuid(String column_name, String table_name, UUID uuid_of_player) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO " + table_name +"(" + column_name + ") VALUES(?)"
        )) {
            stmt.setString(1, uuid_of_player.toString());
            stmt.execute();
            ArrayList<String> e = new ArrayList<String>();
            e.add(table_name);
            e.add(column_name);
            return e;
        } catch (SQLException e) {
            log("&cinsert " + column_name + " in " + table_name + "has failed");
            e.printStackTrace();
        }
        return null;
    }

    public static boolean setstring(String column_name, String table_name,  UUID uuid_of_player, String inserted_stuff) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                "UPDATE " + table_name + " SET " + column_name + " = ? WHERE uuid = ?"
        )) {
            stmt.setString(1, inserted_stuff);
            stmt.setString(2, uuid_of_player.toString());
            stmt.execute();
            return true;
        } catch (SQLException ee) {
            log("&cDb update failed");
            ee.printStackTrace();
        }
        return false;
    }

    public static boolean setint(String column_name, String table_name,  UUID uuid_of_player, Integer inserted_stuff) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                "UPDATE " + table_name + " SET " + column_name + " = ? WHERE uuid = ?"
        )) {
            stmt.setInt(1, inserted_stuff);
            stmt.setString(2, uuid_of_player.toString());
            stmt.execute();
            return true;
        } catch (SQLException ee) {
            log("&cDb update failed");
            ee.printStackTrace();
        }
        return false;
    }

    public static OptionalLong getIntStatus(Player player, String column_name, String table_name) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT " + column_name + " FROM " + table_name + " WHERE uuid = ?;"
        )) {
            stmt.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return OptionalLong.of(resultSet.getInt(column_name));
            }
            return OptionalLong.of(0);
        } catch (SQLException e) {
            log("&cStatus get failed");
            return OptionalLong.empty();
        }
    }

    public static String getStringStatus(Player player, String column_name, String table_name) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT " + column_name + " FROM " + table_name + " WHERE uuid = ?;"
        )) {
            stmt.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(column_name);
            }
            return "";
        } catch (SQLException e) {
            log("&cStatus get failed");
            return null;
        }
    }

    public static boolean database(String sql) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(
                sql
        )) {
            stmt.execute();
            return true;
        } catch (SQLException e) {
            log("&cCustom Database code thingy failed");
            e.printStackTrace();
        }
        return false;
    }

}