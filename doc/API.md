# REST API Description
* Users

|       Description       | Method |       Path       | Sample Request Body                                                                       |
|:-----------------------:|:------:|:----------------:|-------------------------------------------------------------------------------------------|
| Sign up                 | PUT    | /users           | {"type":1, "user_info":{"userid": "admin", "password": "1234", "email": "asdf@test.com"}} |
| Sign in                 | POST   | /users           | {"type":1, "userid": "admin", "password": "1234"}                                         |
| Sign out                | POST   | /users           | {"type":2}                                                                                |
| Delete user             | DELETE | /users/{user_id} | ( just call this api )                                                                    |
| Get user information    | GET    | /user/{user_id}  | ( just call this api )                                                                    |
| Update user information | PUT    | /user/{user_id}  | {"type":2, "user_info":{"userid": "admin", "password": "1234", "email": "asdf@test.com"}} |

* File

| Description                              | Method |              Path              | Sample Request Body                                                                        | Response Status                                                                                            | Sample Response Body                                                                                                                       |
|------------------------------------------|--------|:------------------------------:|--------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| File Upload                              | PUT    | /files/{user_id}               | < multipart form data 로 업로드 >                                                          | 200 OK : 업로드 성공<br> 409 Conflict : 중복된 파일<br> 403 Forbidden : 허용되지 않는 접근<br>             |                                                                                                                                            |
| File Download                            | GET    | /files/{user_id}/{file_id}     |                                                                                            | 200 OK : 다운로드 성공<br> 404 NotFound : 존재하지 않는 파일<br> 403 Forbidden : 허용되지 않는 접근<br>    |                                                                                                                                            |
| File Delete                              | DELETE | /file/{user_id}/{file_id}      |                                                                                            | 200 OK : 삭제 성공<br> 404 NotFound : 존재하지 않는 파일<br> 403 Forbidden : 허용되지 않는 접근            |                                                                                                                                            |
| 파일에 등록된 태그 수정                  | POST   | /file/{user_id}/{file_id}/tags | {"tags": ["a", "b"]}                                                                       | 200 OK : 변경 성공<br> 404 NotFound : 존재하지 않는 파일<br> 403 Forbidden : 허용되지 않는 접근<br>        |                                                                                                                                            |
| 가장 최근에 업로드 된 파일 목록 가져오기 | GET    | /files/{user_id}/recent        | {"marker": 0, "limit": 100} <br>  marker : 어디까지 가져왔는지<br> limit : 가져올 갯수<br> | 200 OK : 요청 승인 <br> 404 NotFound : 사용자가 존재하지 않음 <br> 403 Forbidden : 허용되지 않는 접근 <br> | {"filelist": [ <br>    {"filename": "a.txt", "file_id": "abZzeir394", "filesize": 1024", "uploadtime": "2014-05-23", "tags":["a", "b"]} ]} |
