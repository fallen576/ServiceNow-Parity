package com.snp.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.snp.controller.AMB;
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
import com.snp.security.IAuthenticationFacade;
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
	
	@Autowired
    private IAuthenticationFacade auth;

	@Autowired
	private AMB amb;
	
	@RequestMapping(value="/api/v1/modules", method = RequestMethod.GET)
	@ResponseBody
	public Iterable<Module> loadModules() {
		return modService.findAll();
	}
	
	@PostMapping(path = "/api/v1/update/{table_name}/{sys_id}", 
    consumes = MediaType.APPLICATION_JSON_VALUE, 
    produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> updateRecord(@PathVariable(value="table_name") String table,
			@PathVariable(value="sys_id") String id,
			@RequestBody HashMap<String, Object> fields) {
		
		Map<String, String> resp = new HashMap<String, String>();
		resp.put("success", db.updateRecord(fields, table, id));

		//need sys_id for front end display
		fields.put("SYS_ID", id);
		fields.put("updatedBy", auth.getAuthentication().getName());

		amb.trigger(fields, "update");
		if (table.equals("modules")) {
			amb.trigger(fields, "updateModule");
		}

		return resp;
	}
	
	@PostMapping(path = "/api/v1/userpreference/{table_name}", 
    consumes = MediaType.APPLICATION_JSON_VALUE, 
    produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> setFields(@PathVariable(value="table_name") String table, @RequestBody String[] fields) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("Success", "true");
		map.put("User", auth.getAuthentication().getName());
		map.put("table", table);
		
		String username = auth.getAuthentication().getName();
		
		//ensure fields are accurate. Otherwise would be able to manipulate sql statement
		Collection<String> realFields = db.getColumns(table, true).stream().map(String::toLowerCase).collect(Collectors.toList());
		LinkedList<String> newFields = new LinkedList<String>(Arrays.asList(fields));
		
		newFields.removeIf(x -> (!realFields.contains(x)));
		
		db.setUserPrefFields(username, table,  newFields.toArray(new String[newFields.size()]));
		
		List<String> results = db.getUserPrefFields(auth.getAuthentication().getName(), table);
		map.put("ans", results.toString());
		
		return map;
	}
	
	@GetMapping("/api/v1/fields/{table_name}/checked")
	public ResponseEntity<List<String>> getCheckedFields(@PathVariable(value="table_name") String table) {
		List<String> checked = db.getUserPrefFields(auth.getAuthentication().getName(), table);
		return new ResponseEntity<List<String>>(checked, HttpStatus.OK);
	}
	
	@GetMapping("/api/v1/fields/{table_name}")
	public ResponseEntity<List<String>> getFields(@PathVariable(value="table_name") String table) {
		List<Map<String, Object>> fieldQuery = db.getFields(table);
		List<String> fields = new ArrayList<String>();
		
		for (Map<String, Object> map : fieldQuery) {
		    for (Map.Entry<String, Object> entry : map.entrySet()) {
		        String key = entry.getKey();
		        
		        if (key.toLowerCase().equals("field")) {
		        	fields.add(entry.getValue().toString().toLowerCase());
		        }
		    }
		}
		//ArrayList<String> fields = new ArrayList<String>();
		//fields.add("sys_id");
		//fields.add("field2_goeS_HERE");
		return new ResponseEntity<List<String>>(fields, HttpStatus.OK);
		
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
}
