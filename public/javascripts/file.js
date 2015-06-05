function fileuploadsubmit()
{
	var user_id = getCookie("userid");
	if(user_id == "")
	{
		alert("비정상적인 접근!");
		return;
	}
	
	$("#form_fileUpload").attr("action", "/files/"+user_id);
	$("#form_fileUpload").submit();
}

function filedelete(file_id)
{
	var user_id = getCookie("userid");
	if(user_id == "")
	{
		alert("asdf");
		return;
	}

	var ele = event.target.parentNode.parentNode.parentNode.parentNode.childNodes.item(1);
	var fileid_to_delete = ele.innerText
	if(confirm( fileid_to_delete +" 파일이 삭제됩니다! 계속하시겠습니까?"))
	{
		ele.parentNode.remove();
	}
	else
	{
		return;
	}
	var url = "/files/"+user_id+"/"+ file_id;

	$.ajax({
		type: "DELETE",
		url: url,
		async: false
	})
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) == 0) return c.substring(name.length,c.length);
    }
    return "";
}