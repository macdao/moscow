package com.github.macdao.moscow;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AntPathStringMatcherTest {
    @Test
    public void test_match() throws Exception {
        final Map<String, String> map = new HashMap<>();
        assertThat(new AntPathStringMatcher("a{name}b", "\\{(.*)\\}").matchStrings("ahellob", map), is(true));
        assertThat(map.size(), is(1));
        assertThat(map.get("name"), is("hello"));
    }

    @Test
    public void test_match_2() throws Exception {
        final Map<String, String> map = new HashMap<>();
        assertThat(new AntPathStringMatcher("a{name:}}b", "\\{(.*)\\}").matchStrings("ahellob", map), is(true));
        assertThat(map.size(), is(1));
        assertThat(map.get("name:}"), is("hello"));
    }
}