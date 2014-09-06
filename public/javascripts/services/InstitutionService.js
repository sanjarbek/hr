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

angular.module('app').service('EducationService', function ($http) {

    this.save = function (institution) {
        return $http.post('/educations/save', institution)
            .success(function (result) {
//                console.log(result);
                return result;
            });
    }

    this.list = function () {
        return $http.get('/educations/json/list').then(function (result) {
            return result.data;
        });
    }

});