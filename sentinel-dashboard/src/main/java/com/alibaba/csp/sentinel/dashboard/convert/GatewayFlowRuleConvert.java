package com.alibaba.csp.sentinel.dashboard.convert;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class GatewayFlowRuleConvert implements Converter<String, List<GatewayFlowRule>> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<GatewayFlowRule> convert(String source) {
        try {
            return objectMapper.readValue(source, new TypeReference<List<GatewayFlowRule>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
