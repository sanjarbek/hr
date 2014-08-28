var app = angular.module('app', ['ui.router', 'ngTable']);

app.config(function($stateProvider, $urlRouterProvider) {

    $stateProvider
        .state('employees', {
            url: '/employees',
            abstract: true,
            template: '<div ui-view></div>'
        })
        .state('employees.create', {
            url: '/create',
            templateUrl : '/employees/create',
            controller: function($scope, EmployeeService){
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
                }
            }
        })
        .state('employees.detail', {
            url: '/{:employeeId}',
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
            url: '/list',
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
            absract: true,
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
            templateUrl : '/relationships/create',
            resolve: {
                relationshipTypesData : function(RelationshipTypeService) {
                    return RelationshipTypeService.list();
                }
            },
            controller : function ($scope, RelationshipService, relationshipTypesData) {

                $scope.relationship_types = relationshipTypesData;

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
            }
        })
        .state('employees.detail.relationship.type', {
            abstract: true,
            url: '/create',
            template : '<div ui-view></div>'
        })
        .state('employees.detail.relationship.type.create', {
            url: '/create',
            templateUrl : '/relationship_types/create'
        })
        .state('relationship_types', {
            abstract: true,
            url: '/relationship_types',
            template : '<div ui-view></div>'
        })
        .state('relationship_types.list', {
            url: '/list',
            templateUrl : '/relationship_types/list',
            resolve: {
                relationshipTypesData: function(RelationshipTypeService) {
                    return RelationshipTypeService.list();
                }
            },
            controller: function($scope, relationshipTypesData) {
                $scope.relationship_types = relationshipTypesData;
            }
        })
        .state('relationship_types.create', {
            url: '/create',
            templateUrl : '/relationship_types/create',
            controller: function($scope, RelationshipTypeService) {
                $scope.saveRelationshipType = function () {
                    var data = {
                        id: 0,
                        name: $scope.newRelationshipTypeForm.name
                    };

                    RelationshipTypeService.save(data);
                    $scope.newRelationshipTypeForm = {};
                }
            }
        })
        .state('positions', {
            abstract: true,
            url: '/positions',
            template : '<div ui-view></div>'
        })
        .state('positions.list', {
            url: '/list',
            templateUrl : '/positions/list',
            resolve: {
                positionsData: function(PositionService) {
                    return PositionService.list();
                }
            },
            controller: function($scope, positionsData) {
                $scope.positions = positionsData;
            }
        })
        .state('positions.create', {
            url: '/create',
            templateUrl : '/positions/create',
            controller: function($scope, PositionService) {
                $scope.savePosition = function () {
                    var data = {
                        id: 0,
                        name: $scope.newPositionForm.name
                    };

                    PositionService.save(data);
                    $scope.newPositionForm = {};
                }
            }
        })
        .state('office_types', {
            abstract: true,
            url: '/office_types',
            template : '<div ui-view></div>'
        })
        .state('office_types.list', {
            url: '/list',
            templateUrl : '/office_types/list',
            resolve: {
                office_typesData: function(OfficeTypeService) {
                    return OfficeTypeService.list();
                }
            },
            controller: function($scope, office_typesData) {
                $scope.office_types = office_typesData;
            }
        })
        .state('office_types.create', {
            url: '/create',
            templateUrl : '/office_types/create',
            controller: function($scope, OfficeTypeService) {
                $scope.saveOfficeType = function () {
                    var data = {
                        id: 0,
                        name: $scope.newOfficeTypeForm.name
                    };

                    OfficeTypeService.save(data);
                    $scope.newOfficeTypeForm = {};
                }
            }
        })

    ;
}).run(function($rootScope, $state) {
    $rootScope.$state = $state;
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

app.service('RelationshipTypeService', function ($http) {

    //save method create a new relationship if not already exists
    //else update the existing object
    this.save = function (relationship_type) {
        $http.post('/relationship_types/save', relationship_type)
            .success(function(relationship_type) {
                console.log(relationship_type);
            });
    }

    //simply returns the relationship_types list
    this.list = function () {
        return $http.get('/relationship_types/json/list').then(function (result) {
            return result.data;
        });
    }
});

app.service('PositionService', function ($http) {

    this.save = function (position) {
        $http.post('/positions/save', position)
            .success(function(position) {
                console.log(position);
            });
    }

    this.list = function () {
        return $http.get('/positions/json/list').then(function (result) {
            return result.data;
        });
    }

});

app.service('OfficeTypeService', function ($http) {

    this.save = function (office_type) {
        $http.post('/office_types/save', office_type)
            .success(function(result) {
                console.log(result);
            });
    }

    this.list = function () {
        return $http.get('/office_types/json/list').then(function (result) {
            return result.data;
        });
    }

});

app.controller('EmployeeController', function ($scope, EmployeeService, RelationshipService) {

    $scope.delete = function (id) {
        EmployeeService.delete(id);
        if ($scope.newEmployee.id == id) $scope.newEmployee = {};
    }

    $scope.edit = function (id) {
        $scope.newEmployee = angular.copy(EmployeeService.get(id));
    }

})