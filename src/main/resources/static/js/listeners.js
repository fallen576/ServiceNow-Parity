(function () {
    //Listen for updates
    var sse = new EventSource('/sse');
    
	sse.addEventListener("update", (event) => {
		console.log("event " + event.data);
		
		var json = JSON.parse(event.data);

		if ($('#SYS_ID').val() == json.SYS_ID) {
		    var updatedFields = false;
			for (var i in json) {
			    var val = $('#'+i).val();
				if (i !== "updatedBy" && val != json[i] && val != undefined) {
				    console.log("i " + i + " val " + val + " json[i] " + json[i]);
				    updatedFields = true;
					$('#'+i).val(json[i]);
					$("#"+i).css({"background-color":"lightblue"})
				}
			}
			if (updatedFields) {
			    $('#alert-suc').text("Record updated by " + json.updatedBy + " just now.");
    			$('#alert-suc').slideDown();
                setTimeout(() => {
                    $('#alert-suc').slideUp();
                    $('#alert-suc').text("Success");
                }, 2500);
			}
		}
	});

	sse.addEventListener("insertModule", (event) => {
		console.log("module updated " + event.data);
		
		var json = JSON.parse(event.data);
		var key = Object.keys(json);
		console.log(key + " " + json[key]);
		
		$("#modules").append('<li><a href="/table/'+json[key]+'_list.do"><span class="nav-link">'+key+'</span></a></li>');
		
	});
})();