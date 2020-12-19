(function () {
    'use strict';

    angular.module('ServiceNowParity')
        .controller('CreateTableController', CreateTableController);

    CreateTableController.$inject = ['$http'];
    function CreateTableController($http) {

        var c = this;
        c.fields = [{
            "fieldName": "",
            "fieldType": ""
        }];
        c.name = "";
        c.error = false;

        c.addField = function () {

            if (c.fields.length === 0) {
                c.fields.push({
                    "fieldName": "",
                    "fieldType": ""
                });
                return;
            }

            if (c.fields[c.fields.length-1].fieldName == "" || c.fields[c.fields.length-1].fieldType == "") {
                c.error = true;
                return;
            }

            c.error = false;

            c.fields.push({
                "fieldName": "",
                "fieldType": ""
            });
        };

        c.removeField = function () {
            c.fields.pop();
        }
    
        c.submit = function () {
            $http({
                url: ''
            });
        };

    };

}) ();
