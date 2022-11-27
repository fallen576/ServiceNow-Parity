package com.snp.api;

import com.snp.controller.AMB;
import com.snp.data.es.model.ESLog;
import com.snp.data.es.model.ESModel;
import com.snp.data.es.repository.ESLogRepository;
import com.snp.data.es.repository.ESModelRepository;
import com.snp.db.JdbcRepo;
import com.snp.entity.Module;
import com.snp.model.CreateTable;
import com.snp.security.IAuthenticationFacade;
import com.snp.service.LogService;
import com.snp.service.ModuleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class API {

	@Autowired
	private LogService LOG;

	@Autowired
	private ModuleService modService;
	
	@Autowired
	private ESLogRepository esLogRepo;

	@Autowired
	private ESModelRepository modelRepo;
	
	@Autowired
	private JdbcRepo db;
	
	@Autowired
    private IAuthenticationFacade auth;

	@Autowired
	private AMB amb;

	private static final Logger LOGGER = LogManager.getLogger(API.class);
	
	@GetMapping(value="/api/v1/modules")
	@ResponseBody
	public Iterable<Module> loadModules() {
		return modService.findAll();
	}
	
	@GetMapping(value="api/v1/eslogs/autocomplete")
    public Iterable<ESLog> autocomplete(@RequestParam String search) {
		return esLogRepo.findByText("*" + search + "*");
    }
	
	@GetMapping(value="api/v1/esmodels/autocomplete")
    public Iterable<ESModel> autoCompleteModel(@RequestParam String search) {
		return modelRepo.findByCustomQuery("*" + search + "*");
    }
	
	@GetMapping(value="api/v1/eslogs")
	public Iterable<ESLog> loadESLogs() {		
		return esLogRepo.findAll();
	}

	@GetMapping(value="api/v1/esmodels")
	public Iterable<ESModel> loadESModels() {	
		return modelRepo.findAll();
	}
	
	@GetMapping(value="api/v1/esmodels/{sys_id}")
	public ESModel loadESModels(@PathVariable(value="sys_id") String id) {	
		
		try {
			Optional<ESModel> tmp = modelRepo.findById(id);
			return modelRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			return null;	
		}
	}
	
	@PostMapping(path = "/api/v1/update/{table_name}/{sys_id}", 
    consumes = MediaType.APPLICATION_JSON_VALUE, 
    produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> updateRecord(@PathVariable(value="table_name") String table,
			@PathVariable(value="sys_id") String id,
			@RequestBody HashMap<String, Object> fields) {
		
		Map<String, String> resp = new HashMap<>();
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
		Map<String, String> map = new HashMap<>();
		map.put("Success", "true");
		map.put("User", auth.getAuthentication().getName());
		map.put("table", table);
		
		String username = auth.getAuthentication().getName();
		
		//ensure fields are accurate, otherwise would be able to manipulate sql statement
		Collection<String> realFields = db.getColumns(table, true).stream().map(String::toLowerCase).collect(Collectors.toList());
		LinkedList<String> newFields = new LinkedList<>(Arrays.asList(fields));
		
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
		List<String> fields = new ArrayList<>();
		
		for (Map<String, Object> map : fieldQuery) {
		    for (Map.Entry<String, Object> entry : map.entrySet()) {
		        String key = entry.getKey();
		        
		        if (key.equalsIgnoreCase("field")) {
		        	fields.add(entry.getValue().toString().toLowerCase());
		        }
		    }
		}
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
	public ResponseEntity<String> createTable(@RequestBody CreateTable table) throws JSONException, Exception {
		LOGGER.error("WE HERE MY MAN");
		try {
			//db.createTable(new JSONObject(table));
			db.createTable(table);
		}
		catch (Exception e) {
			LOG.error(e.toString(), API.class.getName());
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>("good", HttpStatus.OK);
	}
}
