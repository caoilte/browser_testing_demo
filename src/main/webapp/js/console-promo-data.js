        /**
         * Checks if a given object is an array, and if not, wraps it in an array
         * XML to JSON parsers don't create arrays when there is only one child
         * tag on a tree structure, so this check is needed
         * @param {Array|Object} originalObject Object to check
         * @returns {Array} Original object or original object wrapped in an array
         */
        function makeArray(originalObject){
            if(Object.prototype.toString.call(originalObject) !== '[object Array]'){
                return [originalObject];
            }
            return originalObject;
        }


        /**
         * Parses the slots XML converted JSON into a better format
         * @param {Array} slots Original configuration format (check example)
         * @returns {Array} Array with slots
         * @example parseSlotsConfig([{
                        "weight":
                            [{
                                "@attributes": {
                                    "src": "src1",
                                    "country": "",
                                    "weight": "100"
                                }
                            }]
                    }]);
         */
        function parseSlotsConfig(slots){
            var newWeight,
                originalWeight,
                newSlots = [];

            // create the array of slots
            for(var i=0, li=slots.length; i<li; i++){
                originalWeight = makeArray(slots[i].weight);
                newWeight = parseWeightsConfig(originalWeight);
                newSlots.push(newWeight);
            }

            return newSlots;
        }


        /**
         * Parses the weights XML converted JSON into a better format
         * @param {Array} slots Original configuration format (check example)
         * @returns {Array} Array with weight objects
         * @example parseWeightsConfig([{
                        "@attributes": {
                            "src": "src1",
                            "country": "",
                            "weight": "100"
                        }
                    }]);
         */
        function parseWeightsConfig(weights){
            var weight,
                newWeight = [];

            // each slot position is populated by an array of weights
            for(var i=0, l=weights.length; i<l; i++){
                if('@attributes' in weights[i]){
                    weight = weights[i]['@attributes'];

                    newWeight.push({
                        weight: weight.weight, // the weight value
                        country: weight.country || '',
                        src: weight.src
                    });
                }
            }

            return newWeight;
        }


        /**
         * Parses the contentPool XML converted JSON into a better format
         * @param {Array} contentPool Original configuration format (check example)
         * @returns {Array} Array with content objects
         * @example parseContentPoolConfig([{
                    "@attributes": {
                        "contentClass": "URLLink",
                        "src": "src1",
                        "campaignId": "petsfooterstsunami"
                    },
                    "url": {
                        "@attributes": {
                            "src": "20110315_pets_footer_mercycorps2.png",
                            "url": "http:\/\/playfi.sh\/fEzsXY"
                        }
                    }
                }]);
         */
        function parseContentPoolConfig(contentPool){
            var content,
                attributes,
                urlAttributes,
                newContentPool = {};

            for(var i=0, l=contentPool.length; i<l; i++){
                content = contentPool[i];
                if(('@attributes' in content) && ('url' in content)){
                    attributes = content['@attributes'];

                    if(('@attributes' in content.url) && ('src' in attributes)){
                        urlAttributes = content.url['@attributes'];

                        // the src parameter is used as a unique identifier for the pool
                        newContentPool[attributes.src] = {
                            campaignId: attributes.campaignId || '',
                            contentClass: attributes.contentClass,
                            src: urlAttributes.src || '',
                            url: urlAttributes.url || ''
                        };
                    }
                }
            }

            return newContentPool;
        }


        /**
         * Parses the current JSON structured which is converted to XML to a more manageable format
         * @public
         * @param {Object} originalConfig Original configuration format
         * @param {Object} consoleConfigObject Console configuration object
         * @returns {Object} new configuration format
         */
        function parseConfig(originalConfig){

            var slots = makeArray(originalConfig.content.slots.slot),
                contentPool = makeArray(originalConfig.content.contentPool.content);

            return {
                slots: parseSlotsConfig(slots),
                contentPool: parseContentPoolConfig(contentPool)
            };
        }


        /**
         * Reprocesses an array of weights by country, if it's defined
         * @param {Array} weights Initial array of weights
         * @param {String} country Country filter
         * @returns {Array} reprocessed array of weights by specified country
         */
        function reprocessByCountry(weights, country){
            var weight,
                re,
                countryIsDefined = false,
                newWeights = [];

            for(var i=0, l=weights.length; i<l; i++){
                weight = weights[i];

                // if a country is defined, we'll add it to the content
                if((typeof(weight.country) === 'string') && (weight.country !== '')){
                    re = new RegExp(country, 'gi');

                    if(weight.country.search(re) != -1){
                        // as soon as we find a country, we reset the list
                        // and make sure we don't add more undefined entries
                        if(!countryIsDefined){
                            newWeights = [];
                            countryIsDefined = true;
                        }

                        newWeights.push(weight);
                    }

                // if the country is not defined and we haven't found any country so far,
                // we keep adding these to the list
                } else if(!countryIsDefined){
                    newWeights.push(weight);
                }
            }

            return newWeights;
        }


        /**
         * Sums up all weight values of a weights array
         * @param {Array} weights Array of weights
         * @return {Number} Summed up weights of the array
         */
        function getTotalWeight(weights){
            var totalWeight = 0;

            for(var i=0, l=weights.length; i<l; i++){
                totalWeight += parseInt(weights[i].weight, 10);
            }

            return totalWeight;
        }



        /**
         * Selects a weight randomly across the ones available
         * @param {Array} weights Array of weights
         * @return {Object} Weight object
         */
        function selectWeight(weights){
            var totalWeight = getTotalWeight(weights),
                // creates a random selection point inside the total weight range
                selectionPoint = totalWeight * Math.random(),
                weightCount = 0,
                weight;

            for(var i=0, l=weights.length; i<l; i++){
                weight = weights[i];
                weightCount += parseInt(weight.weight, 10);

                // only select if our selection point is below the current count
                if(selectionPoint < weightCount){
                    return weight;
                }
            }
        }


        /**
         * Decides which content should be shown based on the slots definitions
         * @param {Array} slots Available slots
         * @param {Object} contentPool Object with available content
         * @returns {Array} Array with content that will be shown
         */
        function getContentBasedOnWeightAndCountry(slots, contentPool){
            var selectedWeight,
                weights,
                content = [],
                country;

            for(var i=0, l=slots.length; i<l; i++){
                weights = reprocessByCountry(slots[i], 'en');
                selectedWeight = selectWeight(weights);

                content.push(contentPool[selectedWeight.src]);
            }

            return content;
        }