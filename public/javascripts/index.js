function signup()
{
	var id = $("#newusername").val()
	var pw = $("#newpassword").val()
	var firstname = $("#firstname").val()
	var lastname = $("#lastname").val()
	var data = {"username": id, "passwd": pw, "firstname": firstname, "lastname": lastname}
	
	sendJson(data, "PUT", false)
}

function signin()
{
	var id = $("#username").val()
	var pw = $("#password").val()
	
	var data = {"username": id, "passwd": pw}
	
	sendJson(data, "POST", true)
}

function sendJson(data, method, redirect)
{
	var jsondata = JSON.stringify(data)
	
	$.ajax
	({
		type: method,
		headers:
		{
			'Accept': 'application/json',
			'Content-Type': 'application/json'
		},
		url: 'users',
		async: false,
		data: jsondata,
		success: function(result){
			if(redirect)
				window.location.assign('main');
			else
				location.reload();
		},
		statusCode:{
			409:function(){
				alert("이미 존재하는 ID입니다.");
			}
		}
	})
}