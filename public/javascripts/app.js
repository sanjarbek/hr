angular.module('app', ['ui.router', 'ngTable', 'treeGrid', 'ui.bootstrap', 'angularFileUpload', 'treeControl']);

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
            controller: function ($scope, activeEmployeeData, $upload) {
                $scope.activeEmployee = activeEmployeeData;

                $scope.onFileSelect = function ($files) {
                    //$files: an array of files selected, each file has name, size, and type.
                    for (var i = 0; i < $files.length; i++) {
                        var file = $files[i];
                        $scope.upload = $upload.upload({
                            url: '/upload', //upload.php script, node.js route, or servlet url
                            method: 'POST',
                            //headers: {'header-key': 'header-value'},
                            //withCredentials: true,
                            data: {myObj: $scope.myModelObj},
                            file: file // or list of files ($files) for html5 only
                            //fileName: 'doc.jpg' or ['1.jpg', '2.jpg', ...] // to modify the name of the file(s)
                            // customize file formData name ('Content-Disposition'), server side file variable name.
                            //fileFormDataName: myFile, //or a list of names for multiple files (html5). Default is 'file'
                            // customize how data is added to formData. See #40#issuecomment-28612000 for sample code
                            //formDataAppender: function(formData, key, val){}
                        }).progress(function (evt) {
                            console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
                        }).success(function (data, status, headers, config) {
                            // file is uploaded successfully
                            console.log(data);
                        });
                        //.error(...)
                        //.then(success, error, progress);
                        // access or attach event listeners to the underlying XMLHttpRequest.
                        //.xhr(function(xhr){xhr.upload.addEventListener(...)})
                    }
                    /* alternative way of uploading, send the file binary with the file's content-type.
                     Could be used to upload files to CouchDB, imgur, etc... html5 FileReader is needed.
                     It could also be used to monitor the progress of a normal http post/put request with large data*/
                    // $scope.upload = $upload.http({...})  see 88#issuecomment-31366487 for sample code.
                };
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
                },
                positionTypesData: function (PositionCategoryService) {
                    return PositionCategoryService.list();
                }
            },
            controller: function ($scope, departmentsData, DepartmentService, FunctionsService, officesData, positionTypesData) {
                $scope.dataForTheTree = FunctionsService.getTree(departmentsData, 'id', 'parent_id');
                console.log($scope.dataForTheTree);

                $scope.selectNode = function (branch) {
                    console.log("You clicked " + branch);
                    $scope.current_department = branch;
                };

                $scope.col_defs = [
//                    { field: "office_id", displayName: "Office"},
//                    { field: "parent_id", displayName: "Up"},
//                    { field: "name", displayName: "Name"}
                ];

                positionTypesData.push({id: null, name: "Выберите..."});

                $scope.position_types = positionTypesData;

                $scope.saveDepartment = function () {
                    var parentId = null;
                    if ($scope.current_department)
                        parentId = $scope.current_department.id;

                    var data = {
                        id: 0,
                        parent_id: parentId,
                        office_id: $scope.newDepartmentForm.office_id,
                        name: $scope.newDepartmentForm.name,
                        is_position: Boolean($scope.newDepartmentForm.is_position),
                        position_type: ($scope.newDepartmentForm.position_type == undefined || Boolean(!$scope.newDepartmentForm.is_position))
                            ? null
                            : $scope.newDepartmentForm.position_type
                    };

                    DepartmentService.save(data).then(function (result) {
                        if (!$scope.current_department.hasOwnProperty('children'))
                            $scope.current_department.children = [];
                        $scope.current_department.children.push(result.data);
                    });
                    $scope.newDepartmentForm = {};
                }
                $scope.departments = departmentsData;
                $scope.offices = officesData;
                $scope.current_department = null;
                $scope.current_office = null;

                $scope.treeOptions = {
                    nodeChildren: "children",
                    dirSelectable: true,
                    isLeaf: function (node) {
                        return node.is_position;
                    },
                    dirSelectable: false,
                    injectClasses: {
                        ul: "a1",
                        li: "a2",
                        liSelected: "a7",
                        iExpanded: "a3",
                        iCollapsed: "a4",
                        iLeaf: "a5",
                        label: "a6",
                        labelSelected: "a8"
                    }
                };
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

                $scope.treeOptions = {
                    nodeChildren: "children",
                    dirSelectable: true,
                    injectClasses: {
                        ul: "a1",
                        li: "a2",
                        liSelected: "a7",
                        iExpanded: "a3",
                        iCollapsed: "a4",
                        iLeaf: "a5",
                        label: "a6",
                        labelSelected: "a8"
                    }
                };
                $scope.dataForTheTree = [
                    { "name": "Joe", "age": "21", "children": [
                        { "name": "Smith", "age": "42", "children": [] },
                        { "name": "Gary", "age": "21", "children": [
                            { "name": "Jenifer", "age": "23", "children": [
                                { "name": "Dani", "age": "32", "children": [] },
                                { "name": "Max", "age": "34", "children": [] }
                            ]}
                        ]}
                    ]},
                    { "name": "Albert", "age": "33", "children": [] },
                    { "name": "Ron", "age": "29", "children": [] }
                ];

                $scope.doIt = function (node) {
                    node.name = "Fuck";
                    alert(node.name);
                }
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
            controller: function ($scope, contract_typesData, $upload) {
                $scope.contract_types = contract_typesData;

                $scope.onFileSelect = function ($files, contract_type) {
                    //$files: an array of files selected, each file has name, size, and type.
                    for (var i = 0; i < $files.length; i++) {
                        var file = $files[i];
                        $scope.upload = $upload.upload({
                            url: '/contract_types/upload', //upload.php script, node.js route, or servlet url
                            method: 'POST',
                            //headers: {'header-key': 'header-value'},
                            //withCredentials: true,
                            data: {id: contract_type.id},
                            file: file // or list of files ($files) for html5 only
                            //fileName: 'doc.jpg' or ['1.jpg', '2.jpg', ...] // to modify the name of the file(s)
                            // customize file formData name ('Content-Disposition'), server side file variable name.
                            //fileFormDataName: myFile, //or a list of names for multiple files (html5). Default is 'file'
                            // customize how data is added to formData. See #40#issuecomment-28612000 for sample code
                            //formDataAppender: function(formData, key, val){}
                        }).progress(function (evt) {
                            console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
                        }).success(function (data, status, headers, config) {
                            // file is uploaded successfully
                            console.log(data);
                            contract_type.file_path = file.name;
                        });
                        //.error(...)
                        //.then(success, error, progress);
                        // access or attach event listeners to the underlying XMLHttpRequest.
                        //.xhr(function(xhr){xhr.upload.addEventListener(...)})
                    }
                    /* alternative way of uploading, send the file binary with the file's content-type.
                     Could be used to upload files to CouchDB, imgur, etc... html5 FileReader is needed.
                     It could also be used to monitor the progress of a normal http post/put request with large data*/
                    // $scope.upload = $upload.http({...})  see 88#issuecomment-31366487 for sample code.
                };
            }
        })
        .state('contract_types.create', {
            url: '/create',
            templateUrl: '/contract_types/create',
            controller: function ($scope, ContractTypeService) {
                $scope.saveContractType = function () {
                    var data = {
                        id: 0,
                        name: $scope.newContractTypeForm.name,
                        file_path: null
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

