$(function()
{
	$.ajax(
	{
		url:"/assets/javascript/test.json",
		dataType:"json",
		type:"GET"
		success:function(result)
		{
			alert(result);
			$.each(result.filelist, function(i, d)
			{
				$('#testList ol').append('<li> + d["k"] +</li>');
			})
		}
	});
});
	