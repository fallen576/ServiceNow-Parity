package com.snp.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;
import com.snp.db.JdbcRepo;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servlet implementation class BackgroundScripts
 */
public class Script extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(Script.class.getPackage().getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Script() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub

        response.setContentType("text/plain");
        String code = request.getParameter("code");
        
        ArrayList<String> prints = new ArrayList<String>();
        String pattern = "gs\\.print\\(.*\\);";
        Matcher m = Pattern.compile(pattern).matcher(code);       
        
        while (m.find()) {  
            prints.add(m.group().replace("gs.print(", "").replace(");", ";"));
        }
        String[] arr = prints.toArray(new String[0]);
        
        LOG.info("arr length " + arr.length + " " + String.join("\n", arr));

        for (String i : arr) {
            LOG.info("i " + i);
        }
        
        code = code.replaceAll("gs.print", "java.util.logging.Logger.getLogger(\"script: \").info");
        Context ctx = Context.enter();
        try {
            Scriptable scope = ctx.initStandardObjects();
            Object result = ctx.evaluateString(scope, code, "script", 0, null);
            
            //still prints undefined
            String content = Context.toString(result);
            if (!"".equals("undefined") && content != null) {
                response.getWriter().println(content);
            }
            
            for (String j : arr) {
                Object r2 = ctx.evaluateString(scope, j, "script", 0, null);
                response.getWriter().println(Context.toString(r2));
            }
            
        } catch (RhinoException ex) {
            response.getWriter().println("rip " + ex.getMessage() + " - " + ex.getScriptStackTrace());
            StackTraceElement[] stackTrace = ex.getStackTrace();
            if (stackTrace != null) {
                for (StackTraceElement stackElement : stackTrace) {
                    response.getWriter().print(stackElement.getFileName() + " - ");
                    response.getWriter().println(stackElement.getLineNumber());
                }
            }
        } finally {
            Context.exit();
        }
    }
}
