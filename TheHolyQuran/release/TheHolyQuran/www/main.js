jQuery(document).ready(function($) {
    // Define any icon actions before calling the toolbar

    $('#article-menu a').on('click', function(event) {
        event.preventDefault();
    });
    $('#article-menu a').button();
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

function insertCssClass(css)
{
    var style = document.createElement('style');
    style.type = 'text/css';

    if (style.styleSheet) { // IE
        style.styleSheet.cssText = css;
    } else {
        style.appendChild(document.createTextNode(css));
    }

    document.getElementsByTagName('head')[0].appendChild(style);
}


function addTranslation(json)
{
    var obj = $.parseJSON(json);
    alert(obj);
    var style = 'style_{0} style_{1}'.format(obj.language, obj.translator_id);
    $('section#document article').each(function(index) {
        var html = '<p class=\"{0}\" style=\"direction:{1}\" data-translator=\"{2}\" data-translator-id=\"{3}\" data-language=\"{4}\">{5}<span>{2}</span></p>'.format(style,obj.text_align, obj.translator, obj.translator_id, obj.language,obj.verses[index]);
        $(html).appendTo(this);
    });
}

function removeTranslation(id)
{
    $('[data-translator-id=\"{0}\"]'.format(id)).remove();
}