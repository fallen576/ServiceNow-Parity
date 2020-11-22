(function () {
    'use strict';

    angular.module('ServiceNowParity')
        .controller('ModuleController', ModuleController);


    ModuleController.$inject = ['GlideService'];
    function ModuleController(GlideService) {
        
        var c = this;
        c.modules = [];

        var promise = GlideService.getAllModules();
        promise.then((result) => {
            
            for (var i in result.data) {
                c.modules.push(result.data[i]);
                console.log(result.data[i]);
            }
        })
        .catch((error) => {
            console.log("Error when reaching endpoint " + JSON.stringify(result));
        });
    }

})();
