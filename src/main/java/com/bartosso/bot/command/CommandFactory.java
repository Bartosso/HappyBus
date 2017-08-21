package com.bartosso.bot.command;


import com.bartosso.bot.command.impl.AdminMenu.BusesMenu.*;
import com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Coordinators.AddNewCoordinatorCommand;
import com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Coordinators.RemoveCoordinatorCommand;
import com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Coordinators.ShowAllCoordinatorsCommand;
import com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Coordinators.ShowEmployeeCoordinatorsMenuCommand;
import com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Drivers.AddNewDriverCommand;
import com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Drivers.RemoveDriverCommand;
import com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Drivers.ShowAllDriversCommand;
import com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.Drivers.ShowEmployeeDriversMenuCommand;
import com.bartosso.bot.command.impl.AdminMenu.EmployeeMenu.ShowEmployeeMenuCommand;
import com.bartosso.bot.command.impl.AdminMenu.INFOMenu.*;
import com.bartosso.bot.command.impl.AdminMenu.SchoolKidsMenu.*;
import com.bartosso.bot.command.impl.AdminMenu.SchoolsMenu.AddNewSchoolCommand;
import com.bartosso.bot.command.impl.AdminMenu.SchoolsMenu.RemoveSchoolCommand;
import com.bartosso.bot.command.impl.AdminMenu.SchoolsMenu.ShowAllSchoolsCommand;
import com.bartosso.bot.command.impl.AdminMenu.SchoolsMenu.ShowSchoolsMenu;
import com.bartosso.bot.command.impl.AdminMenu.ShowMainAdminMenuCommand;
import com.bartosso.bot.command.impl.CoordinatorMenu.ToHome.PutInBusThings.ShowListsOfSchoolsForEveningCommand;
import com.bartosso.bot.command.impl.CoordinatorMenu.ToHome.ReadyToGoBuses.ShowReadyToGoBusesCommand;
import com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ShowListsThings.ShowListsCommand;
import com.bartosso.bot.command.impl.CoordinatorMenu.ToHome.ToHomeMenuCommand;
import com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.StartRoute.StartMorningRouteCommand;
import com.bartosso.bot.command.impl.CoordinatorMenu.ToSchool.ToSchoolMenuCommand;
import com.bartosso.bot.command.impl.DriverMenu.DriverToHomeCommand;
import com.bartosso.bot.command.impl.DriverMenu.DriverToSchoolCommand;
import com.bartosso.bot.command.impl.ParentsMenu.*;
import com.bartosso.bot.command.impl.ParentsMenu.PersonalAreaMenu.*;
import com.bartosso.bot.exception.NotRealizedMethodException;

/**
 * Created by user on 1/2/17.
 */
public class CommandFactory {
    public static Command getCommand(long id) {
        CommandType type = CommandType.getType(id);
        switch (type) {
            case SHOW_MAIN_MENU:
                return new ShowMainMenuCommand();
            case SHOW_ABOUT:
                return new ShowAboutCommand();
            case SHOW_NEWS:
                return new ShowNewsCommand();
            case SHOW_CONTACTS:
                return new ShowContactsCommand();
            case REQUEST_CALL:
                return new RequestCallCommand();
            case SHOW_PERSONAL_AREA:
                return new ShowPersonalAreaCommand();
            case SKIP_SCHOOL:
                return new SkipSchoolCommand();
            case SELECT_DAY_TO_SKIP:
                return new SelectDayToSkipCommand();
            case MAKE_FEEDBACK:
                return new FeedbackCommand();
            case SHOW_MAIN_ADMIN_MENU:
                return new ShowMainAdminMenuCommand();
            case SHOW_EMPLOYEE_MENU:
                return new ShowEmployeeMenuCommand();
            case SHOW_SCHOOLS_MENU:
                return new ShowSchoolsMenu();
            case SHOW_BUSES_MENU:
                return new ShowBusesMenuCommand();
            case SHOW_SCHOOL_KIDS_MENU:
                return new ShowSchoolKidsMenuCommand();
            case SHOW_INFO_MENU:
                return new ShowINFOMenuCommand();
            case SHOW_EMPLOYEE_DRIVERS_MENU:
                return new ShowEmployeeDriversMenuCommand();
            case SHOW_EMPLOYEE_COORDINATORS_MENU:
                return new ShowEmployeeCoordinatorsMenuCommand();
            case SHOW_ALL_DRIVERS:
                return new ShowAllDriversCommand();
            case ADD_NEW_DRIVER:
                return new AddNewDriverCommand();
            case REMOVE_DRIVER:
                return new RemoveDriverCommand();
            case SHOW_ALL_COORDINATORS:
                return new ShowAllCoordinatorsCommand();
            case ADD_NEW_COORDINATOR:
                return new AddNewCoordinatorCommand();
            case REMOVE_COORDINATOR:
                return new RemoveCoordinatorCommand();
            case SHOW_ALL_SCHOOLS:
                return new ShowAllSchoolsCommand();
            case ADD_NEW_SCHOOL:
                return new AddNewSchoolCommand();
            case REMOVE_SCHOOL:
                return new RemoveSchoolCommand();
            case SHOW_ALL_BUSES:
                return new ShowAllBusesCommand();
            case ADD_NEW_BUS:
                return new AddNewBusCommand();
            case REMOVE_BUS:
                return new ShowAndRemoveBusCommand();
            case EDIT_BUS:
                return new EditBusesCommand();
            case ADD_DRIVER_TO_BUS:
                return new AddDriverToBusCommand();
            case CHANGE_DRIVER_FROM_BUS:
                return new ChangeDriverCommand();
            case EDIT_NEWS:
                return new EditNewsInfoCommand();
            case EDIT_ABOUT:
                return new EditInfoAboutCommand();
            case EDIT_CONTACTS:
                return new EditContactsInfoCommand();
            case ADD_NEW_KID:
                return new AddNewKidCommand();
            case REMOVE_KID:
                return new RemoveChildAndParentsCommand();
            case SHOW_KIDS_BY_SCHOOL:
                return new ShowKidsBySchoolCommand();
            case EDIT_KIDS_BY_SEARCH:
                return new FindAndEditKidCommand();
            case SHOW_KIDS_BY_BUS:
                return new ShowKidsByBusCommand();
            case SHOW_COORDINATOR_TO_SCHOOL_MENU:
                return new ToSchoolMenuCommand();
            case SHOW_COORDINATOR_TO_HOME_MENU:
                return new ToHomeMenuCommand();
            case SHOW_TO_SCHOOL_LIST_COMMAND:
                return new ShowListsCommand();
            case START_MORNING_ROUTE_COMMAND:
                return new StartMorningRouteCommand();
            case SHOW_TO_HOME_LIST_COMMAND:
                return new ShowListsOfSchoolsForEveningCommand();
            case SHOW_READY_TO_GO_BUSES:
                return new ShowReadyToGoBusesCommand();
            case DRIVER_TO_SCHOOL:
                return new DriverToSchoolCommand();
            case DRIVER_TO_HOME:
                return new DriverToHomeCommand();
            case WHERE_IS_BUS:
                return new WhereIsBusCommand();
            case SHOW_ARCHIVE_SICK_LEAVES:
                return new ShowAllSkippedDaysCommand();
            case UPDATE_MANAGER:
                return new AddManagerCommand();
            default:
                throw new NotRealizedMethodException("Not realized for type: " + type);
        }
    }
}
