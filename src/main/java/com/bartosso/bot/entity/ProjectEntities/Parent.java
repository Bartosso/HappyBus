package com.bartosso.bot.entity.ProjectEntities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.api.objects.Contact;

/**
 * Created by user on 1/22/17.
 */

@AllArgsConstructor
@Getter @Setter
public class Parent implements Entity {
    private         long   id;
    private         String firstName;
    private         String lastName;
    private         String username;
    private         String phone;
    private final  long    childId;

    @Override
    public String getTextToButton() {
        if (lastName!=null){
            return firstName + " " + lastName;
        } else {
            return firstName;
        }
    }

    public Parent(Contact contact,long childId){
        this.phone     = contact.getPhoneNumber();
        this.firstName = contact.getFirstName();
        this.id        = contact.getUserID();
        this.childId   = childId;
        if (contact.getLastName()!=null){
            this.lastName = contact.getLastName();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nИмя: ").append(firstName);
        if (lastName!=null){
            sb.append("\nФамилия: ").append(lastName);
        }
        sb.append("\nНомер телефона: ").append(phone);
        return sb.toString();
    }
}
