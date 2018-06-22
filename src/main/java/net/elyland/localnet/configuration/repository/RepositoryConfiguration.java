package net.elyland.localnet.configuration.repository;

/**
 * Created by Igor on 07-Jun-16.
 */

import com.mchange.v2.c3p0.ComboPooledDataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import java.beans.PropertyVetoException;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "net.elyland.localnet.repositories",
        entityManagerFactoryRef = "adminEntityManagerFactory",
        transactionManagerRef = "adminTransactionManager"
)
@EntityScan("net.elyland.localnet.domains")
@EnableTransactionManagement
public class RepositoryConfiguration {
    @Value("${admin.datasource.driver}")
    private String DB_DRIVER;

    @Value("${admin.datasource.password}")
    private String DB_PASSWORD;

    @Value("${admin.datasource.url}")
    private String DB_URL;

    @Value("${admin.datasource.username}")
    private String DB_USERNAME;

    @Value("${hibernate.dialect}")
    private String HIBERNATE_DIALECT;

    @Value("${hibernate.show_sql}")
    private String HIBERNATE_SHOW_SQL;

    @Value("${hibernate.hbm2ddl.auto}")
    private String HIBERNATE_HBM2DDL_AUTO;

    @Value("${hibernate.c3p0.max_size}")
    private String CONN_POOL_MAX_SIZE;

    @Value("${hibernate.c3p0.min_size}")
    private String CONN_POOL_MIN_SIZE;

    @Value("${hibernate.c3p0.idle_test_period}")
    private String CONN_POOL_IDLE_PERIOD;


    @Bean
    @Primary
    public ComboPooledDataSource adminDataSource() {

        ComboPooledDataSource dataSource = new ComboPooledDataSource("adminDataSource");

        try {
            dataSource.setDriverClass(DB_DRIVER);
        } catch (PropertyVetoException pve){
            System.out.println("Cannot load datasource driver (" + DB_DRIVER +") : " + pve.getMessage());
            return null;
        }
        dataSource.setJdbcUrl(DB_URL);
        dataSource.setUser(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setMinPoolSize(Integer.parseInt(CONN_POOL_MIN_SIZE));
        dataSource.setMaxPoolSize(Integer.parseInt(CONN_POOL_MAX_SIZE));
        dataSource.setMaxIdleTime(Integer.parseInt(CONN_POOL_IDLE_PERIOD));

        return dataSource;
    }


    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean adminEntityManagerFactory(final EntityManagerFactoryBuilder builder)
    {

        HashMap<String, Object> properties = new HashMap<>();
//        Uncoment for creating new database
//        properties.put("hibernate.hbm2ddl.auto", "create");

        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");

//        Uncoment for showing SQL queries
//        properties.put("hibernate.show_sql", "true");


        return builder
                .dataSource(adminDataSource())
                .packages("net.elyland.localnet.domains")
                .persistenceUnit("adminPersistenceUnit")
                .properties(properties)
                .build();
    }

    @Bean
    @Primary
    public JpaTransactionManager adminTransactionManager(@Qualifier("adminEntityManagerFactory") final EntityManagerFactory factory)
    {
        return new JpaTransactionManager(factory);
    }
}
