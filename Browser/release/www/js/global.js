String.prototype.format = function() {
    var args = arguments;
    return this.replace(/\{\{|\}\}|\{(\d+)\}/g, function(m, n) {
        if (m === "{{") {
            return "{";
        }
        if (m === "}}") {
            return "}";
        }
        return args[n];
    });
};
$.fn.clicktoggle = function(a, b) {
    return this.each(function() {
        var clicked = false;
        $(this).click(function() {
            if (clicked) {
                clicked = false;
                return b.apply(this, arguments);
            }
            clicked = true;
            return a.apply(this, arguments);
        });
    });
};
$.fn.flash = function(options) {
    var config = {
        'highlightColor': '#FFFF9C',
        'duration': 1000,
        'removeBackground': true
    };
    if (options) {
        $.extend(config, options);
    }
    var container = $(this);
    if (container.length)
    {
        var originalColor = container.css('backgroundColor');
        container.animate({
            backgroundColor: config.highlightColor
        }, config.duration, 'linear', function() {
            $(this).animate({
                backgroundColor: originalColor
            }, config.duration, function() {
                if (config.removeBackground)
                {    //my hover fix! the hover color change was not working
                    //without this snippet
                    container.css('background-color', '');
                }
            });
        });
    }
};
$.fn.ensureVisible = function(options) {
    var container = $(this);
    var config = {
        'highlightColor': '#FFFF9C',
        'flash': true,
        'duration': 1000,
        'removeBackground': true
    };
    if (options) {
        $.extend(config, options);
    }
    $('html, body').animate({
        scrollTop: container.offset().top - 20
    }, config.duration, function() {
        if (config.flash)
        {
            container.flash();
        }
    });
};
$(document).ready(function() {
    //insert the loading div into the dom
    var html = '<div id="loader" class="hidden loader"><span style="position: relative; left: 40px;top: 8px; color: white">Loading ...</span></div>';
    $("body").prepend(html);


});

function showLoader()
{
    $("#loader").removeClass("hidden");
}

function hideLoader()
{
    $("#loader").addClass("hidden");
}

function getUrl()
{
    return $('#document').data('url');
}

function notifyInformation(text)
{
    var n = noty({
        text: text,
        type: 'information',
        dismissQueue: true,
        layout: 'top',
        theme: 'defaultTheme'
    });
    return n;
}
function notifyError(text)
{
    var n = noty({
        text: text,
        type: 'error',
        dismissQueue: true,
        layout: 'top',
        theme: 'defaultTheme'
    });
    return n;
}
function notifyWarning(text)
{
    var n = noty({
        text: text,
        type: 'warning',
        dismissQueue: true,
        layout: 'top',
        theme: 'defaultTheme'
    });
    return n;
}
function notifySuccess(text)
{
    var n = noty({
        text: text,
        type: 'success',
        dismissQueue: true,
        layout: 'top',
        theme: 'defaultTheme'
    });
    return n;
}
function notify(text)
{
    var n = noty({
        text: text,
        type: 'notification',
        dismissQueue: true,
        layout: 'top',
        theme: 'defaultTheme'
    });
    return n;
}


function scrollTo(selector)
{
    $('html, body').animate({
        scrollTop: $(selector).offset().top - 20
    }, 2000, function() {
        flashColor(selector);
    });
}

function flashColor(selector)
{
    var container = $(selector);
    if (container.length)
    {
        var originalColor = container.css('backgroundColor');
//the container background is returning tranparent!!
//and is messing with my hover color.. so just bad fix!
//        var originalColor = "#FFFFFF";
        container.animate({
            backgroundColor: "#FFFF9C"
        }, 1000, 'linear', function() {
            $(this).animate({
                backgroundColor: originalColor
            }, 1000, function() {
                //my hover fix! the hover color change was not working
                //without this snippet
                $(selector).css('background-color', '');
            });
        });
    }
}