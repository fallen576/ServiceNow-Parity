(function () {
    'use strict';

    angular.module('ServiceNowParity')
        .service('GlideService', GlideService);


    GlideService.$inject = ["$http"]
    function GlideService($http) {
        var service = this;

        service.getAllModules = function () {
            var promise = $http({
                method: "GET",
                url: "http://localhost:8080/api/v1/modules"
            });
            return promise;
        };

        service.loadListView = function(table) {
            var promise = $http({
                method: "GET",
                url: ("http://localhost:8080/api/v1/table/" + table)
            });
            return promise;
        };
    }

})();
