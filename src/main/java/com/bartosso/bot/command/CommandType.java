package com.bartosso.bot.command;

/**
 * Created by user on 1/1/17.
 */
public enum CommandType {
    SHOW_MAIN_MENU(1),
    SHOW_ABOUT(2),
    SHOW_PERSONAL_AREA(3),
    SHOW_CONTACTS(4),
    SHOW_NEWS(5),
    REQUEST_CALL(6),
    SKIP_SCHOOL(7),
    SELECT_DAY_TO_SKIP(8),
    MAKE_FEEDBACK(9),
    SHOW_MAIN_ADMIN_MENU(10),
    SHOW_EMPLOYEE_MENU(11),
    SHOW_SCHOOLS_MENU(12),
    SHOW_BUSES_MENU(13),
    SHOW_SCHOOL_KIDS_MENU(14),
    SHOW_INFO_MENU(15),
    SHOW_EMPLOYEE_DRIVERS_MENU(16),
    SHOW_EMPLOYEE_COORDINATORS_MENU(17),
    SHOW_ALL_DRIVERS(18),
    ADD_NEW_DRIVER(19),
    REMOVE_DRIVER(20),
    SHOW_ALL_COORDINATORS(21),
    ADD_NEW_COORDINATOR(22),
    REMOVE_COORDINATOR(23),
    SHOW_ALL_SCHOOLS(24),
    ADD_NEW_SCHOOL(25),
    REMOVE_SCHOOL(26),
    SHOW_ALL_BUSES(27),
    ADD_NEW_BUS(28),
    REMOVE_BUS(29),
    EDIT_BUS(30),
    ADD_DRIVER_TO_BUS(31),
    CHANGE_DRIVER_FROM_BUS(32),
    EDIT_NEWS(33),
    EDIT_ABOUT(34),
    EDIT_CONTACTS(35),
    ADD_NEW_KID(36),
    REMOVE_KID(37),
    SHOW_KIDS_BY_SCHOOL(38),
    EDIT_KIDS_BY_SEARCH(39),
    SHOW_KIDS_BY_BUS(40),
    SHOW_COORDINATOR_TO_SCHOOL_MENU(41),
    SHOW_COORDINATOR_TO_HOME_MENU(42),
    SHOW_TO_SCHOOL_LIST_COMMAND(43),
    START_MORNING_ROUTE_COMMAND(44),
    SHOW_TO_HOME_LIST_COMMAND(45),
    SHOW_READY_TO_GO_BUSES(46),
    DRIVER_TO_SCHOOL(47),
    DRIVER_TO_HOME(48),
    WHERE_IS_BUS(49),
    SHOW_ARCHIVE_SICK_LEAVES(50),
    UPDATE_MANAGER(51),
    EDIT_REQUEST_CALL(52),
    SHOW_SCHOOLS_FOR_RENAME(53),
    MAILING(54);


    private final int id;

    CommandType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CommandType getType(long id) {
        for (CommandType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new com.bartosso.bot.exception.NotRealizedMethodException("There are no type for id: " + id);
    }
}
