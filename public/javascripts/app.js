var app = angular.module('app', ['ui.router']);

app.config(function($stateProvider, $urlRouterProvider) {

    $stateProvider
        .state('employeeCreate', {
            url: '/employees/create',
            templateUrl : '/employees/create',
            controller  : 'EmployeeController'
        })
        .state('employeeList', {
            url: '/employees/list',
            templateUrl : '/employees/list',
            controller  : 'EmployeeController'
        });
});

app.service('EmployeeService', function ($http) {
    //to create unique employee id
    var uid = 1;

    //employees array to hold list of all employees
    var employees = [];

    //save method create a new employee if not already exists
    //else update the existing object
    this.save = function (employee) {
        if (employee.id == null) {
            //if this is new employee, add it in employees array
            employee.id = uid++;
            employees.push(employee);
        } else {
            //for existing contact, find this employee using id
            //and update it.
            for (i in employees) {
                if (employees[i].id == employee.id) {
                    employees[i] = employee;
                }
            }
        }

    }

    //simply search contacts list for given id
    //and returns the employee object if found
    this.get = function (id) {
        for (i in employees) {
            if (employees[i].id == id) {
                return employees[i];
            }
        }

    }

    //iterate through contacts list and delete
    //contact if found
    this.delete = function (id) {
        for (i in employees) {
            if (employees[i].id == id) {
                employees.splice(i, 1);
            }
        }
    }

    //simply returns the contacts list
    this.list = function () {
        $http.get('/employees/json/list' ).success(function (largeLoad) {
            employees = largeLoad;
        });
        return employees;
    }
});

app.controller('EmployeeController', function ($scope, EmployeeService) {

    $scope.employees = EmployeeService.list();

    $scope.saveEmployee = function () {
        EmployeeService.save($scope.newEmployee);
        $scope.newEmployee = {};
    }


    $scope.delete = function (id) {

        EmployeeService.delete(id);
        if ($scope.newEmployee.id == id) $scope.newEmployee = {};
    }


    $scope.edit = function (id) {
        $scope.newEmployee = angular.copy(EmployeeService.get(id));
    }
})