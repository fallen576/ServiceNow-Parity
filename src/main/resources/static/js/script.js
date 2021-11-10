$(document).ready( function () {
    $('#table').DataTable({
    	"columnDefs": [
    		{ "width": "10px", "targets": 0 }	
  		]
    });
    
    var table = location.href.split("table/")[1].split("_list.do")[0];
    $.get("/api/v1/fields/" + table, (data, status) => {
    	for (var i in data) {
    		$('#checkboxes').append('<input class="form-check-input field-selection" type="checkbox" name="'+data[i]+'" value="'+data[i]+'"/>'+ data[i] +'<br />');
    	}
    });
    
    $.get("/api/v1/fields/"+table+"/checked", (data, status) => {
    	for (var i in data) {
    			$('input[name="'+data[i]+'"]').prop("checked",true);
    	}
    });
});

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
	    alert("Loaded: "+ JSON.stringify(data));
  	});
}