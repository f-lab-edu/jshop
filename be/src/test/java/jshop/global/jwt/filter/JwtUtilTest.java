package jshop.global.jwt.filter;

import static org.assertj.core.api.Assertions.assertThat;

import jshop.global.jwt.filter.JwtUtil;
import org.junit.jupiter.api.Test;

public class JwtUtilTest {

    @Test
    public void 토큰생성() {
        // given
        JwtUtil jwtUtil = new JwtUtil(
            "KeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecret");
        Long id = 1L;
        String email = "biz.jh.kim@gmail.com";
        String role = "USER";

        // when
        String token = jwtUtil.createJwt(id, email, role, 60 * 60 * 1000L);

        // then
        assertThat(jwtUtil.getId(token)).isEqualTo(id);
        assertThat(jwtUtil.getEmail(token)).isEqualTo(email);
        assertThat(jwtUtil.getRole(token)).isEqualTo(role);
    }

    @Test
    public void 토큰만료() throws Exception {
        // given
        JwtUtil jwtUtil = new JwtUtil(
            "KeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecret");
        Long id = 1L;
        String email = "biz.jh.kim@gmail.com";
        String role = "USER";

        // when
        String token1 = jwtUtil.createJwt(id, email, role, 60 * 60 * 1000L);
        String token2 = jwtUtil.createJwt(id, email, role, 0L);

        // then
        assertThat(jwtUtil.isExpired(token1)).isFalse();
        assertThat(jwtUtil.isExpired(token2)).isTrue();
    }

}