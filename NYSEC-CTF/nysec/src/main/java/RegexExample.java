import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexExample {
    public static boolean regexExample(String path) {
        String regex = "^/([^/]+)/avatar$"; // 匹配以 /email_address/avatar 结尾的字符串
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 创建 Matcher 对象
        Matcher matcher = pattern.matcher(path);

        // 执行匹配操作
        // 返回匹配失败
        return matcher.matches();
    }
}
