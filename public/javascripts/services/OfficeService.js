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
