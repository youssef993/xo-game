package com.xogame.game_service;

import org.springframework.boot.SpringApplication;

public class TestGameServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(GameServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
