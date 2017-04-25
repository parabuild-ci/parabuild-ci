function popupModal(title,areaId) {
	char_width = 8;
	line_height = 16;
	horizontal_margin = 80;
	vertical_margin = ( BrowserDetect.browser == 'Firefox' ? 100 : 80 );
	Dialog.confirm(
		'<textarea id="edit" name="edit" style="margin:20px;" col="100" rows="40" wrap="off"></textarea>',
		{
			className: "parabuild", 
			title: title, 
			draggable: true,
			resizable: true,
			width: 750,
			height: 500, 
			zIndex: 1000,
			opacity: 1,
			showEffect: Element.show,
			destroyOnClose: true,
			ok: function(win) {
				window.top.get_object(areaId).value = $('edit').value;
				return true;
			},
			cancel: function(win) {
				return true;
			},
			onShow: function(win) {
				Windows.unsetOverflow();
				$('edit').value = window.top.get_object(areaId).value;
				$('edit').rows = (win.height-vertical_margin)/line_height;
				$('edit').cols = (win.width-horizontal_margin)/char_width;
				return true;
			},
			onResize: function(win) {
				$('edit').rows = (win.height-vertical_margin) / line_height;
				$('edit').cols = (win.width-horizontal_margin) / char_width;
				return true;
			}
		});
		WindowCloseKey.init();
}
