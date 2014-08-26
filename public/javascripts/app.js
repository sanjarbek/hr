var app = angular.module('app', ['ui.router', 'ngTable', 'ngAnimate']);

app.config(function($stateProvider, $urlRouterProvider) {

    $stateProvider
        .state('employees', {
            url: '',
            abstract: true,
            template: '<div ui-view></div>'
        })
        .state('employees.create', {
            url: '/employees/create',
            templateUrl : '/employees/create'
        })
        .state('employees.detail', {
            url: '/employees/{:employeeId}',
            templateUrl : '/employees/show',
            resolve: {
                activeEmployeeData: function(EmployeeService, $stateParams) {
                    return EmployeeService.get($stateParams.employeeId);
                }
            },
            controller: function($scope, activeEmployeeData) {
                $scope.activeEmployee = activeEmployeeData;
            }
        })
        .state('employees.list', {
            url: '/employees/list',
            templateUrl : '/employees/list',
            resolve: {
                employeesData: function(EmployeeService) {
                    return EmployeeService.list();
                }
            },
            controller  : function($scope, ngTableParams, employeesData) {
                $scope.employees = employeesData;
                $scope.employeeTableParams = new ngTableParams({
                    page: 2,            // show first page
                    count: 10           // count per page
                }, {
                    total: $scope.employees.length, // length of data
                    getData: function($defer, params) {
                        $defer.resolve($scope.employees.slice((params.page() - 1) * params.count(), params.page() * params.count()));
                    }
                });
            }
        })
        .state('employees.detail.relationship', {
            url: '/relationship',
            template : '<div ui-view></div>'
        })
        .state('employees.detail.relationship.list', {
            url: '/list',
            templateUrl : '/relationships/list',
            resolve: {
                relationshipsData: function(activeEmployeeData, RelationshipService) {
                    return RelationshipService.list(activeEmployeeData.id);
                }
            },
            controller  : function($scope, relationshipsData, ngTableParams){
                $scope.relationships = relationshipsData;
                $scope.relationshipTableParams = new ngTableParams({
                    page: 2,            // show first page
                    count: 10           // count per page
                }, {
                    total: $scope.relationships.length, // length of data
                    getData: function($defer, params) {
                        $defer.resolve($scope.relationships.slice((params.page() - 1) * params.count(), params.page() * params.count()));
                    }
                });
            }
        })
        .state('employees.detail.relationship.create', {
            url: '/create',
            templateUrl : '/relationships/create'
        })

    ;
});

app.service('EmployeeService', function ($http) {
    //employees array to hold list of all employees
    var employees = [];

    var activeEmployee = {};

    //save method create a new employee if not already exists
    //else update the existing object
    this.save = function (employee) {
        $http.post('/employees/save', employee)
            .success(function(employee) {
                console.log(employee);
            });

    }

    //simply search employees list for given id
    //and returns the employee object if found
    this.get = function (id) {
//        for (i in employees) {
//            if (employees[i].id == id) {
//                return employees[i];
//            }
//        }
        return $http.get('/employees/json/get', { params : {'id' : id}}).then(function(result) {
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
        return $http.get('/employees/json/list' ).then(function (result) {
            return result.data;
        });
    }
});

app.service('RelationshipService', function ($http, EmployeeService) {
    //to create unique relationship id
    var uid = 1;

    //relationships array to hold list of all relationships
    var relationships = [];

    //save method create a new relationship if not already exists
    //else update the existing object
    this.save = function (relationship) {
        $http.post('/relationships/save', relationship)
            .success(function(relationship) {
                console.log(relationship);
            });
    }

    //simply search contacts list for given id
    //and returns the relationship object if found
    this.get = function (id) {
        for (i in relationships) {
            if (relationships[i].id == id) {
                return relationships[i];
            }
        }

    }

    //iterate through relationships list and delete
    //contact if found
    this.delete = function (id) {
        for (i in relationships) {
            if (relationships[i].id == id) {
                relationships.splice(i, 1);
            }
        }
    }

    //simply returns the relationships list
    this.list = function (id) {
        return $http.get('/employees/json/family', { params : {'employeeId' : id}} ).then(function (result) {
            return result.data;
        });
    }
});

app.controller('EmployeeController', function ($scope, EmployeeService, RelationshipService) {

    $scope.relationship_types = [
        {id: 1, name: "Отец"},
        {id: 2, name: "Дедушка"},
        {id: 3, name: "Мама"},
        {id: 4, name: "Брат"},
        {id: 5, name: "Бабушка"},
        {id: 6, name: "Сестра"}
    ];

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

    $scope.saveRelationship = function () {
        var data = {
            id: 0,
            employee_id: $scope.activeEmployee.id,
            degree: $scope.newRelationshipForm.degree.id,
            surname: $scope.newRelationshipForm.surname,
            firstname: $scope.newRelationshipForm.firstname,
            lastname: $scope.newRelationshipForm.lastname,
            birthday: $scope.newRelationshipForm.birthday
        };

        RelationshipService.save(data);
        $scope.newRelationshipForm = {};
    }

    $scope.delete = function (id) {
        EmployeeService.delete(id);
        if ($scope.newEmployee.id == id) $scope.newEmployee = {};
    }

    $scope.edit = function (id) {
        $scope.newEmployee = angular.copy(EmployeeService.get(id));
    }
})