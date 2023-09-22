package com.vertical.jobapp.config;

import liquibase.integration.spring.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

import javax.sql.*;

@Configuration
public class LiquibaseConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:liquibase/master.xml");
        liquibase.setDataSource(dataSource);
        return liquibase;
    }
}
