package com.alibaba.csp.sentinel.dashboard.repository.gateway;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.discovery.MachineInfo;
import com.alibaba.csp.sentinel.dashboard.nacos.NacosWritableDataSource;
import com.alibaba.csp.sentinel.dashboard.repository.rule.RuleRepository;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Nacos Gateway ApiDefinition 适配器
 *
 * @author xiongying
 * @since 1.8.4
 */
@Component
public class NacosApiDefinitionStoreAdapter implements RuleRepository<ApiDefinitionEntity, Long> {


    @Autowired
    private InMemApiDefinitionStore inMemApiDefinitionStore;

    @Resource(name = "gatewayApiGroupNacosWritableDataSource")
    private NacosWritableDataSource<List<ApiDefinition>> writableDataSource;

    @Resource(name = "gatewayApiGroupNacosReadableDataSource")
    private NacosDataSource<List<ApiDefinition>> readableDataSource;

    @Override
    public ApiDefinitionEntity save(ApiDefinitionEntity entity) {
        try {
            Set<ApiPredicateItem> newApiPathPredicateItems = new HashSet<>(entity.getPredicateItems().size());
            entity.getPredicateItems().forEach(entityPredicateItem -> {
                ApiPathPredicateItem newApiPathPredicateItem = new ApiPathPredicateItem();
                newApiPathPredicateItem.setPattern(entityPredicateItem.getPattern());
                newApiPathPredicateItem.setMatchStrategy(entityPredicateItem.getMatchStrategy());
                newApiPathPredicateItems.add(newApiPathPredicateItem);
            });

            List<ApiDefinition> apiDefinitions = readableDataSource.loadConfig();
            AtomicBoolean isExist = new AtomicBoolean(false);
            apiDefinitions.forEach(apiDefinition -> {
                if (entity.getApiName().equals(apiDefinition.getApiName())) {
                    apiDefinition.setPredicateItems(newApiPathPredicateItems);
                    isExist.set(true);
                }
            });
            if (!isExist.get()) {
                ApiDefinition newApiDefinition = new ApiDefinition();
                newApiDefinition.setApiName(entity.getApiName());
                newApiDefinition.setPredicateItems(newApiPathPredicateItems);
                apiDefinitions.add(newApiDefinition);
            }
            writableDataSource.write(apiDefinitions); // 最新数据同步至nacos
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return inMemApiDefinitionStore.save(entity);
    }

    @Override
    public List<ApiDefinitionEntity> saveAll(List<ApiDefinitionEntity> rules) {
        return inMemApiDefinitionStore.saveAll(rules);
    }

    @Override
    public ApiDefinitionEntity delete(Long id) {
        try {
            ApiDefinitionEntity entity = inMemApiDefinitionStore.findById(id);
            if (entity != null) {
                List<ApiDefinition> apiDefinitions = readableDataSource.loadConfig();
                Iterator<ApiDefinition> apiDefinitionIterator = apiDefinitions.iterator();
                while (apiDefinitionIterator.hasNext()) {
                    ApiDefinition apiDefinition = apiDefinitionIterator.next();
                    if (entity.getApiName().equals(apiDefinition.getApiName())) {
                        apiDefinitionIterator.remove();
                    }
                }
                writableDataSource.write(apiDefinitions); // 最新数据同步至nacos
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return inMemApiDefinitionStore.delete(id);
    }

    @Override
    public ApiDefinitionEntity findById(Long id) {
        return inMemApiDefinitionStore.findById(id);
    }

    @Override
    public List<ApiDefinitionEntity> findAllByMachine(MachineInfo machineInfo) {
        return inMemApiDefinitionStore.findAllByMachine(machineInfo);
    }

    @Override
    public List<ApiDefinitionEntity> findAllByApp(String appName) {
        return inMemApiDefinitionStore.findAllByApp(appName);
    }
}
