import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

public class mainService {
    public static void main(String[] args) throws IOException {
        // 创建 HTTP 服务器实例并监听端口 80
        HttpServer server = HttpServer.create(new InetSocketAddress(81), 0);
        // 创建处理器，并将其与根路径关联
        server.createContext("/", new MyHandler());
        // 设置服务器的执行器为默认值
        server.setExecutor(null);
        // 启动服务器
        server.start();
        InetAddress localHost = InetAddress.getLocalHost();
        // 打印服务器启动信息
        System.out.println("Server is running on http://" + localHost.getHostAddress()+":81");
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
                handleGetAvatar(exchange,requestMethod,path);
                handleGetBackground(exchange, requestMethod, path);
                handleGetcss(exchange, requestMethod, path);
                handleGetjs(exchange, requestMethod, path);
                handleGetIndex(exchange, requestMethod, path);
                handlePOSTdata(exchange, requestMethod, path);
                handleRegister(exchange, requestMethod, path);
                handleLogin(exchange, requestMethod, path);
                handleScoreSort(exchange,requestMethod,path);
                handleWeb(exchange, requestMethod, path);
                responseBody.close();
            } catch (IOException | SQLException | ClassNotFoundException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        }
        private static void handleGetAvatar(HttpExchange exchange, String requestMethod, String path) throws IOException {
        boolean TRY = RegexExample.regexExample(path);
        if (TRY) {
            if (requestMethod.equalsIgnoreCase("GET")) {
                String Dir = "src/main/resources/templates/userInputFile" + path + ".jpg";
                imgSteal(Dir, exchange);
            }
        }
        }
        private static void handleWeb(HttpExchange exchange, String requestMethod, String path) throws IOException, SQLException, ClassNotFoundException {
           if (requestMethod.equalsIgnoreCase("POST") && path.equals("/web/api")) {
            String response = getRequestData(exchange);
            JSONObject responseObject = new JSONObject(response);
            String email = responseObject.getString("email");
            List<web.Info> webInfo = web.getInfo(email);
            JSONArray jsonArray = new JSONArray();
            for (web.Info info : webInfo) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("web",info.web);
                jsonObject.put("id",info.id);
                jsonArray.put(jsonObject);
            }
            String jsonResponse = jsonArray.toString();
            sendJSONResponse(exchange, jsonResponse);
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
                sendJSONResponse(exchange, jsonResponse);
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
                    sendJSONResponse(exchange, sendTOKEN.toString());
                } catch (ClassNotFoundException | SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        }
        private static void sendJSONResponse(HttpExchange exchange, String jsonResponse) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(jsonResponse.getBytes());
            responseBody.close();
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
            return getRequestData(exchange);
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
                        break;
                    case "/js/challenges.js":
                        jsSteal("/templates/js/challenges.js", exchange);
                        break;
                    case "/js/web.js":
                        jsSteal("/templates/js/web.js", exchange);
                        break;
                    case "/js/personHome.js":
                        jsSteal("/templates/js/personHome.js", exchange);
                        break;
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
        if (requestMethod.equalsIgnoreCase("GET"))
        {
           switch (path) {
               case "/picture/background":
                   imgSteal("src/main/resources/templates/picture/background.jpg",exchange);
                   break;
           }

        }
    }
        private static void imgSteal(String imgPath, HttpExchange exchange) throws IOException {
            // 读取图片
            BufferedImage image = ImageIO.read(new File(imgPath));

            // 获取图片文件的后缀名（扩展名）
            String fileExtension = getFileExtension(imgPath);

            // 将图片写入 ByteArrayOutputStream，保持原始格式
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, fileExtension, outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            // 设置响应头部为原始图片格式
            String contentType = "image/" + fileExtension; // 直接使用文件扩展名确定 Content-Type
            exchange.getResponseHeaders().set("Content-Type", contentType);

            // 发送响应
            exchange.sendResponseHeaders(200, imageBytes.length);
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(imageBytes);
        }
        // 获取文件扩展名
        private static String getFileExtension(String filePath) {
            Path path = Paths.get(filePath);
            String fileName = path.getFileName().toString();
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                return fileName.substring(dotIndex + 1).toLowerCase();
            }
            return ""; // 如果找不到扩展名，则返回空字符串
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

