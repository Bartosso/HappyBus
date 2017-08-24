package com.bartosso.bot.Util;

import com.bartosso.bot.entity.ProjectEntities.*;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@SuppressWarnings("Duplicates")
public class CustomKeyboardUtil {

    public static InlineKeyboardMarkup getKeyboardWith9DaysWithCalendarButton(){
        InlineKeyboardMarkup                  keyboard = new InlineKeyboardMarkup();
        ArrayList<List<InlineKeyboardButton>> rows     = new ArrayList<>();
        ArrayList<InlineKeyboardButton>       row;
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatterForText = DateTimeFormatter.ofPattern("dd-MM");
        DateTimeFormatter formatterForData = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        int count = 0;
        for (int i = 0; i < 3; i++){
            row = new ArrayList<>();
            for (int b = 0; b < 4; b++){
                if (count<9){
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(localDate.format(formatterForText));
                button.setCallbackData(localDate.format(formatterForData));
                row.add(button);
                localDate = localDate.plusDays(1);
                count++;}
            }
            rows.add(row);
        }
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData("backOff");

        InlineKeyboardButton calendar = new InlineKeyboardButton();
        calendar.setText("Календарь");
        calendar.setCallbackData("getCalendar");

        row = new ArrayList<>();
        row.add(back);
        row.add(calendar);
        rows.add(row);


       return keyboard.setKeyboard(rows);
    }


    public static InlineKeyboardMarkup getKeyboardWith9Month(){
        InlineKeyboardMarkup                  keyboard = new InlineKeyboardMarkup();
        ArrayList<List<InlineKeyboardButton>> rows     = new ArrayList<>();
        ArrayList<InlineKeyboardButton>       row;
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatterForText     = DateTimeFormatter.ofPattern("MM-yyyy");
        DateTimeFormatter formatterForCallBack = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        int count = 1;
        for (int i = 0; i < 3; i++){
            row = new ArrayList<>();
            for (int b = 0; b < 4; b++){
                if (count<9){
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(localDate.format(formatterForText));
                    button.setCallbackData(localDate.format(formatterForCallBack));
                    row.add(button);
                    localDate = localDate.plusMonths(1);
                    count++;}
            }
            rows.add(row);
        }
        row = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData("backIn9Days");
        row.add(back);
        rows.add(row);

        return keyboard.setKeyboard(rows);
    }

    public static InlineKeyboardMarkup getMonthViaButtons(String monthUndJahr){
        InlineKeyboardMarkup                  keyboard = new InlineKeyboardMarkup();
        ArrayList<List<InlineKeyboardButton>> rows     = new ArrayList<>();
        ArrayList<InlineKeyboardButton>       row;
        DateTimeFormatter formatterInsertAndToCallback     = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter formatterToText                  = DateTimeFormatter.ofPattern("dd-MM");
        LocalDate         selectedMonth                    = LocalDate.parse(monthUndJahr, formatterInsertAndToCallback);
        selectedMonth                                      = selectedMonth.withDayOfMonth(1);
        int               endOfMonth                       = selectedMonth.withDayOfMonth(selectedMonth.lengthOfMonth())
                .getDayOfMonth();
        int count = 0;
        for (int i = 0;i < endOfMonth; i++){
            row = new ArrayList<>();
            for(int b = 0; b < 4; b ++){
               if (count<endOfMonth){
                   InlineKeyboardButton dayOfMonth = new InlineKeyboardButton();
                   dayOfMonth.setText(selectedMonth.format(formatterToText));
                   dayOfMonth.setCallbackData(selectedMonth.format(formatterInsertAndToCallback));
                   selectedMonth = selectedMonth.plusDays(1);
                   row.add(dayOfMonth);
                   count++;
               }
            }
            rows.add(row);
        }
        row = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData("getCalendar");
        row.add(back);
        rows.add(row);
        return keyboard.setKeyboard(rows);

    }

    public static ArrayList<InlineKeyboardMarkup> getPagesWithEntitiesAsButtonText(List<Entity> entityArrayList, String
                                                                                    callBackPrefix){
        ArrayList<InlineKeyboardMarkup>  keyboards = new ArrayList<>();
        List<InlineKeyboardButton>       row;
        List<List<InlineKeyboardButton>> rows;
        int pagesCount = 1 + (entityArrayList.size()/25);
        int counter   = 0;
        for (int b = 0; b < pagesCount; b++) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            rows = new ArrayList<>();
            for (int i = 0; i < 25; i++){
                if (counter<entityArrayList.size()){
                    row                             = new ArrayList<>();
                    InlineKeyboardButton bookButton = new InlineKeyboardButton();
                    bookButton.setText(entityArrayList.get(counter).getTextToButton());
                    bookButton.setCallbackData(callBackPrefix + ":" + entityArrayList.get(counter).getId());
                    row.add(bookButton);
                    rows.add(row);
                    counter++;
                }}
            row = getPagesSelector(keyboards.size(),pagesCount,b);

            rows.add(row);
            keyboard.setKeyboard(rows);
            keyboards.add(keyboard);

        }
        return keyboards;
    }

    public static List<SendMessage> getEntitiesListViaMessageUndPagesSelector(List<Entity> entityArrayList, long chatId){


        ArrayList<SendMessage>  pages = new ArrayList<>();
        List<InlineKeyboardButton>       row;
        List<List<InlineKeyboardButton>> rows;
        int pagesCount = 1 + (entityArrayList.size()/25);
        int counter   = 0;
        for (int b = 0; b < pagesCount; b++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 25; i++){
                if (counter<entityArrayList.size()){
                    sb.append(counter+1).append(". ").append(entityArrayList.get(counter).getTextToButton()).append("\n");
                    counter++;
                }}
            SendMessage sendMessage = new SendMessage(chatId,sb.toString());
            row = getPagesSelector(pages.size(),pagesCount,b);
            if (row!=null){
                rows = new ArrayList<>();
                rows.add(row);
                sendMessage.setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(rows));
            }
            pages.add(sendMessage);
        }
        return pages;

    }

    public static List<SendMessage> getEditDriverKeyboard(List<Entity> entityArrayList, long chatId){


        ArrayList<SendMessage>  pages = new ArrayList<>();
        List<InlineKeyboardButton>       row;
        List<List<InlineKeyboardButton>> rows;
        int pagesCount = 1 + (entityArrayList.size()/25);
        int counter   = 0;
        for (int b = 0; b < pagesCount; b++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 25; i++){
                if (counter<entityArrayList.size()){
                    sb.append(counter+1).append(". ").append(entityArrayList.get(counter).getTextToButton()).append("\n");
                    counter++;
                }}
            SendMessage sendMessage = new SendMessage(chatId,sb.toString());
            row = getPagesSelectorForEditDriver(pages.size(),pagesCount,b);
            if (row!=null){
                rows = new ArrayList<>();
                rows.add(row);
                sendMessage.setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(rows));
            }
            pages.add(sendMessage);
        }
        return pages;

    }

    public static List<SendMessage> getBusesUndDriversListViaMessageUndPagesSelector(List<Bus> buses, List<Driver> drivers, long chatId){


        ArrayList<SendMessage>  pages = new ArrayList<>();
        List<InlineKeyboardButton>       row;
        List<List<InlineKeyboardButton>> rows;
        int pagesCount = 1 + (buses.size()/25);
        int counter   = 0;
        for (int b = 0; b < pagesCount; b++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 25; i++){
                if (counter<buses.size()){
                    Bus bus = buses.get(counter);
                 sb.append(counter+1)
                         .append(".Номер: ")   .append(bus.getNumber())
                         .append("\nМарка: ") .append(bus.getBrand())
                         .append("\nМодель: ").append(bus.getModel())
                         .append("\nЦвет: ")  .append(bus.getColor())
                         .append("\nВодитель: ");
                    String driverName;
                    try {
                 driverName = drivers.stream()
                         .filter(driver -> driver.getId()==bus.getDriver_id())
                         .collect(toList()).get(0).toString()+"\n\n";} catch
                            (IndexOutOfBoundsException |NullPointerException|NumberFormatException e){
                     driverName = "\n\n";
                 }
                 if (driverName.equals("\n\n")){
                    driverName = "Водитель не назначен\n\n";
                 }
                 sb.append(driverName);
                    counter++;
                }}
            SendMessage sendMessage = new SendMessage(chatId,sb.toString());
            row = getPagesSelector(pages.size(),pagesCount,b);
            if (row!=null){
                rows = new ArrayList<>();
                rows.add(row);
                sendMessage.setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(rows));
            }
            pages.add(sendMessage);
        }
        return pages;

    }

    public static InlineKeyboardMarkup getKeyboardForAgreeDelete(){
        InlineKeyboardMarkup             keyboard = new InlineKeyboardMarkup();
        List<InlineKeyboardButton>       row      = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows     = new ArrayList<>();

        InlineKeyboardButton yes = new InlineKeyboardButton();
        InlineKeyboardButton no  = new InlineKeyboardButton();

        yes.setText("Да, удалить");
        yes.setCallbackData("delete");

        no.setText("Нет, назад");
        no.setCallbackData("no, back");

        row.add(yes);
        row.add(no);
        rows.add(row);

        return keyboard.setKeyboard(rows);
    }

    public static InlineKeyboardMarkup getKeyboardForEditBuses(){
        InlineKeyboardMarkup             keyboard = new InlineKeyboardMarkup();
        List<InlineKeyboardButton>       row      = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows     = new ArrayList<>();

        InlineKeyboardButton editNumber = new InlineKeyboardButton();
        InlineKeyboardButton editBrand  = new InlineKeyboardButton();
        InlineKeyboardButton editModel  = new InlineKeyboardButton();
        InlineKeyboardButton editColor  = new InlineKeyboardButton();
        InlineKeyboardButton backOff    = new InlineKeyboardButton();

        editNumber.setText("Изменить номер");
        editNumber.setCallbackData("number");
        row.add(editNumber);
        rows.add(row);

        row = new ArrayList<>();
        editBrand.setText("Изменить марку");
        editBrand.setCallbackData("brand");
        row.add(editBrand);
        rows.add(row);

        row = new ArrayList<>();
        editModel.setText("Изменить модель");
        editModel.setCallbackData("model");
        row.add(editModel);
        rows.add(row);

        row = new ArrayList<>();
        editColor.setText("Изменить цвет");
        editColor.setCallbackData("color");
        row.add(editColor);
        rows.add(row);

        row = new ArrayList<>();
        backOff.setText("Назад");
        backOff.setCallbackData("backOff");
        row.add(backOff);
        rows.add(row);

        return keyboard.setKeyboard(rows);
    }

    public static ArrayList<InlineKeyboardMarkup> getPagesForReorderKidsInBusInAdminMenu(List<Kid> entityArrayList){
        ArrayList<InlineKeyboardMarkup>  keyboards = new ArrayList<>();
        List<InlineKeyboardButton>       row;
        List<List<InlineKeyboardButton>> rows;
        int pagesCount = 1 + (entityArrayList.size()/25);
        int counter   = 0;
        for (int b = 0; b < pagesCount; b++) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            rows = new ArrayList<>();
            for (int i = 0; i < 25; i++){
                if (counter<entityArrayList.size()){
                    row                             = new ArrayList<>();
                    InlineKeyboardButton bookButton = new InlineKeyboardButton();
                    bookButton.setText(entityArrayList.get(counter).getTextToButton());
                    bookButton.setCallbackData("kid" + ":" + entityArrayList.get(counter).getId());
                    row.add(bookButton);
                    rows.add(row);
                    counter++;
                }}

            row = new ArrayList<>();
            InlineKeyboardButton reorder = new InlineKeyboardButton();
            reorder.setText("Изменить порядок");
            reorder.setCallbackData("reorder");
            row.add(reorder);
            rows.add(row);

            row = getPagesSelector(keyboards.size(),pagesCount,b);

            rows.add(row);
            keyboard.setKeyboard(rows);
            keyboards.add(keyboard);

        }
        return keyboards;
    }

    public static ArrayList<InlineKeyboardMarkup> getPagesForCoordinatorToSchoolMenu(List<Kid> entityArrayList){
        ArrayList<InlineKeyboardMarkup>  keyboards = new ArrayList<>();
        List<InlineKeyboardButton>       row;
        List<List<InlineKeyboardButton>> rows;
        int pagesCount = 1 + (entityArrayList.size()/25);
        int counter   = 0;
        for (int b = 0; b < pagesCount; b++) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            rows = new ArrayList<>();
            for (int i = 0; i < 25; i++){
                if (counter<entityArrayList.size()){
                    row                             = new ArrayList<>();
                    InlineKeyboardButton bookButton = new InlineKeyboardButton();
                    bookButton.setText(entityArrayList.get(counter).getTextToButton());
                    bookButton.setCallbackData("kid" + ":" + entityArrayList.get(counter).getId());
                    row.add(bookButton);
                    rows.add(row);
                    counter++;
                }}

            row = new ArrayList<>();
            InlineKeyboardButton reorder = new InlineKeyboardButton();
            reorder.setText("Порядок");
            reorder.setCallbackData("reorder");
            row.add(reorder);

            InlineKeyboardButton add = new InlineKeyboardButton();
            add.setText("Добавить");
            add.setCallbackData("add");
            row.add(add);
            rows.add(row);

            row = new ArrayList<>();
            InlineKeyboardButton Delete = new InlineKeyboardButton();
            Delete.setText("Удалить");
            Delete.setCallbackData("delete");
            row.add(Delete);

            InlineKeyboardButton transplant = new InlineKeyboardButton();
            transplant.setText("Пересадить");
            transplant.setCallbackData("transplant");
            row.add(transplant);
            rows.add(row);

            row = getPagesSelector(keyboards.size(),pagesCount,b);

            rows.add(row);
            keyboard.setKeyboard(rows);
            keyboards.add(keyboard);

        }
        return keyboards;
    }

    public static ArrayList<InlineKeyboardMarkup> getPagesForCoordinatorToHomeMenu(List<Kid> entityArrayList){
        ArrayList<InlineKeyboardMarkup>  keyboards = new ArrayList<>();
        List<InlineKeyboardButton>       row;
        List<List<InlineKeyboardButton>> rows;
        int pagesCount = 1 + (entityArrayList.size()/25);
        int counter   = 0;
        for (int b = 0; b < pagesCount; b++) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            rows = new ArrayList<>();
            for (int i = 0; i < 25; i++){
                if (counter<entityArrayList.size()){
                    row                             = new ArrayList<>();
                    InlineKeyboardButton bookButton = new InlineKeyboardButton();
                    bookButton.setText(entityArrayList.get(counter).getTextToButton());
                    bookButton.setCallbackData("kid" + ":" + entityArrayList.get(counter).getId());
                    row.add(bookButton);
                    rows.add(row);
                    counter++;
                }}

            row = new ArrayList<>();
            InlineKeyboardButton reorder = new InlineKeyboardButton();
            reorder.setText("Изменить порядок");
            reorder.setCallbackData("reorder");
            row.add(reorder);

            InlineKeyboardButton transplant = new InlineKeyboardButton();
            transplant.setText("Пересадить");
            transplant.setCallbackData("transplant");
            row.add(transplant);
            rows.add(row);

            row = new ArrayList<>();
            InlineKeyboardButton add = new InlineKeyboardButton();
            add.setText("Добавить ученика");
            add.setCallbackData("add");
            row.add(add);

            InlineKeyboardButton Delete = new InlineKeyboardButton();
            Delete.setText("Удалить ученика");
            Delete.setCallbackData("remove");
            row.add(Delete);
            rows.add(row);

            row = new ArrayList<>();
            InlineKeyboardButton removeAllList = new InlineKeyboardButton();
            removeAllList.setText("Удалить список");
            removeAllList.setCallbackData("deleteList");
            row.add(removeAllList);

            InlineKeyboardButton giveListToDriver = new InlineKeyboardButton();
            giveListToDriver.setText("Дать водителю");
            giveListToDriver.setCallbackData("giveToDriver");
            row.add(giveListToDriver);
            rows.add(row);


            row = getPagesSelector(keyboards.size(),pagesCount,b);

            rows.add(row);
            keyboard.setKeyboard(rows);
            keyboards.add(keyboard);

        }
        return keyboards;
    }


    @SuppressWarnings("Duplicates")
    public static ArrayList<InlineKeyboardMarkup> getPagesForRemoveDrivers(List<Entity> entityArrayList, String
            callBackPrefix){
        ArrayList<InlineKeyboardMarkup>  keyboards = new ArrayList<>();
        List<InlineKeyboardButton>       row;
        List<List<InlineKeyboardButton>> rows;
        int pagesCount = 1 + (entityArrayList.size()/25);
        int counter   = 0;
        for (int b = 0; b < pagesCount; b++) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            rows = new ArrayList<>();
            for (int i = 0; i < 25; i++){
                if (counter<entityArrayList.size()){
                    row                             = new ArrayList<>();
                    InlineKeyboardButton bookButton = new InlineKeyboardButton();
                    bookButton.setText(entityArrayList.get(counter).getTextToButton());
                    bookButton.setCallbackData(callBackPrefix + ":" + entityArrayList.get(counter).getId());
                    row.add(bookButton);
                    rows.add(row);
                    counter++;
                }}
            row = new ArrayList<>();

            if(keyboards.size()!=0){
                InlineKeyboardButton buttonToPreviousPage = new InlineKeyboardButton();
                buttonToPreviousPage.setText("Предыдущая страница");
                buttonToPreviousPage.setCallbackData("previousPage");
                row.add(buttonToPreviousPage);}

            InlineKeyboardButton back = new InlineKeyboardButton();
            back.setText("Назад");
            back.setCallbackData("back");
            row.add(back);


            if(pagesCount>(b+1)){
                InlineKeyboardButton buttonToNextPage = new InlineKeyboardButton();
                buttonToNextPage.setText("Следующая страница");
                buttonToNextPage.setCallbackData("nextPage");
                row.add(buttonToNextPage);
            }

            InlineKeyboardButton remove = new InlineKeyboardButton();
            remove.setText("Удалить");
            remove.setCallbackData("remove");
            row.add(remove);

            rows.add(row);
            keyboard.setKeyboard(rows);
            keyboards.add(keyboard);

        }
        return keyboards;
    }

    public static List<SendMessage> getParentsWithDeleteUndAddButtons(List<Parent> entityArrayList, long chatId){
        ArrayList<SendMessage>  pages = new ArrayList<>();
        List<InlineKeyboardButton>       row;
        List<List<InlineKeyboardButton>> rows;
        int pagesCount = 1 + (entityArrayList.size()/25);
        int counter   = 0;
        for (int b = 0; b < pagesCount; b++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 25; i++){
                if (counter<entityArrayList.size()){
                    sb.append(counter+1).append(". ").append(entityArrayList.get(counter).getTextToButton()).append("\n");
                    counter++;
                }}
            SendMessage sendMessage = new SendMessage(chatId,sb.toString());

            rows = new ArrayList<>();
            row  = new ArrayList<>();
            InlineKeyboardButton delete = new InlineKeyboardButton();
            delete.setText("Удалить");
            delete.setCallbackData("deleteParent");
            InlineKeyboardButton add    = new InlineKeyboardButton();
            add.setText("Добавить родителя");
            add.setCallbackData("addParent");
            row.addAll(asList(delete,add));
            rows.add(row);

            row = new ArrayList<>();
            if(pages.size()!=0){
                InlineKeyboardButton buttonToPreviousPage = new InlineKeyboardButton();
                buttonToPreviousPage.setText("Предыдущая страница");
                buttonToPreviousPage.setCallbackData("previousPage");
                row.add(buttonToPreviousPage);}

            InlineKeyboardButton backOff    = new InlineKeyboardButton();
            backOff.setText("Назад");
            backOff.setCallbackData("backOff");
            row.add(backOff);

            if(pagesCount>(b+1)){
                InlineKeyboardButton buttonToNextPage = new InlineKeyboardButton();
                buttonToNextPage.setText("Следующая страница");
                buttonToNextPage.setCallbackData("nextPage");
                row.add(buttonToNextPage);
            }
            rows.add(row);

//            if (row!=null){
//                rows = new ArrayList<>();
//                rows.add(row);
                sendMessage.setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(rows));
//            }
            pages.add(sendMessage);
        }
        return pages;

    }


    
    public static InlineKeyboardMarkup getKeyboardForEditKids(){
        InlineKeyboardMarkup             keyboard = new InlineKeyboardMarkup();
        List<InlineKeyboardButton>       row      = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows     = new ArrayList<>();

        InlineKeyboardButton editName   = new InlineKeyboardButton();
        InlineKeyboardButton editSchoolId   = new InlineKeyboardButton();
        InlineKeyboardButton editPhoto  = new InlineKeyboardButton();
        InlineKeyboardButton editParents  = new InlineKeyboardButton();
        InlineKeyboardButton backOff    = new InlineKeyboardButton();

        editName.setText("Изменить имя и фамилию");
        editName.setCallbackData("name");
        row.add(editName);
        rows.add(row);

        row = new ArrayList<>();
        editSchoolId.setText("Изменить школу");
        editSchoolId.setCallbackData("school_id");
        row.add(editSchoolId);
        rows.add(row);

        row = new ArrayList<>();
        editPhoto.setText("Изменить фото");
        editPhoto.setCallbackData("photo");
        row.add(editPhoto);
        rows.add(row);

        row = new ArrayList<>();
        editParents.setText("Изменить родителей");
        editParents.setCallbackData("parents");
        row.add(editParents);
        rows.add(row);

        row = new ArrayList<>();
        backOff.setText("Назад");
        backOff.setCallbackData("backOff");
        row.add(backOff);
        rows.add(row);

        return keyboard.setKeyboard(rows);
    }

    private static List<InlineKeyboardButton> getPagesSelectorForEditDriver(int pagesSize, int pagesCount, int pageNow ){

        ArrayList<InlineKeyboardButton> row = new ArrayList<>();

        if(pagesSize!=0){
            InlineKeyboardButton buttonToPreviousPage = new InlineKeyboardButton();
            buttonToPreviousPage.setText("Предыдущая страница");
            buttonToPreviousPage.setCallbackData("previousPage");
            row.add(buttonToPreviousPage);}

        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData("back");
        row.add(back);

        InlineKeyboardButton changeSchool = new InlineKeyboardButton();
        changeSchool.setText("Прикрепить к другой школе");
        changeSchool.setCallbackData("change");
        row.add(changeSchool);


        if(pagesCount>(pageNow+1)){
            InlineKeyboardButton buttonToNextPage = new InlineKeyboardButton();
            buttonToNextPage.setText("Следующая страница");
            buttonToNextPage.setCallbackData("nextPage");
            row.add(buttonToNextPage);
        }
        return row;
    }

    private static List<InlineKeyboardButton> getPagesSelector(int pagesSize, int pagesCount, int pageNow ){

        ArrayList<InlineKeyboardButton> row = new ArrayList<>();

        if(pagesSize!=0){
            InlineKeyboardButton buttonToPreviousPage = new InlineKeyboardButton();
            buttonToPreviousPage.setText("Предыдущая страница");
            buttonToPreviousPage.setCallbackData("previousPage");
            row.add(buttonToPreviousPage);}

        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData("back");
        row.add(back);


        if(pagesCount>(pageNow+1)){
            InlineKeyboardButton buttonToNextPage = new InlineKeyboardButton();
            buttonToNextPage.setText("Следующая страница");
            buttonToNextPage.setCallbackData("nextPage");
            row.add(buttonToNextPage);
        }
            return row;
    }

    public static ReplyKeyboardMarkup getKeyboardForMorningRoute(){
        ReplyKeyboardMarkup keyboardMarkup  = new ReplyKeyboardMarkup();
        List<KeyboardRow>   rowsList        = new ArrayList<>();
        KeyboardRow         keyboardRow     = new KeyboardRow();
        KeyboardButton buttonChildIn = new KeyboardButton();
        buttonChildIn.setRequestLocation(true);
        buttonChildIn.setText("Сел");

        KeyboardButton buttonChildSkipped = new KeyboardButton();
        buttonChildSkipped.setText("Не сел");

        keyboardRow.add(buttonChildIn);
        keyboardRow.add(buttonChildSkipped);
        rowsList.add(keyboardRow);

        KeyboardButton setList = new KeyboardButton();
        setList.setText("Посмотреть список");
        keyboardRow = new KeyboardRow();
        keyboardRow.add(setList);



        rowsList.add(keyboardRow);
        return keyboardMarkup.setKeyboard(rowsList);
    }

    public static ReplyKeyboardMarkup getKeyboardForEveningRoute(){
        ReplyKeyboardMarkup keyboardMarkup  = new ReplyKeyboardMarkup();
        List<KeyboardRow>   rowsList        = new ArrayList<>();
        KeyboardRow         keyboardRow     = new KeyboardRow();
        KeyboardButton buttonChildIn = new KeyboardButton();
        buttonChildIn.setRequestLocation(true);
        buttonChildIn.setText("Вышел");
        keyboardRow.add(buttonChildIn);
        rowsList.add(keyboardRow);

        KeyboardButton setList = new KeyboardButton();
        setList.setText("Посмотреть список");
        keyboardRow = new KeyboardRow();
        keyboardRow.add(setList);



        rowsList.add(keyboardRow);
        return keyboardMarkup.setKeyboard(rowsList);
    }

}
