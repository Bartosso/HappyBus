package com.bartosso;

import com.bartosso.bot.Bot;
import com.bartosso.bot.Util.Reminder.Reminder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;


@SpringBootApplication
public class BotApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BotApplication.class, args);

	}

	@Override
	public void run(String... args) throws Exception {
		ApiContextInitializer.init();

		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		try {
			Bot bot           = new Bot();
			telegramBotsApi.registerBot(bot);
			Reminder reminder = new Reminder();


		} catch (TelegramApiRequestException e) {
			throw new RuntimeException(e);

		}
	}
}
