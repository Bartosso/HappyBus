package com.bartosso.bot.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by user on 1/2/17.
 */


@AllArgsConstructor
@Getter
public class Button {
    private int id;
    private String text;
    private int commandId;
    private String url;
    private boolean requestContact;

}
