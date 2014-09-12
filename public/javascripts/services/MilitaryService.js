angular.module('app').service('MilitaryService', function ($http) {

    this.save = function (military) {
        console.log(military);
        return $http.post('/militaries/save', military)
            .success(function (result) {
                return result;
            });
    }

    this.list = function (employee_id) {
        return $http.get('/employees/json/military', {params: {employeeId: employee_id}}).then(function (result) {
            return result.data;
        });
    }

    this.update = function (military) {
        return $http.put('/militaries/update', military)
            .success(function (result) {
                return result;
            })
    }

    this.delete = function (military) {
        return $http.delete('/militaries/delete', {params: {id: military.id}})
            .success(function (result) {
                return result;
            })
    }

});


