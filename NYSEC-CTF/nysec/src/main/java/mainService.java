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
import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class mainService {
    private static final Logger logger = Logger.getLogger(mainService.class.getName());
    public static final Cache<String, byte[]> cache = new Cache<>(100);

    public static void main(String[] args) throws IOException {
        logger.info("Server is starting...");
        // 创建 HTTP 服务器实例并监听端口 80
        HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
        // 创建处理器，并将其与根路径关联
        server.createContext("/", new MyHandler());
        // 设置服务器的执行器为默认值
        server.setExecutor(null);
        // 启动服务器
        server.start();
        InetAddress localHost = InetAddress.getLocalHost();
        // 打印服务器启动信息
        logger.info("Server is running on http://" + localHost.getHostAddress() + ":80");

        // 启动定时任务，每隔 10 分钟清空一次缓存
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            logger.info("Clearing cache...");
            cache.clear();
        }, 10, 10, TimeUnit.MINUTES);
    }

    // 自定义处理器类实现 HttpHandler 接口
    static class MyHandler implements HttpHandler {
        private static final ExecutorService executorService = Executors.newFixedThreadPool(10); // 创建一个包含 10 个线程的线程池

        public void handle(HttpExchange exchange) {
            // 将请求分配给线程池处理
            executorService.submit(() -> {
                try {
                    // 获取请求方法和路径
                    String requestMethod = exchange.getRequestMethod();
                    String path = exchange.getRequestURI().getPath();
                    // 获取响应体输出流和响应头部
                    OutputStream responseBody = exchange.getResponseBody();
                    Headers responseHeaders = exchange.getResponseHeaders();

                    handleGetAvatar(exchange, requestMethod, path);
                    avatarSave(exchange, requestMethod, path);
                    handleGetBackground(exchange, requestMethod, path);
                    handleGetcss(exchange, requestMethod, path);
                    handleGetjs(exchange, requestMethod, path);
                    handleGetIndex(exchange, requestMethod, path);
                    handlePOSTdata(exchange, requestMethod, path);
                    handleRegister(exchange, requestMethod, path);
                    handleLogin(exchange, requestMethod, path);
                    handleScoreSort(exchange, requestMethod, path);
                    handleWeb(exchange, requestMethod, path);

                    responseBody.close();
                } catch (IOException | SQLException | ClassNotFoundException | NoSuchAlgorithmException e) {
                    logger.severe("Exception occurred: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            });
        }
        }
        private static void handleGetAvatar(HttpExchange exchange, String requestMethod, String path) throws IOException {
        boolean isValidPath = RegexExample.regexExample(path);
        if (isValidPath) {
            if (requestMethod.equalsIgnoreCase("GET")) {
                String imagePath = "nysec/src/main/resources/templates/userInputFile" + path + ".jpg";
                logger.info("Handling GET request for image: " + imagePath);

                // 检查目录是否存在，如果不存在则创建
                Path directoryPath = Paths.get(imagePath).getParent();
                if (!Files.exists(directoryPath)) {
                    Files.createDirectories(directoryPath);
                    logger.info("Created directory: " + directoryPath.toString());
                }

                imgSteal(imagePath, exchange);
            }
            else {
                logger.warning("Invalid request method for avatar retrieval: " + requestMethod);
            }
        } else {
            logger.warning("Invalid path format for avatar: " + path);
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
                } catch (IOException | NoSuchAlgorithmException | SQLException | ClassNotFoundException e) {
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
                   imgSteal("templates/picture/background.jpg",exchange);
                   break;
           }

        }
    }
        private static void imgSteal(String imgPath, HttpExchange exchange) throws IOException {
            Path filePath = Paths.get(imgPath);
            long lastModified = Files.getLastModifiedTime(filePath).toMillis();

            byte[] imageBytes = mainService.cache.get(imgPath);
            Cache.CacheItem<byte[]> cachedItem = mainService.cache.cacheMap.get(imgPath);

            if (imageBytes == null || (cachedItem != null && cachedItem.getLastModified() < lastModified)) {
                logger.info("Cache miss or file updated for image: " + imgPath);
                // 使用 getResourceAsStream 获取资源文件流


                try (InputStream _ = Files.newInputStream(filePath)) {

                    try (InputStream imageStream = Files.newInputStream(filePath)) {
                    BufferedImage image = ImageIO.read(imageStream);
                    String fileExtension = getFileExtension(imgPath);

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ImageIO.write(image, fileExtension, outputStream);
                    imageBytes = outputStream.toByteArray();

                    // 将图像字节数组存入缓存，设置过期时间为 5 分钟
                    mainService.cache.put(imgPath, imageBytes, 5 * 1000, lastModified);
                } catch (IOException e) {
                    throw new IOException("Error reading image: " + imgPath, e);
                }
            } finally {
                logger.info("Cache hit for image: " + imgPath);
            }

            String fileExtension = getFileExtension(imgPath);
            String contentType = "image/" + fileExtension;
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, imageBytes.length);
            try (OutputStream responseBody = exchange.getResponseBody()) {
                responseBody.write(imageBytes);
            }
        }}

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
        private static void avatarSave(HttpExchange exchange,String requestMethod,String path) throws IOException {
        boolean pass = RegexExample.regexApi(path);
        try {
            if (pass) {
                path = path.substring(0, path.length() - 4);
                if (requestMethod.equalsIgnoreCase("POST")) {
                    InputStreamReader inputStream = new InputStreamReader(exchange.getRequestBody());
                    BufferedReader bufferedReader = new BufferedReader(inputStream);
                    String body = bufferedReader.readLine();
                    JSONObject jsonObject = new JSONObject(body);
                    String avatar = jsonObject.getString("image");
                    byte[] imageBytes = Base64.getDecoder().decode(avatar.split(",")[1]);
                    String Dir = "nysec/src/main/resources/templates/userInputFile" + path + ".jpg";
//                    String Dir = "nysec/src/main/resources/templates/picture/avatar.jpg";
                    Path imgPath = Paths.get(Dir);
                    Files.write(imgPath, imageBytes);
                    exchange.sendResponseHeaders(200, 0);
                    OutputStream responseBody = exchange.getResponseBody();
                    logger.severe("Image uploaded successfully");
                    responseBody.close();
                } else {
                    exchange.sendResponseHeaders(405, 0);
                    OutputStream responseBody = exchange.getResponseBody();
                    responseBody.write("Method Not Allowed".getBytes());
                    responseBody.close();
                }
            }
        }
        catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }
}

