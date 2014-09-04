angular.module('app').service('OfficeService', function ($http) {

    this.save = function (office) {
        $http.post('/offices/save', office)
            .success(function (result) {
                console.log(result);
            });
    }

    this.list = function () {
        return $http.get('/offices/json/list').then(function (result) {
            return result.data;
        });
    }

});


angular.module('app').service('OfficeTypeService', function ($http) {

    this.save = function (office_type) {
        $http.post('/office_types/save', office_type)
            .success(function (result) {
                console.log(result);
            });
    }

    this.list = function () {
        return $http.get('/office_types/json/list').then(function (result) {
            return result.data;
        });
    }

});
