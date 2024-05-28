var loadingImage = {
	init : function() {
		var template = 
			'<div class="ongoing_Wrap">' +
			'	<div class="ongoing_bg"></div>' +
			'	<div class="ongoing_Area">' +
			'		<img class="imgLoading" src=' + JSV.getContextPath("/resources/css/img/loading.gif") +' />' +
			'	</div>' +
			'</div>';
	
		this.lodingElm = $(template).appendTo('body').hide();
	},
	show : function() {
		this.lodingElm.show();
		window.scroll(0, 0);
	},
	hide : function() {
		this.lodingElm.hide();
	}
};
