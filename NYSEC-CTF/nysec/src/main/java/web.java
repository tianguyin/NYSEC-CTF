import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class web{
    static class Info{
        public String id;
        public String web;
        int score;
        public Info(String id, String web) {
            this.id = id;
            this.web = web;
        }
    }

    public static List<Info> getInfo(String email) throws SQLException, ClassNotFoundException {
        List<Info> infoList = new ArrayList<>();
        Connection  connection = dbUserUse.dbConnect();
            // 准备并执行 SQL 查询
        String sql = "SELECT * FROM " + "`" + email +"`";
        PreparedStatement  preparedStatement = connection.prepareStatement(sql);
        ResultSet  resultSet = preparedStatement.executeQuery();

            // 处理查询结果
        while (resultSet.next()) {
                Info info = new Info(resultSet.getString("id"), resultSet.getString("web"));
                infoList.add(info);
        }

        return infoList;
    }


}
