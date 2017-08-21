package com.bartosso.bot.dao;

import com.bartosso.bot.dao.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Created by user on 12/11/16.
 */
@Component
public class DaoFactory {

    private static DaoFactory daoFactory = new DaoFactory();

    @SuppressWarnings({"SpringJavaAutowiringInspection", "AccessStaticViaInstance"})
    @Autowired

    public void setDataSource(@Qualifier("dataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static JdbcTemplate jdbcTemplate;


    /**
     * @return connection
     */
    public static DaoFactory getFactory() {
        return daoFactory;
    }


    public MessageDao getMessageDao() {
        return new MessageDao(jdbcTemplate);
    }

    public CommandDao getCommandDao() {
        return new CommandDao(jdbcTemplate);
    }

    public KeyboardMarkUpDao getKeyboardMarkUpDao() {
        return new KeyboardMarkUpDao(jdbcTemplate);
    }

    public ButtonDao getButtonDao() {
        return new ButtonDao(jdbcTemplate);
    }


    public ParentDao getParentDao(){
        return new ParentDao(jdbcTemplate);
    }

    public DriverDao getDriverDao() { return  new DriverDao(jdbcTemplate); }

    public CoordinatorDao getCoordinatorDao() { return new CoordinatorDao(jdbcTemplate); }

    public SchoolDao getSchoolDao() { return new SchoolDao(jdbcTemplate);}

    public BusesDao getBusesDao() { return new BusesDao(jdbcTemplate);}

    public KidsDao getKidsDao() { return  new KidsDao(jdbcTemplate);}

    public AdminDao getAdminDao() {return new AdminDao(jdbcTemplate);}

    public SickLeaveDao getSickLeaveDao() {return new SickLeaveDao(jdbcTemplate);}

    public ManagerDao getManagerDao() {return new ManagerDao(jdbcTemplate);}


}
