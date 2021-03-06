package com.github.macdao.moscow;

import com.github.macdao.moscow.json.JsonConverter;
import com.github.macdao.moscow.json.JsonConverterFactory;
import com.github.macdao.moscow.model.Contract;
import com.github.macdao.moscow.model.GlobalSetting;

import java.nio.file.Path;
import java.util.List;

public class GlobalSettingsContainer extends AbstractContractContainer {
    private final JsonConverter jsonConverter = JsonConverterFactory.getJsonConverter();

    public GlobalSettingsContainer(Path base, Path file) {
        final List<GlobalSetting> globalSettings = jsonConverter.deserializeGlobalSettings(base.resolve(file));
        for (GlobalSetting globalSetting : globalSettings) {
            final Path contractBase = contractBase(base, globalSetting);
            final List<Contract> contracts = loadContractsFromFile(contractBase, contractBase.resolve(globalSetting.getInclude()));
            addContext(globalSetting, contracts);
        }
    }

    private Path contractBase(Path base, GlobalSetting globalSetting) {
        if (globalSetting.getFileRoot() != null) {
            return base.resolve(globalSetting.getFileRoot());
        }
        return base;
    }

    private void addContext(GlobalSetting globalSetting, List<Contract> contracts) {
        if (globalSetting.getContext() != null) {
            for (Contract contract : contracts) {
                contract.getRequest().setUri(globalSetting.getContext() + contract.getRequest().getUri());
            }
        }
    }
}
