package com.alibaba.csp.sentinel.dashboard.convert;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 *
 * @author xiongying
 * @since 1.8.4
 */
public abstract class SentinelConverter<T extends Object>
        implements Converter<String, Collection<Object>> {

    private static final Logger log = LoggerFactory.getLogger(SentinelConverter.class);

    private final ObjectMapper objectMapper;

    private final Class<T> ruleClass;

    public SentinelConverter(ObjectMapper objectMapper, Class<T> ruleClass) {
        this.objectMapper = objectMapper;
        this.ruleClass = ruleClass;
    }

    @Override
    public Collection<Object> convert(String source) {
        Collection<Object> ruleCollection;

        // hard code
        if (ruleClass == FlowRule.class || ruleClass == DegradeRule.class
                || ruleClass == SystemRule.class || ruleClass == AuthorityRule.class
                || ruleClass == ParamFlowRule.class) {
            ruleCollection = new ArrayList<>();
        }
        else {
            ruleCollection = new HashSet<>();
        }

        if (StringUtils.isEmpty(source)) {
            log.warn("converter can not convert rules because source is empty");
            return ruleCollection;
        }
        try {
            List sourceArray = objectMapper.readValue(source,
                    new TypeReference<List<HashMap>>() {
                    });

            for (Object obj : sourceArray) {
                String item = null;
                try {
                    item = objectMapper.writeValueAsString(obj);
                    Optional.ofNullable(convertRule(item))
                            .ifPresent(convertRule -> ruleCollection.add(convertRule));
                }
                catch (IOException e) {
                    log.error("sentinel rule convert error: " + e.getMessage(), e);
                    throw new IllegalArgumentException(
                            "sentinel rule convert error: " + e.getMessage(), e);
                }
            }
        }
        catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            else {
                throw new RuntimeException("convert error: " + e.getMessage(), e);
            }
        }
        return ruleCollection;
    }

    private Object convertRule(String ruleStr) throws IOException {
        return objectMapper.readValue(ruleStr, ruleClass);
    }

}
