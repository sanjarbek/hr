angular.module('app').service('InstitutionService', function ($http) {

    this.save = function (institution) {
        $http.post('/institutions/save', institution)
            .success(function (result) {
                console.log(result);
            });
    }

    this.list = function () {
        return $http.get('/institutions/json/list').then(function (result) {
            return result.data;
        });
    }

});