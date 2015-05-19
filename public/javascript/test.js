$(function()
{
	$.ajax(
	{
		url:"/assets/javascript/test.json",
		dataType:"json",
		success:function(result)
		{
			$.each(result.filelist, function(i, d)
			{
				$('#testList ol').append('<li> + d["k"] +</li>');
			})
		}
	});
});
	