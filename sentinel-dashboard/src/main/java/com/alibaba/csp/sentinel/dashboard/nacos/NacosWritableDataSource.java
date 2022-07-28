package com.alibaba.csp.sentinel.dashboard.nacos;

import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Properties;

/**
 *
 * @author xiongying
 * @since 1.8.4
 */
public class NacosWritableDataSource<T> implements WritableDataSource<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Properties properties;
    private final String groupId;
    private final String dataId;

    private ConfigService configService = null;

    public NacosWritableDataSource(NacosConfig nacosConfig, String dataId) {
        if (StringUtil.isBlank(dataId)) {
            throw new IllegalArgumentException(String.format("Bad argument: dataId=[%s]", dataId));
        }
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
        this.properties = properties;
        this.groupId = nacosConfig.getGroupId();
        this.dataId = dataId;
        initNacosConfigService();
    }

    @Override
    public void write(T value) throws Exception {
        String content = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        configService.publishConfig(dataId, groupId, content, ConfigType.JSON.getType());
    }

    @Override
    public void close() throws Exception {
    }

    private void initNacosConfigService() {
        try {
            this.configService = NacosFactory.createConfigService(this.properties);
        } catch (Exception e) {
            RecordLog.warn("[NacosDataSource] Error occurred when initializing Nacos data source", e);
            e.printStackTrace();
        }
    }
}
