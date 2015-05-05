/**
 * 
 */

function login(){
	var id = $("#userid").val()
	var pw = $("#userpw").val()
	
	var data = {"userid": id, "password": pw}
	
	var jsondata = JSON.stringify(data)
	
	$.ajax
	({
		type: "POST",
		headers:
		{
			'Accept': 'application/json',
			'Content-Type': 'application/json'
		},
		url: 'login',
		async: false,
		data: jsondata,
		success: function(result){
			alert(result);
		},
		error: function(request, status, error)
		{
			alert("code" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
		}
	})
}