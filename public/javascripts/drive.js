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
    