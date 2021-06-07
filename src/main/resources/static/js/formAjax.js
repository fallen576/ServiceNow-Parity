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
	 			data = JSON.stringify(data);
	 			var parsed = "";
				try {
					parsed = JSON.parse(data);
					if (parsed.action == "insert") {
						$('#suc-message').text();
						$("#alert-suc").fadeIn();
						setTimeout(function() {
							$("#alert-suc").slideUp();
							location.href = "/table/" + parsed.table + "/" + parsed.id;
						}, 4000);
					
						$('#suc-message').text("Record has been created. You will now be redirected");
			 			$("#alert-suc").fadeIn();
			 			setTimeout(function() {
			 				$("#alert-suc").slideUp();
						 }, 3000);
					}	 
					else {
						$('#suc-message').text(parsed.message);
			 			$("#alert-suc").fadeIn();
			 			setTimeout(function() {
			 				$("#alert-suc").slideUp();
						 }, 3000);
					}
				} catch(e) {
					$('#error-message').text(e);
		 			$("#alert-war").fadeIn();
		 			setTimeout(function() {
		 				$("#alert-war").slideUp();
		 			}, 10000);
	 			}
				 
	 		},
	 		error: function(data) {
	 		console.table(data);
	 		$('#submit').prop('disabled', false);
	 			//alert(data.responseText);
	 			$('#error-message').text(data.responseJSON.error);
	 			$("#alert-war").fadeIn();
	 			setTimeout(function() {
	 				$("#alert-war").slideUp();
	 			}, 10000);
	 		},
	 	});
	 	
	 });
 });
 