(function () {
    'use strict';

    angular.module('ServiceNowParity')
        .controller('CreateTableController', CreateTableController);


    function CreateTableController() {
        
        var c = this;
        c.fields = [];
        
        c.addField = function() {
            c.fields.push(1);
        };

        c.removeField = function() {
            c.fields.pop();
        }

        c.submit = function() {
            
        };
    }

})();
