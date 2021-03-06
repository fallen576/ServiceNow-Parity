package com.snp.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.log4j2.Log4J2LoggingSystem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snp.db.JdbcRepo;
import com.snp.entity.Module;
import com.snp.service.ModuleService;
import com.snp.service.UserService;

@RestController
public class API {
	
	private static final Logger LOG =
	        Logger.getLogger(JdbcRepo.class.getPackage().getName());
	
	@Autowired
	private ModuleService modService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JdbcRepo db;
	
	@RequestMapping(value="/api/v1/modules", method = RequestMethod.GET)
	@ResponseBody
	public Iterable<Module> loadModules() {
		
		return modService.findAll();
	}
	
	@GetMapping("/api/v1/table/{table_name}")
	public ResponseEntity<String> loadTable(@PathVariable(value="table_name") String table) {
		try {
			return new ResponseEntity<>(db.findAll(table).toLowerCase(), HttpStatus.OK);
		}
		catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/api/v1/table/create", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> createTable(@RequestBody String body) throws JSONException, Exception {
		try {
			db.createTable(new JSONObject(body));
		}
		catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>("good", HttpStatus.OK);
	}
	
	private List<Module> _loadModules() {
		List<Module> modules = new ArrayList<>();
		modService.findAll().forEach(modules::add);
		return modules;
	}
}
