function showPage(pageUrl,liNode)
{
	$("#left-tree li").removeClass("active");
	$(liNode).parent().addClass("active");
	$("#main-iframe").attr('src',pageUrl);
}