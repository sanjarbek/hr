angular.module('app').service('PassportService', function ($http) {

    this.save = function (passport) {
        return $http.post('/passports/save', passport)
            .success(function (result) {
                return result;
            });
    }

    this.list = function (employee_id) {
        return $http.get('/employees/json/passports', {params: {employeeId: employee_id}}).then(function (result) {
            return result.data;
        });
    }

    this.update = function (passport) {
        return $http.put('/passports/update', passport)
            .success(function (result) {
                return result;
            })
    }

    this.getEmployeePassport = function (employee_id) {
        return $http.get('/passports/json/get', {params: {employee_id: employee_id}})
            .then(function (result) {
                return result.data;
            })
    }

});

