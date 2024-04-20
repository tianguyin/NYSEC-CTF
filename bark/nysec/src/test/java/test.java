import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/test")
public class test extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null || path.equals("/")) {
            path = "/index";
        }

        String filePath = "/WEB-INF/templates" + path + ".html";
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(getServletContext().getResourceAsStream(filePath)));
             PrintWriter out = response.getWriter()) {
            if (fileReader != null) {
                response.setContentType("text/html");
                String line;
                while ((line = fileReader.readLine()) != null) {
                    out.println(line);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println("<html><body><h1>404 Not Found</h1><p>" + path + " HTML file not found</p></body></html>");
            }
        }
    }
}
