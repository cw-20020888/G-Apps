
function ComboFieldEditor(parent,style){this.style=style||{};this.className=this.style.className||'ComboFieldEditor';this.opt=this.style.optionSplit||',';this.vlt=this.style.valueSplit||':';this.$widget=$('<select data-role="none">').addClass(this.className).appendTo(parent).attr('name',JSV.generateId());this.widget=this.$widget.get(0);if(this.style.width){this.$widget.width(this.style.width);}
this.$widget.bind('change',this,function(e){if(e.data.onchange)
e.data.onchange(this.value);});if(this.style.options){var options=this.style.options.split(this.opt);this.init(options);}}
ComboFieldEditor.prototype.init=function(options){for(var i=0;i<options.length;i++){var option=options[i].split(this.vlt);this.widget.options.add(new Option(JSV.getLocaleStr(option[1]),option[0]));}}
ComboFieldEditor.prototype.add=function(text,value){this.widget.options.add(new Option(JSV.getLocaleStr(text),value));}
ComboFieldEditor.prototype.setValue=function(value){if(value!=null){for(var i=0;i<this.widget.options.length;i++){if(this.widget.options[i].value==value){this.widget.value=value;break;}}}
JSV.notify(value,this);}
ComboFieldEditor.prototype.getValue=function(){return this.widget.value;}
ComboFieldEditor.prototype.getSelected=function(){return this.widget.options[this.widget.selectedIndex];}
ComboFieldEditor.prototype.refresh=function(value){while(this.widget.options.length){this.widget.options[0]=null;}
var options=value.split(this.opt);this.init(options);}
function ComboViewer(parent,style){this.style=style||{};this.className=this.style.className||'ComboViewer';this.widget=$('<div>').addClass(this.className).appendTo(parent);this.optionList=ComboViewer.createOptions(this.style);}
ComboViewer.prototype.setValue=function(value){if(value==null&&this.style&&this.style.defaultValue){this.widget.text(this.style.defaultValue);}
if(value!=null){this.value=value;this.widget.text(JSV.getLocaleStr(this.optionList[value]));}
if(this.widget.text().trim()=='')
this.widget.html('&nbsp;');}
ComboViewer.prototype.getValue=function(){return this.value;}
ComboViewer.createOptions=function(style){var map=new Object();if(style.options){var opt=style.optionSplit||',';var vlt=style.valueSplit||':';var options=style.options.split(opt);for(var i=0;i<options.length;i++){var option=options[i].split(vlt);map[option[0]]=option[1];}}
return map;}
function DateFormat(){}
DateFormat.format=function(date,pattern){if(!date){return'';}
if(!pattern){return date.getTime();}
pattern=DateFormat.formatLocale(date,pattern);pattern=pattern.replace(/yyyy/g,date.getFullYear());pattern=pattern.replace(/YY/g,(date.getFullYear())?String(date.getFullYear()).substring(2):date.getFullYear());pattern=pattern.replace(/MM/g,(date.getMonth()+1<10?'0':'')+(date.getMonth()+1));pattern=pattern.replace(/dd/g,(date.getDate()<10?'0':'')+date.getDate());pattern=pattern.replace(/EEEE/g,DateFormat.LONG_WEEKDAYS[date.getDay()]);pattern=pattern.replace(/EEE/g,DateFormat.MEDIUM_WEEKDAYS[date.getDay()]);pattern=pattern.replace(/EE/g,DateFormat.SHORT_WEEKDAYS[date.getDay()]);pattern=pattern.replace(/HH/g,(date.getHours()<10?'0':'')+date.getHours());pattern=pattern.replace(/mm/g,(date.getMinutes()<10?'0':'')+date.getMinutes());pattern=pattern.replace(/ss/g,(date.getSeconds()<10?'0':'')+date.getSeconds());pattern=pattern.replace(/Q/g,(parseInt(date.getMonth()/3)+1)+'/4 분기');return pattern;}
if(JSV.getLocale()=='ko'){DateFormat.formatLocale=function(date,pattern){pattern=pattern.replace(/YYYY/g,date.getFullYear()+'년');pattern=pattern.replace(/MMMM/g,(date.getMonth()+1)+'월');pattern=pattern.replace(/DDDD/g,date.getDate()+'일');return pattern;}
DateFormat.LONG=JSV.getLang('DateFormat','fullType1');DateFormat.SHORT_WEEKDAYS=['일','월','화','수','목','금','토'];DateFormat.MEDIUM_WEEKDAYS=['일','월','화','수','목','금','토'];DateFormat.LONG_WEEKDAYS=['일요일','월요일','화요일','수요일','목요일','금요일','토요일'];DateFormat.MODALTITLE=' 달력  ';}else{DateFormat.formatLocale=function(date,pattern){pattern=pattern.replace(/MMMM/g,DateFormat.MEDIUM_MONTHS[date.getMonth()]);pattern=pattern.replace(/DDDD/g,date.getDate()+' ');pattern=pattern.replace(/YYYY/g,date.getFullYear());return pattern;}
DateFormat.LONG=JSV.getLang('DateFormat','fullType2');DateFormat.SHORT_WEEKDAYS=['S','M','T','W','T','F','S'];DateFormat.MEDIUM_WEEKDAYS=['Sun','Mon','Tue','Wed','Thu','Fri','Sat'];DateFormat.LONG_WEEKDAYS=['Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'];DateFormat.MONTHS=['January','February','March','April','May',"June","July","August","September","October","November","December"];DateFormat.MEDIUM_MONTHS=['Jan','Feb','Mar','Apr','May',"Jun","Jul","Aug","Sep","Oct","Nov","Dec"];DateFormat.MODALTITLE=' Calendar ';}
DateFormat.ENG_SHORT_WEEKDAYS=['S','M','T','W','T','F','S'];DateFormat.ENG_MEDIUM_WEEKDAYS=['Sun','Mon','Tue','Wed','Thu','Fri','Sat'];function DateTermEditor(parent,style){this.style=style||{};this.className=this.style.className||'DateTermEditor';this.widget=$('<div>').addClass(this.className).appendTo(parent);this.start=new SimpleDatePicker($('<div>').addClass('upDiv').appendTo(this.widget),this.style);$('<span>').addClass('itemText').text(JSV.getLang('DateTermEditor','startText')).appendTo(this.start.widget);this.end=new SimpleDatePicker($('<div>').addClass('downDiv').appendTo(this.widget),this.style);$('<span>').addClass('itemText').text(JSV.getLang('DateTermEditor','endText')).appendTo(this.end.widget);JSV.register(this.start,this,'startNotify');JSV.register(this.end,this,'endNotify');}
DateTermEditor.prototype.startNotify=function(value){var start=this.start.getValue(),end=this.end.getValue();if(end<start)
this.end.setValue(start);}
DateTermEditor.prototype.endNotify=function(value){var start=this.start.getValue(),end=this.end.getValue();if(end<start)
this.start.setValue(end);}
DateTermEditor.prototype.getValue=function(){var value=new Array();value[0]=this.start.getValue();value[1]=this.end.getValue();return value;};DateTermEditor.prototype.setValue=function(value){if(value==null||value.length==0){return;}
if(value[0]==null&&value[1]==null){return;}
this.start.setValue(value[0]);this.end.setValue(value[1]);};function DateTextWriter(){}
DateTextWriter.text=function(text,options){options=options||{};var now=new Date(DateTextWriter.currTime),time=new Date(text);var diff=Math.max(now.getTime()-time.getTime(),60*1000);if(now.getFullYear()==time.getFullYear()&&now.getMonth()==time.getMonth()&&now.getDate()==time.getDate()){if(diff<3600000)
return parseInt(diff/60000)+JSV.getLang('DateTextWriter','min');else if(diff<86400000)
return parseInt(diff/3600000)+JSV.getLang('DateTextWriter','hour');return DateFormat.format(time,options.todayPattern||'HH:mm');}else{return DateFormat.format(time,options.pattern||'yyyy.MM.dd');}};DateTextWriter.reloadTime=function(){DateTextWriter.currTime=parseInt(new Date().getTime());}
DateTextWriter.currTime=parseInt(new Date().getTime());function DuplexLoader(style){this.style=style||{};this.param=this.style.param||'';this.loaded=false;}
DuplexLoader.prototype.load=function(){$.ajax({url:JSV.getContextPath('/mjsl/'+this.style.action+'.json'),type:'post',dataType:'json',data:this.paramToObj(),context:this,success:function(data,status){this.setTransition(this.prevObj,data.prev?data.prev.id:null,'notPrev');this.setTransition(this.nextObj,data.next?data.next.id:null,'notNext');},error:function(xhr){},complete:function(){this.loaded=true;}});}
DuplexLoader.prototype.setTransition=function(objs,id,msg){var url=this.style.url;objs.unbind('click').click(function(){if(id!=null&&id>0){JSV.setState('id',id);JSV.doTRANSITION(url);}else{alert(JSV.getLang('DuplexLoader',msg));}});}
DuplexLoader.prototype.setObjs=function(objs,type){objs.bind('click',this,function(e){if(!e.data.loaded)alert(JSV.getLang('DuplexLoader','loading'));});if(type=='prev')
this.prevObj=objs;else
this.nextObj=objs;}
DuplexLoader.prototype.paramToObj=function(){var obj={};var arr=this.param.split(',');var map=JSV.getParameterMap();for(var i=0;i<arr.length;i++){var value=arr[i].split('=');for(var key in map){if(key==value[1]){obj[value[0]]=map[key];break;}}}
return JSV.setModuleParam(obj);}
function EmpTextFieldEditor(parent,style){this.style=style||{};this.parent=$(parent).hide();this.vrtl=this.style.vrtl||null;this.className=this.style.className||'EmpTextFieldEditor';this.styleNotify=this.style.notify||'folderNotify';this.widget=$('<span>').addClass(this.className).appendTo(this.parent);this.input=$('<input type="text" maxlength="20">').addClass('nameInput').appendTo(this.widget);}
EmpTextFieldEditor.prototype.setValue=function(user,observable){this.user=user;this.jName=JSV.getLocaleStr(this.user.name);this.input.val(this.jName);if(observable)
JSV.register(observable,this);}
EmpTextFieldEditor.prototype.getValue=function(){if(this.editable){var nickname=this.input.val().trim();if(nickname.length>0){return(nickname==this.jName)?this.user:{'id':this.vrtl,'name':nickname,'displayName':nickname};}else{return null;}}
return this.user;}
EmpTextFieldEditor.prototype.setEditable=function(isEditable){this.editable=isEditable&&this.vrtl!=null;if(this.editable){this.parent.show();}else{this.parent.hide();}}
EmpTextFieldEditor.prototype.notify=function(value,observable){this[this.styleNotify](value,observable);}
EmpTextFieldEditor.prototype.folderNotify=function(value,observable){if(value==null||value.id==null)
return false;var node=JSV.loadJSON('/mjsl/FolderAction.NodeInfo.json?folderId='+value.id);var isEditable=((parseInt(node.flagCode)&4)!=0);this.setEditable(isEditable);}
EmpTextFieldEditor.prototype.menuNotify=function(value,observable){if(value==null||value.id==null)
return false;var node=JSV.loadJSON('/mjsl/MenuAction.Read.json?id='+value.id);this.setEditable(node.anony);}
function EmpsCompositeEditor(parent,style){this.style=style?style:{};this.className=this.style.className?this.style.className:'EmpsCompositeEditor';this.headName=this.style.headName||JSV.getLang('EmpsCompositeEditor','headName');var selText=this.style.selText||JSV.getLang('EmpsCompositeEditor','selText');var widget=$('<div>').addClass(this.className).appendTo(parent);var selDiv=$('<div>').addClass('select').appendTo(widget);$('<a data-role="button" href="javascript:void(0);"></a>').text(JSV.getLang('EmpsCompositeEditor','select')).addClass('btn').appendTo(selDiv).bind('click',this,function(e){var comp=e.data;var url='/mobile/sys/dialog/empsFieldEditorDialog.jsp';var args=[comp.headName];JSV.showModalDialog(url,args,function(users){var cUsers=EmpsCompositeEditor.copyUsers(users);if(cUsers)
comp.addUsers(cUsers);});return false;});$('<span>').html(selText).addClass('categoryText').appendTo(selDiv);this.ul=$('<ul>').addClass('sharePplArea').appendTo(widget);}
EmpsCompositeEditor.prototype.setValue=function(users){if(users==null)
return;this.ul.empty();this.addUsers(users);}
EmpsCompositeEditor.prototype.getValue=function(){var users=[];this.ul.children().each(function(i){var obj=$(this).data('obj');users.push(obj);});return users;}
EmpsCompositeEditor.prototype.addUsers=function(users){var values=this.getValue();outer:for(var i=0;i<users.length;i++){for(var j=0;j<values.length;j++){if(users[i].id==values[j].id)
continue outer;}
var li=$('<li>').addClass('sharePplItem').data('obj',users[i]).appendTo(this.ul.show());var a=$('<a hidefocus="true" href="javascript:void(0);"></a>').addClass('nameText').text(JSV.getLocaleStr(users[i].name)).appendTo(li).click(this,function(e){$(this).parent().remove();if(e.data.ul.children().length==0)
e.data.ul.hide();});$('<img>').attr('src',JSV.getContextPath('/mobile/img/icon_cross.gif')).appendTo(a);}}
EmpsCompositeEditor.prototype.validate=function(style){var values=this.getValue();if(values.length>0){return null;}else{return style.message;}}
EmpsCompositeEditor.copyUsers=function(users){if(users==null)
return null;var cUsers=[];for(var i=0;i<users.length;i++){cUsers.push({id:users[i].id,name:users[i].name,displayName:users[i].displayName});}
return cUsers;}
function EmpsHtmlViewer(parent,style){this.style=style||{};this.className=this.style.className||'EmpsHtmlViewer';this.widget=$(parent).addClass(this.className);}
EmpsHtmlViewer.prototype.setValue=function(value){if(!value||!value.length||value.length==0){$(this.widget).text('-');return;}
for(var i=0;i<value.length;i++){if(i>0){$(document.createTextNode(', ')).appendTo(this.widget);}
$('<a href="javascript:void(0);"></a>').addClass('user').text(JSV.getLocaleStr(value[i].name)).appendTo(this.widget).click(value[i].id,function(e){JSV.doTRANSITION('/mobile/ekp/emp/empDetail.jsp?userId='+e.data,'slide');});}}
function FileFieldEditor(parent,style){this.obj=new Object();this.style=style||{};this.isHiddenImg=this.style.isHiddenImg||false;this.parent=parent;this.fileCnt=0;this.totalFileSize=0;this.seq=1;this.isAddCase=true;this.maxSize=(this.style.maxSize&&this.style.maxSize!=null)?this.style.maxSize:(5*1024*1024);this.className=this.style.className||'FileFieldEditor';this.fileType=this.style.fileType||'image/*';this.inputName='com.kcube.jsv.file.';this.method=this.style.method||'copy';this.div=$('<div>').addClass(this.className).appendTo(this.parent);this.fileInfoArea=$('<span>').addClass('f_state').appendTo(this.div);this.fileNumArea=$('<span>').addClass('f_num').text(this.fileCnt+JSV.getLang('FileFieldEditor','case')).appendTo(this.fileInfoArea);this.fileTotalSizeArea=$('<span>').addClass('f_size').text('('+this.totalFileSize+'KB/'+this.getSizeFormat(this.maxSize)+')').appendTo(this.fileInfoArea);this.addBtn=$('<a></a>').attr({'data-role':'button','class':'btn_add'}).text(JSV.getLang('FileFieldEditor','add')).appendTo(this.div);this.addBtn.bind('click',this,function(e){var editor=e.data;editor.addInput();});this.addInput();JSV.register(this.fileNumArea,this,'updateCnt');JSV.register(this.fileTotalSizeArea,this,'updateTotalFileSize');}
FileFieldEditor.prototype.addInput=function(){this.fileListLayer=$('<div>').addClass('fileListLayer').appendTo(this.div);this.fileList=$('<ul>').addClass('fileList').appendTo(this.fileListLayer);this.fileItem=$('<li>').addClass('fileItem').appendTo(this.fileList);this.fileItemTextArea=$('<span>').addClass('fileItemText fileItemInfo').text(JSV.getLang('FileFieldEditor','noFile')).appendTo(this.fileItem);this.fileItemNameArea=$('<span>').addClass('fileItemText fileItemName').appendTo(this.fileItem);this.fileItemSizeArea=$('<span>').addClass('fileItemText fileItemSize').appendTo(this.fileItem);this.inputPlace=$('<div>').addClass('inputPlace').appendTo(this.fileItem);this.input=$('<input type="file" data-role="none">').addClass('fileInput').attr({'name':this.inputName+this.seq,'id':'file'+this.seq}).attr('accept',this.fileType).appendTo(this.inputPlace);this.btn_file=$('<a>').attr({'href':'#','data-role':'none'}).addClass('btn_file').text(JSV.getLang('FileFieldEditor','select')).appendTo(this.inputPlace);this.btn_del=$('<a>').attr('data-role','none').addClass('btn_f_del').appendTo(this.fileItem);this.img_cross=$('<img>').attr({'src':JSV.getContextPath(FileFieldEditor.DELIMAGEPATH),'alt':'close','width':21,'height':21}).appendTo(this.btn_del);this.obj['file'+this.seq++]=this.input.get(0);this.btnUiUpdate();$.data(this.input.get(0),'isEdit',false);$.data(this.input.get(0),'oldFileSize',0);this.input.bind('change',this,function(e){var comp=e.data;if(this.files[0]!=null){var size=comp.totalFileSize+this.files[0].size;if(parseInt(size)>parseInt(comp.maxSize)){alert(JSV.getLang('FileFieldEditor','sizeExceed'));delete comp.obj[this.id];$(this).val('');return;}
comp.isAddCase=true;$(this).parent().parent().find('.fileItemInfo').hide();var fileName=comp.getFileName(this);$(this).parent().parent().find('.fileItemName').text(fileName);var fileSize=comp.getSizeFormat(this.files[0].size);$(this).parent().parent().find('.fileItemSize').text('('+fileSize+')');if(!$.data(this,'isEdit')){JSV.notify(++comp.fileCnt,comp.fileNumArea,'updateCnt');}else{comp.totalFileSize-=$.data(this,'oldFileSize');}
JSV.notify(this.files[0],comp.fileTotalSizeArea,'updateTotalFileSize');$.data(this,'isEdit',true);$.data(this,'oldFileSize',this.files[0].size);}});this.btn_del.bind('click',this,function(e){var comp=e.data;var inputCnt=$(comp.parent).find('.fileListLayer').size();if(inputCnt==1){alert(JSV.getLang('FileFieldEditor','notDelete'));return;}
$(this).parent().parent().parent().empty().remove();var inputObj=$(this).parent().parent().find('.fileInput');if(inputObj.val()){comp.isAddCase=false;JSV.notify(--comp.fileCnt,comp.fileNumArea,'updateCnt');JSV.notify(inputObj.get(0).files[0],comp.fileTotalSizeArea,'updateTotalFileSize');}
delete comp.obj[inputObj.attr('id')];});}
FileFieldEditor.prototype.btnUiUpdate=function(){this.btn_file.addClass('ui-btn ui-btn-up-c ui-shadow ui-btn-corner-all');this.btn_file.attr({'data-corners':true,'data-shadow':true,'data-iconshadow':true,'data-wrapperels':'span','data-theme':'c'});}
FileFieldEditor.prototype.isExceed=function(size){return parseInt(size)>parseInt(this.maxSize);}
FileFieldEditor.prototype.getSizeFormat=function(size){var fileSize=size;if(fileSize>(1024*1024))
fileSize=(fileSize/(1024*1024)).toFixed(1)+'MB';else
fileSize=(fileSize/1024).toFixed(1)+'KB';return fileSize;}
FileFieldEditor.prototype.getFileName=function(obj){if(obj!=null&&obj.value){var name=obj.value;var i=name.lastIndexOf("\\");var fileName=(i<0)?name:name.substring(i+1);return fileName;}
return null;}
FileFieldEditor.prototype.getValue=function(){var fileArray=new Array();var totSize=0;for(var key in this.obj){var fileObj;var curObj=this.obj[key];if(curObj.value){var fileName=this.getFileName(curObj);var fileObj={'method':'copy','filename':this.style.isHiddenImg?FileFieldEditor.KCUBECONTENTIMAGEHIDDEN+fileName:fileName,'path':curObj.name,'type':3200};fileArray.push(fileObj);}}
return fileArray;}
FileFieldEditor.prototype.updateCnt=function(value,observable){var text=value+JSV.getLang('FileFieldEditor','case');this.fileNumArea.text(text);}
FileFieldEditor.prototype.updateTotalFileSize=function(value,observable){var fileSize=value.size;if(this.isAddCase)
this.totalFileSize+=fileSize;else
this.totalFileSize-=fileSize;if(this.totalFileSize>(1024*1024))
fileSize=(this.totalFileSize/(1024*1024)).toFixed(1)+'MB';else
fileSize=(this.totalFileSize/1024).toFixed(1)+'KB';this.fileTotalSizeArea.text('('+fileSize+'/'+this.getSizeFormat(this.maxSize)+')');}
FileFieldEditor.KCUBECONTENTIMAGEHIDDEN='KCUBECONTENTIMAGEHIDDEN';FileFieldEditor.DELIMAGEPATH='/mobile/img/icon_cross_21.gif';function FileViewer(parent,style){this.style=style||{};this.className=this.style.className||'FileViewerTag';this.inline=this.style.inline||null;this.docviewurl=this.style.docviewurl||null;this.widget=$('<ul>').addClass(this.className).appendTo(parent);}
FileViewer.prototype.setValue=function(files){for(var i=0;i<files.length;i++){this.createItem(files[i]);}}
FileViewer.prototype.createItem=function(data){var $file=$('<p/>',{'class':'FVFileApp'}).appendTo(this.widget);var file=$file.get(0);$('<img/>',{'src':JSV.getContextPath(FileViewer.DIR+FileViewer.getIconName(data.filename)),'class':'FVFileIcon'}).appendTo(file);$('<span/>',{'class':'FileName','text':data.filename}).appendTo(file);$('<span/>',{'class':'FVFilesize','text':'('+FileViewer.getSize(data.size)+')'}).appendTo(file);if(this.docviewurl!=null&&this.inline!=null){$file.addClass('link').bind('click',this,function(e){MobileFileViewer.click(FileViewer.mergeValue(e.data.docviewurl,data),FileViewer.mergeValue(e.data.inline,data),this);});}}
FileViewer.mergeValue=function(str,file){var i=str.indexOf('@{');var j=str.indexOf('@}',i);var val=file.getAttribute(str.substring(i+2,j));return str.substring(0,i)+val+str.substring(j+1);}
FileViewer.getSize=function(filesize){if(isNaN(filesize)){filesize=0;}
var kt=Math.ceil(filesize*10/1024);var kp=kt%10;var k=(kt-kp)/10;var mt=Math.ceil(filesize*10/(1024*1024));var mp=mt%10;var m=(mt-mp)/10;return(m>0)?(m+'.'+mp+'M'):(k+'.'+kp+'K');}
FileViewer.getIconName=function(filename){var i=filename.lastIndexOf('.');if(i<0){return"unknown.gif";}
var ext=filename.substring(i+1).toLowerCase();var j=FileViewer.EXT.indexOf(ext);if(j<0){return"unknown.gif";}
return FileViewer.EXT[j]+".gif";}
FileViewer.DIR='/img/ico/file/';FileViewer.EXT=['ai','arj','bmp','dll','doc','eps','exe','file','fla','gif','gz','htm','html','hwp','jpg','tif','lzh','mid','mov','movie','mp3','mpeg','mpg','null','pdf','ppt','psd','swf','tar','ttf','txt','unknown','url','wav','xls','z','zip','iwd','mpp','gul','hwx','wmv','docx','xlsx','pptx','rar'];function FolderFieldEditor(parent,style){this.style=style||{};this.className=this.style.className||'FolderFieldEditor';this.btnName=this.style.btnName||JSV.getLang('FolderFieldEditor','btnName');this.headName=this.style.headName||JSV.getLang('FolderFieldEditor','headName');this.isRead=this.style.isRead||false;this.axisCode=this.style.axisCode||null;this.idx=this.style.idx||1;this.url=this.style.url||null;this.needNode=this.style.needNode||false;this.widget=$('<div>').addClass(this.className).appendTo(parent);this.textViewer=$('<span>').html(JSV.getLang('FolderFieldEditor','select')).addClass('categoryText').appendTo(this.widget);if(this.isRead){this.widget.addClass(this.className+'_paddTop');}else{this.button=$('<a data-role="button" href="javascript:void(0);"></a>').text(this.btnName).addClass('btn').appendTo(this.widget).bind('click',this,function(e){var comp=e.data;var url='/mobile/sys/dialog/folderFieldEditorDialog.jsp';var args=[comp.headName,comp.url,comp.axisCode,comp.selectedId,comp.contentProvider];JSV.showModalDialog(url,args,function(folder){var nFolder=FolderFieldEditor.copyFolder(folder,comp.needNode);if(nFolder)
comp.setValue(nFolder);});return false;});}}
FolderFieldEditor.prototype.setContentProvider=function(provider){this.contentProvider=provider;}
FolderFieldEditor.prototype.setValue=function(folder){this.folder=folder;if(this.folder){this.selectedId=this.folder.id;this.textViewer.html(FolderFieldEditor.getPathText(this.folder.path,this.idx));JSV.notify(this.folder,this);}}
FolderFieldEditor.prototype.getValue=function(){return this.folder;}
FolderFieldEditor.copyFolder=function(oFolder,needNode){if(oFolder==null)
return null;var folder={};folder.id=oFolder.id;folder.path=[].concat(oFolder.path);folder.rootId=oFolder.rootId;if(needNode)
folder.obj=oFolder.obj;return folder;}
FolderFieldEditor.getPathText=function(path,idx){if(idx==null||idx==undefined)idx=(path.length>0)?1:0;if(path.length-1<idx||idx<0)idx=path.length-1;return(path?path.slice(idx).join(FolderFieldEditor.SEPARATOR):'');}
FolderFieldEditor.SEPARATOR=' > ';function FoldersFieldEditor(parent,area,style){this.style=style||{};this.className=this.style.className||'FoldersFieldEditor';this.btnName=this.style.btnName||JSV.getLang('FoldersFieldEditor','btnName');this.headName=this.style.headName||JSV.getLang('FoldersFieldEditor','headName');this.axisCode=this.style.axisCode||null;if(this.axisCode){this.axisList=JSV.loadJSON('/mjsl/AxisSelector.ListByCode.json?code='+this.axisCode).array;}else{this.axisList=JSV.loadJSON(JSV.getModuleUrl('/mjsl/AxisSelector.ListByModuleNoScrt.json')).array;}
if(!this.axisList)
return;this.widget=$('<div>').addClass(this.className).appendTo(parent);this.button=$('<a data-role="button" href="javascript:void(0);"></a>').text(this.btnName).addClass('btn').appendTo(this.widget).bind('click',this,function(e){var comp=e.data;var url='/mobile/sys/dialog/folderFieldEditorDialog.jsp';var args=[comp.headName,null,comp.axisCode,null,comp.contentProvider];JSV.showModalDialog(url,args,function(folder){var nFolder=FolderFieldEditor.copyFolder(folder,comp.needNode);if(nFolder)
comp.add(nFolder);});return false;});var requiredMap=[];$.each(this.axisList,function(i){if(this.min>0)
requiredMap.push(this.name);});if(requiredMap.length>0)
$('<span>').html(requiredMap.join(', ')+' '+JSV.getLang('FoldersFieldEditor','required')).addClass('categoryText').appendTo(this.widget);this.ul=$('<ul>').addClass(this.className+'_mapArea').appendTo(area);}
FoldersFieldEditor.prototype.setContentProvider=function(provider){this.contentProvider=provider;}
FoldersFieldEditor.prototype.setValue=function(folder){if(!this.axisList)
return;if(folder!=null){if($.isArray(folder)){for(var i=0;i<folder.length;i++){this.add(folder[i]);}}else{this.add(folder);}}}
FoldersFieldEditor.prototype.getValue=function(){var folders=[];this.ul.children().each(function(i){var obj=$(this).data('obj');folders.push(obj);});return folders;}
FoldersFieldEditor.prototype.add=function(folder){if(this.isIncluded(folder)){var li=$('<li>').addClass('mapItem').data('obj',folder).appendTo(this.ul.show());$('<span>').addClass('mapText').text(FolderFieldEditor.getPathText(folder.path,1)).appendTo(li);var a=$('<a hidefocus="true" href="javascript:void(0);"></a>').addClass('del').appendTo(li).click(this,function(e){$(this).parent().remove();if(e.data.ul.children().length==0)
e.data.ul.hide();});$('<img>').attr('src',JSV.getContextPath('/mobile/img/icon_cross_box.gif')).appendTo(a);}}
FoldersFieldEditor.prototype.isIncluded=function(folder){for(var i=0;i<this.axisList.length;i++){if(folder.rootId==this.axisList[i].rootId){var value=this.getValue();var cnt=0;for(var j=0;j<value.length;j++){if(value[j].id==folder.id){alert(JSV.getLang('FoldersFieldEditor','exist'));return false;}
if(value[j].rootId==this.axisList[i].rootId)
cnt++;}
if(this.axisList[i].max<=cnt){alert(JSV.getLang('FoldersFieldEditor','max'));return false;}
return true;}}
return false;}
FoldersFieldEditor.prototype.validate=function(){var error='';var value=this.getValue();for(var i=0;i<this.axisList.length;i++){var cnt=0;for(var j=0;j<value.length;j++){if(value[j].rootId==this.axisList[i].rootId)
cnt++;}
if(this.axisList[i].min>cnt)
error+='['+this.axisList[i].name+'] '+JSV.getLang('FoldersFieldEditor','validate')+'\n';}
return error;}
function ItemForm(form,style){this.style=style||{};this.className=this.style.className||'ItemForm';if(form){this.form=$(form).get(0);}else{this.form=$('<form>').appendTo('body').get(0);}
$(this.form).addClass(this.className);this.form.method=this.style.method||'POST';this.form.target=this.style.target||'_self';this.context={};}
ItemForm.prototype.setMethod=function(method){this.form.method=method;}
ItemForm.prototype.setTarget=function(target){this.form.target=target;}
ItemForm.prototype.put=function(name,value){this.context[name]=value;}
ItemForm.prototype.setRedirect=function(url,msg){if(msg){url=JSV.setUrlAlert(url,msg);}
this.context[JSV.REDIRECT_KEY]=JSV.getContextPath(url);}
ItemForm.prototype.setForward=function(url){this.context[JSV.FORWARD_KEY]=url;}
ItemForm.prototype.setError=function(url){this.context[JSV.ERROR_FORWARD_KEY]=url;}
ItemForm.prototype.setErrorRedirect=function(url){this.context[JSV.ERROR_KEY]=url;}
ItemForm.prototype.submit=function(action,redirect,error){this.form.action=JSV.getContextPath(action);if(redirect){this.setRedirect(redirect);}
if(error){this.setError(error);}
for(var name in this.context){var value=this.context[name];if(value){JSV.addHidden(this.form,name,value);}}
JSV.setFormState(this.form);this.setEncoding();this.form.submit();}
ItemForm.prototype.transition=function(action,redirect,error,effect,notInNames){if(this.isProcess){alert(JSV.getLang('ItemForm','NOT_AVAILABLE'));return;}
this.isProcess=true;var data={};$.extend(data,JSV.setMapState());for(var name in this.context){var value=this.context[name];if(value){data[name]=value;}}
$.ajax({url:JSV.getContextPath(action),type:'post',dataType:'json',data:data,context:this,async:false,success:function(data,status){this.isProcess=false;if(notInNames&&$.isArray(notInNames))
JSV.removeStateNotInNames(notInNames);JSV.doTRANSITION(redirect,effect);},error:function(xhr){this.isProcess=false;alert(JSV.getLang('ItemForm','ERROR'));}});}
ItemForm.prototype.setEncoding=function(){var elements=this.form.elements;for(var i=0;i<elements.length;i++){if(elements[i].tagName=='INPUT'&&elements[i].type=='file'){this.form.encoding='multipart/form-data';return;}}
this.form.encoding='application/x-www-form-urlencoded';}
function MobileFileViewer(){}
MobileFileViewer.click=function(url,inline,element){JSV.doGET('/mjsl/attach/'+inline);}
function MobileMenu(){}
MobileMenu.setMenu=function(url,name){var moduleName=$('#MobileModuleName'+JSV.CURRPAGE);if(name)
moduleName.text(name);if(url)
moduleName.attr('href',JSV.getContextPath(JSV.getModuleUrl(url)));}
MobileMenu.getSubMenuDiv=function(){$('#MobileMenuSubMenu'+JSV.CURRPAGE).css('visibility','visible');$('#MobileMenuName'+JSV.CURRPAGE).attr('href','javascript:$("#MobileMenuSubMenuDiv'+JSV.CURRPAGE+'").toggle();');return $('#MobileMenuSubMenuDiv'+JSV.CURRPAGE);}
MobileMenu.setRightBtn=function(name,url){$('#MobileModuleBtn'+JSV.CURRPAGE).show();$('#MobileBtnR'+JSV.CURRPAGE).attr('href',url.indexOf('javascript')<0?JSV.getContextPath(JSV.getModuleUrl(url)):url).text(name);}
function OpnLoader(cntElm,style){this.style=style||{};this.editable=false;this.cntElm=cntElm;this.vrtl=this.style.vrtl||null;this.currUserId=this.style.currUserId;this.itemId=this.style.itemId;this.addAction=this.style.addAction;this.delAction=this.style.delAction;this.moreAction=this.style.moreAction;this.maxCnt=this.style.maxCnt||300;this.rowPerPage=this.style.rowPerPage||20;this.notify=this.style.notify||'folderNotify';this.folderId=this.style.folderId||null;this.isCurrInfo=(this.style.isCurrInfo=='false')?false:true;this.body=$('#opnIncl'+JSV.CURRPAGE);this.widget=$('#opnInclList'+JSV.CURRPAGE);this.writer=$('#opnInclWrite'+JSV.CURRPAGE);this.wInput=$('#opnInclWriteInp'+JSV.CURRPAGE);this.wTxArea=$('#opnInclWriteTxArea'+JSV.CURRPAGE);this.wCnt=$('#opnInclWriteCnt'+JSV.CURRPAGE);this.rplyWriter=$('#opnInclReply'+JSV.CURRPAGE);this.rInput=$('#opnInclReplyInp'+JSV.CURRPAGE);this.rTxArea=$('#opnInclReplyTxArea'+JSV.CURRPAGE);this.rCnt=$('#opnInclReplyCnt'+JSV.CURRPAGE);this.moreDiv=$('#opnInclMoreDiv'+JSV.CURRPAGE);this.more=$('#opnInclMore'+JSV.CURRPAGE);this.init();if(this.folderId)
this[this.notify]();if(this.more.length>0){this.more.bind('click',this,function(e){e.data.opnMore();});}}
OpnLoader.prototype.init=function(){this.writer.find('a.writeBtn').bind('click',this,function(e){var data={content:e.data.wTxArea.val(),nickName:e.data.wInput.val()};e.data.saveOpn(e.data.writer,data);});this.writer.find('a.cancelBtn').bind('click',this,function(e){e.data.wInput.val('');e.data.wTxArea.val('');e.data.wCnt.text(e.data.maxCnt);});this.writer.find('a.talkBtn').bind('click',this,function(e){e.data.saveTalksOpn();});this.rplyWriter.find('a.writeBtn').bind('click',this,function(e){var data={content:e.data.rTxArea.val(),nickName:e.data.rInput.val(),gid:e.data.selectedId};e.data.saveOpn(e.data.rplyWriter,data);});this.rplyWriter.find('a.cancelBtn').bind('click',this,function(e){e.data.rInput.val('');e.data.rTxArea.val('');e.data.rCnt.text(e.data.maxCnt);});this.rplyWriter.find('a.talkBtn').bind('click',this,function(e){e.data.saveTalksOpn();});this.widget.find('li div a.opnDel').bind('click',this,function(e){e.data.delOpn($(this).attr('oId'));return;});this.widget.find('li div a.opnReply').bind('click',this,function(e){var id=$(this).attr('oId');if(id==e.data.selectedId){e.data.selectedId=null;e.data.rplyWriter.hide();}else{e.data.selectedId=id;e.data.rplyWriter.appendTo(e.data.widget.find('li[oId='+id+']')).show();}
return;});this.wTxArea.bind('keyup',this,function(e){var $this=$(this);var text=$this.val();var leng=text.length;if(leng>e.data.maxCnt){alert(JSV.getLang('OpnLoader','maxError'));$this.val(text.substr(0,e.data.maxCnt));leng=e.data.maxCnt;}
e.data.wCnt.text(e.data.maxCnt-leng);});this.rTxArea.bind('keyup',this,function(e){var $this=$(this);var text=$this.val();var leng=text.length;if(leng>e.data.maxCnt){alert(JSV.getLang('OpnLoader','maxError'));$this.val(text.substr(0,e.data.maxCnt));leng=e.data.maxCnt;}
e.data.rCnt.text(e.data.maxCnt-leng);});}
OpnLoader.prototype.saveOpn=function(writer,oData){if(this.startSave){alert(JSV.getLang('OpnLoader','startSave'));return;}
if(oData.content.trim()==''){alert(JSV.getLang('OpnLoader','emptyContent'));return;}else if(oData.content.length>this.maxCnt){alert(JSV.getLang('OpnLoader','maxError'));writer.find('textarea').val(oData.content.substr(0,this.maxCnt));return;}
if(this.editable&&oData.nickName.trim()==''){alert(JSV.getLang('OpnLoader','emptyNickName'));return;}
this.startSave=true;var objParam={itemId:this.itemId,mobile:true,content:oData.content.replace(/\n/gi,'<br>')};if(oData.gid)
objParam.gid=oData.gid;if(this.editable)
objParam.user={id:this.vrtl,name:oData.nickName,displayName:oData.nickName};var param={itemid:this.itemId,userId:this.itemId,mobile:true,content:oData.content.replace(/\n/gi,'<br>')};param.opn=JSV.toJSON(objParam);if(oData.gid)
param.gid=oData.gid;param=JSV.setModuleParam(param);$.ajax({url:JSV.getContextPath('/mjsl/'+this.style.addAction+'.json'),type:'post',dataType:'json',data:param,context:this,success:function(data,status){writer.find('a.cancelBtn').click();data.isNew=true;var opn=this.createOpn(data);if(data.id==data.gid){opn.prependTo(this.widget);}else{this.selectedId=null;this.rplyWriter.appendTo(this.body).hide();var lis=this.widget.find('li[oGid='+data.gid+']');opn.insertAfter(lis[lis.length-1]);}
this.cntElm.each(function(n){var $this=$(this);var cnt=parseInt($this.text());$this.text(cnt+1);});},error:function(xhr){alert(JSV.getLang('OpnLoader','saveFail'));},complete:function(){this.startSave=false;}});}
OpnLoader.prototype.delOpn=function(id){if(confirm(JSV.getLang('OpnLoader','del'))){var lis=this.widget.find('li[oGid='+id+']');if(lis.length>1){alert(JSV.getLang('OpnLoader','delFail'));return;}
$.ajax({url:JSV.getContextPath('/mjsl/'+this.style.delAction+'.jsl'),type:'post',dataType:'json',data:{id:id},context:this,success:function(data,status){var li=this.widget.find('li[oId='+id+']');var pOid=this.rplyWriter.parent().attr('oId');if(pOid&&pOid==id)
this.rplyWriter.appendTo(this.body).hide();li.remove();this.cntElm.each(function(n){var $this=$(this);var cnt=parseInt($this.text());$this.text(cnt-1);});},error:function(xhr){alert(JSV.getLang('OpnLoader','delFail'));}});}}
OpnLoader.prototype.opnMore=function(){var lastLi=this.widget.find('li:not([new]):last');var param={id:this.itemId,minId:lastLi.attr('oId'),minGid:lastLi.attr('oGid'),ots:'1.'+this.rowPerPage+'..'};param=JSV.setModuleParam(param);$.ajax({url:JSV.getContextPath('/mjsl/'+this.style.moreAction+'.json'),type:'post',dataType:'json',data:param,context:this,success:function(data,status){var array=data.array;var length=array.length;this.rplyWriter.appendTo(this.body).hide();for(var i=0;i<length;i++){this.widget.find('li[oId='+array[i].id+']').remove();this.createOpn(array[i]).appendTo(this.widget);}
if(length<this.rowPerPage){this.moreDiv.hide();}},error:function(xhr){alert(JSV.getLang('OpnLoader','moreFail'));}});}
OpnLoader.prototype.saveTalksOpn=function(oData){}
OpnLoader.prototype.createOpn=function(data){var li=$('<li>').addClass('replyList').attr({oId:data.id,oGid:data.gid});if(data.id!=data.gid)
li.addClass('replyChild');if(data.isNew)
li.attr('new','true');var authorP=$('<p>').appendTo(li);$('<span>').addClass('authorText').text(JSV.getLocaleStr(data.user?data.user.name:data.userName)).appendTo(authorP);$('<span>').addClass('barText').text('|').appendTo(authorP);$('<span>').addClass('regidate').text(DateFormat.format(new Date(parseInt(data.rgstDate)),JSV.getLang('DateFormat','fullType3'))).appendTo(authorP);var contentDiv=$('<div>').appendTo(li);$('<span>').addClass('contentText').html(OpnLoader.contentDecode(data.content.substr(data.content.indexOf('|')+1))).appendTo(contentDiv);var btnSpan=$('<span>').addClass('regDelText').appendTo(contentDiv);if(data.id==data.gid&&this.isCurrInfo){$('<a href="javascript:void(0);"></a>').addClass('regLink').attr({oId:data.id}).text(JSV.getLang('OpnLoader','opnText')).appendTo(btnSpan).bind('click',this,function(e){var id=$(this).attr('oId');if(id==e.data.selectedId){e.data.selectedId=null;e.data.rplyWriter.hide();}else{e.data.selectedId=id;e.data.rplyWriter.appendTo(e.data.widget.find('li[oId='+id+']')).show();}
return;});$('<span>').addClass('barText').text('|').appendTo(btnSpan);}
if(this.isDeletable(data)){$('<a href="javascript:void(0);"></a>').addClass('regLink').attr({oId:data.id}).text(JSV.getLang('OpnLoader','delText')).appendTo(btnSpan).bind('click',this,function(e){e.data.delOpn($(this).attr('oId'));return;});}
return li;}
OpnLoader.prototype.isDeletable=function(data){var userId=data.user?data.user.id||data.user.userId:data.userId||null;return(this.currUserId==userId)||data.currentOwner;}
OpnLoader.prototype.setEditable=function(editable){this.editable=editable&&this.vrtl;if(this.editable){this.wInput.parent().show();this.rInput.parent().show();}}
OpnLoader.prototype.folderNotify=function(){var node=JSV.loadJSON('/mjsl/FolderAction.NodeInfo.json?folderId='+this.folderId);var isEditable=((parseInt(node.flagCode)&4)!=0);this.setEditable(isEditable);}
OpnLoader.prototype.menuNotify=function(value,observable){var node=JSV.loadJSON('/mjsl/MenuAction.Read.json?id='+this.folderId);this.setEditable(node.anony);}
OpnLoader.contentDecode=function(value){if(value!=null){value=value.replace(/<(SCRIPT|FORM|OBJECT)/gi,"<XF").replace(/<\/(SCRIPT|FORM|OBJECT)/gi,"</XF");}
return value;}
function PlanCommon(){}
PlanCommon.getPS=function(ps,type,value,option){var array=ps.split(".");switch(type){case"tab":array[0]=value;break;case"type":array[1]=value;break;case"date":var date=new Date(+array[2]);date.setDate(date.getDate()+value*(option?7:1));array[2]=date.getTime();break;case"today":var date=new Date(value);array[2]=date.getTime();break;}
return array.join(".");}
function RadioGroupFieldEditor(parent,style){this.radios=new Array();this.style=style||{};this.className=this.style.className||'RadioGroupFieldEditor';this.name=this.style.name||JSV.generateId();this.empty=this.style.empty&&this.style.empty=='true'?true:false;this.seperator=this.style.seperator||RadioGroupFieldEditor.SEPERATOR;this.widget=$('<div>').addClass(this.className).appendTo(parent).get(0);if(this.style.options)
{var opt=this.style.optionSplit||',';var vlt=this.style.valueSplit||':';var options=this.style.options.split(opt);for(var i=0;i<options.length;i++){var option=options[i].split(vlt);this.add(option[1],option[0]);}}}
RadioGroupFieldEditor.prototype.add=function(html,value){var id=this.name+'_'+this.radios.length;var oInput=$('<input>',{'class':'oInput','type':'radio','id':id,'name':this.name,'value':value,'data-role':'none'}).appendTo(this.widget).bind('click',this,function(e){if(e.data.onchange){e.data.onchange(e.data.getValue());}}).get(0);$('<label>').addClass('oLabel').appendTo(this.widget).attr({'for':id}).html(RadioGroupFieldEditor.LabelProvider(html));this.radios[this.radios.length]=oInput;}
RadioGroupFieldEditor.prototype.getValue=function(){for(var i=0;i<this.radios.length;i++){if(this.radios[i].checked){return this.radios[i].value;}}
return null;}
RadioGroupFieldEditor.prototype.setValue=function(value){if(value!=null){value=value.toString();for(var i=0;i<this.radios.length;i++){if(this.radios[i].value==value){this.radios[i].checked=true;break;}}}else if(!this.empty&&this.radios.length>0){this.radios[0].checked=true;}}
RadioGroupFieldEditor.SEPERATOR="&nbsp;";RadioGroupFieldEditor.LabelProvider=function(html){return JSV.getLocaleStr(html);}
function SimpleDatePicker(parent,style){this.style=style||{};this.isEditor=this.style.isEditor||false;this.widget=$("<div />",{'class':"SimpleDatePicker"}).appendTo(parent);this.yearPicker=$("<select />",{"data-icon":"false"}).appendTo(this.widget).change(this,function(e,year){var fullYear=new Date().getFullYear();year=year||+(this.value||fullYear);var gap=year-fullYear;var yearString=JSV.getLang("SimpleDatePicker","year"),y=year-(e.data.isEditor?gap<5?gap:5:5);$(this).empty();for(var i=0;i<11;i++){$("<option />",{val:y,html:y+yearString}).appendTo(this);y++;}
$(this).val(year);e.data.monthPicker.change();});this.monthPicker=$("<select />",{"data-icon":"false"}).appendTo(this.widget).each(function(){var monthString=JSV.getLang("SimpleDatePicker","month");for(var i=0;i<12;i++){$("<option />",{val:i,html:(i+1)+monthString}).appendTo(this);}}).change(this,function(e){var nDate=new Date();var today=nDate.getDate();nDate=new Date(nDate.getFullYear(),nDate.getMonth(),1);var isMsg=false;var dayString=JSV.getLang("SimpleDatePicker","day"),date=new Date(e.data.yearPicker.val(),+(this.value!=null?this.value:nDate.getMonth()),1);if(e.data.isEditor&&nDate>date){$(this).val(nDate.getMonth());date=new Date(e.data.yearPicker.val(),nDate.getMonth(),1);isMsg=true;}
var val=e.data.dayPicker.val();e.data.dayPicker.empty();do{var day=date.getDate();date.setDate(day+1);$("<option />",{val:day,html:day+dayString}).appendTo(e.data.dayPicker);}while(day<date.getDate());e.data.dayPicker.val(val||today);e.data.dayPicker.trigger("change",isMsg);});var date=new Date();this.monthPicker.val(date.getMonth());this.dayPicker=$("<select />",{"data-icon":"false"}).appendTo(this.widget).change(this,function(e,isMsg){if(e.data.isEditor){var nDate=new Date();nDate=new Date(nDate.getFullYear(),nDate.getMonth(),nDate.getDate());var date=new Date(e.data.yearPicker.val(),e.data.monthPicker.val(),+(this.value));if(nDate>date){alert(JSV.getLang("SimpleDatePicker","isEditor"));$(this).val(nDate.getDate());}else if(isMsg){alert(JSV.getLang("SimpleDatePicker","isEditor"));}}
JSV.notify(null,e.data);});this.yearPicker.change();}
SimpleDatePicker.prototype.getValue=function(){return new Date(this.yearPicker.val(),this.monthPicker.val(),this.dayPicker.val()).getTime();};SimpleDatePicker.prototype.setValue=function(time){if(time){var date=new Date(time);this.monthPicker.val(date.getMonth());this.dayPicker.val(date.getDate());this.yearPicker.trigger("change",date.getFullYear());}};function TableFooter(parent,style){this.parent=parent;this.style=style||{};this.name=this.style.name||'ts';this.className=this.style.className||'TableFooter';this.custSrchArea=this.style.custSrchArea||null;this.onlyCustSrch=eval(this.style.onlyCustSrch||false);this.pagesPerSet=parseInt(this.style.pagesPerSet||5);this.options=this.style.options||null;this.initSort=this.style.initSort||null;this.initial=JSV.getParameter(this.name);this.oSelects=new Array();this.oInputs=new Array();if(this.initial){var i=this.initial.indexOf('.');var j=this.initial.indexOf('.',i+1);var k=this.initial.indexOf('.',j+1);this.currentPage=parseInt(this.initial.substring(0,i));this.rowsPerPage=parseInt(this.initial.substring(i+1,j));this.sort=this.initial.substring(j+1,k);this.search=this.initial.substring(k+1);}else{this.currentPage=1;this.rowsPerPage=parseInt(this.style.rowsPerPage||10);this.sort=this.initSort;this.search=null;}
this.widget=$('<div>').addClass(this.className).appendTo(this.parent);this.page=$('<div>').attr('align','center').addClass('pagingArea').appendTo(this.widget);if(this.options){if(!this.onlyCustSrch){this.initSearch(this.widget);}
if(this.custSrchArea){this.initSearch($('<div>').addClass(this.className).appendTo(this.custSrchArea));}}}
TableFooter.prototype.initSearch=function(srchArea){var srch=$('<div>').attr('align','center').addClass('searchArea').appendTo(srchArea).get(0);var oSelect=new ComboFieldEditor(srch,{options:this.options,className:'searchSel'});var sId='TableFooterSInput'+JSV.SEQUENCE++;var oInput=$('<input>').addClass('searchText').attr({id:sId,type:'search'}).appendTo($('<div>').addClass('searchInput').appendTo(srch));this.oSelects.push(oSelect);this.oInputs.push(sId);var index=this.oSelects.length;var _this=this;$('div[data-role=page]').live('pagebeforeshow.'+sId,function(event,ui){$('#'+sId).bind('keypress',_this,function(e){if(e.keyCode==13){e.data.sClick(index-1);return;}});if(_this.search&&_this.search!=''){var i=_this.search.indexOf('_');oSelect.setValue(_this.search.substring(0,i));$('#'+sId).val(_this.search.substring(i+1)).trigger('keyup');}
$('div[data-role=page]').die('pagebeforeshow.'+sId);});var oButton=$('<a href="javascript:void(0);" hidefocus="true"></a>').addClass('searchBtn').html('<span class="searchIcon"></span>').bind('click',this,function(e){e.data.sClick(index-1);return;}).appendTo(srch);}
TableFooter.prototype.sClick=function(i){var oSelect=this.oSelects[i];var oInput=$('#'+this.oInputs[i]);if(oSelect.getValue()=='id'&&oInput.val()!=''&&!TableFooter.isNumber(oInput.val())){alert(JSV.getLang('TableFooter','numError'));}else{var value=oInput.val();if(value&&value!='')
this.search=oSelect.getValue()+'_'+value;else
this.search=null;this.submit(1);}}
TableFooter.prototype.setValue=function(totalRows){var totalPages=Math.ceil(totalRows/this.rowsPerPage);var beginPage=this.getBeginPage();var endPage=this.getEndPage(beginPage,totalPages);if(beginPage==1&&endPage==1){return;}
if(beginPage>1){var prePage=Math.floor((this.currentPage-1)/this.pagesPerSet)*this.pagesPerSet;this.drawPage('',prePage,'pagingBtn pl');}
for(var i=beginPage;i<=endPage;i++){if(i==this.currentPage){this.drawPage(i,i,'paging on');}else{this.drawPage(i,i,'paging');}}
if(endPage<totalPages){var nextPage=Math.floor((this.currentPage+this.pagesPerSet-1)/this.pagesPerSet)*this.pagesPerSet+1;this.drawPage('',nextPage,'pagingBtn pr');}}
TableFooter.prototype.getBeginPage=function(){var idx=Math.floor((this.currentPage-1)/this.pagesPerSet)*this.pagesPerSet;return Math.max(idx,0)+1;}
TableFooter.prototype.getEndPage=function(beginPage,totalPages){var idx=beginPage+this.pagesPerSet-1;return Math.min(Math.max(idx,this.pagesPerSet),Math.max(totalPages,1));}
TableFooter.prototype.drawPage=function(html,page,className){$('<a href="javascript:void(0);"></a>').addClass(className).html(html).appendTo(this.page).bind('click',this,function(e){e.data.submit(page);return;});}
TableFooter.prototype.submit=function(page){JSV.setState(this.name,this.getState(page));var url=location.href;var i=url.lastIndexOf('#');var hasCtxpath=false;if(i>=0){url=url.substring(i+1);hasCtxpath=true;}else{i=url.lastIndexOf('/');if(i>=0)
url=url.substring(i+1);}
i=url.indexOf('?');if(i>=0)
url=url.substring(0,i);JSV.doTRANSITION(url,null,null,null,hasCtxpath);}
TableFooter.prototype.getState=function(page){return page+'.'+this.rowsPerPage+'.'+(this.sort||'')+'.'+(this.search||'');}
TableFooter.isNumber=function(value){return/^[0-9]*$/.exec(value);}
function TreeViewer(parent,style){this.parent=parent;this.style=style||{};this.className=this.style.className||'TreeViewer';this.isSrch=this.style.isSrch||false;this.rootName=this.style.rootName||JSV.getLang('TreeViewer','ROOT');this.type=this.style.type||'read';this.orgnRootId=this.style.orgnRootId||null;this.main=$('<div>').addClass(this.className).appendTo(this.parent);this.head=$('<div>').addClass('head').appendTo(this.main);this.navi=$('<span>').addClass('navi').appendTo(this.head);if(this.isSrch){this.btn=$('<span>').addClass('btn').appendTo(this.head);this.srch=$('<div>').addClass('srch').appendTo(this.main);}
this.widget=$('<div>').addClass('list').appendTo(this.main);this.nodes={};this.checked={};}
TreeViewer.prototype.setContentProvider=function(contentProvider){this.contentProvider=contentProvider;}
TreeViewer.prototype.setLabelProvider=function(labelProvider){this.labelProvider=labelProvider;}
TreeViewer.prototype.setInput=function(input,selectedId){if(this.contentProvider==null)
this.contentProvider=new XMLTreeContentProvider();if(this.labelProvider==null)
this.labelProvider=new XMLTreeLabelProvider();this.selectedId=selectedId;this.contentProvider.setInput(this,input);this.setInputAfterInit();}
TreeViewer.prototype.setAxis=function(code,selectedId){if(this.contentProvider==null){alert('ContentProvider is Null');return;}
if(this.labelProvider==null)
this.labelProvider=new XMLTreeLabelProvider();this.selectedId=selectedId;this.contentProvider.setAxis(this,code,this.orgnRootId);this.setInputAfterInit();}
TreeViewer.prototype.setTree=function(selectedId){this.setAxis(null,selectedId);}
TreeViewer.prototype.setInputAfterInit=function(){var root=this.contentProvider.getElement();if(root){this.root=new this.Root(this,root);if(this.selectedId)
this.callNode(this.selectedId);else
this.root.expand();}
if(this.onload)
this.onload();}
TreeViewer.prototype.callNode=function(id){var node=this.findNode(id);if(node&&!this.isRoot(id))
node.select();else
this.root.expand();}
TreeViewer.prototype.findNode=function(id){var node=this.nodes[id];if(node)
return node;var obj=this.contentProvider.getObject(id);if(obj==null)
return null;var pobj=this.contentProvider.getParent(obj);var pid=this.contentProvider.getId(pobj);var parent=this.findNode(pid);if(parent)
parent.expand();return this.nodes[id];}
TreeViewer.prototype.addNode=function(obj,parentNode){if(parentNode)
parentNode.appendChild(new this.Node(this,obj));}
TreeViewer.prototype.setViewNode=function(node){if(this.viewNode){this.viewNode.ul.hide();if(!$.isEmptyObject(this.checked)){for(var key in this.checked)
this.nodes[key].click();}}
this.viewNode=node;this.viewNode.ul.show();if(this.onViewNode)
this.onViewNode(this.viewNode.id);}
TreeViewer.prototype.expand=function(){if(!this.viewNode.isRoot)
$('<span>').addClass('sep').text('>').appendTo(this.navi);var page=$('<span>').addClass('page').html(this.viewNode.name).data('node',this.viewNode).appendTo(this.navi);var prev=page.prev().get(0);if(prev){$(prev).prev().addClass('anchor').bind('click',this,function(e){var $this=$(this);e.data.setViewNode($this.data('node'));e.data.move($this);});}}
TreeViewer.prototype.move=function(page){page.removeClass('anchor').unbind('click');page.nextAll().remove();}
TreeViewer.prototype._onclick=function(id){if(this.onclick)
this.onclick(this.getObject(id));}
TreeViewer.prototype.getSelectedId=function(){return(this.selectedNode?this.selectedNode.id:null);}
TreeViewer.prototype.getSelectedNode=function(){var id=this.getSelectedId();return id?this.getObject(id):null;}
TreeViewer.prototype.getObject=function(id){return this.contentProvider.getObject(id);}
TreeViewer.prototype.getId=function(obj){return this.contentProvider.getId(obj);}
TreeViewer.prototype.getRootId=function(obj){return this.contentProvider.getRootId(obj);}
TreeViewer.prototype.getChecked=function(){return this.checked;}
TreeViewer.prototype.getAncestorsOrSelf=function(id){var ancestors=new Array();var parent=this.getObject(id);do{ancestors[ancestors.length]=parent;parent=this.contentProvider.getParent(parent);}while(parent!=null);return ancestors.reverse();}
TreeViewer.prototype.getPath=function(id){var ancestors=this.getAncestorsOrSelf(id);var arr=new Array();for(var i=0;i<ancestors.length;i++){arr[arr.length]=this.labelProvider.getName(ancestors[i]);}
return arr;}
TreeViewer.prototype.isRoot=function(id){var obj=this.contentProvider.getObject(id);var root=this.contentProvider.getElement();if(root==obj){return true;}
return false;}
TreeViewer.prototype.isLeaf=function(id){var obj=this.contentProvider.getObject(id);return!this.hasChildren(obj);}
TreeViewer.prototype.hasChildren=function(obj){if(this.contentProvider.hasChildren){return this.contentProvider.hasChildren(obj);}else{var c=this.contentProvider.getChildren(obj);return(c&&c.length>0);}}
TreeViewer.prototype.Node=function(tree,obj){this.tree=tree;this.id=this.tree.contentProvider.getId(obj);this.name=this.tree.labelProvider.getName(obj);this.tree.nodes[this.id]=this;this.isRead=this.tree.type=='read';this.isCheck=this.tree.type=='check';this.isView=this.isCheck||this.tree.type=='view';this.isLeaf=!this.isRead&&!this.isView&&this.tree.contentProvider.isLeaf(obj);this.hasChild=this.tree.hasChildren(obj);this.li=$('<li>').addClass('nodeLI');this.nameP=$('<span>').addClass('nameP').appendTo(this.li);this.spanChild=$('<span>').appendTo(this.nameP);if(this.tree.contentProvider.getParentId(obj)==0){this.spanChild.html(this.name);}else{this.spanChild.text(this.name);}
if(this.isCheck)
this.on=false;if(this.hasChild||this.isView){this.nameP.addClass('mapHas').bind('click',$.proxy(this,'expand'));this.ul=$('<ul>').addClass('nodeUL').appendTo(tree.widget);if(!this.hasChild)
this.loaded=true;}
this.act=$('<span>').addClass('actSpan pointer').appendTo(this.li).bind('click',$.proxy(this,'click'));this.createActImg();}
TreeViewer.prototype.Node.prototype.createActImg=function(){this.actImg=$('<span>').addClass(this.isRead?'listIcon':this.isCheck?'chkIcon':'selIcon').appendTo(this.act);}
TreeViewer.prototype.Node.prototype.appendChild=function(node){node.parentNode=this;if(!this.isRoot)
node.isLeaf=this.isLeaf;if((node.isLeaf&&node.hasChild)||(node.isView&&!node.isCheck)){node.actImg.hide();node.act.removeClass('pointer').unbind('click');}
this.ul.append(node.li);}
TreeViewer.prototype.Node.prototype.loadChildren=function(){if(this.loaded)
return;this.loaded=true;var obj=this.tree.contentProvider.getObject(this.id);var children=this.tree.contentProvider.getChildren(obj);if(children.length==0){this.nameP.removeClass('mapHas').unbind('click');this.ul.remove();this.ul=null;return;}
for(var i=0;i<children.length;i++){this.tree.addNode(children[i],this);}}
TreeViewer.prototype.Node.prototype.expand=function(){if(!this.loaded)
this.loadChildren();if(this.ul){this.tree.setViewNode(this);this.tree.expand();}}
TreeViewer.prototype.Node.prototype.select=function(){if(this.isCheck){if(this.on){this.setUnselected();delete this.tree.checked[this.id];}else{this.setSelected();this.tree.checked[this.id]={id:this.id,name:this.name};}}else{this.setSelected();this.tree.selectedNode=this;}}
TreeViewer.prototype.Node.prototype.click=function(){this.select();if(!this.isCheck||this.on)
this.tree._onclick(this.id);}
TreeViewer.prototype.Node.prototype.setSelected=function(){if(!this.isCheck&&this.tree.selectedNode)
this.tree.selectedNode.setUnselected();if(!this.isRead){if(this.isCheck){this.on=true;this.actImg.addClass('check');}else if(!this.isView)
this.actImg.addClass('on');}
this.nameP.addClass('selected');}
TreeViewer.prototype.Node.prototype.setUnselected=function(){if(!this.isRead){if(this.isCheck){this.on=false;this.actImg.removeClass('check');}else if(!this.isView)
this.actImg.removeClass('on');}
this.nameP.removeClass('selected');}
TreeViewer.prototype.Root=function(tree,obj){this.isRoot=true;this.name=tree.rootName;this.id=tree.contentProvider.getId(obj);this.tree=tree;this.tree.nodes[this.id]=this;this.ul=$('<ul>').addClass('nodeUL').appendTo(tree.widget);}
TreeViewer.prototype.Root.prototype.select=function(){return;}
TreeViewer.prototype.Root.prototype.appendChild=TreeViewer.prototype.Node.prototype.appendChild;TreeViewer.prototype.Root.prototype.expand=TreeViewer.prototype.Node.prototype.expand;TreeViewer.prototype.Root.prototype.loadChildren=TreeViewer.prototype.Node.prototype.loadChildren;function XMLTreeLabelProvider(){}
XMLTreeLabelProvider.prototype.getName=function(obj){return $(obj).text();}
function XMLTreeContentProvider(childrenList,ancestorsOrSelf,scrtCode){this.isLazy=childrenList&&ancestorsOrSelf;this.childrenList=childrenList;this.ancestorsOrSelf=ancestorsOrSelf;if(scrtCode){this.exclUrl=JSV.getModuleUrl('/mjsl/FolderScrt.ExcludedList.xml');this.exclQuery='scrtCode='+scrtCode+'&rootId=';}}
XMLTreeContentProvider.prototype.setAxis=function(view,code,orgnRootId){if(!this.isLazy){alert('Not Lazy Mode');return;}
this.nodes={};this.children={};this.root=$('<n>').attr('id','0');this.nodes['0']=this.root;if(code){this.axisList=JSV.loadJSON('/mjsl/AxisSelector.ListByCode.json?code='+code).array;}else{this.axisList=JSV.loadJSON(JSV.getModuleUrl('/mjsl/AxisSelector.ListByModuleNoScrt.json')).array;if(orgnRootId){this.axisList.push({id:0,rootId:orgnRootId,name:JSV.getLang('TreeViewer','orgn'),min:0,max:1,leaf:1,scrt:0});}}
var children=[];if(this.exclUrl)
this.excl={};for(var i=0;i<this.axisList.length;i++){var axis=this.axisList[i];this.nodes[axis.rootId]=$('<n>').attr({id:axis.rootId,pid:'0',leaf:axis.leaf,flag_code:'0'}).text(axis.name);children.push(this.nodes[axis.rootId]);if(this.exclUrl)
this.excl[axis.rootId]=$(JSV.loadXml(this.exclUrl,this.exclQuery+axis.rootId));}
this.children['0']=children;}
XMLTreeContentProvider.prototype.setInput=function(view,input){this.doc=$(JSV.loadXml(input));if(this.isLazy){this.nodes={};this.children={};var children=this.doc.children();for(var i=0;i<children.length;i++){var child=children[i];var id=this.getId(child);var pid=this.getParentId(child);this.nodes[id]=child;if(!pid)
this.root=child;}
if(this.exclUrl){var rootId=$(this.root).attr('id');this.excl={};this.excl[rootId]=$(JSV.loadXml(this.exclUrl,this.exclQuery+rootId));}}}
XMLTreeContentProvider.prototype.getElement=function(){return this.isLazy?this.root:this.doc.find(':not([pid])').get(0);}
XMLTreeContentProvider.prototype.getChildren=function(obj){if(this.isLazy){var pid=this.getId(obj);if(this.children[pid])
return this.children[pid];var rootId=null;if(this.excl)
rootId=this.getRootId(obj);var scrtChildren=[];var children=$(JSV.loadXml(this.childrenList,'pid='+pid)).children();for(var i=0;i<children.length;i++){var child=children[i];if(this.excl&&this.isDenied(child,rootId))continue;scrtChildren.push(child);this.nodes[this.getId(child)]=child;}
this.children[pid]=scrtChildren;return scrtChildren;}else{return this.doc.find('[pid='+$(obj).attr('id')+']');}}
XMLTreeContentProvider.prototype.hasChildren=function(obj){if(this.isLazy)
return((parseInt($(obj).attr('flag_code'))&1)==0);else
return this.doc.children().is('[pid='+$(obj).attr('id')+']');}
XMLTreeContentProvider.prototype.getParent=function(obj){if(this.isLazy)
return this.getObject(this.getParentId(obj));else
return this.doc.find('[id='+$(obj).attr('pid')+']').get(0);}
XMLTreeContentProvider.prototype.getObject=function(id){if(!id)
return null;if(this.isLazy){var obj=this.nodes[id];if(obj){return obj;}
var doc=$(JSV.loadXml(this.ancestorsOrSelf,'id='+id));var rootId=null;if(this.excl)
rootId=doc.find(':not([pid])').attr('id');var children=doc.children();for(var i=0;i<children.length;i++){var child=children[i];if(this.excl&&this.isDenied(child,rootId))continue;if(!this.nodes[this.getId(child)])
this.nodes[this.getId(child)]=child;}
return this.nodes[id];}else{return this.doc.find('[id='+id+']').get(0);}}
XMLTreeContentProvider.prototype.getId=function(obj){return $(obj).attr('id');}
XMLTreeContentProvider.prototype.getParentId=function(obj){return $(obj).attr('pid');}
XMLTreeContentProvider.prototype.isLeaf=function(obj){if(this.axisList){var leaf=$(obj).attr('leaf');if(leaf&&leaf==1)
return true;return false;}
return true;}
XMLTreeContentProvider.prototype.getRootId=function(obj){var $obj=$(obj);var pid=$obj.attr('pid');if((pid&&pid=='0')||!pid)
return $obj.attr('id');else
return this.getRootId(this.getObject(pid));}
XMLTreeContentProvider.prototype.isDenied=function(obj,rootId){return(this.isAncestorsOrSelfExcluded(obj,rootId));}
XMLTreeContentProvider.prototype.isAncestorsOrSelfExcluded=function(obj,rootId,id){var jObj=$(obj);if(id==null)
id=jObj.attr('id');if(this.isExcluded(id,rootId))
return true;var pid=jObj.attr('pid');if(pid==null)
return false;return this.isAncestorsOrSelfExcluded(this.getObject(pid),rootId,pid);}
XMLTreeContentProvider.prototype.isExcluded=function(id,rootId){return this.excl[rootId].children().is('[id='+id+']');}
