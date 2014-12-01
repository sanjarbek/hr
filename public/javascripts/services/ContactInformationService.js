angular.module('app').service('ContactInformationService', function ($http) {

    this.save = function (passport) {
        return $http.post('/contact_informations/save', passport)
            .success(function (result) {
                return result;
            });
    }

    this.update = function (passport) {
        return $http.put('/contact_informations/update', passport)
            .success(function (result) {
                return result;
            })
    }

    this.getEmployeeContactInformation = function (employee_id) {
        return $http.get('/contact_informations/json/get', {params: {employeeId: employee_id}})
            .then(function (result) {
                return result.data;
            })
    }

});

