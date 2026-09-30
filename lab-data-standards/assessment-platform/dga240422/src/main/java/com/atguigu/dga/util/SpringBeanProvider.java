package com.atguigu.dga.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author WEIYUNHUI
 * @date 2024/8/31 9:03
 */
@Component
public class SpringBeanProvider implements ApplicationContextAware {

    private ApplicationContext applicationContext ;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext ;
    }

    /**
     * 从容器中获取指定名字的Bean对象
     */
    public <T> T  getBean(String beanName , Class<T> clsType){
        T assessor = applicationContext.getBean(beanName, clsType);
        return assessor ;
    }
}
