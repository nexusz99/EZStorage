/**
 * 
 */
$(function() {
	var id = 'contentsbox';
	$.ajax
	({
		type: "GET",
		url: "/files",
		success: function(data){
		},
		error: function(request, status, error){
			alert("code" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
		}
	})
});