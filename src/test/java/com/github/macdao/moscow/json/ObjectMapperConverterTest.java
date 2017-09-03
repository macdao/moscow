package com.github.macdao.moscow.json;

import com.github.macdao.moscow.model.GlobalSetting;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectMapperConverterTest {
    @Test
    public void testDeserializeGlobalSettings() throws Exception {
        final ObjectMapperConverter objectMapperConverter = new ObjectMapperConverter();
        final List<GlobalSetting> globalSettings = objectMapperConverter.deserializeGlobalSettings(Paths.get("src/test/resources/contracts/global-settings.json"));

        assertThat(globalSettings).hasSize(1);
        final GlobalSetting globalSetting = globalSettings.get(0);
        assertThat(globalSetting.getInclude()).isEqualTo("default.json");
    }
}