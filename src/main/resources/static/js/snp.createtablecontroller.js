(function () {
    'use strict';

    angular.module('ServiceNowParity')
        .controller('CreateTableController', CreateTableController);

    CreateTableController.$inject = ['$http', '$scope', '$rootScope'];
    function CreateTableController($http, $scope, $rootScope) {

        var c = this;

        c.fields = [{
            "fieldName": "",
            "fieldType": "",
            "reference": false,
            "referenceField": {
                "value": "",
                "display_value": ""
            }
        }];

        c.name = "";
        c.nameError = false;
        c.error = false;
        c.url = "";
        c.genericError = "";

        c.addField = function () {

            setTimeout(() => {
                $scope.$digest();
              }, 5);

            if (c.fields.length === 0) {
                c.fields.push({
                    "fieldName": "",
                    "fieldType": "",
                    "reference": false,
                    "referenceField": {
                        "value": "",
                        "display_value": ""
                    }
                });
                return;
            }

            if (c.fields[c.fields.length - 1].fieldName == "" || c.fields[c.fields.length - 1].fieldType == "") {
                c.error = true;
                return;
            }

            c.error = false;

            c.fields.push({
                "fieldName": "",
                "fieldType": "",
                "reference": false,
                "referenceField": {
                    "value": "",
                    "display_value": ""
                }
            });
        };

        c.setFrameDestination = function(name) {
            c.url = '/reference/modules?name=' + name;
        };

        c.ref = function (i) {
            c.fields[i].reference = c.fields[i].fieldType == "reference" ? true : false;
        };

        c.removeField = function (i) {
            c.fields.splice(i, 1);
        };


        c.submit = function () {
            
            if (c.name === "") {
                c.nameError = true;
                return;
            }
            c.nameError = false;

            $http({
                url: "/api/v1/table/create",
                method: "POST",
                data: ({ "tableName": c.name, "tableFields": c.fields })
            }).then(function (data) {
                //alert(JSON.stringify(data));
                location.href = "/" + c.name + ".do";
            }, function (data) {
                alert(data.data);
                console.log(data);
                c.genericError = data.data.split("org.h2.jdbc.JdbcSQLException: ")[1];
            });
        };

        $scope.$on('update', function(event, reference) {

            $scope.$apply(function() {
                c.fields[reference.index].referenceField.value = reference.value;
                c.fields[reference.index].referenceField.display_value = reference.display_value;
            });

        });
    }

})();
