angular.module('app', ['ui.router', 'ngTable', 'treeGrid', 'ui.bootstrap']);

angular.module('app').config(function ($stateProvider, $urlRouterProvider, $parseProvider) {

    $stateProvider
        .state('employees', {
            url: '/employees',
            abstract: true,
            template: '<div ui-view></div>'
        })
        .state('employees.create', {
            url: '/create',
            templateUrl: '/employees/create',
            controller: function ($scope, EmployeeService) {
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
            templateUrl: '/employees/list',
            resolve: {
                employeesData: function (EmployeeService) {
                    return EmployeeService.list();
                }
            },
            controller: 'EmployeeController'
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
            template: '<div ui-view></div>'
        })
        .state('employees.detail.relationship.list', {
            url: '/list',
            templateUrl: '/relationships/list',
            resolve: {
                relationshipsData: function (activeEmployeeData, RelationshipService) {
                    return RelationshipService.list(activeEmployeeData.id);
                },
                relationshipTypesData: function (RelationshipTypeService) {
                    return RelationshipTypeService.list();
                }
            },
            controller: 'RelationshipController'
        })
        .state('employees.detail.relationship.type', {
            abstract: true,
            url: '/create',
            template: '<div ui-view></div>'
        })
        .state('employees.detail.relationship.type.create', {
            url: '/create',
            templateUrl: '/relationship_types/create'
        })
        .state('relationship_types', {
            abstract: true,
            url: '/relationship_types',
            template: '<div ui-view></div>'
        })
        .state('relationship_types.list', {
            url: '/list',
            templateUrl: '/relationship_types/list',
            resolve: {
                relationshipTypesData: function (RelationshipTypeService) {
                    return RelationshipTypeService.list();
                }
            },
            controller: function ($scope, relationshipTypesData) {
                $scope.relationship_types = relationshipTypesData;
            }
        })
        .state('relationship_types.create', {
            url: '/create',
            templateUrl: '/relationship_types/create',
            controller: function ($scope, RelationshipTypeService) {
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
        .state('employees.detail.contract', {
            absract: true,
            url: '/contract',
            template: '<div ui-view></div>'
        })
        .state('employees.detail.contract.list', {
            url: '/list',
            templateUrl: '/contracts/list',
            resolve: {
                departmentsData: function (DepartmentService) {
                    return DepartmentService.list();
                },
                contractTypeData: function (ContractTypeService) {
                    return ContractTypeService.list();
                },
                officesData: function (OfficeService) {
                    return OfficeService.list();
                },
                contractsData: function (ContractService) {
                    return ContractService.list();
                }
            },
            controller: function ($scope, contractsData, departmentsData, contractTypeData, officesData) {
                $scope.contracts = contractsData;
                $scope.offices = officesData;
                $scope.contract_types = contractTypeData;
                $scope.departments = departmentsData;

                $scope.getOfficeName = function (positionId) {
                    var name = "Не найден офис.";
                    $scope.departments.forEach(function (department) {
                        if (department.id == positionId) {
                            $scope.offices.forEach(function (office) {
                                if (office.id == department.office_id) {
                                    name = office.name;
                                }
                            });
                        }
                    });
                    return name;
                }

                $scope.getDepartmentName = function (positionId) {
                    var name = "Не найден отдел.";
                    $scope.departments.forEach(function (position) {
                        if (position.id == positionId) {
                            $scope.departments.forEach(function (department) {
                                if (department.id == position.parent_id) {
                                    name = department.name;
                                }
                            });
                        }
                    });
                    return name;
                }

                $scope.getPositionName = function (positionId) {
                    var name = "Не найдена должность.";
                    $scope.departments.forEach(function (position) {
                        if (position.id == positionId) {
                            name = position.name;
                        }
                    });
                    return name;
                }

                $scope.getStatusText = function (status) {
                    return status == 1 ? "<span class='label label-success'>Активен</span>" : "<span class='label label-warning'>Окончен</span>";
                }
            }
        })
        .state('employees.detail.contract.create', {
            url: '/create',
            templateUrl: '/contracts/create',
            resolve: {
                departmentsData: function (DepartmentService) {
                    return DepartmentService.list();
                },
                contractTypeData: function (ContractTypeService) {
                    return ContractTypeService.list();
                },
                officesData: function (OfficeService) {
                    return OfficeService.list();
                }
            },
            controller: function ($scope, departmentsData, contractTypeData, officesData, ContractService) {

                $scope.contract_types = contractTypeData;
                $scope.departments = departmentsData;
                $scope.offices = officesData;


                $scope.working_time_types = [
                    {id: 1, name: "Понедельник-Пятница"},
                    {id: 2, name: "Понедельник-Суббота"}
                ];

                $scope.saveContract = function () {
                    var data = {
                        id: 0,
                        employee_id: $scope.activeEmployee.id,
                        position_id: $scope.newContractForm.position.id,
                        trial_period_open: $scope.newContractForm.trial_period_open,
                        trial_period_end: $scope.newContractForm.trial_period_end,
                        salary: $scope.newContractForm.salary,
                        working_time_type: $scope.newContractForm.working_time_type.id,
                        open_date: $scope.newContractForm.openDate,
                        end_date: $scope.newContractForm.endDate,
                        close_date: null,
                        status: $scope.newContractForm.status ? 1 : 0,
                        contract_type: $scope.newContractForm.contract_type.id
                    };

                    console.log(data);

                    ContractService.save(data);
                    $scope.newContractForm = {};
                }
            }
        })
        .state('positions', {
            abstract: true,
            url: '/positions',
            template: '<div ui-view  class="anim-in-out"></div>'
        })
        .state('positions.list', {
            url: '/list',
            templateUrl: '/positions/list',
            resolve: {
                positionsData: function (PositionService) {
                    return PositionService.list();
                }
            },
            controller: function ($scope, positionsData) {
                $scope.positions = positionsData;
            }
        })
        .state('positions.create', {
            url: '/create',
            templateUrl: '/positions/create',
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
            template: '<div ui-view></div>'
        })
        .state('office_types.list', {
            url: '/list',
            templateUrl: '/office_types/list',
            resolve: {
                office_typesData: function (OfficeTypeService) {
                    return OfficeTypeService.list();
                }
            },
            controller: function ($scope, office_typesData) {
                $scope.office_types = office_typesData;
            }
        })
        .state('office_types.create', {
            url: '/create',
            templateUrl: '/office_types/create',
            controller: function ($scope, OfficeTypeService) {
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
            template: '<div ui-view></div>'
        })
        .state('offices.list', {
            url: '/list',
            templateUrl: '/offices/list',
            resolve: {
                officesData: function (OfficeService) {
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
            templateUrl: '/offices/create',
            resolve: {
                office_typesData: function (OfficeTypeService) {
                    return OfficeTypeService.list();
                },
                officesData: function (OfficeService) {
                    return OfficeService.list();
                }
            },
            controller: function ($scope, OfficeService, office_typesData, officesData) {
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
                },
                officesData: function (OfficeService) {
                    return OfficeService.list();
                }
            },
            controller: function ($scope, departmentsData, DepartmentService, FunctionsService, officesData) {
                $scope.departmentsTemp = departmentsData;
                $scope.departments_data = FunctionsService.getTree(departmentsData, 'id', 'parent_id');
                $scope.expanding_property = "name";
                $scope.departmentsTree = {};
                $scope.departmentsTreeHandler = function (branch) {
                    console.log("You clicked " + branch);
                    $scope.current_department = branch;
                };
                $scope.col_defs = [
//                    { field: "office_id", displayName: "Office"},
//                    { field: "parent_id", displayName: "Up"},
//                    { field: "name", displayName: "Name"}
                ];
                $scope.saveDepartment = function () {
                    var parentId = null;
                    if ($scope.current_department)
                        parentId = $scope.current_department.id;

                    var data = {
                        id: 0,
                        parent_id: parentId,
                        office_id: $scope.newDepartmentForm.office.id,
                        name: $scope.newDepartmentForm.name,
                        category: $scope.newDepartmentForm.category.id
                    };

                    var success = DepartmentService.save(data);
                    $scope.departmentsTemp.push(success);
//                    $scope.departments_data = [];
//                    $scope.departments_data = FunctionsService.getTree($scope.departmentsTemp, 'id', 'parent_id');
                    $scope.newDepartmentForm = {};
                }
                $scope.categories = [
                    { id: 1, name: "Должность" },
                    { id: 2, name: "Структура" }
                ];
                $scope.departments = departmentsData;
                $scope.offices = officesData;
                $scope.current_department = null;
                $scope.current_office = null;
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
                        office_id: $scope.newDepartmentForm.office.id,
                        name: $scope.newDepartmentForm.name,
                        category: $scope.newDepartmentForm.category.id
                    };

                    DepartmentService.save(data);
                    $scope.newDepartmentForm = {};
                }
                $scope.categories = [
                    { id: 1, name: "Должность" },
                    { id: 2, name: "Структура" }
                ];
                $scope.departments = departmentsData;
                $scope.offices = officesData;
            }
        })
        .state('contract_types', {
            abstract: true,
            url: '/contract_types',
            template: '<div ui-view></div>'
        })
        .state('contract_types.list', {
            url: '/list',
            templateUrl: '/contract_types/list',
            resolve: {
                contract_typesData: function (ContractTypeService) {
                    return ContractTypeService.list();
                }
            },
            controller: function ($scope, contract_typesData) {
                $scope.contract_types = contract_typesData;
            }
        })
        .state('contract_types.create', {
            url: '/create',
            templateUrl: '/contract_types/create',
            controller: function ($scope, ContractTypeService) {
                $scope.saveContractType = function () {
                    var data = {
                        id: 0,
                        name: $scope.newContractTypeForm.name
                    };

                    ContractTypeService.save(data);
                    $scope.newContractTypeForm = {};
                }
            }
        })
        .state('institutions', {
            abstract: true,
            url: '/institutions',
            template: '<div ui-view></div>'
        })
        .state('institutions.list', {
            url: '/list',
            templateUrl: '/institutions/list',
            resolve: {
                institutionsData: function (InstitutionService) {
                    return InstitutionService.list();
                }
            },
            controller: function ($scope, institutionsData) {
                $scope.institutions = institutionsData;
            }
        })
        .state('institutions.create', {
            url: '/create',
            templateUrl: '/institutions/create',
            controller: function ($scope, InstitutionService) {
                $scope.saveInstitution = function () {
                    var data = {
                        id: 0,
                        shortname: $scope.newInstitutionForm.shortname,
                        longname: $scope.newInstitutionForm.longname
                    };

                    InstitutionService.save(data);
                    $scope.newInstitutionForm = {};
                }
            }
        })
        .state('employees.detail.educations', {
            absract: true,
            url: '/educations',
            template: '<div ui-view></div>'
        })
        .state('employees.detail.educations.list', {
            url: '/list',
            templateUrl: '/educations/list',
            resolve: {
                institutionsData: function (InstitutionService) {
                    return InstitutionService.list();
                },
                educationsData: function (activeEmployeeData, EducationService) {
                    return EducationService.list(activeEmployeeData.id);
                }
            },
            controller: 'EducationController'
        })
        .state('employees.detail.passports', {
            absract: true,
            url: '/passports',
            template: '<div ui-view></div>'
        })
        .state('employees.detail.passports.list', {
            url: '/list',
            templateUrl: '/passports/list',
            resolve: {
                passportsData: function (PassportService, activeEmployeeData) {
                    return PassportService.list(activeEmployeeData.id);
                }
            },
            controller: 'PassportController'
        })
        .state('employees.detail.military', {
            absract: true,
            url: '/militaries',
            template: '<div ui-view></div>'
        })
        .state('employees.detail.military.list', {
            url: '/list',
            templateUrl: '/militaries/list',
            resolve: {
                militariesData: function (MilitaryService, activeEmployeeData) {
                    return MilitaryService.list(activeEmployeeData.id);
                }
            },
            controller: 'MilitaryController'
        })

    ;
}).run(function ($rootScope, $state) {
    $rootScope.$state = $state;
});


//angular.module('app').controller('ModalDemoCtrl', '$scope', '$modal', '$log', function ($scope, $modal, $log) {
//
//    $scope.items = ['item1', 'item2', 'item3'];
//
//    $scope.open = function (size) {
//
//        var modalInstance = $modal.open({
//            templateUrl: 'myModalContent.html',
//            controller: ModalInstanceCtrl,
//            size: size,
//            resolve: {
//                items: function () {
//                    return $scope.items;
//                }
//            }
//        });
//
//        modalInstance.result.then(function (selectedItem) {
//            $scope.selected = selectedItem;
//        }, function () {
//            $log.info('Modal dismissed at: ' + new Date());
//        });
//    };
//});

// Please note that $modalInstance represents a modal window (instance) dependency.
// It is not the same as the $modal service used above.

