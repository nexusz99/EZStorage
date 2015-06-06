// (디자인 요소) 툴팁 초기화 함수
$(function () {
  $('[data-toggle="tooltip"]').tooltip();
});

// 파일 삭제 시 UI상에서 파일 아이콘을 제거하는 함수
$(function()
{
	$('#btn_delete').click(function()
	{
		var fileid_to_delete = $(this).parent().parent().parent().next().html();
		if(confirm( fileid_to_delete +" 파일이 삭제됩니다! 계속하시겠습니까?"))
		{$(this).parent().parent().parent().parent().remove();}
	});
});

// 파일 정보창 '적용' 함수
 $('#btn_apply_fileinfo').on('click', function () {
    var $btn = $(this).button('loading');
    
    
    
    // business logic...
    
    
   	setTimeout(function(){$btn.button('reset');}, 800);
});

// 카테고리 정보창 '적용' 함수
 $('#btn_apply_categoryinfo').on('click', function () {
    var $btn = $(this).button('loading');
    
    
    
    // business logic...
    
    
   	setTimeout(function(){$btn.button('reset');}, 800);
});

// 카테고리 정보창 '카테고리 제거' 함수
$(function()
{
	$('#btn_delete_categoryInfo').click(function()
	{
		$('#categoryname').parent().remove();
	});
});

// 카테고리 추가창 '추가' 함수
 $('#btn_apply_categoryadd').on('click', function () {
    var $btn = $(this).button('loading');

    var name = $("#categoryadd_ninput").val()
    var tags = $("#categoryadd_tinput").val()
    
    var user_id = getCookie("userid");
	if(user_id == "")
	{
		alert("비정상적인 접근!");
		return;
	}
    
   	setTimeout(function()
   	{
   		$btn.button('reset');
   		$('#categoryadd_ninput').val("");
   		$('#categoryadd_tinput').tagsinput('removeAll');
   		$('#modal_categoryadd').modal('hide');
	}, 800);
   	
   	var data = {"name": name, "tags":tags.split(',')}
   	var jsondata = JSON.stringify(data);
   	
	$.ajax
	({
		type: 'PUT',
		headers:
		{
			'Accept': 'application/json',
			'Content-Type': 'application/json'
		},
		url: '/category/'+user_id,
		async: false,
		data: jsondata,
		success: function(result){
			location.reload();
		},
		statusCode:{
			409:function(){
				alert("이미 존재하는 카테고리입니다.");
			}
		}
	});
});


$(function() {
	loadfile();
	loadCategory();
});

function loadCategory()
{
	var user_id = getCookie("userid");
	if(user_id == "")
	{
		alert("비정상적인 접근!");
		return;
	}
	var u = "/category/"+user_id+"/lists";
	$.ajax({
		url: u,
		dataType:"json",
		method: "GET",
		async: false,
		success:function(result)
		{
			var content = ''
			if(result.length == 0)
			{
				content = "<li><a class=\"item_category\" id=\"categoryname\" href=\"#\">카테고리 없음</a>";
				content += "<button class=\"noncolored nonbordered vertical_mid\">";
				content += "<span class=\"glyphicon glyphicon-info-sign\" data-toggle=\"modal\" data-target=\"#modal_categoryinfo\"></span>	</button></li>";
			}
			for(k = 0; k < result.length; k++)
			{
				var file = result[k];
				var body = "<li><a class=\"item_category\" catid=\""+file.id+"\" id=\"categoryname\" href=\"#\" onclick=searching(this.getAttribute(\"catid\"))>"+file.name+"</a>";
				body += "<button class=\"noncolored nonbordered vertical_mid\" onclick=getCategoryInfo("+file.id+")>";
				body += "<span class=\"glyphicon glyphicon-info-sign\" data-toggle=\"modal\" data-target=\"#modal_categoryinfo\"></span>	</button></li>";
				content += body;
			}
			$("#list_category").html(content);
		}
	})
}

function getCategoryInfo(category_id)
{
	$('#categoryinfo_tinput').tagsinput('removeAll');
	var user_id = getCookie("userid");
	if(user_id == "")
	{
		alert("비정상적인 접근!");
		return;
	}
	var u = "/category/"+user_id+"/"+category_id;
	$.ajax({
		url: u,
		dataType:"json",
		method: "GET",
		async: false,
		success:function(result)
		{
			$("#categoryinfo_name").html(result.name);
			for(i = 0; i < result.tags.length; i++)
			{
				$('#categoryinfo_tinput').tagsinput("add", result.tags[i]);
			}
			$('#categoryinfo_tinput').tagsinput("refresh");
			$('#btn_delete_categoryInfo').attr('catid', result.id);
		}
	})
}

function deleteCategory(category_id)
{
	var user_id = getCookie("userid");
	if(user_id == "")
	{
		alert("비정상적인 접근!");
		return;
	}
	var u = "/category/"+user_id+"/"+category_id;
	$.ajax({
		url: u,
		dataType:"json",
		method: "DELETE",
		async: false,
		success:function(result)
		{
		}
	})
}

function loadfile()
{
	var user_id = getCookie("userid");
	if(user_id == "")
	{
		alert("비정상적인 접근!");
		return;
	}
	
	var u = "/files/"+user_id+"/recent?marker=0&limit=100";
	$.ajax(
			{
				url: u,
				dataType:"json",
				method: "GET",
				async: false,
				success:function(result)
				{
					var content = "<ol>";
					for(k = 0; k < result.length; k++)
					{
						var file = result[k];
						content += clickMouse(file.type, user_id, file.file_id);
						content += file.filename;
						content += "</div></li>";

					}
					content += "</ol>";
					$("#container_fileDriveGrid").html(content);

				}
			})
}

function searching(flg)
{
	var user_id = getCookie("userid");
	if(user_id == "")
	{
		alert("비정상적인 접근!");
		return;
	}
	var tags;
	var str;
	var url;
	
	if(flg == '-1')
	{
		tags = $('#tagsArea_search').val().split(",");
		str = {"user_id": user_id, "tags": tags};
		url = '/search/file';
	}
	else
	{
		url = '/search/category';
		str = {"user_id": user_id, "category_id": flg};
	}
	
	var jsondata = JSON.stringify(str);
	
	$.ajax
	({
		type: 'POST',
		headers:
		{
			'Accept': 'application/json',
			'Content-Type': 'application/json'
		},
		url: url,
		async: false,
		data: jsondata,
		success: function(result){
			var content = "<ol>";
			for(k = 0; k < result.length; k++)
			{
				var file = result[k];
				content += clickMouse(file.type, user_id, file.id);
				content += file.file_name;
				content += "</div></li>";

			}
			content += "</ol>";
			$("#container_fileDriveGrid").html(content);
		},
		statusCode:{
			409:function(){
				alert("이미 존재하는 ID입니다.");
			}
		}
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
};

// 프로필 바_사용자 이름 출력
$(function()
{
	var lastname = $.cookie('lastname');
	var firstname = $.cookie('firstname');
	decodeURIComponent(lastname);
	decodeURIComponent(firstname);
	
	var UserName = lastname+firstname;;
	$('#UserName').html(UserName);
});

function clickMouse(type, userid, fileid)
{
	var content = "<li class=\"grid_fileIcon\">" +

	"<div class=\"btn-group\"><button type=\"button\" class=\"btn btn_fileIcon\" data-toggle=\"dropdown\" aria-expanded=\"false\">";


	if(type ==1)	// Document file (.hwp .docx .pdf ...)
	{
		content +=	"<img class=\"img_fileIcon\" src=\"/assets/images/icons/word.png\"></button>";
	}
	else if(type ==2)	// Compressed file (.zip .rar .jar .iso ...)
	{
		content += "<img class=\"img_fileIcon\" src=\"/assets/images/icons/zip.png\"></button>";
	}
	else if(type ==3)	// Code file (.cpp .java ...)
	{
		content +=  "<img class=\"img_fileIcon\" src=\"/assets/images/icons/code.png\"></button>";
	}
	else if(type ==4)	// Image file (.jpg .gif .png ...)
	{
		content +=  "<img class=\"img_fileIcon\" src=\"/assets/images/icons/picture.png\"></button>";
	}
	else if(type ==5)	// Media file (.mp3 .mp4 .avi .wav ...)
	{
		content +=  "<img class=\"img_fileIcon\" src=\"/assets/images/icons/media.png\"></button>";
	}
	else if(type ==6)	// PPT file (.ppt .pptx .show ...)
	{
		content +=  "<img class=\"img_fileIcon\" src=\"/assets/images/icons/PPT.png\"></button>";
	}
	else				// etc. file (all other types ...)
	{
		content += "<img class=\"img_fileIcon\" src=\"/assets/images/icons/etc.png\"></button>";
	}
	

	content += "<ul id=" + fileid + " class=\"dropdown-menu pull-right\" role=\"menu\">" +
	"<li><a href=\"/files/"+userid+"/"+fileid+"\" ><span class=\"glyphicon glyphicon-download\"></span>&nbsp;&nbsp;다운로드</a></li>" +
	"<li><a id=\"btn_delete\" href=\"#\" fileid=\""+fileid+"\" onclick='filedelete(this.getAttribute(\"fileid\"))'><span class=\"glyphicon glyphicon-trash\"></span>&nbsp;&nbsp;삭제하기</a></li>" +
	"<li class=\"divider\"></li>" +
	"<li><a href=\"#\" fileid=\""+fileid+"\" data-toggle=\"modal\" data-target=\"#modal_fileInfo\" onclick=getfileinfo(this.getAttribute(\"fileid\"))><span class=\"glyphicon glyphicon-info-sign\"></span>&nbsp;&nbsp;파일 정보</a></li>" +
	"</ul></div>" +
	"<div class=\"header_fileIcon\" id=\"fileName\">";
	return content;
};

function getfileinfo(fileid)
{
	var user_id = getCookie("userid");
	$('#fileinfo_tinput').tagsinput('removeAll');
	if(user_id == "")
	{
		alert("비정상적인 접근!");
		return;
	}
	var u = "/files/"+user_id+"/"+fileid+"/info";
	$.ajax(
	{
		url: u,
		dataType:"json",
		method: "GET",
		async: false,
		success:function(result)
		{
			$('#fileinfo_name').html(result.filename);
			$('#fileinfo_date').html(result.uploadtime);
			
			var size = result.size;
			var rsize = result.size + "Bytes";
			if(size >= 1024 && size < 1024*1024)
				rsize = (size / 1024).toFixed(3) + " KB";
			else if(size >= 1024*1024 && size < 1024*1024*1024)
				rsize = (size / (1024*1024)).toFixed(3) + " MB";
			else if(size >= 1024*1024*1024)
				rsize = (size / (1024*1024*1024)).toFixed(3) + " GB";
			
			$('#fileinfo_size').html(rsize);
			for(i = 0; i < result.tags.length; i++)
			{
				$('#fileinfo_tinput').tagsinput("add", result.tags[i]);
			}
			$('#fileinfo_tinput').tagsinput("refresh");
		}
	})
};

function sendJsonUserdata(data, method)
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
		url: '/users',
		async: false,
		data: jsondata,
		success: function(result){
			location.reload();
		},
		statusCode:{
			409:function(){
				alert("error.");
			}
		}
	})
}

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


//크롬 브라우저의 fakepath의 출력 해결    
$(function()
		{
	$('#btn_fileUpload').change(function()
			{
		$('#name_fileUpload').val(this.files && this.files.length ? this.files[0].name : this.value.replace(/^C:\\fakepath\\/i, ''));
			});
		});

/*
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
});*/

//파일 업로드 모달 취소 버튼 액션
$(function()
{
	$('#btn_cancle_fileUpload').click(function()
		{
			$('#name_fileUpload').val("");
			$('#btn_fileUpload').val("");
			$('#tagsArea').val("");
			$('.bootstrap-tagsinput > span').remove();
		});
});

//Bootstrap - Tags Input 컴포넌트
(function ($) {
	"use strict";

	var defaultOptions = {
			tagClass: function(item) {
				return 'label label-info';
			},
			itemValue: function(item) {
				return item ? item.toString() : item;
			},
			itemText: function(item) {
				return this.itemValue(item);
			},
			freeInput: true,
			addOnBlur: true,
			maxTags: undefined,
			maxChars: undefined,
			confirmKeys: [13, 44],
			onTagExists: function(item, $tag) {
				$tag.hide().fadeIn();
			},
			trimValue: false,
			allowDuplicates: false
	};

	/**
	 * Constructor function
	 */
	function TagsInput(element, options) {
		this.itemsArray = [];

		this.$element = $(element);
		this.$element.hide();

		this.isSelect = (element.tagName === 'SELECT');
		this.multiple = (this.isSelect && element.hasAttribute('multiple'));
		this.objectItems = options && options.itemValue;
		this.placeholderText = element.hasAttribute('placeholder') ? this.$element.attr('placeholder') : '';
		this.inputSize = Math.max(1, this.placeholderText.length);

		this.$container = $('<div class="bootstrap-tagsinput"></div>');
		this.$input = $('<input type="text" placeholder="' + this.placeholderText + '"/>').appendTo(this.$container);

		this.$element.after(this.$container);

		var inputWidth = (this.inputSize < 3 ? 3 : this.inputSize) + "em";
		this.$input.get(0).style.cssText = "width: " + inputWidth + " !important;";
		this.build(options);
	}

	TagsInput.prototype = {
			constructor: TagsInput,

			/**
			 * Adds the given item as a new tag. Pass true to dontPushVal to prevent
			 * updating the elements val()
			 */
			add: function(item, dontPushVal) {
				var self = this;

				if (self.options.maxTags && self.itemsArray.length >= self.options.maxTags)
					return;

				// Ignore falsey values, except false
				if (item !== false && !item)
					return;

				// Trim value
				if (typeof item === "string" && self.options.trimValue) {
					item = $.trim(item);
				}

				// Throw an error when trying to add an object while the itemValue option was not set
				if (typeof item === "object" && !self.objectItems)
					throw("Can't add objects when itemValue option is not set");

				// Ignore strings only containg whitespace
				if (item.toString().match(/^\s*$/))
					return;

				// If SELECT but not multiple, remove current tag
				if (self.isSelect && !self.multiple && self.itemsArray.length > 0)
					self.remove(self.itemsArray[0]);

				if (typeof item === "string" && this.$element[0].tagName === 'INPUT') {
					var items = item.split(',');
					if (items.length > 1) {
						for (var i = 0; i < items.length; i++) {
							this.add(items[i], true);
						}

						if (!dontPushVal)
							self.pushVal();
						return;
					}
				}

				var itemValue = self.options.itemValue(item),
				itemText = self.options.itemText(item),
				tagClass = self.options.tagClass(item);

				// Ignore items allready added
				var existing = $.grep(self.itemsArray, function(item) { return self.options.itemValue(item) === itemValue; } )[0];
				if (existing && !self.options.allowDuplicates) {
					// Invoke onTagExists
					if (self.options.onTagExists) {
						var $existingTag = $(".tag", self.$container).filter(function() { return $(this).data("item") === existing; });
						self.options.onTagExists(item, $existingTag);
					}
					return;
				}

				// if length greater than limit
				if (self.items().toString().length + item.length + 1 > self.options.maxInputLength)
					return;

				// raise beforeItemAdd arg
				var beforeItemAddEvent = $.Event('beforeItemAdd', { item: item, cancel: false });
				self.$element.trigger(beforeItemAddEvent);
				if (beforeItemAddEvent.cancel)
					return;

				// register item in internal array and map
				self.itemsArray.push(item);

				// add a tag element
				var $tag = $('<span class="tag ' + htmlEncode(tagClass) + '">' + htmlEncode(itemText) + '<span data-role="remove"></span></span>');
				$tag.data('item', item);
				self.findInputWrapper().before($tag);
				$tag.after(' ');

				// add <option /> if item represents a value not present in one of the <select />'s options
				if (self.isSelect && !$('option[value="' + encodeURIComponent(itemValue) + '"]',self.$element)[0]) {
					var $option = $('<option selected>' + htmlEncode(itemText) + '</option>');
					$option.data('item', item);
					$option.attr('value', itemValue);
					self.$element.append($option);
				}

				if (!dontPushVal)
					self.pushVal();

				// Add class when reached maxTags
				if (self.options.maxTags === self.itemsArray.length || self.items().toString().length === self.options.maxInputLength)
					self.$container.addClass('bootstrap-tagsinput-max');

				self.$element.trigger($.Event('itemAdded', { item: item }));
			},

			/**
			 * Removes the given item. Pass true to dontPushVal to prevent updating the
			 * elements val()
			 */
			remove: function(item, dontPushVal) {
				var self = this;

				if (self.objectItems) {
					if (typeof item === "object")
						item = $.grep(self.itemsArray, function(other) { return self.options.itemValue(other) ==  self.options.itemValue(item); } );
					else
						item = $.grep(self.itemsArray, function(other) { return self.options.itemValue(other) ==  item; } );

					item = item[item.length-1];
				}

				if (item) {
					var beforeItemRemoveEvent = $.Event('beforeItemRemove', { item: item, cancel: false });
					self.$element.trigger(beforeItemRemoveEvent);
					if (beforeItemRemoveEvent.cancel)
						return;

					$('.tag', self.$container).filter(function() { return $(this).data('item') === item; }).remove();
					$('option', self.$element).filter(function() { return $(this).data('item') === item; }).remove();
					if($.inArray(item, self.itemsArray) !== -1)
						self.itemsArray.splice($.inArray(item, self.itemsArray), 1);
				}

				if (!dontPushVal)
					self.pushVal();

				// Remove class when reached maxTags
				if (self.options.maxTags > self.itemsArray.length)
					self.$container.removeClass('bootstrap-tagsinput-max');

				self.$element.trigger($.Event('itemRemoved',  { item: item }));
			},

			/**
			 * Removes all items
			 */
			removeAll: function() {
				var self = this;

				$('.tag', self.$container).remove();
				$('option', self.$element).remove();

				while(self.itemsArray.length > 0)
					self.itemsArray.pop();

				self.pushVal();
			},

			/**
			 * Refreshes the tags so they match the text/value of their corresponding
			 * item.
			 */
			refresh: function() {
				var self = this;
				$('.tag', self.$container).each(function() {
					var $tag = $(this),
					item = $tag.data('item'),
					itemValue = self.options.itemValue(item),
					itemText = self.options.itemText(item),
					tagClass = self.options.tagClass(item);

					// Update tag's class and inner text
					$tag.attr('class', null);
					$tag.addClass('tag ' + htmlEncode(tagClass));
					$tag.contents().filter(function() {
						return this.nodeType == 3;
					})[0].nodeValue = htmlEncode(itemText);

					if (self.isSelect) {
						var option = $('option', self.$element).filter(function() { return $(this).data('item') === item; });
						option.attr('value', itemValue);
					}
				});
			},

			/**
			 * Returns the items added as tags
			 */
			items: function() {
				return this.itemsArray;
			},

			/**
			 * Assembly value by retrieving the value of each item, and set it on the
			 * element.
			 */
			pushVal: function() {
				var self = this,
				val = $.map(self.items(), function(item) {
					return self.options.itemValue(item).toString();
				});

				self.$element.val(val, true).trigger('change');
			},

			/**
			 * Initializes the tags input behaviour on the element
			 */
			build: function(options) {
				var self = this;

				self.options = $.extend({}, defaultOptions, options);
				// When itemValue is set, freeInput should always be false
				if (self.objectItems)
					self.options.freeInput = false;

				makeOptionItemFunction(self.options, 'itemValue');
				makeOptionItemFunction(self.options, 'itemText');
				makeOptionFunction(self.options, 'tagClass');

				// Typeahead Bootstrap version 2.3.2
				if (self.options.typeahead) {
					var typeahead = self.options.typeahead || {};

					makeOptionFunction(typeahead, 'source');

					self.$input.typeahead($.extend({}, typeahead, {
						source: function (query, process) {
							function processItems(items) {
								var texts = [];

								for (var i = 0; i < items.length; i++) {
									var text = self.options.itemText(items[i]);
									map[text] = items[i];
									texts.push(text);
								}
								process(texts);
							}

							this.map = {};
							var map = this.map,
							data = typeahead.source(query);

							if ($.isFunction(data.success)) {
								// support for Angular callbacks
								data.success(processItems);
							} else if ($.isFunction(data.then)) {
								// support for Angular promises
								data.then(processItems);
							} else {
								// support for functions and jquery promises
								$.when(data)
								.then(processItems);
							}
						},
						updater: function (text) {
							self.add(this.map[text]);
						},
						matcher: function (text) {
							return (text.toLowerCase().indexOf(this.query.trim().toLowerCase()) !== -1);
						},
						sorter: function (texts) {
							return texts.sort();
						},
						highlighter: function (text) {
							var regex = new RegExp( '(' + this.query + ')', 'gi' );
							return text.replace( regex, "<strong>$1</strong>" );
						}
					}));
				}

				// typeahead.js
				if (self.options.typeaheadjs) {
					var typeaheadjs = self.options.typeaheadjs || {};

					self.$input.typeahead(null, typeaheadjs).on('typeahead:selected', $.proxy(function (obj, datum) {
						if (typeaheadjs.valueKey)
							self.add(datum[typeaheadjs.valueKey]);
						else
							self.add(datum);
						self.$input.typeahead('val', '');
					}, self));
				}

				self.$container.on('click', $.proxy(function(event) {
					if (! self.$element.attr('disabled')) {
						self.$input.removeAttr('disabled');
					}
					self.$input.focus();
				}, self));

				if (self.options.addOnBlur && self.options.freeInput) {
					self.$input.on('focusout', $.proxy(function(event) {
						// HACK: only process on focusout when no typeahead opened, to
						//       avoid adding the typeahead text as tag
						if ($('.typeahead, .twitter-typeahead', self.$container).length === 0) {
							self.add(self.$input.val());
							self.$input.val('');
						}
					}, self));
				}


				self.$container.on('keydown', 'input', $.proxy(function(event) {
					var $input = $(event.target),
					$inputWrapper = self.findInputWrapper();

					if (self.$element.attr('disabled')) {
						self.$input.attr('disabled', 'disabled');
						return;
					}

					switch (event.which) {
					// BACKSPACE
					case 8:
						if (doGetCaretPosition($input[0]) === 0) {
							var prev = $inputWrapper.prev();
							if (prev) {
								self.remove(prev.data('item'));
							}
						}
						break;

						// DELETE
					case 46:
						if (doGetCaretPosition($input[0]) === 0) {
							var next = $inputWrapper.next();
							if (next) {
								self.remove(next.data('item'));
							}
						}
						break;

						// LEFT ARROW
					case 37:
						// Try to move the input before the previous tag
						var $prevTag = $inputWrapper.prev();
						if ($input.val().length === 0 && $prevTag[0]) {
							$prevTag.before($inputWrapper);
							$input.focus();
						}
						break;
						// RIGHT ARROW
					case 39:
						// Try to move the input after the next tag
						var $nextTag = $inputWrapper.next();
						if ($input.val().length === 0 && $nextTag[0]) {
							$nextTag.after($inputWrapper);
							$input.focus();
						}
						break;
					default:
						// ignore
					}

					// Reset internal input's size
					var textLength = $input.val().length,
					wordSpace = Math.ceil(textLength / 5),
					size = textLength + wordSpace + 1;
					$input.attr('size', Math.max(this.inputSize, $input.val().length));
				}, self));

				self.$container.on('keypress', 'input', $.proxy(function(event) {
					var $input = $(event.target);

					if (self.$element.attr('disabled')) {
						self.$input.attr('disabled', 'disabled');
						return;
					}

					var text = $input.val(),
					maxLengthReached = self.options.maxChars && text.length >= self.options.maxChars;
					if (self.options.freeInput && (keyCombinationInList(event, self.options.confirmKeys) || maxLengthReached)) {
						self.add(maxLengthReached ? text.substr(0, self.options.maxChars) : text);
						$input.val('');
						event.preventDefault();
					}

					// Reset internal input's size
					var textLength = $input.val().length,
					wordSpace = Math.ceil(textLength / 5),
					size = textLength + wordSpace + 1;
					$input.attr('size', Math.max(this.inputSize, $input.val().length));
				}, self));

				// Remove icon clicked
				self.$container.on('click', '[data-role=remove]', $.proxy(function(event) {
					if (self.$element.attr('disabled')) {
						return;
					}
					self.remove($(event.target).closest('.tag').data('item'));
				}, self));

				// Only add existing value as tags when using strings as tags
				if (self.options.itemValue === defaultOptions.itemValue) {
					if (self.$element[0].tagName === 'INPUT') {
						self.add(self.$element.val());
					} else {
						$('option', self.$element).each(function() {
							self.add($(this).attr('value'), true);
						});
					}
				}
			},

			/**
			 * Removes all tagsinput behaviour and unregsiter all event handlers
			 */
			destroy: function() {
				var self = this;

				// Unbind events
				self.$container.off('keypress', 'input');
				self.$container.off('click', '[role=remove]');

				self.$container.remove();
				self.$element.removeData('tagsinput');
				self.$element.show();
			},

			/**
			 * Sets focus on the tagsinput
			 */
			focus: function() {
				this.$input.focus();
			},

			/**
			 * Returns the internal input element
			 */
			input: function() {
				return this.$input;
			},

			/**
			 * Returns the element which is wrapped around the internal input. This
			 * is normally the $container, but typeahead.js moves the $input element.
			 */
			findInputWrapper: function() {
				var elt = this.$input[0],
				container = this.$container[0];
				while(elt && elt.parentNode !== container)
					elt = elt.parentNode;

				return $(elt);
			}
	};

	/**
	 * Register JQuery plugin
	 */
	$.fn.tagsinput = function(arg1, arg2) {
		var results = [];

		this.each(function() {
			var tagsinput = $(this).data('tagsinput');
			// Initialize a new tags input
			if (!tagsinput) {
				tagsinput = new TagsInput(this, arg1);
				$(this).data('tagsinput', tagsinput);
				results.push(tagsinput);

				if (this.tagName === 'SELECT') {
					$('option', $(this)).attr('selected', 'selected');
				}

				// Init tags from $(this).val()
				$(this).val($(this).val());
			} else if (!arg1 && !arg2) {
				// tagsinput already exists
				// no function, trying to init
				results.push(tagsinput);
			} else if(tagsinput[arg1] !== undefined) {
				// Invoke function on existing tags input
				var retVal = tagsinput[arg1](arg2);
				if (retVal !== undefined)
					results.push(retVal);
			}
		});

		if ( typeof arg1 == 'string') {
			// Return the results from the invoked function calls
			return results.length > 1 ? results : results[0];
		} else {
			return results;
		}
	};

	$.fn.tagsinput.Constructor = TagsInput;

	/**
	 * Most options support both a string or number as well as a function as
	 * option value. This function makes sure that the option with the given
	 * key in the given options is wrapped in a function
	 */
	function makeOptionItemFunction(options, key) {
		if (typeof options[key] !== 'function') {
			var propertyName = options[key];
			options[key] = function(item) { return item[propertyName]; };
		}
	}
	function makeOptionFunction(options, key) {
		if (typeof options[key] !== 'function') {
			var value = options[key];
			options[key] = function() { return value; };
		}
	}
	/**
	 * HtmlEncodes the given value
	 */
	var htmlEncodeContainer = $('<div />');
	function htmlEncode(value) {
		if (value) {
			return htmlEncodeContainer.text(value).html();
		} else {
			return '';
		}
	}

	/**
	 * Returns the position of the caret in the given input field
	 * http://flightschool.acylt.com/devnotes/caret-position-woes/
	 */
	function doGetCaretPosition(oField) {
		var iCaretPos = 0;
		if (document.selection) {
			oField.focus ();
			var oSel = document.selection.createRange();
			oSel.moveStart ('character', -oField.value.length);
			iCaretPos = oSel.text.length;
		} else if (oField.selectionStart || oField.selectionStart == '0') {
			iCaretPos = oField.selectionStart;
		}
		return (iCaretPos);
	}

	/**
	 * Returns boolean indicates whether user has pressed an expected key combination. 
	 * @param object keyPressEvent: JavaScript event object, refer
	 *     http://www.w3.org/TR/2003/WD-DOM-Level-3-Events-20030331/ecma-script-binding.html
	 * @param object lookupList: expected key combinations, as in:
	 *     [13, {which: 188, shiftKey: true}]
	 */
	function keyCombinationInList(keyPressEvent, lookupList) {
		var found = false;
		$.each(lookupList, function (index, keyCombination) {
			if (typeof (keyCombination) === 'number' && keyPressEvent.which === keyCombination) {
				found = true;
				return false;
			}

			if (keyPressEvent.which === keyCombination.which) {
				var alt = !keyCombination.hasOwnProperty('altKey') || keyPressEvent.altKey === keyCombination.altKey,
				shift = !keyCombination.hasOwnProperty('shiftKey') || keyPressEvent.shiftKey === keyCombination.shiftKey,
				ctrl = !keyCombination.hasOwnProperty('ctrlKey') || keyPressEvent.ctrlKey === keyCombination.ctrlKey;
				if (alt && shift && ctrl) {
					found = true;
					return false;
				}
			}
		});

		return found;
	}

	/**
	 * Initialize tagsinput behaviour on inputs and selects which have
	 * data-role=tagsinput
	 */
	$(function() {
		$("input[data-role=tagsinput], select[multiple][data-role=tagsinput]").tagsinput();
	});
})(window.jQuery);


