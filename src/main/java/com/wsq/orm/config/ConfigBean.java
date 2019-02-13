package com.wsq.orm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wangshuangquan<wangshuangquan-a @ qq.com>
 * @date 2019-01-25 17:02
 */
@ConfigurationProperties(prefix = ConfigBean.PREFIX)
public class ConfigBean {
    public static final String PREFIX = "wsq";
}
