
var CROSSPROMO_TYPES = {
    header: 'crossPromosHeader',
    footer: 'crossPromosFooter'
},

CLASSNAMES = {
    crossPromosHeader: 'pf-cross-promos-header',
    crossPromosFooter: 'pf-cross-promos-footer',
    slotContainer: 'slot-container'
},

ELEMENT_NODE = (typeof(Node) !== 'undefined') ? Node.ELEMENT_NODE : 1,

consoleConfig,

positionQueues = {};


/**
 * Creates a DOM element with a given class
 * @param {String} type Element type
 * @param {String} className Class name
 * @returns DOM object
 */
function createElement(type, className){
    var container = document.createElement('div');
    container.className = className;
    return container;
}

/**
 * Gets the slot container for a given image
 * @param {Object} img Image object
 * @returns {Object} Container of the image
 */
function getImageSlotContainer(img){
    var slotContainer = img.parentNode;
    if(slotContainer.tagName.toLowerCase() == 'a'){
        slotContainer = slotContainer.parentNode;
    }
    return slotContainer;
}


/**
 * Update the image vertical position according to it's container size
 * @param {Object} img Image content
 * @param {Number} containerHeight Height of the container
 */
function updateImageVerticalPos(img, containerHeight){
    var slotContainer = getImageSlotContainer(img),
        imgHeight = img.offsetHeight;

    var newTopPos = parseInt((containerHeight - imgHeight) / 2, 10);

    if(imgHeight > 0 &&
       containerHeight > 0 &&
       newTopPos >= 0 &&
       (imgHeight + newTopPos) < containerHeight
      ){
        slotContainer.style.marginTop = newTopPos +'px';
    }
}


/**
 * Update the image horizontal position with a margin value
 * @param {Object} img Image content
 * @param {Number} leftMargin Left margin to be applied to the image container
 * @param {Number} rightMargin Right margin to be applied to the image container
 */
function updateImageHorizontalPos(img, leftMargin, rightMargin){
    var currentContainer = getImageSlotContainer(img);
    currentContainer.style.marginLeft = leftMargin +'px';
    currentContainer.style.marginRight = rightMargin +'px';
}


/**
 * Gets the margin value to be used for each items based on the
 * container size and the content size
 * @param {Number} containerWidth Width of the container
 * @param {Number} contentWidth Width of the content
 * @param {Number} contentItemsLength Length of the content items
 * @returns {Object} Object with margin value to be applied to individual
 * containers and the leftover margin to be applied to inner margins
 */
function getContentMargin(containerWidth, contentWidth, contentItemsLength){
    var unusedWidth = containerWidth - contentWidth;
    var margin;

    if(unusedWidth > 0){
        margin = parseInt(unusedWidth / contentItemsLength, 10);
        margin = parseInt(margin / 2, 10);

        var sumOfAllMargins = margin * (contentItemsLength * 2);
        var usedWidth = contentWidth + sumOfAllMargins;
        var leftoverWidth = containerWidth - usedWidth;
        var leftoverWidthPerItem = parseInt(leftoverWidth / (contentItemsLength - 1), 10);

        return {
            value: margin,
            leftover: leftoverWidthPerItem
        };
    }

    return {
        margin: unusedWidth
    };
}


/**
 * Gets the real width of all the loaded images
 * @param {Array} imgs Array of images
 * @param {Array} nonImages Objects present in the crosspromo which
 * @returns {Number} Sum of all the images width
 */
function getContentWidth(imgs, nonImages){
    var i, l;
    var contentWidth = 0;

    for(i=0, l=imgs.length; i<l; i++){
        contentWidth += imgs[i].offsetWidth;
    }

    for(i=0, l=nonImages.length; i<l; i++){
        contentWidth += nonImages[i].width;
    }

    return contentWidth;
}


/**
 * Updates the position of a slot based on it's size and the size of the content
 * @param {Object} img Image
 * @param {Array} nonImages Objects present in the crosspromo which
 * are not images
 */
function updateImagePosition(img, nonImages){
    var slotContainer = getImageSlotContainer(img);

    var slotStyle = getMyComputedStyle(slotContainer.parentNode);
    var containerHeight = parseInt(slotStyle.height, 10) || 0;
    var containerWidth = parseInt(slotStyle.width, 10) || 0;

    var imgs = slotContainer.parentNode.getElementsByTagName('img');

    var contentWidth = getContentWidth(imgs, nonImages);
    var rightMargin;
    var margin = getContentMargin(containerWidth, contentWidth, imgs.length);

    for(var i=0, l=imgs.length; i<l; i++){
        updateImageVerticalPos(imgs[i], containerHeight);

        if(margin.value > 0){
            rightMargin = margin.value;

            // if we have any leftover margins, add it to the
            // margin between all items
            if(i<l-1 && margin.leftover > 0){
                rightMargin += margin.leftover;
            }

            updateImageHorizontalPos(imgs[i], margin.value, rightMargin);
        }
    }
}


/**
 * Creates the link element and attaches the necessary behaviour based on the url
 * as well as tracking information
 * @param {Object} content Content definition for the parent widget
 * @param {String} trackingZone Zone ID for event tracking
 * @returns {Object} HTML element for the link
 */
function createLinkElement(content, trackingZone){
    var link = document.createElement('a'),
        url = content.url;



        link.href = url;
        link.target = '_blank';
    return link;
}



/**
 * Creates and returns HTML elements for an image with an hyperlink
 * @param {Object} content Content definition for the widget
 * @param {String} trackingZone Zone ID for event tracking
 * @returns {Object} html object for hyperlink
 */
function createURLLinkWidget(content, trackingZone){
    var link = createLinkElement(content, trackingZone),
        img = document.createElement('img');

    img.src = content.src;
    img.alt = '';

    link.appendChild(img);
    return link;
}



/**
 * Creates the widgets based on the given content and inserts them into the containers
 * @param {Object} content Content definition for the widgets
 * @param {Object} containers Containers into which the content is to be inserted
 * @param {String} trackingZone Zone ID for event tracking
 */
function insertWidgets(content, containers, trackingZone){
    var widget;

    var parentStyle = getMyComputedStyle(containers[0].parentNode)
    var parentWidth = parseInt(parentStyle.width, 10) || 0;

    var containerSize = {
        width: parseInt(parentWidth / containers.length, 10),
        height: parseInt(parentStyle.height, 10) || 0
    };

    for(var i=0, l=containers.length; i<l; i++){

        widget = createURLLinkWidget(content[i], trackingZone);

        if(widget !== null){
            containers[i].appendChild(widget);
        }
    }
}


/**
 * Creates containers for the available slots and injects them into the DOM
 * @param {Number} slotQuantity Number of slots to create
 * @param {Object} container DOM element where the containers are to be injected
 * @return {Array} array with created DOM element references
 */
function createContainersOnDOM(slotQuantity, container){
    var slotContainer,
        containerReferences = [];

    for(var i=0; i<slotQuantity; i++){
        slotContainer = createElement('div', CLASSNAMES.slotContainer);

        containerReferences.push(slotContainer);
        container.appendChild(slotContainer);
    }

    return containerReferences;
}


/**
 * Renders the DOM elements for the crosspromos
 * @param {Obejct} content Content data for crosspromo
 * @param {String} promoType Indicates the type (header or footer) of the crosspromo
 * @param {String} nextSibling Insert the crosspromo before this element
 * @param {String} consoleContainer Id for the console container
 */
function renderElements(content, promoType, consoleContainer, nextSibling){
    var trackingZone = 0

    var mainContainer = createElement('div', CLASSNAMES[promoType]);
    consoleContainer.insertBefore(mainContainer, nextSibling);

    positionQueues[trackingZone] = {
        count: 0,
        max: content.length,
        nonImages: []
    };
    var containers = createContainersOnDOM(content.length, mainContainer);
    insertWidgets(content, containers, trackingZone);
}


/**
 * Initializes a set of cross promotions based on a configuration object
 * @public
 * @see <a href="https://playfish.jira.com/wiki/display/CONSOLE/Cross+Promo+footer+porting+to+JS">Spec format</a>
 * @param {Object} configObject Object holding the cross promotions configuration
 * @param {Object} consoleConfigObject Console configuration object
 * @param {String} consoleContainerId Id for the console container
 * @param {String} nextSibling Insert the crosspromo before this element
 * @param {String} promoType Indicates the type (header or footer) of the crosspromo
 */
function init(configObject, consoleContainerId, nextSibling){
    var config = configObject,
        content;

    if('content' in configObject && '@attributes' in configObject.content){
        config = parseConfig(configObject);
    }

    content = getContentBasedOnWeightAndCountry(
        config.slots,
        config.contentPool
    );

    renderElements(
        content,
        'crossPromosHeader',
        document.getElementById(consoleContainerId),
        nextSibling
    );
}