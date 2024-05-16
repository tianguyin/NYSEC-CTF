public class keyWord {
    public String uuid = "uuid";

    public String name = "user_name";

    public String passwd = "user_passwd";

    public String salt = "salt";

    public String email = "user_email";

    public String sqlSearchAllUser = "SELECT * FROM user ";

    public String sqlUpdate = "UPDATE user SET ";

    public String sqlExecuteUser = "INSERT INTO user (user_name,user_passwd,user_email,user_introduction,hash) VALUES (?,?,?,?,?)";

    public String sqlWhere = "WHERE hash = ";
}
