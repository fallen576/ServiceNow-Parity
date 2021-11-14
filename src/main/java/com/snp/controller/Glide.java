package com.snp.controller;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snp.db.JdbcRepo;
import com.snp.entity.Module;
import com.snp.entity.User;
import com.snp.model.Field;
import com.snp.model.Record;
import com.snp.service.ModuleService;
import com.snp.service.UserService;

@Controller
public class Glide {
	@Autowired
	private ModuleService modService;
	
	@Autowired
	private JdbcRepo db;
	
	@Autowired 
	private AMB amb;
	
	//private static CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
	private static final Logger LOG =
	        Logger.getLogger(JdbcRepo.class.getPackage().getName());
	
	@RequestMapping(value="/Glide", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> loadModules() {
		//this method retrieves modules
		String json = "";
		try {
			json = new ObjectMapper().writeValueAsString(modService.findAll());
		} catch (JsonProcessingException e) {
			return new ResponseEntity<>(json,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(json,HttpStatus.OK);
	}	
	
	@GetMapping("/table/{table_name}_list.do")
	public ModelAndView loadTable(Model model, @PathVariable(value="table_name") String table) {
		
		if (table.equals("createTable")) {
			model.addAttribute("modules", (List<Module>) modService.findAll());
			model.addAttribute("table", table);
			return new ModelAndView("createTable");
		}
		
		if (table.equals("h2-console")) {
			return new ModelAndView("redirect:/h2-console");
		}
		
		List<Record> records = db.normalGet(table, null);

		if (records.size() == 0) {
			model.addAttribute("message", "No data yet.");
		}
		else {
			model.addAttribute("records", records);
		}
		
		if (table.equals("modules")) {
			model.addAttribute("display", "MODULE_NAME");
		}
		else {
			model.addAttribute("display", db.getDisplay(table));
		}
		
		model.addAttribute("modules", (List<Module>) modService.findAll());
		model.addAttribute("table", table);
		
		return new ModelAndView("home");
	}
	
	@GetMapping("/")
	public String loadTable(Model model) {
		model.addAttribute("modules", this._loadModules());
		return "home";
	}
	
	@GetMapping("/popover/{table_name}/{id}")
	public ModelAndView popoverView(Model model, 
			@PathVariable(value="table_name") String table,
			@PathVariable(value="id") String id) {
		
		model.addAttribute("record", db.normalGet(table, id).get(0));
		model.addAttribute("row", db.viewRecord(table, id));
		model.addAttribute("table", table);
		
		return new ModelAndView("popover");
	}
	
	@GetMapping("/reference/{table_name}")
	public ModelAndView referenceView(Model model, @PathVariable(value="table_name") String table) {
		
		List<Record> records = db.normalGet(table, null);

		if (records.size() == 0) {
			model.addAttribute("message", "No data yet.");
		}
		else {
			model.addAttribute("records", records);
		}
		
		
		if (table.equals("modules")) {
			model.addAttribute("display", "MODULE_NAME");
		}
		else {
			model.addAttribute("display", db.getDisplay(table));
		}

		model.addAttribute("table", table);
		model.addAttribute("reference", true);
		
		return new ModelAndView("reference");
	}
	
	@GetMapping("/{table_name}.do")	
	public ModelAndView newRecord(Model model, @PathVariable(value="table_name") String table) {
		Map<String, Object> params = new HashMap<>();
		model.addAttribute("modules", this._loadModules());
		model.addAttribute("columns", db.getColumns(table, false));
		model.addAttribute("record",db.normalGetNewRecord(table));
		
		params.put("table", table);
		
		return new ModelAndView("newrecord", params);
	}
	
	@GetMapping("/table/{table_name}/{id}")
	public ModelAndView viewRecord(Model model, 
			@PathVariable(value="table_name") String table,
			@PathVariable(value="id") String id) {
		
		model.addAttribute("modules", this._loadModules());	
		model.addAttribute("record", db.normalGet(table, id).get(0));
		model.addAttribute("row", db.viewRecord(table, id));
		model.addAttribute("table", table);
		return new ModelAndView("record");
		//return new ModelAndView("listview");
	}
	
	@GetMapping("/table/{table_name}")
	public ModelAndView loadView(Model model, 
									@PathVariable(value="table_name") String table,
									@RequestParam String sysparm_query) {
		
		boolean listView = true;
		
		List<NameValuePair> qParams = URLEncodedUtils.parse(sysparm_query, Charset.forName("UTF-8"));
		
		if (qParams.get(0).getName().equals("sys_id")) {
			listView = false;
		}

		List<Record> records = db.lookup(table, qParams);

		if (records.size() == 0) {
			model.addAttribute("message", "No data yet.");
		}
		else {
			model.addAttribute("records", records);
		}
		
		
		if (table.equals("modules")) {
			model.addAttribute("display", "MODULE_NAME");
		}
		else {
			model.addAttribute("display", db.getDisplay(table));
		}

		model.addAttribute("table", table);

		if (listView) 
			return new ModelAndView("home");
		
		return new ModelAndView("listview");
	}
	
	@PostMapping(path = "/delete/{table_name}/{sys_id}")
	public ModelAndView delete(Model model, @PathVariable(value="table_name") String table, @PathVariable(value="sys_id") String id) {
		LOG.warning("deleting " + id + " from " + table);
		db.delete(table, id);
		model.addAttribute("modules", this._loadModules());
		model.addAttribute("table", table);
		return new ModelAndView("redirect:/table/"+table+"_list.do");
	}
	
	@PostMapping(path = "/update/{table_name}", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public HashMap<String, String> update(Model model, HttpServletRequest req,
							  @PathVariable(value="table_name") String table) throws JsonProcessingException {
		
		HashMap<String, String> response = new HashMap<>();
		Map<?, ?> m =req.getParameterMap();
		LOG.info("data "  + m);
		try {
        String id = this.db.updateRecord(m, table);
        response.put("sys_id", id);
        response.put("action", "update");
        
        	amb.trigger(m, "update");
        	
        	if (table.equals("modules")) {
        		amb.trigger(m, "updateModule");
        	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.put("message", e.getMessage());
			return response;
		}
        
        response.put("message", "Successfully updated record.");
        return response;
		
	}
	
	@PostMapping(path = "/insert/{table_name}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<HashMap<String, String>> insert(Model model, HttpServletRequest req,
							  @PathVariable(value="table_name") String table) throws JsonProcessingException {
		
		Map<?, ?> m =req.getParameterMap();
		HashMap<String, String> resp = new HashMap<>();
		try {
			String id = this.db.insertRecord(m, table);
			resp.put("table", table);
			resp.put("id", id);
			resp.put("action", "insert");
			return ResponseEntity
		            .status(HttpStatus.OK)                 
		            .body(resp);
		} catch (Exception e) {
			LOG.warning(e.getMessage());
			resp.put("error", e.getMessage());
			return ResponseEntity
		            .status(HttpStatus.INTERNAL_SERVER_ERROR)                 
		            .body(resp);
		}
        
        
		
	}
	
	private List<Module> _loadModules() {
		List<Module> modules = new ArrayList<>();
		modService.findAll().forEach(modules::add);
		return modules;
	}
	
}
