package com.ruoyi.framework.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.ruoyi.common.constant.TableNameConstant;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.handle.CreateAndUpdateMetaObjectHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Mybatis Plus 配置
 *
 * @author ruoyi
 */
@EnableTransactionManagement(proxyTargetClass = true)
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(paginationInnerInterceptor());
        // 乐观锁插件
        interceptor.addInnerInterceptor(optimisticLockerInnerInterceptor());
        // 阻断插件
        interceptor.addInnerInterceptor(blockAttackInnerInterceptor());

        dynamicTableNameHandler(interceptor);
        return interceptor;
    }

    /**
     * 分页插件，自动识别数据库类型 https://baomidou.com/guide/interceptor-pagination.html
     */
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 设置数据库类型为mysql
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInnerInterceptor.setMaxLimit(-1L);
        return paginationInnerInterceptor;
    }

    /**
     * 乐观锁插件 https://baomidou.com/guide/interceptor-optimistic-locker.html
     */
    public OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }

    /**
     * 如果是对全表的删除或更新操作，就会终止该操作 https://baomidou.com/guide/interceptor-block-attack.html
     */
    public BlockAttackInnerInterceptor blockAttackInnerInterceptor() {
        return new BlockAttackInnerInterceptor();
    }

    /**
     * 元对象字段填充控制器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new CreateAndUpdateMetaObjectHandler();
    }


    public void dynamicTableNameHandler(MybatisPlusInterceptor interceptor) {

        // 动态表名拦截器
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();

        // 配置表名处理器（根据当前月份动态生成表名）
        Map<String, TableNameHandler> tableNameHandlerMap = new HashMap<>();
        tableNameHandlerMap.put(TableNameConstant.SH_MQTT_DEVICE_DATA_TEMPLATE, (sql, tableName) -> {
            // 生成当前月份的表名（格式：sh_mqtt_device_recent_data_202310）
            String yyyyMM = DateUtils.parseDateToStr(DateUtils.YYYYMM,new Date());
            return TableNameConstant.MQTT_TABLE_NAME + "_" + yyyyMM;
        });
        tableNameHandlerMap.put(TableNameConstant.SH_UDP_DEVICE_DATA_TEMPLATE, (sql, tableName) -> {
            // 生成当前月份的表名（格式：sh_mqtt_device_recent_data_202310）
            String yyyyMM = DateUtils.parseDateToStr(DateUtils.YYYYMM,new Date());
            return TableNameConstant.UDP_TABLE_NAME + "_" + yyyyMM;
        });

        dynamicTableNameInnerInterceptor.setTableNameHandlerMap(tableNameHandlerMap);
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
    }


}
