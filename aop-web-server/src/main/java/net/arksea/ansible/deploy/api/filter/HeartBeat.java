package net.arksea.ansible.deploy.api.filter;

import org.apache.commons.io.FileUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class HeartBeat extends HttpServlet {

    private static final long serialVersionUID = -8581373683797931205L;
    private String heartBeatSetStatusKey;

    public HeartBeat() throws IOException {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties p = new Properties();
            p.load(in);
            heartBeatSetStatusKey = p.getProperty("heartBeat.setStatusKey");
        }
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final File file = new File(getPath(request));
        String status = "ONLINE";
        if (file.exists()) {
            String str = FileUtils.readFileToString(file);
            if (str == null || str.isEmpty()) {
                status = "ONLINE";
            } else {
                status = str.trim();
            }
        }
        response.getWriter().write(status);
    }

    @Override
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final InputStream input = request.getInputStream();
        final byte[] buf = new byte[1024];
        final int len = input.read(buf);
        final String content = new String(buf, 0, len, StandardCharsets.UTF_8);
        final String[] body = content.split(";");
        final String key = body[1];
        final String status = body[0];
        if (key.equals(heartBeatSetStatusKey)) {
            FileUtils.writeStringToFile(new File(getPath(request)), status, "utf-8");
            response.getWriter().write("succeed");
        } else {
            response.getWriter().write("failed");
        }
    }

    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

    private String getPath(final HttpServletRequest request) {
        return "tomcat_" + request.getLocalPort() + "_" + request.getServletContext().getContextPath().replace("/", "");
    }
}
