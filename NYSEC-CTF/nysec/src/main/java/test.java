import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class test {
    public static Properties loadConfig(String filePath) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void main(String[] args) {
        String filePath = "nysec/src/main/resources/templates/info.ini"; // 配置文件路径
        Properties config = loadConfig(filePath);

        // 读取配置项
        String userName = config.getProperty("userName");
        String password = config.getProperty("password");
        String dbName = config.getProperty("dbName");
        String host = config.getProperty("host");

        // 打印配置项
        System.out.println("用户名: " + userName);
        System.out.println("密码: " + password);
        System.out.println("数据库名称: " + dbName);
        System.out.println("主机地址: " + host);
    }
}
