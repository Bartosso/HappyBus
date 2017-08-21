package com.bartosso.bot.entity.ProjectEntities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.api.objects.Contact;

@AllArgsConstructor
@Getter
@Setter
public class Driver implements Entity {
    private long    id;
    private String  name;
    private String  phone;
    private boolean gotBus;

    @Override
    public String getTextToButton() {
        return name;
    }

    public Driver(Contact contact){
        if (contact.getLastName()!=null){
            this.name = contact.getFirstName() +" "+ contact.getLastName();
        } else {
            this.name = contact.getFirstName();
        }
        this.id      = contact.getUserID();
        this.phone   = contact.getPhoneNumber();
    }

    @Override
    public String toString() {
        return "\nИмя фамилия: "+ name +"\nНомер телефона: "+ phone;
    }
}
