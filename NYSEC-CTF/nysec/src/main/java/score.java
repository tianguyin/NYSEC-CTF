import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class score{
    public static void dbScoreInsert(String[] scoreInfo) throws SQLException, ClassNotFoundException {
        Connection connection = dbUserUse.dbConnect();
        String sql = "INSERT INTO score (hash,score,user_name) VALUES (?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,scoreInfo[0]);
        preparedStatement.setString(2,scoreInfo[1]);
        preparedStatement.setString(3,scoreInfo[2]);
        preparedStatement.execute();
        preparedStatement.close();
        connection.close();
    }
    public static void dbScoreUpdate(int score,String keyWord) throws SQLException, ClassNotFoundException {
        Connection connection = dbUserUse.dbConnect();
        String sql = "UPDATE score SET score = ? WHERE hash = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,score);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
}
