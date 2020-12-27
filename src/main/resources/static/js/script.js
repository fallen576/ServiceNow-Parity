$(document).ready( function () {
    $('#table').DataTable();
});

function selectReference(sys_id) {
    var url = window.location.href;
    var id = url.split("?name=")[1];
    window.top.$("#"+id).val(sys_id);
    parent.close();
}