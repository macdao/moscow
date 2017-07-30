package com.github.macdao.moscow;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Part of this mapping code has been kindly borrowed from org.springframework:spring-core
 */
public class AntPathStringMatcher {

    private static final String DEFAULT_VARIABLE_PATTERN = "(.*)";

    private final Pattern pattern;

    private final List<String> variableNames = new LinkedList<>();

    public AntPathStringMatcher(String pattern, String globPattern) {
        final StringBuilder patternBuilder = new StringBuilder();
        final Matcher matcher = Pattern.compile(globPattern).matcher(pattern);
        int end = 0;
        while (matcher.find()) {
            patternBuilder.append(quote(pattern, end, matcher.start()));
            patternBuilder.append(DEFAULT_VARIABLE_PATTERN);
            this.variableNames.add(matcher.group(1));
            end = matcher.end();
        }
        patternBuilder.append(quote(pattern, end, pattern.length()));
        this.pattern = Pattern.compile(patternBuilder.toString());
    }

    public boolean matchStrings(String str, Map<String, String> variables) {
        final Matcher matcher = this.pattern.matcher(str);

        if (!matcher.matches()) {
            return false;
        }

        for (int i = 1; i <= matcher.groupCount(); i++) {
            final String name = this.variableNames.get(i - 1);
            final String value = matcher.group(i);
            variables.put(name, value);
        }
        return true;
    }

    private String quote(String s, int start, int end) {
        if (start == end) {
            return "";
        }
        return Pattern.quote(s.substring(start, end));
    }
}
