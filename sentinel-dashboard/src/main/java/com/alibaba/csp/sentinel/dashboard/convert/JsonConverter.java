package com.alibaba.csp.sentinel.dashboard.convert;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author xiongying
 * @since 1.8.4
 */
public class JsonConverter<T> extends SentinelConverter {

    public JsonConverter(ObjectMapper objectMapper, Class<T> ruleClass) {
        super(objectMapper, ruleClass);
    }

}
