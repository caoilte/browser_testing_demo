var TYPES = {
        Header: 'header',
        Footer: 'footer'
    };

    /**
     * Gets the element computed style
     * @param {Object} element DOM element
     * @returns {Object} Computed style object
     */
    function getMyComputedStyle(element){
        if(window.getComputedStyle){
            return window.getComputedStyle(element, null);
        } else {
            return element.currentStyle;
        }
    }

$(document).ready(function(){
    var url = "http://localhost:8080/mcs/bootstrap.json&callback=?";
    $.getJSON(url);
});

function bootstrapCallback(consoleConfig) {
    var config = consoleConfig.consoleFacetConfig['header'];
    init(config, 'console')
}