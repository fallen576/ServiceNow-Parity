$(document).ready( function () {
    $('#table').DataTable();
    /*
    $.get({ url: "Nav",
        context: document.body,
        success: function(data){
           	console.log(data);
           	data = JSON.parse(data);
           	var items = [];
           	for (var i in data) {
           		console.log("module = " + data[i].module_name + " tableName " + data[i].tableName);
           		var table = data[i].tableName;
           		var module = data[i].module_name;
           		
           		items.push('<li class="nav-item"><a class="nav-link" name="table" href="/SNP/table?table_name=' + table + '">' + module + '</a></li>');
           	}
           	$('#modules').append(items.join(''));
        },
        error: function(){
			alert("There was an issue getting the courses");
        }
    });
    */
});