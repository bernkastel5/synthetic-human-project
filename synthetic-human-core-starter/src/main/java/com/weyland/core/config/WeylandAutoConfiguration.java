package com.weyland.core.config;

import com.weyland.core.audit.AuditAspect;
import com.weyland.core.audit.AuditProperties;
import com.weyland.core.command.CommandService;
import com.weyland.core.error.GlobalApiExceptionHandler;
import com.weyland.core.monitoring.MetricsService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({WeylandCoreProperties.class, AuditProperties.class})
public class WeylandAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolExecutor commonPriorityExecutor(WeylandCoreProperties props) {
        WeylandCoreProperties.Queue qProps = props.queue();
        return new ThreadPoolExecutor(
            qProps.corePoolSize(),
            qProps.maxPoolSize(),
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(qProps.capacity())
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public MetricsService metricsService(MeterRegistry meterRegistry, ThreadPoolExecutor executor) {
        return new MetricsService(meterRegistry, executor);
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandService commandService(ThreadPoolExecutor commonPriorityExecutor, MetricsService metricsService) {
        return new CommandService(commonPriorityExecutor, metricsService);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public GlobalApiExceptionHandler globalApiExceptionHandler() {
        return new GlobalApiExceptionHandler();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
    static class AuditConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public AuditAspect auditAspect(
            AuditProperties auditProperties,
            ObjectProvider<KafkaTemplate<String, String>> kafkaTemplateProvider) {
            
            return new AuditAspect(auditProperties, kafkaTemplateProvider.getIfAvailable());
        }
    }
}