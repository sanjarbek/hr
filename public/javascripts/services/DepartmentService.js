angular.module('app').service('DepartmentService', function ($http) {

    this.save = function (department) {
        return $http.post('/departments/save', department)
            .success(function (result) {
                return result;
            });
    }

    this.list = function () {
        return $http.get('/departments/json/list').then(function (result) {
            return result.data;
        });
    }

});

