package com.github.macdao.moscow.spring;

import org.junit.Test;

public class MyApiTest extends ApiTestBase {
    @Test
    public void request_param_should_response_text_bar4() throws Exception {
        assertContract();
    }

    @Test
    public void get_property_should_response_property() throws Exception {
        assertContract();
    }
}
