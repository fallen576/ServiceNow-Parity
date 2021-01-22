(function () {
    'use strict';

    angular.module('ServiceNowParity')
        .controller('ReferenceController', ReferenceController);

        ReferenceController.$inject = ['$http', '$rootScope', '$scope', '$window'];
    function ReferenceController($http, $rootScope, $scope, $window) {

        var c = this;

        c.trigger = function(item) {

            var url = window.location.href;
            var id = url.split("?name=")[1];
            var ind = id.split("_")[1];
            var reference = {
                "id": id,
                "index": ind,
                "value": item.currentTarget.getAttribute('raw_value'),
                "display_value": item.currentTarget.getAttribute('display_value')
            };

            if ($window.parent.angular != undefined) {
                $window.parent.angular.element($window.frameElement).scope().$emit('update', reference);
            }

            window.top.$("#"+id).val(reference.value);
            window.top.$("#"+id+"_dv").val(reference.display_value);

            //have to close in the parent window.
            parent.close();
        };

    }
})();