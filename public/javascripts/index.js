// 비밀번호 확인 함수
$(function()
{
	$('#newpassword').keyup(function()
	{
		if($('#newpassword').val() == $('#confirm_newpassword').val())
		{
			$('.has-error').addClass('has-success');
			$('.has-error').removeClass('has-error');
		}
		else
		{
			$('.has-success').addClass('has-error');
			$('.has-success').removeClass('has-success');
		}
	});
	$('#confirm_newpassword').keyup(function()
	{
		if($('#newpassword').val() == $('#confirm_newpassword').val())
		{
			$('.has-error').addClass('has-success');
			$('.has-error').removeClass('has-error');
		}
		else
		{
			$('.has-success').addClass('has-error');
			$('.has-success').removeClass('has-success');
		}
	});
});

function signup()
{
	var id = $("#newusername").val();
	var pw = $("#newpassword").val();
	var firstname = $("#firstname").val();
	var lastname = $("#lastname").val();
	var data = {"type": 1, "username": id, "passwd": pw, "firstname": firstname, "lastname": lastname};
	
	sendJson(data, "PUT");
}

function signin()
{
	var id = $("#username").val();
	var pw = $("#password").val();
	
	var data = {"type": 1, "username": id, "passwd": pw};
	
	sendJson(data, "POST");
}

function signout()
{
	var data = {"type": 2};
	
	sendJson(data, "POST");
}

function sendJson(data, method)
{
	var jsondata = JSON.stringify(data);
	
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
			location.reload();
		},
		statusCode:{
			409:function(){
				alert("이미 존재하는 ID입니다.");
			}
		}
	});
}