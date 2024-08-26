package jshop.web.logging;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LoggingHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final ServletInputStream original;
    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    public LoggingHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.original = request.getInputStream();

        int data;
        while((data = original.read()) != -1) {
            byteArrayOutputStream.write(data);
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ByteServletInputStream(byteArrayOutputStream);
    }

    static class ByteServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;
        public ByteServletInputStream(ByteArrayOutputStream byteArrayOutputStream) {
            inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }
}
