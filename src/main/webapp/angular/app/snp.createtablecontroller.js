(function () {
    'use strict';

    angular.module('ServiceNowParity')
        .controller('CreateTableController', CreateTableController);

    CreateTableController.$inject = ['$http', '$scope'];
    function CreateTableController($http, $scope) {

        var c = this;
        c.fields = [{
            "fieldName": "",
            "fieldType": "",
            "reference": false,
            "referenceValue": "",
            "referenceTableName": "",
            "dv": ""
        }];
        
        c.name = "";
        c.nameError = false;
        c.error = false;

        c.addField = function () {

            if (c.fields.length === 0) {
                c.fields.push({
                    "fieldName": "",
                    "fieldType": "",
                    "reference": false,
                    "referenceValue": "",
                    "referenceTableName": "",
                    "dv": ""
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
                "fieldType": "",
                "reference": false,
                "referenceValue": "",
                "referenceTableName": "",
                "dv": ""
            });
        };

        c.ref = function(i) {
            c.fields[i].reference = c.fields[i].fieldType == "reference" ? true : false;
        };

        c.removeField = function (i) {
            c.fields.splice(i, 1);
        };

        c.setReferenceField = function(i, value, dv, name) {
        	c.fields[i].referenceTableName = name;
            c.fields[i].referenceValue = value;
            c.fields[i].dv = dv;
        };

		
        c.submit = function () {
            //alert(JSON.stringify(c.fields))
            if (c.name === "") {
                c.nameError = true;
                return;
            }
            c.nameError = false;

            $http({
                url: "/api/v1/table/create",
                method: "POST",
                data: ({"tableName": c.name, "tableFields": c.fields})
            }).success(function(data) {
                //alert(JSON.stringify(data));
                location.href = "/"+c.name+".do";
            }).error(function(data) {
                alert("whoops " + JSON.stringify(data));
            });
        };

    };

}) ();
