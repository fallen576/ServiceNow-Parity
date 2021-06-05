 $(document).ready( function () {
	$('#submit').prop('disabled', false);
	 $('#updateForm').submit((e) => {
	 	//prevent original submit
		$('#submit').prop('disabled', true);
	 	e.preventDefault();	
	 	var form = $('#updateForm');
	 	$.ajax({
	 		type: "POST",
	 		url: form.attr('action'),
	 		data: form.serialize(),
	 		success: function(data) {
				//pretty ugly, definitely needs changed.. also when there is an error it doesn't show now..
				try {	
					//insert
					var parsed = JSON.parse(data);
					//alert(parsed.table + " " + parsed.id);
					$('#suc-message').text();
					$("#alert-suc").fadeIn();
					setTimeout(function() {
						$("#alert-suc").slideUp();
						location.href = "/table/" + parsed.table + "/" + parsed.id;
					}, 4000);
				
				} catch(e) {
					//update
					data = "Record has been updated."
				}
				 //console.log("Success " + JSON.stringify(data));
				
	 			$('#suc-message').text("Record has been created. You will now be redirected");
	 			$("#alert-suc").fadeIn();
	 			setTimeout(function() {
	 				$("#alert-suc").slideUp();
				 }, 3000);
				 
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
 