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
				try {
					var parsed = JSON.parse(data);
					//alert(parsed.table + " " + parsed.id);
				location.href = "/table/" + parsed.table + "/" + parsed.id;
				} catch(e) {
					data = "Record has been updated."
				}
				 //console.log("Success " + JSON.stringify(data));
				
	 			$('#suc-message').text(data);
	 			$("#alert-suc").fadeIn();
	 			setTimeout(function() {
	 				$("#alert-suc").slideUp();
	 			}, 5000);
	 		},
	 		error: function(data) {
	 		console.table(data);
	 			//alert(data.responseText);
	 			$('#error-message').text(data.responseText);
	 			$("#alert-war").fadeIn();
	 			setTimeout(function() {
	 				$("#alert-war").slideUp();
	 			}, 10000);
	 		},
	 	});
	 	
	 });
 });
 