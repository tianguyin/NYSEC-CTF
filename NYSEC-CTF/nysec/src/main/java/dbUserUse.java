import java.sql.*;

public class dbUserUse {

    public static void dbExecute(String sql,String[] userInfo) throws SQLException, ClassNotFoundException {
        Connection connection = dbConnect();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,userInfo[0]);
            preparedStatement.setString(2,userInfo[1]);
            preparedStatement.setString(3,userInfo[2]);
            preparedStatement.setString(4,userInfo[3]);
            preparedStatement.setString(5,userInfo[4]);
            preparedStatement.execute();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static Connection dbConnect() throws ClassNotFoundException, SQLException {
        Connection connection;
        String userName = "tianguyin";
        String password = "BaSFYE7VpUhPmazr";
        String dbName = "tianguyinsql";
        String host = "mysql.sqlpub.com:3306";
        String url = "jdbc:mysql://" + host + "/" + dbName;
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(url, userName, password);
        return connection;
    }
    public static String[] dbSearch(String sql) throws ClassNotFoundException, SQLException {
        Connection connection = dbConnect();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        String[] dbRow = null;
        if (resultSet.next()) {
            dbRow = new String[]{
                    resultSet.getString("user_name"),
                    resultSet.getString("user_email"),
                    resultSet.getString("user_introduction"),
                    resultSet.getString("hash")
            };
        }

        // 关闭资源
        resultSet.close();
        preparedStatement.close();
        connection.close();

        return dbRow;
    }
    public static void dbRevise(String sql, String newValue) throws SQLException, ClassNotFoundException {
        try (Connection connection = dbConnect()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newValue);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e);
        }

    }
}
