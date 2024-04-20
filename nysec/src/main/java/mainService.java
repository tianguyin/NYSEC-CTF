import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

public class mainService {
    public static void main(String[] args) throws IOException {
        // 创建 HTTP 服务器实例并监听端口 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        // 创建处理器，并将其与根路径关联
        server.createContext("/", new MyHandler());
        // 设置服务器的执行器为默认值
        server.setExecutor(null);
        // 启动服务器
        server.start();
        // 打印服务器启动信息
        System.out.println("Server is running on http://localhost:8080");
    }

    // 自定义处理器类实现 HttpHandler 接口
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {// 获取请求方法和路径
                String requestMethod = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                // 获取响应体输出流和响应头部
                OutputStream responseBody = exchange.getResponseBody();
                Headers responseHeaders = exchange.getResponseHeaders();
                handleGetBackground(exchange, requestMethod, path);
                handleGetcss(exchange, requestMethod, path);
                handleGetjs(exchange, requestMethod, path);
                handleGetIndex(exchange, requestMethod, path);
                handlePOSTdata(exchange, requestMethod, path);
                handleRegister(exchange, requestMethod, path);
                handleLogin(exchange, requestMethod, path);
                handleScoreSort(exchange,requestMethod,path);
                responseBody.close();
            } catch (IOException | NoSuchAlgorithmException|SQLException | ClassNotFoundException e) {
                sendResponse(exchange, 404, "404 Not Found: Page not found");
                throw new RuntimeException(e);
            }
        }
        }
        private static void handleScoreSort(HttpExchange exchange, String requestMethod, String path) throws IOException, SQLException, ClassNotFoundException {
            if (requestMethod.equalsIgnoreCase("POST") && path.equals("/data/common/scoredata/api")) {
                List<sort.Info> scoreInfo = sort.getInfo();
                scoreInfo.sort(Comparator.comparingInt(info -> info.score));
                JSONArray jsonArray = new JSONArray();
                for (sort.Info info : scoreInfo) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("username", info.username);
                    jsonObject.put("score", info.score);
                    jsonArray.put(jsonObject);
                }
                String jsonResponse = jsonArray.toString();
                sendJSONResponse(exchange,200,jsonResponse);
            }
        }
        private static void handleRegister(HttpExchange exchange, String requestMethod, String path) throws IOException, NoSuchAlgorithmException {
            keyWord keyWord = new keyWord();
            if(requestMethod.equalsIgnoreCase("POST") && path.equals("/register/api")){
                try {
                    String response = getRequestData(exchange);
                    JSONObject jsonObject = new JSONObject(response);
                    String username = (String) jsonObject.get("username");
                    String password = (String) jsonObject.get("password");
                    String email = (String) jsonObject.get("email");
                    String introduction = (String) jsonObject.get("introduction");
                    String comfirem = email + password;
                    String comfiremHash = enCryption.hash(comfirem);
                    String[] userInfo = {username, password,email, introduction, comfiremHash};
                    dbUserUse.dbExecute(keyWord.sqlExecuteUser, userInfo);
                    String[] scoreInfo = {comfiremHash, String.valueOf(0),username};
                    score.dbScoreInsert(scoreInfo);
                } catch (IOException | NoSuchAlgorithmException | SQLException |
                         ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        private static void handleLogin(HttpExchange exchange, String requestMethod, String path) throws IOException, NoSuchAlgorithmException {
            keyWord keyWord = new keyWord();
        if(requestMethod.equalsIgnoreCase("POST") && path.equals("/login/api")){
            try {
                String response = getRequestData(exchange);
                JSONObject jsonObject = new JSONObject(response);
                String email = (String) jsonObject.get("email");
                String password = (String) jsonObject.get("password");
                String comfire = email + password;
                String comfirehash = enCryption.hash(comfire);
                String sql = "SELECT * FROM user WHERE hash = " + "'"+comfirehash+"'";
                try {
                    String[] Info = dbUserUse.dbSearch(sql);
                    String token = Info[3];
                    JSONObject sendTOKEN = new JSONObject();
                    sendTOKEN.put("isLogin", "true");
                    sendTOKEN.put("username", Info[0]);
                    sendTOKEN.put("email", Info[1]);
                    sendTOKEN.put("introduction", Info[2]);
                    sendTOKEN.put("token",token);
                    sendJSONResponse(exchange,200,sendTOKEN.toString());
                } catch (ClassNotFoundException | SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        }
        private static void sendJSONResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, jsonResponse.getBytes().length);
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(jsonResponse.getBytes());
            responseBody.close();
        }
        private static void handleUploadImage(HttpExchange exchange, String requestMethod, String path,String username) throws IOException {
                if(requestMethod.equalsIgnoreCase("POST") && path.equals("/register/upload/picture/api")){
                    try {
                        String imgData = getRequestData(exchange);
                        saveImage(username,imgData);
                        sendResponse(exchange, 200, "Image uploaded successfully");
                    } catch (Exception e) {
                        sendResponse(exchange,404,"404 NOT FOUND");
                    }
                }
        }
        private static void saveImage(String username, String imgData) {
                String uploadDir = "./userInputFile";
                File directory = new File(uploadDir);
                if(!directory.exists()){
                    directory.mkdirs();
                }
                String path = uploadDir + File.separator + username + ".jpg";
                try (OutputStream outputStream = new FileOutputStream(path)){
                    outputStream.write(imgData.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
        private static void handlePOSTdata(HttpExchange exchange, String requestMethod,String path) throws IOException {
            if (requestMethod.equalsIgnoreCase("POST") && path.equals("/start")) {
                String response = handlePostRequest(exchange);
                JSONObject responseObject = new JSONObject(response);
                System.out.println(responseObject);
            }
        }
        private static void handleGetIndex(HttpExchange exchange, String requestMethod, String path) throws IOException {
        if (requestMethod.equalsIgnoreCase("GET") && path.equals("/")) {
            sendFileResponse(exchange, "/templates/index.html");
        } else if (requestMethod.equalsIgnoreCase("GET")) {
            handleGetPage(exchange, path);
        }

    }
        private static void handleGetPage(HttpExchange exchange, String page) throws IOException {
        sendFileResponse(exchange, "/templates/" + page + ".html");
    }
        private static String handlePostRequest(HttpExchange exchange) throws IOException {
        String requestBody = getRequestData(exchange);
        sendResponse(exchange, 200, "Received POST data successfully");
        return requestBody;
    }
        private static String getRequestData(HttpExchange exchange) throws IOException {
            // 从请求体中获取数据
            InputStream inputStream = exchange.getRequestBody();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            // 将数据读取为字符串
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                requestBody.append(line);
            }
            bufferedReader.close();
            return requestBody.toString();
        }
        private static void sendFileResponse(HttpExchange exchange, String filePath) throws IOException {
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "text/html");

            try (InputStream fileStream = mainService.class.getResourceAsStream(filePath)) {
                if (fileStream != null) {
                    // 如果文件存在，返回 200 OK 和文件内容
                    exchange.sendResponseHeaders(200, 0);
                    OutputStream responseBody = exchange.getResponseBody();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileStream.read(buffer)) != -1) {
                        responseBody.write(buffer, 0, bytesRead);
                    }
                    responseBody.close();
                } else {
                    // 如果文件不存在，返回 404 Not Found
                    sendResponse(exchange, 404, "404 Not Found: HTML file not found");
                }
            }
        }
        private static void handleGetcss(HttpExchange exchange,String requestMethod,String path) throws IOException {
        if (requestMethod.equalsIgnoreCase("GET") && path.equals("/css/styles.css")) {
            cssSteal("/templates/css/styles.css",exchange);
        }
        }
        private static void handleGetjs(HttpExchange exchange,String requestMethod,String path) throws IOException {
            if (requestMethod.equalsIgnoreCase("GET")) {
                switch (path) {
                    case "/js/styles.js":
                        jsSteal("/templates/js/styles.js", exchange);
                        break;
                    case "/js/server.js":
                        jsSteal("/templates/js/server.js", exchange);
                        break;
                    case "/js/user.js":
                        jsSteal("/templates/js/user.js", exchange);
                        break;
                    case "/js/home.js":
                        jsSteal("/templates/js/home.js", exchange);
                    default:
                        break;
                }
            }
        }
        private static void sendResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
            exchange.sendResponseHeaders(statusCode, responseText.getBytes().length);
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(responseText.getBytes());
            responseBody.close();
        }
        private static void handleGetBackground(HttpExchange exchange,String requestMethod,String path) throws IOException {
        if (requestMethod.equalsIgnoreCase("GET")&path.equals("/picture/background"))
        {
         imgSteal("./picture/background.jpg",exchange);
        }
    }
        private static void imgSteal(String imgPath,HttpExchange exchange) throws IOException{
            BufferedImage image = ImageIO.read(new File(imgPath));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            // 设置响应头部
            exchange.getResponseHeaders().set("Content-Type", "image/jpeg");
            // 发送响应
            exchange.sendResponseHeaders(200, imageBytes.length);
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(imageBytes);
        }
        private static void cssSteal(String path,HttpExchange exchange) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/css");
        InputStream css = mainService.class.getResourceAsStream(path);
            // 如果文件存在，返回 200 OK 和文件内容
            exchange.sendResponseHeaders(200, 0);
            OutputStream responseBody = exchange.getResponseBody();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = css.read(buffer)) != -1) {
                responseBody.write(buffer, 0, bytesRead);
            }
            responseBody.close();
    }
        private static void jsSteal(String path,HttpExchange exchange) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Content-Type", "text/js");
        InputStream css = mainService.class.getResourceAsStream(path);
        // 如果文件存在，返回 200 OK 和文件内容
        exchange.sendResponseHeaders(200, 0);
        OutputStream responseBody = exchange.getResponseBody();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = css.read(buffer)) != -1) {
            responseBody.write(buffer, 0, bytesRead);
        }
        responseBody.close();
    }
}

class dbUserUse {

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
class enCryption {
    public static String hash(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }
    public static StringBuilder salt(String token, String salt) throws NoSuchAlgorithmException {
        int numParts = salt.length();
        int tokenLength = token.length();
        String[] parts = new String[numParts];
        int spliteParts = tokenLength / numParts;
        StringBuilder entoken = new StringBuilder();
        for (int i = 0; i < numParts; i++) {
            int startIndex = i * spliteParts;
            int endIndex = (i == numParts - 1) ? token.length() : (i + 1) * spliteParts;
            parts[i] = token.substring(startIndex, endIndex);
        }
        for (int i = 0; i < parts.length; i++) {
            entoken.append(parts[i]).append(salt);

        }
        // 打印切分后的部分
        return entoken;
    }
    public static StringBuilder unsalt(String token,String salt) throws NoSuchAlgorithmException {
        int numParts = salt.length();
        int tokenLength = token.length();
        String[] parts = new String[numParts];
        int spliteParts = tokenLength / numParts;
        StringBuilder untoken = new StringBuilder();
        for (int i = 0; i < numParts; i++) {
            int startIndex = i * spliteParts;
            int endIndex = (i == numParts - 1) ? token.length() : (i + 1) * spliteParts;
            parts[i] = token.substring(startIndex, endIndex);
        }
        for (int i = 0; i < parts.length; i++) {
            untoken.append(parts[i]);
            untoken.delete(untoken.length()-numParts, untoken.length());
        }

        return untoken;
    }
    public static String token(String[] Info) {
        StringBuilder tokenBuilder = new StringBuilder();
        for (int i = 0; i < Info.length; i++) {
            String part = Base64.getEncoder().encodeToString(Info[i].getBytes(StandardCharsets.UTF_8));
            tokenBuilder.append(part);
            if (i < Info.length - 1) {
                tokenBuilder.append(",");
            }
        }
        return tokenBuilder.toString();
    }
}
class keyWord {
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
class score{
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
class sort{
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
