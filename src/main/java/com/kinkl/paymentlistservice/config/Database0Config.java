package com.kinkl.paymentlistservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "db0EntityManagerFactory",
        transactionManagerRef = "db0TransactionManager",
        basePackages = {"com.kinkl.paymentlistservice.repository.db0"})
public class Database0Config {

    @Primary
    @Bean(name = "db0DataSource")
    @ConfigurationProperties(prefix = "payments.datasource0")
    public DataSource dataSource() {
        return new DriverManagerDataSource();
    }

    @Primary
    @Bean(name = "db0EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean db0EntityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("db0DataSource") DataSource dataSource) {
        return builder.dataSource(dataSource) //
                .packages("com.kinkl.paymentlistservice.domain") //
                .build();
    }

    @Primary
    @Bean(name = "db0TransactionManager")
    public PlatformTransactionManager db0TransactionManager(@Qualifier("db0EntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
