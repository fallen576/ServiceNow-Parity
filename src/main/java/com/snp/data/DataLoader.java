package com.snp.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.snp.entity.Module;
import com.snp.entity.User;
import com.snp.service.ModuleService;
import com.snp.service.UserService;

@Component
public class DataLoader implements ApplicationRunner {

    private UserService userService;
    private ModuleService modService;

    @Autowired
    public DataLoader(UserService userService, ModuleService modService) {
        this.userService = userService;
        this.modService = modService;
    }

	@Override
	public void run(ApplicationArguments args) throws Exception {		
		
		this.modService.save(new Module("Users", "users"));
		this.modService.save(new Module("Settings", "settings"));
		
		this.userService.save(new User("admin", "admin", "admin"));
		this.userService.save(new User("Ben", "Gianni", "BenGianni"));
	}
}