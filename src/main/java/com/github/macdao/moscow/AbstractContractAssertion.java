package com.github.macdao.moscow;

import com.github.macdao.moscow.http.RestResponse;
import com.github.macdao.moscow.model.Contract;
import com.github.macdao.moscow.model.ContractResponse;

public abstract class AbstractContractAssertion {
    public abstract void assertContract(RestResponse responseEntity, Contract contract);
    public abstract void assertStatusCode(RestResponse responseEntity, ContractResponse contractResponse);
    public abstract void assertHeaders(RestResponse responseEntity, ContractResponse contractResponse);
    public abstract void assertBody(RestResponse responseEntity, Contract contract);
}
