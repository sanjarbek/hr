angular.module('app').service('DepartmentService', function ($http) {

    this.save = function (department) {
        $http.post('/departments/save', department)
            .success(function (result) {
                console.log(result);
                return result;
            });
    }

    this.list = function () {
        return $http.get('/departments/json/list').then(function (result) {
            return result.data;
        });
    }

});

