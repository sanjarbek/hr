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

    this.delete = function (passport) {
        return $http.delete('/relationships/delete', {params: {id: passport.id}})
            .success(function (result) {
                return result;
            })
    }

});

