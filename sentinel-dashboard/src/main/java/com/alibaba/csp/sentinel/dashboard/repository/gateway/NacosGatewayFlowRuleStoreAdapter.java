package com.alibaba.csp.sentinel.dashboard.repository.gateway;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.discovery.MachineInfo;
import com.alibaba.csp.sentinel.dashboard.nacos.NacosWritableDataSource;
import com.alibaba.csp.sentinel.dashboard.repository.rule.RuleRepository;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;

/**
 * Nacos Gateway 流控规则 适配器
 *
 * @author xiongying
 * @since 1.8.4
 */
@Component
public class NacosGatewayFlowRuleStoreAdapter implements RuleRepository<GatewayFlowRuleEntity, Long> {

    @Autowired
    private InMemGatewayFlowRuleStore inMemGatewayFlowRuleStore;

    @Resource(name = "gatewayFlowNacosWritableDataSource")
    private NacosWritableDataSource<List<GatewayFlowRule>> writableDataSource;

    @Resource(name = "gatewayFlowNacosReadableDataSource")
    private NacosDataSource<List<GatewayFlowRule>> readableDataSource;

    @Override
    public GatewayFlowRuleEntity save(GatewayFlowRuleEntity entity) {
        try {
            // get rules from nacos
            List<GatewayFlowRule> gatewayFlowRules = readableDataSource.loadConfig();
            // delete old rule
            deleteGatewayFlowRule(entity.getResource(), gatewayFlowRules);
            // add new rule
            gatewayFlowRules.add(entity.toGatewayFlowRule());
            // sync nacos
            writableDataSource.write(gatewayFlowRules);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return inMemGatewayFlowRuleStore.save(entity);
    }

    @Override
    public List<GatewayFlowRuleEntity> saveAll(List<GatewayFlowRuleEntity> rules) {
        return inMemGatewayFlowRuleStore.saveAll(rules);
    }

    @Override
    public GatewayFlowRuleEntity delete(Long id) {
        try {
            // get rules from nacos
            List<GatewayFlowRule> gatewayFlowRules = readableDataSource.loadConfig();
            // findById
            GatewayFlowRuleEntity entity = inMemGatewayFlowRuleStore.findById(id);
            if (entity != null) {
                // delete old rule
                deleteGatewayFlowRule(entity.getResource(), gatewayFlowRules);
                // sync nacos
                writableDataSource.write(gatewayFlowRules);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return inMemGatewayFlowRuleStore.delete(id);
    }

    @Override
    public GatewayFlowRuleEntity findById(Long id) {
        return inMemGatewayFlowRuleStore.findById(id);
    }

    @Override
    public List<GatewayFlowRuleEntity> findAllByMachine(MachineInfo machineInfo) {
        return inMemGatewayFlowRuleStore.findAllByMachine(machineInfo);
    }

    @Override
    public List<GatewayFlowRuleEntity> findAllByApp(String appName) {
        return inMemGatewayFlowRuleStore.findAllByApp(appName);
    }

    private void deleteGatewayFlowRule(String resource, List<GatewayFlowRule> gatewayFlowRules) {
        if (gatewayFlowRules == null) {
            return;
        }
        Iterator<GatewayFlowRule> gatewayFlowRuleIterator = gatewayFlowRules.iterator();
        while (gatewayFlowRuleIterator.hasNext()) {
            GatewayFlowRule gatewayFlowRule = gatewayFlowRuleIterator.next();
            if (resource.equals(gatewayFlowRule.getResource())) {
                gatewayFlowRuleIterator.remove();
            }
        }
    }
}
