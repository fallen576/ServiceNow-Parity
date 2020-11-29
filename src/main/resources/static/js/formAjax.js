 $(document).ready( function () {
	 $('#updateForm').submit((e) => {
	 	//prevent original submit
	 	e.preventDefault();
	 	var form = $('#updateForm');
	 	$.ajax({
	 		type: "POST",
	 		url: form.attr('action'),
	 		data: form.serialize(),
	 		success: function(data) {
	 			console.log("Success " + JSON.stringify(data));
	 			$("#alert").fadeIn();
	 			setTimeout(function() {
	 				$("#alert").slideUp();
	 			}, 5000);
	 		},
	 		error: function(data) {
	 			alert("Error " + JSON.stringify(data));
	 		},
	 	});
	 	
	 });
 });
 