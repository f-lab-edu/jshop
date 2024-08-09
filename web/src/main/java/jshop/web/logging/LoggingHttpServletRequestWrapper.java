package jshop.web.logging;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

public class LoggingHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final LoggingServletInputStream loggingServletInputStream;
    private final ServletInputStream original;

    public LoggingHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.original = request.getInputStream();
        this.loggingServletInputStream = new LoggingServletInputStream(original);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return loggingServletInputStream;
    }

    public byte[] getRequestData() {
        return loggingServletInputStream.getBytes();
    }
}
