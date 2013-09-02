var toolbarItemsId = "toolbar-items";
var toolbarId = "toolbar";

/**
 * <pre>
 *     <div id="user-options" class="toolbar-icons" style="display: none;">
 *     <a href="#"><i class="icon-user"></i></a>
 *     <a href="#"><image src="file:///C:/Users/Naveed/Downloads/gear-icon.png" width="32" height="32"></a>
 *     <a href="#"><i class="icon-star"></i></a>
 *     <a href="#"><i class="icon-edit"></i></a>
 *     <a href="#"><i class="icon-trash"></i></a>
 *     <a href="#"><i class="icon-ban-circle"></i></a>
 *     </div>
 * </pre>
 * @param {type} html
 * @returns {undefined}
 */
function initToolbar(html)
{
    var toolbarHtml = "<section style='position: absolute; top: 5px; right: 25px'>"
            + "<a id='" + toolbarId + "'> Tools</a>"
            + "< /section>";
    $('body').append(toolbarHtml);
    $('body').append(html);
    // Define any icon actions before calling the toolbar
    $('#' + toolbarItemsId + ' a').on('click', function(event) {
        event.preventDefault();
    });
    $("#" + toolbarId).button(
            {
                icons: {primary: 'ui-icon-gear'},
                text: false});

    $("#" + toolbarId).sticky({topSpacing: 0});
    $('#' + toolbarId).toolbar({content: '#' + toolbarItemsId, position: 'bottom', orientation: 'vertical', hideOnClick: true});
}

jQuery(document).ready(function($) {
    alert("ready");
    // Define any icon actions before calling the toolbar
    $('.toolbar-icons a').on('click', function(event) {
        event.preventDefault();
    });
    $('#article-menu a').on('click', function(event) {
        event.preventDefault();
    });
    $('#article-menu a').button();
    $("#link-toolbar").button();
    $("#link-keyboard-shortcut").button(
            {
                icons: {primary: 'ui-icon-keyboard'},
                text: false});
    $("#link-toolbar").sticky({topSpacing: 0});
    $('#link-toolbar').toolbar({content: '#user-options', position: 'bottom', orientation: 'vertical', hideOnClick: true});

    $("article.verse").click(function(e)
    {
        var selected = $(this).hasClass("selected");
        $('article.verse').removeClass("selected");
        if (selected)
            $(this).removeClass("selected");
        else
            $(this).addClass("selected");
    });
});