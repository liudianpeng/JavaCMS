package testBlog.controller.admin;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

public class AjaxServlet extends HttpServlet {
    public AjaxServlet() {
        System.out.println("Servlet created!");
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException,IOException {
        System.out.println(request.getMethod());
    }
}
