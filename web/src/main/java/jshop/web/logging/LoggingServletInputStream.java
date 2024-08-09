package jshop.web.logging;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LoggingServletInputStream extends ServletInputStream {

    private final ByteArrayOutputStream byteArrayOutputStream;
    private final ServletInputStream original;

    public LoggingServletInputStream(ServletInputStream original) {
        this.original = original;
        this.byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @Override
    public boolean isFinished() {
        return original.isFinished();
    }

    @Override
    public boolean isReady() {
        return original.isReady();
    }

    @Override
    public void setReadListener(ReadListener listener) {
        original.setReadListener(listener);
    }

    @Override
    public int read() throws IOException {
        int data = original.read();
        if (data != -1) {
            byteArrayOutputStream.write(data);
        }

        return data;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int bytesRead = original.read(b, off, len);
        if (bytesRead > 0) {
            byteArrayOutputStream.write(b, off, bytesRead);
        }
        return bytesRead;
    }

    public byte[] getBytes() {
        return byteArrayOutputStream.toByteArray();
    }
}
