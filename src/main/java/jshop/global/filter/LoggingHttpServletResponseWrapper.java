package jshop.global.filter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

public class LoggingHttpServletResponseWrapper extends HttpServletResponseWrapper {

    private final LoggingServletOutputStream loggingServletOutputStream;
    private final ServletOutputStream original;

    public LoggingHttpServletResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        this.original = response.getOutputStream();
        this.loggingServletOutputStream = new LoggingServletOutputStream(original);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return loggingServletOutputStream;
    }

    public byte[] getResponseData() {
        return loggingServletOutputStream.getBytes();
    }
}
