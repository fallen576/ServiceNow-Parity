package com.snp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@Controller
public class AMB {
	
	private static CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
	
	
	@GetMapping("/sse")
	public SseEmitter _emit(HttpServletResponse response, Map<?, ?> payload) throws Exception {
		response.setHeader("Cache-Control", "no-store");
		
		SseEmitter emitter = new SseEmitter(86400000L);

        emitters.add(emitter);
        
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
       
        return emitter;
	}
	
	public void trigger(Map<?,?> payload, String eventName) {
		List<SseEmitter> deadEmitters = new ArrayList<>();
		
		emitters.forEach(emitter -> {
		      try {
		        //String json = new ObjectMapper().writeValueAsString(payload);
		        SseEventBuilder builder = SseEmitter.event()
		        									.name(eventName)
		        									.data(payload);
		        emitter.send(builder);
		      }
		      catch (Exception e) {
		        deadEmitters.add(emitter);
		      }
		    });

		    emitters.removeAll(deadEmitters);
	}
}
