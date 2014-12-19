angular.module('app').service('EmployeeService', function ($http, $state) {

    this.save = function (employee) {
        $http.post('/employees/save', employee)
            .success(function (employee) {
                console.log(employee)
                $state.go('panel.employees.detail', {employeeId: employee.id});
            });
    }

    this.update = function (employee) {
        return $http.put('/employees/update', employee)
            .success(function (result) {
                return result;
            });
    }

    this.get = function (id) {
        return $http.get('/employees/json/get', { params: {'id': id}}).then(function (result) {
            return result.data;
        })

    }

    this.unemployedList = function () {
        return $http.get('/employees/json/list', {params: {'status': false}}).then(function (result) {
            return result.data;
        });
    }

    this.employedList = function () {
        return $http.get('/employees/json/list', {params: {'status': true}}).then(function (result) {
            return result.data;
        });
    }

    //simply returns the employees list
    this.list = function () {
        return $http.get('/employees/json/list').then(function (result) {
            return result.data;
        });
    }
});

angular.module('app').service('RelationshipStatusService', function ($http) {
    this.list = function () {
        return $http.get('/relationship_statuses/json/list').then(function (result) {
            return result.data;
        });
    }

    this.save = function (relationshipStatus) {
        return $http.post('/relationship_statuses/save', relationshipStatus)
            .success(function (result) {
                return result;
            });
    }
});

angular.module('app').service('NationalityService', function ($http) {
    this.list = function () {
        return $http.get('/nationalities/json/list').then(function (result) {
            return result.data;
        });
    }

    this.save = function (nationality) {
        return $http.post('/nationalities/save', nationality)
            .success(function (result) {
                return result;
            });
    }
});
