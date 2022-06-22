package com.snp.rhino;

import java.util.logging.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.snp.db.JdbcRepo;
import java.util.logging.Level;
public class GlideRecord {
	
	private static final Logger LOG = 
			Logger.getLogger(JdbcRepo.class.getPackage().getName());
	
	public GlideRecord() {
		
	}
	
	public String Print(String code) {
		
		String answer = "";
		Context ctx = Context.enter();
		
		try {
			
			Scriptable scope = ctx.initStandardObjects();
			Object result = ctx.evaluateString(scope, code, "<code>", 1, null);
			answer = result.toString();
		} catch (Exception e) {
			
			LOG.log(Level.SEVERE, e.toString());
			answer = e.toString();
			
		} finally {
			
			Context.exit();
			
		}
		return answer;
	}
}
