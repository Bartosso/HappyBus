package com.bartosso.bot.dao.impl;

import com.bartosso.bot.dao.AbstractDao;
import com.bartosso.bot.dao.rowMaps.BusMap;
import com.bartosso.bot.entity.ProjectEntities.Bus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.SQLException;
import java.util.List;

@SuppressWarnings("unchecked")
@Repository
public class BusesDao extends AbstractDao implements EntityDao {
    public BusesDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void addNew(Bus bus) {
        jdbcTemplate.update("INSERT INTO buses (number, brand, model, color) VALUES (?,?,?,?)",
                bus.getNumber(),bus.getBrand(),bus.getModel(), bus.getColor());
    }
    public List getAll() {
        List<Bus> a = jdbcTemplate.query("SELECT * FROM buses",new BusMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public void deleteBus(Integer idToDelete){
        jdbcTemplate.update("DELETE FROM buses WHERE id=?", idToDelete);
    }

    public void updateBusThings(Object newValue, String whatToChange, int id){
        if (whatToChange.equals("driver_id")) {
            newValue = Long.parseLong(String.valueOf(newValue));
        }
        jdbcTemplate.update("UPDATE buses SET "+whatToChange+"=? WHERE id=?",newValue,id);
    }

    public void updateMorningRoute(Long[] kids, long busId){
        Array kidsInArray = null;
        try {
            kidsInArray = jdbcTemplate.getDataSource().getConnection().createArrayOf("BIGINT",kids);
        } catch (SQLException ignored) { }
        jdbcTemplate.update("UPDATE buses SET to_school_kids=? WHERE id=?",kidsInArray,busId);
    }

    public void updateEveningRoute(Long[] kids, long busId){
        Array kidsInArray = null;
        try {
            kidsInArray = jdbcTemplate.getDataSource().getConnection().createArrayOf("BIGINT",kids);
        } catch (SQLException ignored) { }
        jdbcTemplate.update("UPDATE buses SET to_home_kids=? WHERE id=?",kidsInArray,busId);
    }

    public Bus getBusById(long id){
        return (Bus) jdbcTemplate.queryForObject("SELECT * FROM buses WHERE id=?",new Object[] {id}, new BusMap());
    }

    public Bus getBusByDriverId(long driverId){
        return (Bus) jdbcTemplate.queryForObject("SELECT * FROM buses WHERE driver_id=?",new Object[] {driverId}, new BusMap());
    }

    public void removeDriverFromBuses(long driverId){
        jdbcTemplate.update("UPDATE buses SET driver_id=NULL WHERE driver_id=?;",driverId);
    }

    public List getAllWithThatNumber(String number) {
        List<Bus> a = jdbcTemplate.query("SELECT * FROM buses WHERE driver_id IS NULL AND number=?",new Object[]{number},new BusMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public List getAllBusesFromSchool(String school_id){
        List<Bus> a = jdbcTemplate.query("SELECT DISTINCT ON (id) * FROM get_all_buses_from_school(?)",new Object[]{school_id},new BusMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public List getAllBusesWithoutDrivers() {
        List<Bus> a = jdbcTemplate.query("SELECT * FROM buses WHERE driver_id IS NULL",new BusMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public List getAllBusesWhoCanDoEveningRoute() {
        List<Bus> a = jdbcTemplate.query("SELECT * FROM buses WHERE ready_to_home=FALSE ",new BusMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public void addNewKidToEveningBusWithLastCoordinatorIds(long kidId, long lastCoordinatorId, long busId){
        jdbcTemplate.update("UPDATE buses SET to_home_kids = array_append(to_home_kids,?) WHERE id =?;" +
                "UPDATE buses SET last_cordinator_id = ? WHERE id =?", kidId,busId,lastCoordinatorId,busId);
    }

    public Bus getBusByChildInsideInMorning(long kidId){
        return  (Bus) jdbcTemplate.queryForObject("SELECT * from buses WHERE ? = ANY (buses.to_school_kids)",
                new Object[]{kidId},new BusMap());
    }

    public void updateLastGpsCords(String cords, long busId){
        jdbcTemplate.update("UPDATE buses SET last_gps_cords =? WHERE  id=?",cords,busId);

    }

    public Bus getBusByChildInsideInEvening(long kidId){
        return  (Bus) jdbcTemplate.queryForObject("SELECT * from buses WHERE ? = ANY (buses.to_home_kids)",
                new Object[]{kidId},new BusMap());
    }

    public List<Bus> getUndSetAllBusesWhoReadyForMorningRoute(){
        List<Bus> a = jdbcTemplate.query("UPDATE buses SET ready_to_school = TRUE  WHERE ready_to_school = FALSE  AND array_length(to_school_kids,1) > 0 RETURNING *",new BusMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public void setMorningBusRouteEnd(long busId){
        jdbcTemplate.update("UPDATE buses SET ready_to_school = FALSE  WHERE id = ?",busId);
    }

    public void setEveningBusRouteEnd(long busId){
        jdbcTemplate.update("UPDATE buses SET (ready_to_home,last_cordinator_id,to_home_kids, last_gps_cords) = (FALSE , NULL, NULL, NULL ) WHERE id = ?",busId);
    }

    public void setBusCordsNull(long busId){
        jdbcTemplate.update("UPDATE buses SET last_gps_cords = NULL  WHERE id = ?",busId);
    }

    public Long makeEveningRouteForBusAndGetDriverId(long busId){
       return jdbcTemplate.queryForObject("UPDATE buses SET ready_to_home = TRUE WHERE id=? RETURNING buses.driver_id",Long.class,busId);

    }

    public void setLastCoordinatorId(long lastCoordinatorId, long busId){
        jdbcTemplate.update("UPDATE buses SET last_cordinator_id = ? WHERE id=?",lastCoordinatorId,busId);
    }

    public List<Bus> getAllBusesWhoReadyToGoEveningUndGotThisCoordinatorId(long coordinator_id){
        List<Bus> a = jdbcTemplate.query("SELECT * FROM buses WHERE ready_to_home=FALSE AND buses.last_cordinator_id=? AND array_length(to_home_kids,1) > 0 ",new Object[]{coordinator_id},new BusMap());
        if (a.isEmpty()) return null;
        else return a;
    }

    public String getBusCordsByChildId(long childID){
        return  jdbcTemplate.queryForObject("SELECT * FROM get_bus_cords_by_child(?)",String.class,childID);
    }

    public void deleteKidFromBus(long kidId){
        Bus a = (Bus) jdbcTemplate.queryForObject("SELECT * FROM buses WHERE ? = ANY (buses.to_school_kids);",new Object[]{kidId}
        ,new BusMap());
        if (a!=null){
            a.getTo_school_kids().removeIf(aLong -> aLong==kidId);
            updateMorningRoute(a.getTo_school_kids().toArray(new Long[a.getTo_school_kids().size()]),a.getId());
        }
    }

}
