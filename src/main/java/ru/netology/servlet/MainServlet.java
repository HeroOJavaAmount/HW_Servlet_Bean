package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.exception.NotFoundException;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

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

    @Override
    public void init() {
        // Создаём зависимости: репозиторий → сервис → контроллер
        final var repository = new PostRepository();
        final var service = new PostService(repository);
        controller = new PostController(service);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var path = req.getRequestURI();   // например, "/api/posts" или "/api/posts/123"
            final var method = req.getMethod();     // GET, POST, DELETE и т.д.
            // Маршрутизация
            if (GET_METHOD.equals(method) && API_POSTS.equals(path)) {
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

    private long extractId(String path) {
        // path имеет вид "/api/posts/123"
        String idPart = path.substring(path.lastIndexOf('/') + 1);
        return Long.parseLong(idPart);
    }
}

