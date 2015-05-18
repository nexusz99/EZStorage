function resize_fileGrid() // container_fileSystemGrid의 너비를 윈도우 크기에 맞게 리사이징
{
    var cont_fileDrive = document.getElementById('container_fileDriveGrid');
    cont_fileDrive.style.width = window.innerWidth - 300 + 'px';
    
    var cont_fileSystem = document.getElementById('container_fileSystemGrid');
    cont_fileSystem.style.height = window.innerHeight - 175 + 'px';
}
    resize_fileGrid();
    // 브라우저 크기가 변할 시 동적으로 사이즈를 조절
    window.addEventListener('resize', resize_fileGrid);
    

// 크롬 브라우저의 fakepath의 출력 해결    
$(function()
{
	$('#btn_fileUpload').change(function()
	{
		$('#name_fileUpload').val(this.files && this.files.length ? this.files[0].name : this.value.replace(/^C:\\fakepath\\/i, ''));
	});
});


// 태그 생성/제거 함수
$(function()
{
	$('#btn_createTag').click(function()
	{
		$('#list_fileTag').append('<li><input type="text" class="txt_fileTag"></li>');
	});
	
	$('#btn_deleteTag').click(function()
	{
		$('#list_fileTag > li:last').remove();
	});
});

function resize_fileGrid() 
{
    var cont_fileDrive = document.getElementById('container_fileDriveGrid');
    cont_fileDrive.style.width = window.innerWidth - 300 + 'px';
    
    var cont_fileSystem = document.getElementById('container_fileSystemGrid');
    cont_fileSystem.style.height = window.innerHeight - 175 + 'px';
}

    resize_fileGrid();

    // 브라우저 크기가 변할 시 동적으로 사이즈를 조절해야 하는경우
    window.addEventListener('resize', resize_fileGrid);
    