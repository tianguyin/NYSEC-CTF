import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class sort{
    static class Info{
    public String comfireHash;
    public String username;
    int score;
    public Info(String comfire_Hash, String username,int score) {
        this.comfireHash = comfire_Hash;
        this.username = username;
        this.score = score;
    }
    }

    public static List<Info> getInfo() throws SQLException, ClassNotFoundException {
        List<Info> infoList = new ArrayList<>();
        Connection connection = dbUserUse.dbConnect();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM score");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Info info = new Info(resultSet.getString("hash"),resultSet.getString("user_name"),resultSet.getInt("score"));
            infoList.add(info);
        }
        return infoList;
    }
}
