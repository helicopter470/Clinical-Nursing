package com.example.security;

import com.example.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 放行预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        // 放行登录/注册/静态资源等
        return path.equals("/")
                || path.startsWith("/login")
                || path.startsWith("/register")
                || path.startsWith("/ai/")
                || path.startsWith("/files/")
                || path.startsWith("/error");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            write401(response, "未登录或缺少Token");
            return;
        }

        String token = auth.substring("Bearer ".length()).trim();
        try {
            Claims claims = jwtService.parseClaims(token);
            // 将用户信息放入 request，业务需要时可取
            request.setAttribute("uid", claims.get("uid"));
            request.setAttribute("username", claims.get("username"));
            request.setAttribute("role", claims.get("role"));
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            write401(response, "Token无效或已过期");
        }
    }

    private void write401(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Result.unauthorized(msg)));
    }
}
