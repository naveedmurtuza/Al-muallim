/*
 * Author:      Marco Kuiper (http://www.marcofolio.net/)
 */

// Speed of the automatic slideshow
var slideshowSpeed = 6000;
var removeAnimation;

/**
 * called by the Java app after initializing
 * unhides the search the text and grabs focus on the textfield
 * @returns {undefined}
 */
function doInit()
{
    $('#search').removeClass('hidden');
    $("#searchTxt").focus();
}

$(document).ready(function() {

    var pageOffset = 1;
    /**
     * Fetch more results when user scrolls down to the end
     * of the page
     */
    $(window).scroll(function() {
        //only fetch more results if #search has class header
        //by defautl it has the class main
        //coz that means we are in 'results view'
        if ($('#search').hasClass('header'))
        {
            var scrollPoint = ($(document).height() / 5);
            if ($(window).scrollTop() >= $(document).height() - $(window).height() - scrollPoint) {
                search.actionPerformed($('#searchTxt').val(), ++pageOffset);
            }
        }
    });


    $("#searchBtn").click(function() {
        var text = $('#searchTxt').val();
        if (text === '')
            return;
        //clear the existing results
        $("#searchResults").empty();
        search.actionPerformed(text, 1);
    });
    $("#searchTxt").keyup(function(event) {
        if (event.keyCode === 13) {
            $("#searchBtn").click();
        }
    });
    //enable the tips
    $('#searchResults').on('click', 'article', function() {
        var url = $(this).data('url');
        alert(url);
        open.openResult(url);
    });
    $('.tips').qtip({
        content: {
            text: $('#searchTips'), button: true // Add .clone() if you don't want the matched elements to be removed, but simply copied
        }, style: {
            widget: true, // Use the jQuery UI widget classes
            def: false // Remove the default styling (usually a good idea, see below)
        },
        show: {
            effect: function() {
                $(this).slideDown();
            }
        },
        hide: {
            event: 'click',
            effect: function() {
                $(this).slideUp();
            }
        }
    });
    // Backwards navigation
    $("#back").click(function() {
        stopAnimation();
        navigate("back");
    });

    // Forward navigation
    $("#next").click(function() {
        stopAnimation();
        navigate("next");
    });


    var interval;
    $("#control").clicktoggle(function() {
        stopAnimation();
    }, function() {
        // Change the background image to "pause"
//        $(this).css({"background-image": "url(images/btn_pause.png)"});
        var selector = $("#control");
        if (selector.hasClass("play"))
            selector.removeClass("play");
        selector.addClass("pause");
        // Show the next image
        navigate("next");

        // Start playing the animation
        interval = setInterval(function() {
            navigate("next");
        });
    });


    var activeContainer = 1;
    var currentImg = 0;
    var animating = false;
    var navigate = function(direction) {
        // Check if no animation is running. If it is, prevent the action
        if (animating) {
            return;
        }

        // Check which current image we need to show
        if (direction == "next") {
            currentImg++;
            if (currentImg == photos.length + 1) {
                currentImg = 1;
            }
        } else {
            currentImg--;
            if (currentImg == 0) {
                currentImg = photos.length;
            }
        }

        // Check which container we need to use
        var currentContainer = activeContainer;
        if (activeContainer == 1) {
            activeContainer = 2;
        } else {
            activeContainer = 1;
        }
        showImage(photos[currentImg - 1], currentContainer, activeContainer);
    };

    var currentZindex = -1;
    var showImage = function(photoObject, currentContainer, activeContainer) {
        animating = true;

        // Make sure the new container is always on the background
        currentZindex--;
        // Set the background image of the new active container
        $("#headerimg" + activeContainer).css({
            "background-image": "url(\"" + photoObject.image + "\")", // + " center center no-repeat",
            "display": "block",
        });

        // Hide the header text
        //$("#headertxt").css({"display" : "none"});

        // Set the new header text
        $("#firstline").html(photoObject.firstline);



        // Fade out the current container
        // and display the header text when animation is complete
        $("#headerimg" + currentContainer).fadeOut(function() {
            setTimeout(function() {
                $("#headertxt").css({"display": "block"});
                animating = false;
            }, 500);
        });
    };

    var stopAnimation = function() {
        // Change the background image to "play"
//        $("#control").css({"background-image": "url(images/btn_play.png)"});
        var selector = $("#control");
        if (selector.hasClass("pause"))
            selector.removeClass("pause");
        selector.addClass("play");
        // Clear the interval
        clearInterval(interval);
    };

    removeAnimation = function() {
        stopAnimation();
        $('#background-animation').remove();
    };
    // We should statically set the first image
    navigate("next");

    // Start playing the animation
    interval = setInterval(function() {
        navigate("next");
    }, slideshowSpeed);

});

function addSearchResult(json) {
    var obj = $.parseJSON(json);
    var codetags = '';
    $.each(obj.fields, function(key, value) {
        var codetag = '<code>{0} : {1}</code>'.format(key, value);
        codetags += codetag;
    });
    var html = '<article class=\"result\" data-url=\"{0}\" data-module=\"{1}\"><a href=\"#\">{2}</a><p>{3}</p>{4}</article>'.format(obj.uri, obj.moduleName, obj.title, obj.abstractTxt, codetags);
    $(html).appendTo("#searchResults");
}