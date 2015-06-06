// 메인 캔버스 요소 동적 중앙정렬 함수
function draw_by_size() // 
{
	var header_canvas_sub= document.getElementById('header_canvas_sub');
	height = window.innerHeight;
	if(height<800)
	{
		header_canvas_sub.style.top = 52+'%';
	}
	else
	{
		header_canvas_sub.style.top = 48+'%';
	}
}
draw_by_size();
// 브라우저 크기가 변할 시 동적으로 위치 조절
window.addEventListener('resize', draw_by_size);

// 하단 정보창 생성 함수
$(function()
{
	$('#btn_call_navbar_bottom').click(function()
	{
		$('body').append(
			"<div class=\"container_navbar_bottom alert alert-info alert-dismissible fade in\" role=\"alert\">"+
	     	"<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-label=\"Close\"><span aria-hidden=\"true\">×</span></button>"+
      		"<span class=\"glyphicon glyphicon-exclamation-sign\"></span>"+
      		"&nbsp;<strong>Oops!</strong>&nbsp;&nbsp; 본 서비스는 1680*1050 이상의 해상도에 최적화 되어있으며, <strong>최신버전의 Chrome</strong> 이외의 브라우저에서는 일부 기능이 제한될 수도 있습니다."+
	    "</div>");
	});
});

// 로그인셋 요소 동적 중앙정렬 함수
function deploy_on_center() //
{
	var centerd_loginset = document.getElementById('container_loginset');
	centerd_loginset.style.left = window.innerWidth/2 - 270 + 'px';
	var height = window.innerHeight/2.3;
	if(height < 300)
	{
		height = 300;
	}
	centerd_loginset.style.top = height + height/2.5 +'px';
}
deploy_on_center();
// 브라우저 크기가 변할 시 동적으로 위치 조절
window.addEventListener('resize', deploy_on_center);

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
	var firstname = $("#newfirstname").val();
	var lastname = $("#newlastname").val();
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
			},
			403: function() {
				alert("없는 아이디이거나, 패스워드가 틀렸습니다.");
			}
		}
	});
}