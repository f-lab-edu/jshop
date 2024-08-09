package jshop.web.logging;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LoggingServletOutputStream extends ServletOutputStream {

    private final ByteArrayOutputStream byteArrayOutputStream;
    private final ServletOutputStream original;

    public LoggingServletOutputStream(ServletOutputStream original) {
        this.original = original;
        this.byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @Override
    public boolean isReady() {
        return original.isReady();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        original.setWriteListener(writeListener);
    }

    @Override
    public void write(int b) throws IOException {
        byteArrayOutputStream.write(b);
        original.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        byteArrayOutputStream.write(b, off, len);
        original.write(b, off, len);
    }

    public byte[] getBytes() {
        return byteArrayOutputStream.toByteArray();
    }
}
