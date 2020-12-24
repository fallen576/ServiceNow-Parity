(function () {
//Listen for updates
    var sse = new EventSource('http://localhost:8080/sse');
    
	sse.addEventListener("update", (event) => {
		console.log("event " + event.data);
		
		var json = JSON.parse(event.data);
		
		if ($('#SYS_ID').val() == json.SYS_ID[0]) {
			for (var i in json) {
				if ($('#'+i).val() != json[i][0]) {
					$('#'+i).val(json[i][0]);
					$("#"+i).css({"background-color":"lightblue"})
				}
			}
		}
	});
	
	sse.addEventListener("updateModule", (event) => {
		console.log("module updated " + event.data);
		
		var json = JSON.parse(event.data);
		
	});
	
	sse.addEventListener("insertModule", (event) => {
		console.log("module updated " + event.data);
		
		var json = JSON.parse(event.data);
		var key = Object.keys(json);
		console.log(key + " " + json[key]);
		
		$("#modules").append('<li><a href="/table/'+json[key]+'_list.do"><span class="nav-link">'+key+'</span></a></li>');
		
	});
})();