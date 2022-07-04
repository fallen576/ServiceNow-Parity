(function () {
    'use strict';

    angular.module('ServiceNowParity')
        .controller('ModuleController', ModuleController);


    ModuleController.$inject = ['GlideService'];
    function ModuleController(GlideService) {
        
        var c = this;

        c.modules = [];
        c.table = [];
        c.headers = [];
		
		var sse = new EventSource('/sse');
    	sse.addEventListener("updateModule", (event) => {
			c.modules = [];
			c.fetchModules();
		});
		
		c.fetchModules = function() {
			var promise = GlideService.getAllModules();
        	promise.then((result) => {
            
	            for (var i in result.data) {
                	c.modules.push(result.data[i]);
            	}
        	})
        	.catch((error) => {
            	console.log("Error when reaching endpoint " + JSON.stringify(result));
        	});
		};
		
		c.fetchModules();
		
        c.loadTable = function(table) {
            c.headers = [];
            c.table = [];
            
            var data = GlideService.loadListView(table);
            data.then((result) => {
                console.log(JSON.stringify(result.data[0]));
                c.headers = Object.keys(result.data[0]);
                for (var i in result.data) {
                    c.table.push(result.data[i]);
                }  
                console.table(result.data[0]);
                console.log(c.headers);
            });
        };
	}
	
})();
