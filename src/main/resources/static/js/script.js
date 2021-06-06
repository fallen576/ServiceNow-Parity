$(document).ready( function () {
    $('#table').DataTable({
    	"columnDefs": [
    		{ "width": "10px", "targets": 0 }	
  		]
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