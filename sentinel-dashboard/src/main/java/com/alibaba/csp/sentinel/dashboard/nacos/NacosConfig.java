package com.alibaba.csp.sentinel.dashboard.nacos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 *
 * @author xiongying
 * @since 1.8.4
 */
@ConfigurationProperties(prefix = "nacos")
@Component
public class NacosConfig {

    private static final Logger logger = LoggerFactory.getLogger(NacosConfig.class);

    private String serverAddr;

    private String username;

    private String password;


    private String groupId;

    private String gatewayApiGroupDataId;

    private String gatewayFlowDataId;


    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGatewayApiGroupDataId() {
        return gatewayApiGroupDataId;
    }

    public void setGatewayApiGroupDataId(String gatewayApiGroupDataId) {
        this.gatewayApiGroupDataId = gatewayApiGroupDataId;
    }

    public String getGatewayFlowDataId() {
        return gatewayFlowDataId;
    }

    public void setGatewayFlowDataId(String gatewayFlowDataId) {
        this.gatewayFlowDataId = gatewayFlowDataId;
    }

    @Override
    public String toString() {
        return "NacosConfig{" +
                "serverAddr='" + serverAddr + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", groupId='" + groupId + '\'' +
                ", gatewayApiGroupDataId='" + gatewayApiGroupDataId + '\'' +
                ", gatewayFlowDataId='" + gatewayFlowDataId + '\'' +
                '}';
    }
}
