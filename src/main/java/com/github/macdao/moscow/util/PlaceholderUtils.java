package com.github.macdao.moscow.util;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderUtils {
    private static final Pattern PATTERN = Pattern.compile("\\$\\{(.+?)}");

    private PlaceholderUtils() {
    }

    public static List<String> parse(String input) {
        final Matcher matcher = PATTERN.matcher(input);

        final ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();
        while (matcher.find()) {
            builder.add(matcher.group(1));
        }

        return builder.build();
    }
}
