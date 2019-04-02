package cn.henry.springbootlearning.config.data;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * @Description:连接数据库
 * @Author:hang
 * @Data:2018/11/20 1:58 PM
 **/
@Configuration
@EnableJpaRepositories(basePackages = {CreditAuditDataSourceConfig.REPOSITORY_PACKAGE}, entityManagerFactoryRef = "auditEntityManagerFactory")
public class CreditAuditDataSourceConfig extends AbstractDataSourceConfig {

    static final String REPOSITORY_PACKAGE = "cn.henry.springbootlearning.repository";
    static final String ENTITY_PACKAGE = "cn.henry.springbootlearning.entity";

    @Value("${pandaloan.audit.mysql.driverClassName}")
    private String jdbcDriverClassName;
    @Value("${pandaloan.audit.mysql.jdbc-url}")
    private String jdbcUrl;
    @Value("${pandaloan.audit.mysql.user}")
    private String jdbcUser;
    @Value("${pandaloan.audit.mysql.password}")
    private String jdbcPassword;
    @Value("${pandaloan.audit.mysql.max-pool-size}")
    private Integer maxPoolSize;
    @Value("${pandaloan.audit.mysql.minIdle}")
    private Integer minIdle;

    @Primary
    @Bean(name = "auditDataSource")
    public DataSource dataSource() {
        return generateDataSource(jdbcUrl, jdbcUser, jdbcPassword, maxPoolSize, minIdle);
    }

    @Primary
    @Bean(name = "auditEntityManagerFactory")
    @DependsOn(value = "auditDataSource")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("auditDataSource")DataSource orderDataSource) {
        return generateEntityManagerFactory(orderDataSource, ENTITY_PACKAGE, "auditDataSource");
    }

    @Primary
    @Bean
    @DependsOn(value = "auditEntityManagerFactory")
    public PlatformTransactionManager transactionManager(@Qualifier("auditEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return generateTransactionManager(entityManagerFactory);
    }
}
