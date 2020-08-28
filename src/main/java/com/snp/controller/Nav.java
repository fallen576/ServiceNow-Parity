package com.snp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snp.entity.Module;
import com.snp.service.ModuleService;

@Controller
public class Nav {
	@Autowired
	private ModuleService modService;
	
	@RequestMapping(value="/Nav", method = RequestMethod.GET)
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
	
	@GetMapping("/table/{table_name}")
	public ModelAndView loadTable(Model model, @PathVariable(value="table_name") String table) {
		model.addAttribute("modules", (List<Module>) modService.findAll());		
		Map<String, Object> params = new HashMap<>();
	    params.put("table", table);
		return new ModelAndView("home", params);
	}
	
	@GetMapping("/")
	public String loadTable(Model model) {
		model.addAttribute("modules", this._loadModules());
		return "home";
	}
	
	private List<Module> _loadModules() {
		List<Module> modules = new ArrayList<>();
		modService.findAll().forEach(modules::add);
		return modules;
	}
}
