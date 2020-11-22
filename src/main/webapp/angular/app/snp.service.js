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
                url: "http://localhost:8080/SNP/Glide"
            });
            return promise;
        };

        service.loadListView = function(table) {
            var promise = $http({
                method: "GET",
                url: ("http://localhost:808/table/" + table)
            });
            return promise;
        };
    }

})();
