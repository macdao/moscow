package com.github.macdao.moscow;

import com.github.macdao.moscow.json.JsonConverter;
import com.github.macdao.moscow.json.JsonConverterFactory;
import com.github.macdao.moscow.model.GlobalSetting;

import java.nio.file.Path;
import java.util.List;

public class GlobalSettingsContainer extends AbstractContractContainer {
    private final JsonConverter jsonConverter = JsonConverterFactory.getJsonConverter();

    public GlobalSettingsContainer(Path base, Path file) {
        final List<GlobalSetting> globalSettings = jsonConverter.deserializeGlobalSettings(base.resolve(file));
        for (GlobalSetting globalSetting : globalSettings) {
            loadContractsFromFile(base, base.resolve(globalSetting.getInclude()));
        }
    }
}
