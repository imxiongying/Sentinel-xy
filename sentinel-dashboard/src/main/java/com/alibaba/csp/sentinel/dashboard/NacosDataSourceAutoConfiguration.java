package com.alibaba.csp.sentinel.dashboard;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.dashboard.convert.ApiDefinitionRuleConvert;
import com.alibaba.csp.sentinel.dashboard.convert.GatewayFlowRuleConvert;
import com.alibaba.csp.sentinel.dashboard.nacos.NacosConfig;
import com.alibaba.csp.sentinel.dashboard.nacos.NacosWritableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.PropertyKeyConst;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;

/**
 *
 * @author xiongying
 * @since 1.8.4
 */
@Configuration
public class NacosDataSourceAutoConfiguration {

    private static final String NACOS_GROUP_ID = "DEFAULT_GROUP";


    @Bean("gatewayApiGroupNacosWritableDataSource")
    public NacosWritableDataSource<List<ApiDefinition>> gatewayApiGroupNacosWritableDataSource(NacosConfig nacosConfig) {
        return new NacosWritableDataSource<>(nacosConfig, nacosConfig.getGatewayApiGroupDataId());
    }

    @Bean("gatewayApiGroupNacosReadableDataSource")
    public NacosDataSource<List<ApiDefinition>> gatewayApiGroupNacosReadableDataSource(NacosConfig nacosConfig) {
        return new NacosDataSource<>(buildProperties(nacosConfig), nacosConfig.getGroupId(), nacosConfig.getGatewayApiGroupDataId(), new ApiDefinitionRuleConvert());
    }

    @Bean("gatewayFlowNacosWritableDataSource")
    public NacosWritableDataSource<List<GatewayFlowRule>> gatewayFlowNacosWritableDataSource(NacosConfig nacosConfig) {
        return new NacosWritableDataSource<>(nacosConfig, nacosConfig.getGatewayFlowDataId());
    }

    @Bean("gatewayFlowNacosReadableDataSource")
    public NacosDataSource<List<GatewayFlowRule>> gatewayFlowNacosReadableDataSource(NacosConfig nacosConfig) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        JsonConverter jsonConverter = new JsonConverter<>(objectMapper, GatewayFlowRule.class);
        return new NacosDataSource(buildProperties(nacosConfig), nacosConfig.getGroupId(), nacosConfig.getGatewayFlowDataId(), new GatewayFlowRuleConvert());
    }

    private Properties buildProperties(NacosConfig nacosConfig) {
        Properties properties = new Properties();
        if (StringUtil.isNotBlank(nacosConfig.getServerAddr())) {
            properties.setProperty(PropertyKeyConst.SERVER_ADDR, nacosConfig.getServerAddr());
        }
        if (StringUtil.isNotBlank(nacosConfig.getUsername())) {
            properties.setProperty(PropertyKeyConst.USERNAME, nacosConfig.getUsername());
        }
        if (StringUtil.isNotBlank(nacosConfig.getPassword())) {
            properties.setProperty(PropertyKeyConst.PASSWORD, nacosConfig.getPassword());
        }
        return properties;
    }
}


