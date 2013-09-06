/**
 * Replaces one or more format items in a specified string with the string representation 
 * of a specified object. C# Style
 * @returns {unresolved}
 */
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
/**
 * JQuery 1.9 deprecated the toggle function. Just Convenience method to replace that
 * @param {type} a function to call if in toggled state
 * @param {type} b function to call if not in toggled state
 * @returns {unresolved}
 */
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
/**
 * Flashes (highlights) the current selector
 * @param {type} options an array containing options
 *<pre><code>
 *var config = {
 *      'highlightColor': '#FFFF9C',
 *      'duration': 1000,
 *      'removeBackground': true
 *  };
 *</code></pre>
 * @returns {undefined}
 */
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
/**
 * 
 * Enusures the given selector is visible by scrolling to the selector. Option to flash, highlight
 * the selector.
 * @param {type} options an array containing options
 * <pre><code>
 * var config = {
 *   'highlightColor': '#FFFF9C',
 *    'flash': true,
 *     'duration': 1000,
 *      'removeBackground': true
 *   };
 * </code></pre>
 * @returns {undefined} */
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

/**
 * displays the animated loader
 * @returns {undefined}
 */
function showLoader()
{
    $("#loader").removeClass("hidden");
}

/**
 * hides the loader
 * @returns {undefined}
 */
function hideLoader()
{
    $("#loader").addClass("hidden");
}

/**
 * Gets the data-url attribute from the document
 * @returns {String} url
 */
function getUrl()
{
    return $('#document').data('url');
}
/**
 * Displays a information bar
 * @param {String} text
 * @returns {noty}
 */
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
/**
 * Displays a error bar
 * @param {String} text
 * @returns {noty}
 */
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
/**
 * Displays a warning bar
 * @param {String} text
 * @returns {noty}
 */
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
/**
 * Displays a success bar
 * @param {String} text
 * @returns {noty}
 */
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
/**
 * Displays a notification bar
 * @param {String} text
 * @returns {noty}
 */
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

function getSelectItem()
{
    return   $('article.selected').data();
}
/**
 * @see http://stackoverflow.com/a/5379408
 * @returns {String} selected text
 */
function getSelectionText() {
    var text = "";
    if (window.getSelection) {
        text = window.getSelection().toString();
        // The second branch is for IE <= 8 (IE 9 implements window.getSelection()). 
        // The document.selection.type check is testing that the selection is a text selection rather 
        // than a control selection. In IE, a control selection is a selection inside an editable
        //  element containing one or more elements (such as images and form controls)
        //   with outlines and resize handles. If you call .createRange() on such a selection, 
        //   you get a ControlRange rather than a TextRange, and ControlRanges have no text property
    } else if (document.selection && document.selection.type !== "Control") {
        text = document.selection.createRange().text;
    }
    return text;
}