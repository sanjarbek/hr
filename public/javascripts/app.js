angular.module('app', ['ui.router', 'ngTable', 'treeGrid']);

angular.module('app').config(function ($stateProvider, $urlRouterProvider) {

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
        .state('employees.detail', {
            url: '/{employeeId:[0-9]{1,6}}',
            templateUrl: '/employees/show',
            resolve: {
                activeEmployeeData: function (EmployeeService, $stateParams) {
                    return EmployeeService.get($stateParams.employeeId);
                }
            },
            controller: function ($scope, activeEmployeeData) {
                $scope.activeEmployee = activeEmployeeData;
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
            resolve: {
                positionCategoriesData: function (PositionCategoryService) {
                    return PositionCategoryService.list();
                }
            },
            controller: function ($scope, PositionService, positionCategoriesData) {
                $scope.position_categories = positionCategoriesData;

                $scope.savePosition = function () {
                    var data = {
                        id: 0,
                        category_id: $scope.newPositionForm.category_id.id,
                        name: $scope.newPositionForm.name
                    };

                    PositionService.save(data);
                    $scope.newPositionForm = {};
                }
            }
        })
        .state('position_categories', {
            abstract: true,
            url: '/position_categories',
            template: '<div ui-view></div>'
        })
        .state('position_categories.list', {
            url: '/list',
            templateUrl: '/position_categories/list',
            resolve: {
                positionCategoriesData: function (PositionCategoryService) {
                    return PositionCategoryService.list();
                }
            },
            controller: function ($scope, positionCategoriesData) {
                $scope.position_categories = positionCategoriesData;
            }
        })
        .state('position_categories.create', {
            url: '/create',
            templateUrl: '/position_categories/create',
            controller: function ($scope, PositionCategoryService) {
                $scope.savePositionCategory = function () {
                    var data = {
                        id: 0,
                        name: $scope.newPositionCategoryForm.name
                    };

                    PositionCategoryService.save(data);
                    $scope.newPositionCategoryForm = {};
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
        .state('offices', {
            abstract: true,
            url: '/offices',
            template : '<div ui-view></div>'
        })
        .state('offices.list', {
            url: '/list',
            templateUrl : '/offices/list',
            resolve: {
                officesData: function(OfficeService) {
                    return OfficeService.list();
                }
            },
            controller: function ($scope, officesData, OfficeService, FunctionsService) {
                $scope.tree_data = FunctionsService.getTree(officesData, 'id', 'parent_id');
                $scope.expanding_property = "name";
                $scope.officesTree = {};
                $scope.officesTreeHandler = function (office) {
                    console.log('you clicked on', office)
                };
                $scope.col_defs = [
                    { field: "email", displayName: "Э-почта"},
                    { field: "phone", displayName: "Телефон"},
                    { field: "fax", displayName: "Факс"}
                ];
            }
        })
        .state('offices.create', {
            url: '/create',
            templateUrl : '/offices/create',
            resolve: {
                office_typesData: function(OfficeTypeService){
                    return OfficeTypeService.list();
                },
                officesData: function(OfficeService) {
                    return OfficeService.list();
                }
            },
            controller: function($scope, OfficeService, office_typesData, officesData) {
                $scope.saveOffice = function () {
                    var parentId = null;
                    if ($scope.newOfficeForm.parent_id)
                        parentId = $scope.newOfficeForm.parent_id.id;

                    var data = {
                        id: 0,
                        parent_id: parentId,
                        type_id: $scope.newOfficeForm.type_id.id,
                        name: $scope.newOfficeForm.name,
                        email: $scope.newOfficeForm.email,
                        phone: $scope.newOfficeForm.phone,
                        fax: $scope.newOfficeForm.fax,
                        address: $scope.newOfficeForm.address
                    };

                    OfficeService.save(data);
                    $scope.newOfficeForm = {};
                }
                $scope.office_types = office_typesData;
                $scope.offices = officesData;
            }
        })
        .state('departments', {
            abstract: true,
            url: '/departments',
            template: '<div ui-view></div>'
        })
        .state('departments.list', {
            url: '/list',
            templateUrl: '/departments/list',
            resolve: {
                departmentsData: function (DepartmentService) {
                    return DepartmentService.list();
                }
            },
            controller: function ($scope, departmentsData, DepartmentService, FunctionsService) {
                $scope.departments_data = FunctionsService.getTree(departmentsData, 'id', 'parent_id');
                $scope.expanding_property = "name";
                $scope.departmentsTree = {};
                $scope.departmentsTreeHandler = function (test) {
                    console.log('you clicked on', test)
                };
                $scope.col_defs = [
                    { field: "office_id", displayName: "Office"},
                    { field: "parent_id", displayName: "Up"},
                    { field: "name", displayName: "Name"}
                ];
            }
        })
        .state('departments.create', {
            url: '/create',
            templateUrl: '/departments/create',
            resolve: {
                departmentsData: function (DepartmentService) {
                    return DepartmentService.list();
                },
                officesData: function (OfficeService) {
                    return OfficeService.list();
                }
            },
            controller: function ($scope, DepartmentService, departmentsData, officesData) {
                $scope.saveDepartment = function () {
                    var parentId = null;
                    if ($scope.newDepartmentForm.parent_id)
                        parentId = $scope.newDepartmentForm.parent_id.id;

                    var data = {
                        id: 0,
                        parent_id: parentId,
                        office_id: $scope.newDepartmentForm.type_id.id,
                        name: $scope.newDepartmentForm.name
                    };

                    DepartmentService.save(data);
                    $scope.newDepartmentForm = {};
                }
                $scope.departments = departmentsData;
                $scope.offices = officesData;
            }
        })

    ;
}).run(function($rootScope, $state) {
    $rootScope.$state = $state;
});

angular.module('app').service('EmployeeService', function ($http) {
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

angular.module('app').service('RelationshipService', function ($http, EmployeeService) {
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

angular.module('app').service('RelationshipTypeService', function ($http) {

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

angular.module('app').service('PositionService', function ($http) {

    this.save = function (position) {
        $http.post('/positions/save', position)
            .success(function (result) {
                console.log(result);
            });
    }

    this.list = function () {
        return $http.get('/positions/json/list').then(function (result) {
            return result.data;
        });
    }

});

angular.module('app').service('PositionCategoryService', function ($http) {

    this.save = function (position_category) {
        $http.post('/position_categories/save', position_category)
            .success(function (result) {
                console.log(result);
            });
    }

    this.list = function () {
        return $http.get('/position_categories/json/list').then(function (result) {
            return result.data;
        });
    }

});

angular.module('app').service('OfficeTypeService', function ($http) {

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

angular.module('app').service('OfficeService', function ($http) {

    this.save = function (office) {
        $http.post('/offices/save', office)
            .success(function(result) {
                console.log(result);
            });
    }

    this.list = function () {
        return $http.get('/offices/json/list').then(function (result) {
            return result.data;
        });
    }

});

angular.module('app').service('DepartmentService', function ($http) {

    this.save = function (department) {
        $http.post('/departments/save', department)
            .success(function (result) {
                console.log(result);
            });
    }

    this.list = function () {
        return $http.get('/departments/json/list').then(function (result) {
            return result.data;
        });
    }

});

angular.module('app').service('FunctionsService', function () {

    this.getTree = function (data, primaryIdName, parentIdName) {
        if (!data || data.length == 0 || !primaryIdName || !parentIdName)
            return [];

        var tree = [],
            rootIds = [],
            item = data[0],
            primaryKey = item[primaryIdName],
            treeObjs = {},
            parentId,
            parent,
            len = data.length,
            i = 0;

        while (i < len) {
            item = data[i++];
            primaryKey = item[primaryIdName];
            treeObjs[primaryKey] = item;
            parentId = item[parentIdName];

            if (parentId) {
                parent = treeObjs[parentId];

                if (parent.children) {
                    parent.children.push(item);
                }
                else {
                    parent.children = [item];
                }
            }
            else {
                rootIds.push(primaryKey);
            }
        }

        for (var i = 0; i < rootIds.length; i++) {
            tree.push(treeObjs[rootIds[i]]);
        }
        ;

        return tree;
    }

});


angular.module('app').controller('EmployeeController', function ($scope, EmployeeService, RelationshipService) {

    $scope.delete = function (id) {
        EmployeeService.delete(id);
        if ($scope.newEmployee.id == id) $scope.newEmployee = {};
    }

    $scope.edit = function (id) {
        $scope.newEmployee = angular.copy(EmployeeService.get(id));
    }

})