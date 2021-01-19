function addRow() {
    var i = fields.length;
    $('#fields tbody').append("<tr>"+ 
        "<td>" + '<input class="form-control" onchange="save(\'fieldName\', this.value, $(this).parent().parent().parent().children().index($(this).parent().parent()))" placeholder="Field Name"/></td>'+
        "<td>" + '<select class="form-control" name="types" onchange="save(\'fieldType\', this.value)"><option value="">-- None --</option><option value="string">String</option><option value="integer">Integer</option><option value="reference">Reference</option></select>' + "</td>" +
        "<td></td><td></td>"+
        '<td><button class="btn btn-primary" onclick="addRow();">Add Field</button></td>' +
        '<td><button class="btn btn-danger" onclick="removeRow(this);">Remove Field</button></td>' +
        "</tr>");
}
function removeRow(row) {
    if (fields.length === 1) {
       // alert('Must have at least one row.');
    }
    $(row).parents("tr").remove();
}
function updateRow() {

}

function save(prop, value, rowNum) {
    console.log("value " + value + " row number " + rowNum);
}