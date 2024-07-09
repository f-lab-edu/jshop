package jshop.global.jwt.filter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[단위 테스트] JwtUtil")
public class JwtUtilTest {

    @Test
    @DisplayName("토큰은 key, id, email, role로 생성함. 반대로 생성된 토큰에서 값을 가져올 수 있음")
    public void createToken() {
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
    @DisplayName("토큰은 시간이 만료되면 사용할 수 없음")
    public void isExpired() throws Exception {
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