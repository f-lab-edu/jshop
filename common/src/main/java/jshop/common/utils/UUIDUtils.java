package jshop.common.utils;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public class UUIDUtils {

    public static String generateB64UUID() {
        UUID uuid = UUID.randomUUID();
        return uuidToB64UUID(uuid);
    }

    public static String generateB64UUID(UUID uuid) {
        return uuidToB64UUID(uuid);
    }

    private static String uuidToB64UUID(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        byte[] uuidBytes = byteBuffer.array();

        String b64UUID = Base64.getUrlEncoder().withoutPadding().encodeToString(uuidBytes);
        return b64UUID;
    }
}
