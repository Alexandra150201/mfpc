package com.example.rezervari.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;


import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.rezervari.postgresql",  // Specify the package where your repositories are
        entityManagerFactoryRef = "postgresqlEntityManagerFactory",  // Reference the entity manager bean
        transactionManagerRef = "postgresqlTransactionManager"  // Reference the transaction manager bean
)
@EnableTransactionManagement
public class PostgreSQLDataSourceConfig {


    @Bean(name = "postgresqlDataSource")
    @Qualifier("postgresqlDataSource")
    public DataSource postgresqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/users_db");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");
        return dataSource;
    }

    @Bean(name = "postgresqlEntityManagerFactory")
    @Qualifier("postgresqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean postgresqlEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(postgresqlDataSource());  // Link the data source
        factoryBean.setPackagesToScan("com.example.rezervari.entities.postgres");  // Specify the package where your JPA entities are located

        // Use Hibernate as JPA vendor
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(vendorAdapter);

        // Set Hibernate properties
        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");  // PostgreSQL dialect
        jpaProperties.put("hibernate.show_sql", "true");  // Show SQL statements in logs
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");  // Automatically update database schema
        jpaProperties.put("hibernate.format_sql", "true");  // Format SQL for better readability

        factoryBean.setJpaPropertyMap(jpaProperties);
        return factoryBean;
    }

    @Bean(name = "postgresqlTransactionManager")
    @Qualifier("postgresqlTransactionManager")
    public PlatformTransactionManager postgresqlTransactionManager(@Qualifier("postgresqlEntityManagerFactory")LocalContainerEntityManagerFactoryBean factoryBean) {
        EntityManagerFactory entityManagerFactory = factoryBean.getObject();
        return new JpaTransactionManager(entityManagerFactory);  // Link the entity manager factory
    }
}
