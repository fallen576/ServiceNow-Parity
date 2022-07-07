package com.snp.api;

import com.snp.db.JdbcRepo;
import com.snp.entity.Module;
import com.snp.service.ModuleService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class APITest {

    @Autowired
    private JdbcRepo db;
    @Autowired
    private ModuleService modService;

    @Test
    void loadModules() {
        List<Module> moduleList = (List<Module>)modService.findAll();
        assertFalse(!moduleList.isEmpty());//
    }

    @Test
    public void getCheckedFields() {
        db.setUserPrefFields("admin", "users", new String[]{"SYS_ID", "FIRST_NAME"});
        List<String> checked = db.getUserPrefFields("admin", "users");
        assertTrue(checked.size() == 2);
    }

    @Test
    public void getFields() {
        List<Map<String, Object>> fieldQuery = db.getFields("users");
        List<String> fields = new ArrayList<>();

        for (Map<String, Object> map : fieldQuery) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();

                if (key.equalsIgnoreCase("field")) {
                    fields.add(entry.getValue().toString().toLowerCase());
                }
            }
        }
        assertTrue(fields.size() > 1 && fields.contains("sys_id"));
    }

    @Test
    public void loadTable() {
        assertTrue(!db.findAll("users").isEmpty());

    }
}