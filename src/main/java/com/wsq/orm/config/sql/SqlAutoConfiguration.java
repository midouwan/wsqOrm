package com.wsq.orm.config.sql;

import com.wsq.orm.config.ConfigBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author wangshuangquan<wangshuangquan-a@qq.com>
 * @date 2019-01-17 0:01
 */
@Configuration
@EnableConfigurationProperties({ConfigBean.class,SqlConfigBean.class,SqlResultTypeConfigBean.class})
@ConditionalOnBean(DataSource.class)
@ConditionalOnProperty(value = "wsq.sql.enabled", matchIfMissing = true)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class SqlAutoConfiguration {

}

