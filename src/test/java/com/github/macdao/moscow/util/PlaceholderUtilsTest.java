package com.github.macdao.moscow.util;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PlaceholderUtilsTest {
    @Test
    public void parse_should_return_result() throws Exception {
        final List<String> result = PlaceholderUtils.parse("${hello},${world}");

        assertThat(result.size(), is(2));
        assertThat(result.get(0), is("hello"));
        assertThat(result.get(1), is("world"));
    }
}