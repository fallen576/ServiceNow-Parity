package com.snp.controller;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.snp.service.LogService;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.snp.db.JdbcRepo;
import com.snp.model.Record;

@Controller
public class Glide {
    @Autowired
    private JdbcRepo db;

    @Autowired
    private AMB amb;

    @Autowired
    private LogService LOG;

    @GetMapping("/table/{table_name}_list.do")
    public ModelAndView loadTable(Model model, @PathVariable(value = "table_name") String table) {
        if (table.equals("h2-console")) {
            return new ModelAndView("redirect:/h2-console");
        }

        model.addAttribute("table", table);

        if (table.equals("createTable")) {
            return new ModelAndView("createTable");
        }

        List<Record> records = db.normalGet(table, null);

        if (records.size() == 0) {
            model.addAttribute("message", "No data yet.");
        } else {
            model.addAttribute("records", records);
        }

        if (table.equals("modules")) {
            model.addAttribute("display", "MODULE_NAME");
        } else {
            model.addAttribute("display", db.getDisplay(table));
        }

        return new ModelAndView("home");
    }

    @GetMapping("/")
    public String loadTable(Model model) {
        return "home";
    }

    @GetMapping("/popover/{table_name}/{id}")
    public ModelAndView popoverView(Model model,
            @PathVariable(value = "table_name") String table,
            @PathVariable(value = "id") String id) {

        model.addAttribute("record", db.normalGet(table, id).get(0));
        model.addAttribute("row", db.viewRecord(table, id));
        model.addAttribute("table", table);

        return new ModelAndView("popover");
    }

    @GetMapping("/reference/{table_name}")
    public ModelAndView referenceView(Model model, @PathVariable(value = "table_name") String table) {

        List<Record> records = db.normalGet(table, null);

        if (records.isEmpty()) {
            model.addAttribute("message", "No data yet.");
        } else {
            model.addAttribute("records", records);
        }

        if (table.equals("modules")) {
            model.addAttribute("display", "MODULE_NAME");
        } else {
            model.addAttribute("display", db.getDisplay(table));
        }

        model.addAttribute("table", table);
        model.addAttribute("reference", true);

        return new ModelAndView("reference");
    }

    @GetMapping("/{table_name}.do")
    public ModelAndView newRecord(Model model, @PathVariable(value = "table_name") String table) {
        Map<String, Object> params = new HashMap<>();
        model.addAttribute("columns", db.getColumns(table, false));
        model.addAttribute("record", db.normalGetNewRecord(table));

        params.put("table", table);

        return new ModelAndView("newrecord", params);
    }

    @GetMapping("/table/{table_name}/{id}")
    public ModelAndView viewRecord(Model model,
            @PathVariable(value = "table_name") String table,
            @PathVariable(value = "id") String id) {

        model.addAttribute("record", db.normalGet(table, id).get(0));
        model.addAttribute("row", db.viewRecord(table, id));
        model.addAttribute("table", table);
        return new ModelAndView("record");
    }

    @GetMapping("/table/{table_name}")
    public ModelAndView loadView(Model model,
            @PathVariable(value = "table_name") String table,
            @RequestParam String sysparm_query) {

        boolean listView = true;

        List<NameValuePair> qParams = URLEncodedUtils.parse(sysparm_query, Charset.forName("UTF-8"));

        if (qParams.get(0).getName().equals("sys_id")) {
            listView = false;
        }

        List<Record> records = db.lookup(table, qParams);

        if (records.isEmpty()) {
            model.addAttribute("message", "No data yet.");
        } else {
            model.addAttribute("records", records);
        }

        if (table.equals("modules")) {
            model.addAttribute("display", "MODULE_NAME");
        } else {
            model.addAttribute("display", db.getDisplay(table));
        }

        model.addAttribute("table", table);

        if (listView) {
            return new ModelAndView("home");
        }

        return new ModelAndView("listview");
    }

    @PostMapping(path = "/delete/{table_name}/{sys_id}")
    public ModelAndView delete(Model model, @PathVariable(value = "table_name") String table, @PathVariable(value = "sys_id") String id) {
        LOG.info("deleting " + id + " from " + table, Glide.class.getName());
        db.delete(table, id);
        model.addAttribute("table", table);
        return new ModelAndView("redirect:/table/" + table + "_list.do");
    }

    @GetMapping("/script")
    public String backgroundScript(Model model) {
        return "backgroundScript";
    }

    @PostMapping(path = "/insert/{table_name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<HashMap<String, String>> insert(Model model, HttpServletRequest req,
            @PathVariable(value = "table_name") String table) throws JsonProcessingException {

        Map<?, ?> m = req.getParameterMap();
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
            LOG.error(e.getMessage(), Glide.class.getName());
            resp.put("error", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(resp);
        }
    }
}
