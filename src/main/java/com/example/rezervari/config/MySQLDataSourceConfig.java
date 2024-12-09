package com.example.rezervari.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
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
        basePackages = "com.example.rezervari.mysql",  // Specify the package where your repositories are
        entityManagerFactoryRef = "mysqlEntityManagerFactory",  // Reference the entity manager bean
        transactionManagerRef = "mysqlTransactionManager"  // Reference the transaction manager bean
)
@EnableTransactionManagement
public class MySQLDataSourceConfig {

    @Bean(name = "mysqlDataSource")
    @Qualifier("mysqlDataSource")
    public DataSource mysqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver"); // MySQL JDBC driver
        dataSource.setUrl("jdbc:mysql://localhost:3306/reservations_db");
        dataSource.setUsername("root");
        dataSource.setPassword("password");
        return dataSource;
    }


    @Bean(name = "mysqlEntityManagerFactory")
    @Qualifier("mysqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(mysqlDataSource());  // Link the data source
        factoryBean.setPackagesToScan("com.example.rezervari.entities.mysql");  // Specify the package where your JPA entities are located

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        jpaProperties.put("hibernate.show_sql", "true");
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        factoryBean.setJpaPropertyMap(jpaProperties);

        return factoryBean;
    }

    @Bean(name = "mysqlTransactionManager")
    @Qualifier("mysqlTransactionManager")
    public PlatformTransactionManager mysqlTransactionManager( @Qualifier("mysqlEntityManagerFactory") LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory) {
        EntityManagerFactory entityManagerFactory = mysqlEntityManagerFactory.getObject();
        return new JpaTransactionManager(entityManagerFactory);  // Link the entity manager factory
    }
}

