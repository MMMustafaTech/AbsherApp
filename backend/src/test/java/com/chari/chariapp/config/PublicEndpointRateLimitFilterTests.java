package com.chari.chariapp.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.security.rate-limit.login.max-requests=2",
        "app.security.rate-limit.window=PT1H"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PublicEndpointRateLimitFilterTests {

    @Autowired private MockMvc mockMvc;

    @Test
    void limitsRepeatedLoginAttemptsFromTheSameSourceAddress() throws Exception {
        for (int attempt = 0; attempt < 2; attempt++) {
            mockMvc.perform(loginAttempt()).andExpect(status().isUnauthorized());
        }
        mockMvc.perform(loginAttempt())
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"));
    }

    private static org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder loginAttempt() {
        return post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"unknown@example.com\",\"password\":\"a-secure-password\"}")
                .with(request -> {
                    request.setRemoteAddr("198.51.100.7");
                    return request;
                });
    }
}
