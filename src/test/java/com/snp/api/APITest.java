package com.snp.api;

import com.snp.data.es.model.ESLog;
import com.snp.data.es.repository.ESLogRepository;
import com.snp.db.JdbcRepo;
import com.snp.entity.Module;
import com.snp.service.ModuleService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class APITest {

    @Autowired
    private JdbcRepo db;
    @Autowired
    private ModuleService modService;
    @Autowired
    private ESLogRepository modelRepo;

    @Test
    void loadModules() {
        List<Module> moduleList = (List<Module>)modService.findAll();
        assertFalse(moduleList.isEmpty());
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
    
    @Test
    public void elasticSearchTest() {
    	//create new model
    	ESLog model = new ESLog();
    	model.setSource("test");
    	model.setText("Elastic search test");
    	model.setUser("ben");
    	model.setId("123456");
    	model = modelRepo.save(model);
    	
    	//read new model
    	Optional<ESLog> model2 = modelRepo.findById("123456");
    	assertTrue(model2.isPresent());
    }
}