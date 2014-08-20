var app = angular.module('app', ['ui.router', 'ngTable', 'ngAnimate']);

app.config(function($stateProvider, $urlRouterProvider) {

    $stateProvider
        .state('employees', {
            url: '',
            templateUrl : '/employees/template',
            controller  : 'EmployeeController'
        })
        .state('employees.create', {
            url: '/employees/create',
            templateUrl : '/employees/create',
            controller  : 'EmployeeController'
        })
        .state('employees.details', {
            url: '/employees/show',
            templateUrl : '/employees/show',
            controller  : 'EmployeeController'
        })
        .state('employees.list', {
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
        $http.post('/employees/save', employee)
            .success(function(employee) {
                console.log(employee);

//                if (!employee.success) {
//                    // if not successful, bind errors to error variables
//                    $scope.errorName = employee.errors.name;
//                    $scope.errorSuperhero = employee.errors.superheroAlias;
//                } else {
//                    // if successful, bind success message to message
//                    $scope.message = employee.message;
//                }
            });

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

app.controller('EmployeeController', function ($scope, EmployeeService, ngTableParams) {

    $scope.employees = EmployeeService.list();

    $scope.getList = function(){
        $scope.employees = EmployeeService.list();
    }

    $scope.saveEmployee = function () {
        var data = {
            id: 0,
            surname: $scope.newEmployeeForm.surname,
            firstname: $scope.newEmployeeForm.firstname,
            lastname: $scope.newEmployeeForm.lastname,
            birthday: $scope.newEmployeeForm.birthday,
            citizenship: $scope.newEmployeeForm.citizenship,
            insurance_number: $scope.newEmployeeForm.insurance_number,
            tax_number: $scope.newEmployeeForm.tax_number,
            home_phone: $scope.newEmployeeForm.home_phone,
            mobile_phone: $scope.newEmployeeForm.mobile_phone,
            email: $scope.newEmployeeForm.email
        };

        EmployeeService.save(data);
        $scope.newEmployeeForm = {};
        employees=[];
        employees = $scope.getList();
    }

    $scope.employeeTableParams = new ngTableParams({
        page: 2,            // show first page
        count: 10           // count per page
    }, {
        total: $scope.employees.length, // length of data
        getData: function($defer, params) {
            $defer.resolve($scope.employees.slice((params.page() - 1) * params.count(), params.page() * params.count()));
        }
    });


    $scope.delete = function (id) {
        EmployeeService.delete(id);
        if ($scope.newEmployee.id == id) $scope.newEmployee = {};
    }


    $scope.edit = function (id) {
        $scope.newEmployee = angular.copy(EmployeeService.get(id));
    }
})