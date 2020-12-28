$(document).ready( function () {
    $('#table').DataTable();
});

function selectReference(sys_id, dv) {
    var url = window.location.href;
    var id = url.split("?name=")[1];
    var ind = id.split("_")[1];
    window.top.$("#"+id).val(sys_id);
    window.top.$("#"+id+"_dv").val(dv);
    parent.close(ind, sys_id, dv, window.location.href.split("reference/")[1].split("?")[0]);
}