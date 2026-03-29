package ru.netology.servlet;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.BeanConfig;
import ru.netology.controller.PostController;
import ru.netology.exception.NotFoundException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {

    // Константы для маршрутов и методов (рефакторинг)
    private static final String API_POSTS = "/api/posts";
    private static final String API_POSTS_WITH_ID = "/api/posts/\\d+";
    private static final String GET_METHOD = "GET";
    private static final String POST_METHOD = "POST";
    private static final String DELETE_METHOD = "DELETE";

    private PostController controller;
    private ApplicationContext context;

    @Override
    public void init() {
        context = new AnnotationConfigApplicationContext(BeanConfig.class);
        controller = context.getBean(PostController.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var path = req.getRequestURI();   // например, "/api/posts" или "/api/posts/123"
            final var method = req.getMethod();     // GET, POST, DELETE и т.д.

            if (GET_METHOD.equals(method) && API_POSTS.equals(path)) {// Маршрутизация
                controller.all(resp);
                return;
            }
            if (GET_METHOD.equals(method) && path.matches(API_POSTS_WITH_ID)) {
                long id = extractId(path);
                controller.getById(id, resp);
                return;
            }
            if (POST_METHOD.equals(method) && API_POSTS.equals(path)) {
                controller.save(req.getReader(), resp);
                return;
            }
            if (DELETE_METHOD.equals(method) && path.matches(API_POSTS_WITH_ID)) {
                long id = extractId(path);
                controller.removeById(id, resp);
                return;
            }
            // Если ни один маршрут не подошёл
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void destroy() {
        if (context instanceof AnnotationConfigApplicationContext) {
            ((AnnotationConfigApplicationContext) context).close();
        }
    }

    private long extractId(String path) {
        // path имеет вид "/api/posts/123"
        String idPart = path.substring(path.lastIndexOf('/') + 1);
        return Long.parseLong(idPart);
    }
}


