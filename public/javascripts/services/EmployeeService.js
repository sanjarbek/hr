angular.module('app').service('EmployeeService', function ($http, $state) {
    //employees array to hold list of all employees
    var employees = [];

    var activeEmployee = {};

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

    //iterate through employees list and delete
    //contact if found
    this.delete = function (id) {
        for (i in employees) {
            if (employees[i].id == id) {
                employees.splice(i, 1);
            }
        }
    }

    //simply returns the employees list
    this.list = function () {
        return $http.get('/employees/json/list').then(function (result) {
            return result.data;
        });
    }
});
