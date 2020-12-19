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
import com.snp.service.ModuleService;
import com.snp.service.UserService;

@Controller
public class Glide {
	@Autowired
	private ModuleService modService;
	
	@Autowired
	private JdbcRepo db;
	
	private static CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
	
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
			return new ModelAndView("redirect:/createTable.html");
		}
		
		if (table.equals("h2-console")) {
			return new ModelAndView("redirect:/h2-console");
		}
		
		model.addAttribute("modules", (List<Module>) modService.findAll());		
		
		String schema = this.db.findAll(table);
		JSONArray json = new JSONArray(schema);
		String[] elementNames = null;
		ArrayList<ArrayList<String>> data2 = new ArrayList<ArrayList<String>>();
		
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		
		for (int i = 0; i < json.length(); i++) {
			JSONObject obj = json.getJSONObject(i);
			elementNames = JSONObject.getNames(obj);
			ArrayList<String> tmp = new ArrayList<String>();			
			for (String elementName : elementNames) {
				tmp.add(obj.getString(elementName));
			}
			data2.add(tmp);
		}
		
		Map<String, Object> params = new HashMap<>();
	    params.put("table", table);
	    params.put("columns", elementNames);
	    params.put("data", data2);
	    params.put("schema", json);
		return new ModelAndView("home", params);
	}
	
	@GetMapping("/")
	public String loadTable(Model model) {
		model.addAttribute("modules", this._loadModules());
		return "home";
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
		
		String schema = this.db.lookup(table, qParams);
		
		Map<String, Object> params = new HashMap<>();
		
		JSONArray json = new JSONArray(schema);
		String[] elementNames = null;
		ArrayList<ArrayList<String>> data2 = new ArrayList<ArrayList<String>>();
		
		for (int i = 0; i < json.length(); i++) {
			JSONObject obj = json.getJSONObject(i);
			elementNames = JSONObject.getNames(obj);
			ArrayList<String> tmp = new ArrayList<String>();			
			for (String elementName : elementNames) {
				tmp.add(obj.getString(elementName));
			}
			data2.add(tmp);
		}
		
		model.addAttribute("modules", this._loadModules());		
		params.put("table", table);
	    params.put("columns", elementNames);
	    params.put("data", data2);
	    params.put("schema", json);
		
		if (listView) 
			return new ModelAndView("home", params);
		
		return new ModelAndView("listview", params);
	}
	
	@PostMapping(path = "/update/{table_name}", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public HashMap<String, String> update(Model model, HttpServletRequest req,
							  @PathVariable(value="table_name") String table) throws JsonProcessingException {
		
		HashMap<String, String> response = new HashMap<>();
		Map<?, ?> m =req.getParameterMap();
        String id = this.db.updateRecord(m, table);
        response.put("sys_id", id);
        try {
        	sendSseEvent(m);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.put("message", "We ran into an error. Please try again.");
			return response;
		}
        
        response.put("message", "Successfully updated record.");
        return response;
		
	}
	

	@GetMapping("/sse")
	public SseEmitter _emit(HttpServletResponse response, Map<?, ?> payload) throws Exception {
		response.setHeader("Cache-Control", "no-store");
		
		SseEmitter emitter = new SseEmitter(86400000L);

        emitters.add(emitter);
        
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
       
        return emitter;
	}
	
	public void sendSseEvent(Map<?,?> payload) {
		List<SseEmitter> deadEmitters = new ArrayList<>();
		
		emitters.forEach(emitter -> {
		      try {
		        //String json = new ObjectMapper().writeValueAsString(payload);
		        SseEventBuilder builder = SseEmitter.event()
		        									.name("update")
		        									.data(payload);
		        emitter.send(builder);
		      }
		      catch (Exception e) {
		        deadEmitters.add(emitter);
		      }
		    });

		    emitters.removeAll(deadEmitters);
	}
	
	private List<Module> _loadModules() {
		List<Module> modules = new ArrayList<>();
		modService.findAll().forEach(modules::add);
		return modules;
	}
	
}
