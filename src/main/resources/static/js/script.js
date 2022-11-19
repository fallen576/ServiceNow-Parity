$(document).ready( function () {
    $('#table').DataTable({
    	"columnDefs": [
    		{ "width": "10px", "targets": 0 }	
  		]
    });
    
    $('.icon-pop').popover({
    	 trigger: "hover",
    	  html: true,
    	   delay: {
    		show: 250
  		   },
  		  resize: true
  	});
    
    
    $(".icon-pop").hover(function () {
		var id = $(this).attr('id');
		var table = location.href.split("table/")[1].split("_list.do")[0];
		//console.log($(this).attr('id'));
		$.ajax({
		    url: "/popover/"+table+"/"+id,
		    type: "GET",
		 }).done(function(data) {
		 	console.log(data);
		 	$("#"+id).attr('data-content', data);
		 	//$('#'+id).popover({ trigger: "hover", html: true });
		 });
	}, 
	function () {
	    //stuff to do on mouse leave
	});
	
	$.typeahead({
		input: ".js-typeahead",
		order: "asc",
		highlight: true,
		accent: true,
	    dynamic: true,
	    filter: false,
	    delay: 300,
	    template: function (query, item) {
	    	if (item.data) {
				let s = "<ul>";
				let cardBody = '<div class="card"><ul class="list-group list-group-flush">';
				cardBody += '<li class="list-group-item"> table - ' + item.table + '</li>'
	    		var obj = item.data;
	    		for (var i in obj){
					s+= `<li>${i} - ${obj[i]}</li>`
					cardBody += '<li class="list-group-item"> ' + i + ' - ' + obj[i]  + '</li>'
	    		}
				s += "</ul>"
				cardBody += "</ul></div>";
				return cardBody;
	    		//return s;
	    	}
	    	return '<p>{{table}}</p><br /><p>{{id}}</p><br /><p>{{data}}</p>'
	    },
	    href: "/table/{{table}}/{{id}}",
	    source: {
	        models: {
	            ajax: function(query) {
	                return {
	                    url: "/api/v1/esmodels/autocomplete?search=" + query
	                }
	            }
	        }
	    }
	});
	/*
	$("#global-search").on('input', function() {
		let text = $(this).val();
		$.get("/api/v1/esmodels/autocomplete?search=" + text, (data, status) => {
    		if (data.length == 0) {
    			console.log("could not find any results matching " + text);
    		}
    		else {
    			console.table(data);
    			console.log(JSON.stringify(data));
    			console.log(JSON.stringify(status));
    		}
	    });
    });
    */
});

function sendUpdateRequest() {
	var id = $('#updateForm').attr('guid');
	var table = $('#updateForm').attr('table');
    $.ajax({
        url: "/api/v1/update/"+table+"/"+id,
        type: "POST",
       	data: JSON.stringify($("#updateForm :input[updateable='true']").serializeArray().reduce(function(m,o){  m[o.name] = o.value; return m;}, {})),
  	   contentType:"application/json"
     }).done(function(data) {
     	$('#alert-suc').slideDown();
     	setTimeout(() => {$('#alert-suc').slideUp();}, 2500);
     });
}

function loadListControl() {
	var table = location.href.split("table/")[1].split("_list.do")[0];
    $.get("/api/v1/fields/" + table, (data, status) => {
    	$('.control').remove();
    	for (var i in data) {
 			$('#checkboxes').append('<div class="form-check control"><input class="form-check-input field-selection" type="checkbox" name="'+data[i]+'" value="'+data[i]+'" id="'+ data[i] +'"><label class="form-check-label" for="'+ data[i] +'">'+ data[i] +'</label></div>');
    		//$('#checkboxes').append('<input class="form-check-input field-selection" type="checkbox" name="'+data[i]+'" value="'+data[i]+'"/>'+ data[i] +'<br />');
    	}
    	$.get("/api/v1/fields/"+table+"/checked", (data, status) => {
    		if (data.length == 0) {
    			$('#user-pref-msg').show();
    		}
	    	for (var i in data) {
    			$('input[name="'+data[i]+'"]').prop("checked",true);
	    	}
	    });
    });
}

function selectReference(sys_id, dv) {
    var url = window.location.href;
    var id = url.split("?name=")[1];
    var ind = id.split("_")[1];
    window.top.$("#"+id).val(sys_id).change();
    window.top.$("#"+id).trigger('input');
    window.top.$("#"+id+"_dv").val(dv).change();
    window.top.$("#"+id+"_dv").trigger('input');

    //have to close in the parent window.
    parent.close(ind, sys_id, dv, window.location.href.split("reference/")[1].split("?")[0]);

}

function close() {
	 $('#exampleModal').modal('toggle');
}

function openModal(table, queryParam) {
	$('.reference-frame').attr('src', '/reference/' + table.toLowerCase() + '?name=' + queryParam);
}

function listControl() {
	var list = [];
	$('.field-selection:checkbox:checked').each(function() {
		list.push($(this).val());
	});
	var table = location.href.split("table/")[1].split("_list.do")[0];
	$.ajax({
	  url:"/api/v1/userpreference/"+table,
	  type:"POST",
	  data:JSON.stringify(list),
	  contentType:"application/json",
	}).done(function(data){
	    location.href = "/table/"+table+"_list.do";
  	});
}