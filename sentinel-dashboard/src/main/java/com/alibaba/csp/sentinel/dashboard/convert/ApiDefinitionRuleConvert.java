package com.alibaba.csp.sentinel.dashboard.convert;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

/**
 *
 * @author xiongying
 * @since 1.8.4
 */
public class ApiDefinitionRuleConvert implements Converter<String, List<ApiDefinition>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<ApiDefinition> convert(String source) {
        List<ApiDefinition> apiDefinitions = new ArrayList<>();
        try {
            List<HashMap> sourceArray = objectMapper.readValue(source, new TypeReference<List<HashMap>>() {
            });
            for (HashMap map : sourceArray) {
                Set<ApiPredicateItem> predicateItems = new HashSet<>();
                String apiName = (String) map.get("apiName");
                List<LinkedHashMap> predicateItemsMaps = (List<LinkedHashMap>) map.get("predicateItems");
                for (LinkedHashMap predicateItemsMap : predicateItemsMaps) {
                    String pattern = (String) predicateItemsMap.get("pattern");
                    Integer matchStrategy = (Integer) predicateItemsMap.get("matchStrategy");

                    ApiPathPredicateItem apiPathPredicateItem = new ApiPathPredicateItem();
                    apiPathPredicateItem.setPattern(pattern);
                    if (matchStrategy != null) {
                        apiPathPredicateItem.setMatchStrategy(matchStrategy);
                    }
                    predicateItems.add(apiPathPredicateItem);
                }
                ApiDefinition apiDefinition = new ApiDefinition();
                apiDefinition.setApiName(apiName);
                apiDefinition.setPredicateItems(predicateItems);
                apiDefinitions.add(apiDefinition);
            }
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException("convert error: " + e.getMessage(), e);
            }
        }
        return apiDefinitions;
    }
}
