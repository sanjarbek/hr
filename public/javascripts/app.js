angular.module('app', [
    'ui.router',
    'ngCookies',
    'ngTable',
    'ui.grid',
    'treeGrid',
    'ui.bootstrap',
    'ui.calendar',
    'angularFileUpload',
    'treeControl',
    'ngTagsInput',
    'textAngular',
    'ckeditor',
    'smart-table',
    'io.dennis.contextmenu'
]);

angular.module('app').config(function ($stateProvider, $urlRouterProvider, $parseProvider, $httpProvider) {

    var interceptor = ['$rootScope', '$q', '$timeout', '$injector', function ($rootScope, $q, $timeout, $injector) {
        return function (promise) {
            return promise.then(
                function (response) {
                    return response;
                },
                function (response) {
                    if (response.status == 401) {
                        console.log("Invalid token.");
                        $rootScope.$broadcast("InvalidToken");
                        $rootScope.sessionExpired = true;
                        $timeout(function () {
                            $rootScope.sessionExpired = false;
                        }, 5000);
                        PNotify.desktop.permission();
                        (new PNotify({
                            title: 'Права доступа.',
                            text: 'Надо пройти аутентификацию.',
                            desktop: {
                                desktop: true
                            }
                        })).get().click(function (e) {
                                if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                            });
                        $injector.get('$state').transitionTo('login');
                    } else if (response.status == 403) {
                        console.log("Insufficient privileges.");
                        $rootScope.$broadcast("InsufficientPrivileges");
                    }
                    return $q.reject(response);
                }
            );
        };
    }];
    $httpProvider.responseInterceptors.push(interceptor);

    $stateProvider
        .state('login', {
            url: '/login',
            templateUrl: '/login',
            controller: 'LoginCtrl'
        })
        .state('panel', {
            url: '',
            abstract: true,
            templateUrl: '/menu'
        })
        .state('panel.orders', {
            url: '/orders',
            abstract: true,
            templateUrl: '/orders/tabTemplate',
            controller: function ($scope, $state) {
                $scope.tabs = [
                    { heading: 'Прием на работу', route: 'panel.orders.employmentOrders.list', active: true},
                    { heading: 'Перемещение', route: 'panel.orders.transferOrders.list', active: false },
                    { heading: 'Увольнение', route: 'panel.orders.dismissalOrders.list', active: false }
                ];

                $scope.go = function (t) {
                    $scope.tabs.forEach(function (tab) {
                        tab.active = $scope.active(t.route);
                    });
                    $state.go(t.route);
                };

                $scope.active = function (route) {
                    return $state.includes(route);
                };
            }
        })
        .state('panel.orders.employmentOrders', {
            url: '/employments',
            abstract: true,
            template: '<div ui-view></div>'
        })
        .state('panel.orders.employmentOrders.list', {
            url: '/list',
            templateUrl: '/orders/employment/list',
            resolve: {
                ordersData: function (EmploymentOrderService) {
                    return EmploymentOrderService.list();
                }
            },
            controller: function ($scope, ordersData) {
                $scope.items = ordersData;
                $scope.itemsByPage = 20;
            }
        })
        .state('panel.orders.employmentOrders.create', {
            url: '/create',
            templateUrl: '/orders/employment/create',
            resolve: {
                contractTypesData: function (ContractTypeService) {
                    return ContractTypeService.list();
                },
                calendarTypesData: function (CalendarTypeService) {
                    return CalendarTypeService.list();
                },
                employeesData: function (EmployeeService) {
                    return EmployeeService.unemployedList();
                }
            },
            controller: function ($scope, $modal, $log, EmploymentOrderService, contractTypesData, calendarTypesData, employeesData) {

                $scope.title = 'Новый приказ приема на работу';

                $scope.contractTypes = contractTypesData;
                $scope.calendarTypes = calendarTypesData;
                $scope.employees = employeesData;
                $scope.trial_period = false;

                $scope.newEmploymentOrderForm = {
                    id: 0,
                    employee_id: null,
                    order_type_id: 1,
                    is_combined_work: false,
                    end_date: null,
                    created_at: '2014-01-01 00:00:00',
                    updated_at: '2014-01-01 00:00:00',
                    trial_period_end: null,
                    trial_period_start: null
                }

                $scope.selected = null;

                $scope.selectAction = function () {
                    $scope.saveEmploymentOrderForm();
                }

                $scope.saveEmploymentOrderForm = function () {
                    $scope.newEmploymentOrderForm.employee_id = $scope.selected.id;

                    if (!$scope.trial_period) {
                        $scope.newEmploymentOrderForm.trial_period_start = null;
                        $scope.newEmploymentOrderForm.trial_period_end = null;
                    }

                    EmploymentOrderService.save($scope.newEmploymentOrderForm).then(function (result) {
                        PNotify.desktop.permission();
                        (new PNotify({
                            title: 'Статус сохранения',
                            text: 'Новая запись успешно сохранена.',
                            desktop: {
                                desktop: true
                            }
                        })).get().click(function (e) {
                                if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                                alert('Hey! You clicked the desktop notification!');
                            });
                    });
                }

                $scope.trialPeriodChanged = function () {
                    if (!$scope.trial_period) {
                        $scope.newEmploymentOrderForm.trial_period_start = null;
                        $scope.newEmploymentOrderForm.trial_period_end = null;
                    }
                }

                $scope.openPositionModal = function (size) {

                    var modalInstance = $modal.open({
                        templateUrl: 'positionModal.html',
                        resolve: {
                            structuresData: function (StructureService) {
                                return StructureService.freepositions();
                            }
                        },
                        controller: 'ModalStructuresCtrl',
                        size: size
                    });

                    modalInstance.result.then(function (result) {
//                        $scope.temp.position = result.name;
                        $scope.newEmploymentOrderForm.position_id = result.id;
                        $scope.tempPosition = result.name;
                    }, function () {
                        console.info('Modal dismissed at: ' + new Date());
                    });
                }
            }
        })
        .state('panel.orders.employmentOrders.edit', {
            url: '/{employmentOrderId:[0-9]{1,6}}',
            templateUrl: '/orders/employment/create',
            resolve: {
                employmentOrderData: function (EmploymentOrderService, $stateParams) {
                    return EmploymentOrderService.get($stateParams.employmentOrderId);
                },
                contractTypesData: function (ContractTypeService) {
                    return ContractTypeService.list();
                },
                calendarTypesData: function (CalendarTypeService) {
                    return CalendarTypeService.list();
                },
                employeesData: function (EmployeeService) {
                    return EmployeeService.list();
                }
            },
            controller: function ($scope, $filter, $modal, $log, StructureService, EmployeeService, EmploymentOrderService, contractTypesData, calendarTypesData, employeesData, employmentOrderData) {

                $scope.contractTypes = contractTypesData;
                $scope.calendarTypes = calendarTypesData;
                $scope.employees = employeesData;

                $scope.title = 'Редактировать приказ приема на работу';

                $scope.formInit = function () {
                    $scope.newEmploymentOrderForm = employmentOrderData;
                    $scope.newEmploymentOrderForm.date_of_order = $filter("date")(employmentOrderData.date_of_order, 'yyyy-MM-dd');
                    $scope.newEmploymentOrderForm.trial_period_start = $filter("date")(employmentOrderData.trial_period_start, 'yyyy-MM-dd');
                    $scope.newEmploymentOrderForm.trial_period_end = $filter("date")(employmentOrderData.trial_period_end, 'yyyy-MM-dd');
                    $scope.newEmploymentOrderForm.start_date = $filter("date")(employmentOrderData.start_date, 'yyyy-MM-dd');
                    $scope.newEmploymentOrderForm.end_date = $filter("date")(employmentOrderData.end_date, 'yyyy-MM-dd');
                    StructureService.get($scope.newEmploymentOrderForm.position_id).then(function (result) {
                        $scope.tempPosition = result.name;
                    });
                    $scope.selected = EmployeeService.get($scope.newEmploymentOrderForm.employee_id).then(function (result) {
                        $scope.selected = result;
                    });
                }

                $scope.formInit();

                $scope.updateEmploymentOrderForm = function () {
                    $scope.newEmploymentOrderForm.employee_id = $scope.selected.id;
                    EmploymentOrderService.update($scope.newEmploymentOrderForm).then(function (result) {
                        PNotify.desktop.permission();
                        (new PNotify({
                            title: 'Статус обновления',
                            text: 'Запись успешно обновилась.',
                            desktop: {
                                desktop: true
                            }
                        })).get().click(function (e) {
                                if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                                alert('Hey! You clicked the desktop notification!');
                            });
                    });
                }

                $scope.selectAction = function () {
                    $scope.updateEmploymentOrderForm();
                }

                $scope.openPositionModal = function (size) {

                    var modalInstance = $modal.open({
                        templateUrl: 'positionModal.html',
                        resolve: {
                            structuresData: function (StructureService) {
                                return StructureService.freepositions();
                            }
                        },
                        controller: 'ModalStructuresCtrl',
                        size: size
                    });

                    modalInstance.result.then(function (result) {
//                        $scope.temp.position = result.name;
                        $scope.newEmploymentOrderForm.position_id = result.id;
                        $scope.tempPosition = result.name;
                    }, function () {
                        console.info('Modal dismissed at: ' + new Date());
                    });
                }
            }
        })
        .state('panel.orders.dismissalOrders', {
            url: '/dismissals',
            abstract: true,
            template: '<div ui-view></div>'
        })
        .state('panel.orders.dismissalOrders.list', {
            url: '/list',
            templateUrl: '/orders/dismissal/list',
            resolve: {
                ordersData: function (DismissalOrderService) {
                    return DismissalOrderService.list();
                }
            },
            controller: function ($scope, ordersData) {
                $scope.items = ordersData;
                $scope.itemsByPage = 20;
            }
        })
        .state('panel.worksheet', {
            url: '/worksheet',
            abstract: true,
            template: '<div ui-view></div>'
        })
        .state('panel.worksheet.list', {
            url: '/list',
            templateUrl: '/worksheets/day/list',
            resolve: {
                workingSheetDaysData: function (WorkSheetDayService) {
                    return WorkSheetDayService.list();
//                    return WorkSheetDayService.list(1);
                },
                dayTypesData: function (DayTypeService) {
                    return DayTypeService.list();
                }
            },
            controller: function ($scope, workingSheetDaysData, dayTypesData) {
                $scope.items = workingSheetDaysData;

                $scope.dayTypes = dayTypesData;

                $scope.monthDays = [
                    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31
                ];

                $scope.changedValue = function (item, workingSheetDay) {
                    alert(workingSheetDay.hours);
                }

//                $scope.getWorkSheetDays = function() {
//                    WorkSheetDayService.list(1).then(function(result){
//                        $scope.items = result
//                    });
//                }
            }
        })
        .state('panel.orders.dismissalOrders.create', {
            url: '/create',
            templateUrl: '/orders/dismissal/create',
            resolve: {
                employeesData: function (EmployeeService) {
                    return EmployeeService.employedList();
                },
                leavingReasonsData: function (LeavingReasonService) {
                    return LeavingReasonService.list();
                }
            },
            controller: function ($scope, $modal, $log, EmployeeService, DismissalOrderService, employeesData, leavingReasonsData) {

                $scope.title = 'Новый приказ увольнения';

                $scope.employees = employeesData;
                $scope.leavingReasons = leavingReasonsData;

                $scope.newDismissalOrderForm = {
                    id: 0,
                    employee_id: null,
                    position_id: null,
                    order_type_id: 2,
                    end_date: null,
                    close_date: null,
                    created_at: '2014-01-01 00:00:00',
                    updated_at: '2014-01-01 00:00:00'
                }

                $scope.selected = null;

                $scope.selectAction = function () {
                    $scope.saveDismissalOrderForm();
                }

                $scope.onSelect = function ($item, $model, $label) {

                    EmployeeService.getPosition($item.id).then(function (result) {
                        $scope.newDismissalOrderForm.position_id = result.id;
                        $scope.position_name = result.name;
                    });
                };

                $scope.saveDismissalOrderForm = function () {
                    $scope.newDismissalOrderForm.employee_id = $scope.selected.id;
                    DismissalOrderService.save($scope.newDismissalOrderForm).then(function (result) {
                        PNotify.desktop.permission();
                        (new PNotify({
                            title: 'Статус сохранения',
                            text: 'Новая запись успешно сохранена.',
                            desktop: {
                                desktop: true
                            }
                        })).get().click(function (e) {
                                if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                                alert('Hey! You clicked the desktop notification!');
                            });
                    });
                }

                $scope.openLeavingReasonModal = function (size) {
                    var modalInstance = $modal.open({
                        templateUrl: 'leavingReasonModal.html',
                        controller: 'ModalLeavingReasonController',
                        size: size

                    });

                    modalInstance.result.then(function (result) {
                        $scope.leavingReasons.push(result.data);
                    }, function () {
                        $log.info('Modal dismissed at: ' + new Date());
                    });
                };

            }
        })
        .state('panel.orders.transferOrders', {
            url: '/transfers',
            abstract: true,
            template: '<div ui-view></div>'
        })
        .state('panel.orders.transferOrders.list', {
            url: '/list',
            templateUrl: '/orders/transfer/list',
            resolve: {
                ordersData: function (TransferOrderService) {
                    return TransferOrderService.list();
                }
            },
            controller: function ($scope, ordersData) {
                $scope.items = ordersData;
                $scope.itemsByPage = 20;
            }
        })
        .state('panel.orders.transferOrders.create', {
            url: '/create',
            templateUrl: '/orders/transfer/create',
            resolve: {
                contractTypesData: function (ContractTypeService) {
                    return ContractTypeService.list();
                },
                calendarTypesData: function (CalendarTypeService) {
                    return CalendarTypeService.list();
                },
                employeesData: function (EmployeeService) {
                    return EmployeeService.employedList();
                }
            },
            controller: function ($scope, $modal, $log, TransferOrderService, contractTypesData, calendarTypesData, employeesData) {

                $scope.title = 'Перемещение';

                $scope.contractTypes = contractTypesData;
                $scope.calendarTypes = calendarTypesData;
                $scope.employees = employeesData;

                $scope.newTransferOrderForm = {
                    id: 0,
                    employee_id: null,
                    order_type_id: 3,
                    is_combined_work: false,
                    end_date: null,
                    created_at: '2014-01-01 00:00:00',
                    updated_at: '2014-01-01 00:00:00',
                    trial_period_end: null,
                    trial_period_start: null
                }

                $scope.selected = null;

                $scope.selectAction = function () {
                    $scope.saveTransferOrderForm();
                }

                $scope.saveTransferOrderForm = function () {
                    $scope.newTransferOrderForm.employee_id = $scope.selected.id;
                    TransferOrderService.save($scope.newTransferOrderForm).then(function (result) {
                        PNotify.desktop.permission();
                        (new PNotify({
                            title: 'Статус сохранения',
                            text: 'Новая запись успешно сохранена.',
                            desktop: {
                                desktop: true
                            }
                        })).get().click(function (e) {
                                if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                                alert('Hey! You clicked the desktop notification!');
                            });
                    });
                }

                $scope.openPositionModal = function (size) {

                    var modalInstance = $modal.open({
                        templateUrl: 'positionModal.html',
                        resolve: {
                            structuresData: function (StructureService) {
                                return StructureService.freepositions();
                            }
                        },
                        controller: 'ModalStructuresCtrl',
                        size: size
                    });

                    modalInstance.result.then(function (result) {
//                        $scope.temp.position = result.name;
                        $scope.newTransferOrderForm.position_id = result.id;
                        $scope.tempPosition = result.name;
                    }, function () {
                        console.info('Modal dismissed at: ' + new Date());
                    });
                }
            }
        })
        .state('panel.orders.list', {
            url: '/list1',
            templateUrl: '/orders/list',
            resolve: {
                ordersData: function (OrderService) {
                    return OrderService.list();
                }
            },
            controller: function ($scope, ordersData) {
                $scope.orders = ordersData;
            }
        })
        .state('panel.orders.edit', {
            url: '/{orderId:[0-9]{1,6}}',
            templateUrl: '/orders/create',
            resolve: {
                orderData: function (OrderService, $stateParams) {
                    return OrderService.get($stateParams.orderId)
                }
            },
            controller: function ($scope, $filter, $http, $state, OrderService, orderData) {
                $scope.title = 'Редактировать приказ';

                $scope.formInit = function () {
                    $scope.newOrderForm = orderData;

                    if (orderData.tags != null) {
                        var tags = orderData.tags;
                        $scope.newOrderForm.tags = [];
                        tags.split(",").forEach(function (item) {
                            $scope.newOrderForm.tags.push(Object({text: item}));
                        });
                    }
                    $scope.newOrderForm.date_of_order = $filter("date")(orderData.date_of_order, 'yyyy-MM-dd');
                };

                $scope.formInit();

                $scope.loadTags = function (query) {
                    return OrderService.tagsList(query);
                };

                $scope.order_categories = [
                    {id: 1, name: 'Личный состав'},
                    {id: 2, name: 'Основной состав'}
                ];

                $scope.options = {
                    language: 'ru',
                    allowedContent: true,
                    entities: false,
                    toolbar: [
                        { name: 'document', groups: [ 'mode', 'document', 'doctools' ], items: [ 'Source', '-', 'Save', 'NewPage', 'Preview', 'Print', '-', 'Templates' ] },
                        { name: 'clipboard', groups: [ 'clipboard', 'undo' ], items: [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ] },
                        { name: 'editing', groups: [ 'find', 'selection', 'spellchecker' ], items: [ 'Find', 'Replace', '-', 'SelectAll', '-'] },
//                        { name: 'forms', items: [ 'Form', 'Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', 'Button', 'ImageButton', 'HiddenField' ] },
//                        '/',
                        { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ], items: [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'RemoveFormat' ] },
                        { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ], items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', 'CreateDiv', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'BidiLtr', 'BidiRtl', 'Language' ] },
//                        { name: 'links', items: [ 'Link', 'Unlink', 'Anchor' ] },
                        { name: 'insert', items: [ 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar', 'PageBreak' ] },
//                        '/',
                        { name: 'styles', items: [ 'Styles', 'Format', 'Font', 'FontSize' ] },
                        { name: 'colors', items: [ 'TextColor', 'BGColor' ] },
                        { name: 'tools', items: [ 'Maximize', 'ShowBlocks' ] },
                        { name: 'others', items: [ '-' ] }
                    ]
                };

                $scope.save = function () {
                    var tags = [];
                    console.log($scope.newOrderForm.tags);
                    var tags_old = $scope.newOrderForm.tags;
                    $scope.newOrderForm.tags.forEach(function (item) {
                        tags.push(item.text);
                    });
                    $scope.newOrderForm.tags = tags.toString();
                    console.log($scope.newOrderForm.tags);
                    OrderService.update($scope.newOrderForm).then(function (result) {
                        $scope.newOrderForm.tags = tags_old;
                        PNotify.desktop.permission();
                        (new PNotify({
                            title: 'Статус сохранения',
                            text: 'Изменения успешно сохранены.',
                            desktop: {
                                desktop: true
                            }
                        })).get().click(function (e) {
                                if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                            });
                    });
                }
            }
        })
        .state('panel.orders.create', {
            url: '/create',
            templateUrl: '/orders/create',
            controller: function ($scope, OrderService, $state) {

                $scope.title = 'Новый приказ';

                $scope.tags = [
                    { text: 'Tag1' },
                    { text: 'Tag2' },
                    { text: 'Tag3' }
                ];
                $scope.content = '';

                $scope.loadTags = function (query) {
                    return OrderService.tagsList(query);
                };

                $scope.order_categories = [
                    {id: 1, name: 'Личный состав'},
                    {id: 2, name: 'Основной состав'}
                ];

                $scope.options = {
                    language: 'ru',
                    allowedContent: true,
                    entities: false,
                    toolbar: [
                        { name: 'document', groups: [ 'mode', 'document', 'doctools' ], items: [ 'Source', '-', 'Save', 'NewPage', 'Preview', 'Print', '-', 'Templates' ] },
                        { name: 'clipboard', groups: [ 'clipboard', 'undo' ], items: [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ] },
                        { name: 'editing', groups: [ 'find', 'selection', 'spellchecker' ], items: [ 'Find', 'Replace', '-', 'SelectAll', '-'] },
//                        { name: 'forms', items: [ 'Form', 'Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', 'Button', 'ImageButton', 'HiddenField' ] },
//                        '/',
                        { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ], items: [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'RemoveFormat' ] },
                        { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ], items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', 'CreateDiv', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'BidiLtr', 'BidiRtl', 'Language' ] },
//                        { name: 'links', items: [ 'Link', 'Unlink', 'Anchor' ] },
                        { name: 'insert', items: [ 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar', 'PageBreak' ] },
//                        '/',
                        { name: 'styles', items: [ 'Styles', 'Format', 'Font', 'FontSize' ] },
                        { name: 'colors', items: [ 'TextColor', 'BGColor' ] },
                        { name: 'tools', items: [ 'Maximize', 'ShowBlocks' ] },
                        { name: 'others', items: [ '-' ] }
                    ]
                };

                $scope.newOrderForm = {
                    id: 0,
                    content: null,
                    name: null,
                    date_of_order: null,
                    tags: null,
                    nomer: null,
                    order_category: null
                }

                $scope.save = function () {
                    var tags = [];
                    $scope.newOrderForm.tags.forEach(function (item) {
                        tags.push(item.text);
                    });
                    $scope.newOrderForm.tags = tags.toString();
                    OrderService.save($scope.newOrderForm).then(function (result) {

                        PNotify.desktop.permission();
                        (new PNotify({
                            title: 'Статус сохранения',
                            text: 'Новая запись успешно сохранена.',
                            desktop: {
                                desktop: true
                            }
                        })).get().click(function (e) {
                                if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                            });
                    });
                }
            }
        })
        .state('panel.employees', {
            url: '/employees',
            abstract: true,
            template: '<div ui-view></div>'
        })
        .state('panel.employees.create', {
            url: '/create',
            templateUrl: '/employees/create',
            resolve: {
                nationalitiesData: function (NationalityService) {
                    return NationalityService.list();
                },
                relationshipStatusesData: function (RelationshipStatusService) {
                    return RelationshipStatusService.list();
                }
            },
            controller: 'EmployeeCreateController'
        })
        .state('panel.employees.list', {
            url: '/list',
            templateUrl: '/employees/list',
            resolve: {
                employeesData: function (EmployeeService) {
                    return EmployeeService.list();
                },
                structuresData: function (StructureService) {
                    return StructureService.list();
                }
            },
            controller: 'EmployeeController'
        })
        .state('panel.employees.detail', {
            url: '/{employeeId:[0-9]{1,6}}',
            templateUrl: '/employees/show',
            resolve: {
                activeEmployeeData: function (EmployeeService, $stateParams) {
                    return EmployeeService.get($stateParams.employeeId);
                },
                nationalitiesData: function (NationalityService) {
                    return NationalityService.list();
                },
                relationshipStatusesData: function (RelationshipStatusService) {
                    return RelationshipStatusService.list();
                }
            },
            controller: function ($scope, EmployeeService, activeEmployeeData, $upload, $state, $modal, $filter, nationalitiesData, relationshipStatusesData) {
                $scope.activeEmployee = activeEmployeeData;
                $scope.relationshipStatuses = relationshipStatusesData;
                $scope.nationalities = nationalitiesData;

                $scope.newEmployeeForm = activeEmployeeData;
                $scope.newEmployeeForm.birthday = $filter("date")(activeEmployeeData.birthday, 'yyyy-MM-dd');
                $scope.newEmployeeForm.created_at = $filter("date")(activeEmployeeData.created_at, 'yyyy-MM-dd');
                $scope.newEmployeeForm.updated_at = $filter("date")(activeEmployeeData.updated_at, 'yyyy-MM-dd');

                $scope.editMode = false;

                $scope.updateEmployee = function () {
                    EmployeeService.update($scope.newEmployeeForm).then(function (result) {

                        PNotify.desktop.permission();
                        (new PNotify({
                            title: 'Статус изменений',
                            text: 'Изменения успешно сохранены.',
                            desktop: {
                                desktop: true
                            }
                        })).get().click(function (e) {
                                if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                            });
                    });
                }

                $scope.openNationalityModal = function (size) {

                    var modalInstance = $modal.open({
                        templateUrl: 'nationalityModal.html',
                        controller: 'ModalNationalityController',
                        size: size

                    });

                    modalInstance.result.then(function (result) {
                        $scope.nationalities.push(result.data);
                    }, function () {
                        $log.info('Modal dismissed at: ' + new Date());
                    });
                };

                $scope.openRelationshipStatusModal = function (size) {

                    var modalInstance = $modal.open({
                        templateUrl: 'relationshipStatusModal.html',
                        controller: 'ModalRelationshipStatusController',
                        size: size

                    });

                    modalInstance.result.then(function (result) {
                        $scope.relationshipStatuses.push(result.data);
                    }, function () {
                        $log.info('Modal dismissed at: ' + new Date());
                    });
                };

//                $scope.onFileSelect = function ($files) {
//                    //$files: an array of files selected, each file has name, size, and type.
//                    for (var i = 0; i < $files.length; i++) {
//                        var file = $files[i];
//                        $scope.upload = $upload.upload({
//                            url: '/upload', //upload.php script, node.js route, or servlet url
//                            method: 'POST',
//                            //headers: {'header-key': 'header-value'},
//                            //withCredentials: true,
//                            data: {myObj: $scope.myModelObj},
//                            file: file // or list of files ($files) for html5 only
//                            //fileName: 'doc.jpg' or ['1.jpg', '2.jpg', ...] // to modify the name of the file(s)
//                            // customize file formData name ('Content-Disposition'), server side file variable name.
//                            //fileFormDataName: myFile, //or a list of names for multiple files (html5). Default is 'file'
//                            // customize how data is added to formData. See #40#issuecomment-28612000 for sample code
//                            //formDataAppender: function(formData, key, val){}
//                        }).progress(function (evt) {
//                            console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
//                        }).success(function (data, status, headers, config) {
//                            // file is uploaded successfully
//                            console.log(data);
//                        });
//                        //.error(...)
//                        //.then(success, error, progress);
//                        // access or attach event listeners to the underlying XMLHttpRequest.
//                        //.xhr(function(xhr){xhr.upload.addEventListener(...)})
//                    }
//                    /* alternative way of uploading, send the file binary with the file's content-type.
//                     Could be used to upload files to CouchDB, imgur, etc... html5 FileReader is needed.
//                     It could also be used to monitor the progress of a normal http post/put request with large data*/
//                    // $scope.upload = $upload.http({...})  see 88#issuecomment-31366487 for sample code.
//                };
            }
        })
        .state('panel.employees.detail.relationship', {
            absract: true,
            url: '/relationship',
            template: '<div ui-view></div>'
        })
        .state('panel.employees.detail.relationship.list', {
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
        .state('panel.employees.detail.relationship.type', {
            abstract: true,
            url: '/create',
            template: '<div ui-view></div>'
        })
        .state('panel.employees.detail.relationship.type.create', {
            url: '/create',
            templateUrl: '/relationship_types/create'
        })
        .state('panel.relationship_types', {
            abstract: true,
            url: '/relationship_types',
            template: '<div ui-view></div>'
        })
        .state('panel.relationship_types.list', {
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
        .state('panel.relationship_types.create', {
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
        .state('panel.employees.detail.contract', {
            absract: true,
            url: '/contract',
            template: '<div ui-view></div>'
        })
        .state('panel.employees.detail.contract.list', {
            url: '/list',
            templateUrl: '/contracts/list',
            resolve: {
                contractTypeData: function (ContractTypeService) {
                    return ContractTypeService.list();
                },
                structuresData: function (StructureService) {
                    return StructureService.list();
                },
                contractsData: function (ContractService, activeEmployeeData) {
                    return ContractService.employee_contracts(activeEmployeeData.id);
                }
            },
            controller: function ($scope, contractsData, structuresData, contractTypeData) {
                $scope.contracts = contractsData;
                $scope.contract_types = contractTypeData;

                function getStructureFullPath(parent_id) {
                    if (parent_id != null) {
                        for (var i = 0; i < structuresData.length; i++) {
                            if (structuresData[i].id == parent_id) {
                                return path = getStructureFullPath(structuresData[i].parent_id)
                                    + structuresData[i].name + ' > ';
                                break;
                            }
                        }
                    }
                    return '';
                }

                $scope.getStructureFullPath = function (parent_id) {
                    var path = '';
                    path = getStructureFullPath(parent_id);
                    return path.substr(0, path.length - 3);
                }

                $scope.getOffice = function (positionId) {
                    var department = $scope.getDepartment(positionId);
                    if (department != null) {
                        for (var i = 0; i < structuresData.length; i++) {
                            if (structuresData[i].id == department.parent_id) {
                                return structuresData[i];
                            }
                        }
                    }
                    return null;
                }

                $scope.getDepartment = function (positionId) {
                    var position = $scope.getPosition(positionId);
                    if (position != null) {
                        for (var i = 0; i < structuresData.length; i++) {
                            if (structuresData[i].id == position.parent_id) {
                                return structuresData[i];
                            }
                        }
                    }
                    return null;
                }

                $scope.getPosition = function (positionId) {
                    for (var i = 0; i < structuresData.length; i++) {
                        if (structuresData[i].id == positionId) {
                            return structuresData[i];
                        }
                    }
                    return null;
                }

                $scope.getStatusText = function (status) {
                    return status == 1 ? "<span class='label label-success'>Активен</span>" : "<span class='label label-warning'>Окончен</span>";
                }
            }
        })
        .state('panel.employees.detail.contract.create', {
            url: '/create',
            templateUrl: '/contracts/create',
            resolve: {
                contractTypeData: function (ContractTypeService, activeEmployeeData) {
                    return ContractTypeService.list();
                },
                structuresData: function (StructureService) {
                    return StructureService.list();
                }
            },
            controller: function ($scope, FunctionsService, structuresData, contractTypeData, ContractService, $modal) {
                $scope.contract_types = contractTypeData;

                $scope.working_time_types = [
                    {id: 1, name: "Понедельник-Пятница"},
                    {id: 2, name: "Понедельник-Суббота"}
                ];

                $scope.saveContract = function () {
                    $scope.newContractForm.status = 1;
                    ContractService.save($scope.newContractForm);
                    $scope.newContractForm = {};
                }

                $scope.newContractForm = {
                    id: 0,
                    employee_id: $scope.activeEmployee.id,
                    position_id: null,
                    trial_period_open: null,
                    trial_period_end: null,
                    salary: null,
                    working_time_type: null,
                    open_date: null,
                    end_date: null,
                    close_date: null,
                    status: 1,
                    contract_type: null
                };

                $scope.temp = {
                    position: 'Выберите должность...'
                };

                $scope.open = function (size) {

                    var modalInstance = $modal.open({
                        templateUrl: 'myModalContent.html',
                        resolve: {
                            structuresData: function (StructureService) {
                                return StructureService.freepositions();
                            }
                        },
                        controller: 'ModalStructuresCtrl',
                        size: size
                    });

                    modalInstance.result.then(function (result) {
                        $scope.temp.position = result.name;
                        $scope.newContractForm.position_id = result.id;
                    }, function () {
                        console.info('Modal dismissed at: ' + new Date());
                    });
                }
            }
        })
        .state('panel.positions', {
            abstract: true,
            url: '/positions',
            template: '<div ui-view  class="anim-in-out"></div>'
        })
        .state('panel.positions.list', {
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
        .state('panel.positions.create', {
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
        .state('panel.position_categories', {
            abstract: true,
            url: '/position_categories',
            template: '<div ui-view></div>'
        })
        .state('panel.position_categories.list', {
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
        .state('panel.position_categories.create', {
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
        .state('panel.structure_types', {
            abstract: true,
            url: '/office_types',
            template: '<div ui-view></div>'
        })
        .state('panel.structure_types.list', {
            url: '/list',
            templateUrl: '/structure_types/list',
            resolve: {
                structureTypesData: function (StructureTypeService) {
                    return StructureTypeService.list();
                }
            },
            controller: function ($scope, structureTypesData) {
                $scope.structure_types = structureTypesData;
            }
        })
        .state('panel.structure_types.create', {
            url: '/create',
            templateUrl: '/structure_types/create',
            controller: function ($scope, StructureTypeService) {
                $scope.saveStructureType = function () {
                    var data = {
                        id: 0,
                        name: $scope.newStructureTypeForm.name,
                        has_children: Boolean($scope.newStructureTypeForm.has_children)
                    };

                    StructureTypeService.save(data);
                    $scope.newStructureTypeForm = {};
                }
            }
        })
        .state('panel.offices', {
            abstract: true,
            url: '/offices',
            template: '<div ui-view></div>'
        })
        .state('panel.offices.list', {
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
        .state('panel.offices.create', {
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
        .state('panel.departments', {
            abstract: true,
            url: '/departments',
            template: '<div ui-view></div>'
        })
        .state('panel.departments.list', {
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
        .state('panel.departments.create', {
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
        .state('panel.structures', {
            abstract: true,
            url: '/structures',
            template: '<div ui-view></div>'
        })
        .state('panel.structures.list', {
            url: '/list',
            templateUrl: '/structures/list',
            resolve: {
                structuresData: function (StructureService) {
                    return StructureService.list();
                },
                structureTypesData: function (StructureTypeService) {
                    return StructureTypeService.list();
                },
                positionTypesData: function (PositionCategoryService) {
                    return PositionCategoryService.list();
                }
            },
            controller: 'StructureCtrl'
        })
        .state('panel.contract_types', {
            abstract: true,
            url: '/contract_types',
            template: '<div ui-view></div>'
        })
        .state('panel.contract_types.list', {
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
        .state('panel.contract_types.create', {
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
        .state('panel.institutions', {
            abstract: true,
            url: '/institutions',
            template: '<div ui-view></div>'
        })
        .state('panel.institutions.list', {
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
        .state('panel.institutions.create', {
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
        .state('panel.employees.detail.educations', {
            absract: true,
            url: '/educations',
            template: '<div ui-view></div>'
        })
        .state('panel.employees.detail.educations.list', {
            url: '/list',
            templateUrl: '/educations/list',
            resolve: {
                institutionsData: function (InstitutionService) {
                    return InstitutionService.list();
                },
                qualificationsData: function (QualificationService) {
                    return QualificationService.list();
                },
                educationsData: function (activeEmployeeData, EducationService) {
                    return EducationService.list(activeEmployeeData.id);
                }
            },
            controller: 'EducationController'
        })
        .state('panel.employees.detail.passports', {
            absract: true,
            url: '/passports',
            template: '<div ui-view></div>'
        })
        .state('panel.employees.detail.passports.show', {
            url: '/show',
            templateUrl: '/passports/show',
            resolve: {
                passportData: function (PassportService, activeEmployeeData) {
                    return PassportService.getEmployeePassport(activeEmployeeData.id);
                }
            },
            controller: 'PassportController'
        })
        .state('panel.employees.detail.contact_info', {
            absract: true,
            url: '/contact',
            template: '<div ui-view></div>'
        })
        .state('panel.employees.detail.contact_info.show', {
            url: '/show',
            templateUrl: '/contact_informations/show',
            resolve: {
                contactInformationData: function (ContactInformationService, activeEmployeeData) {
                    return ContactInformationService.getEmployeeContactInformation(activeEmployeeData.id);
                }
            },
            controller: 'ContactCtrl'
        })
        .state('panel.employees.detail.military', {
            absract: true,
            url: '/militaries',
            template: '<div ui-view></div>'
        })
        .state('panel.employees.detail.military.show', {
            url: '/show',
            templateUrl: '/militaries/show',
            resolve: {
                militaryData: function (MilitaryService, activeEmployeeData) {
                    return MilitaryService.getEmployeeMilitary(activeEmployeeData.id);
                }
            },
            controller: 'MilitaryController'
        })
        .state('panel.employees.detail.seminar', {
            absract: true,
            url: '/seminars',
            template: '<div ui-view></div>'
        })
        .state('panel.employees.detail.seminar.list', {
            url: '/list',
            templateUrl: '/seminars/list',
            resolve: {
                seminarsData: function (SeminarService, activeEmployeeData) {
                    return SeminarService.getEmployeeSeminars(activeEmployeeData.id);
                }
            },
            controller: 'SeminarController'
        })
        .state('panel.calendar_types', {
            abstract: true,
            url: '/calendar_types',
            template: '<div ui-view></div>'
        })
        .state('panel.calendar_types.list', {
            url: '/list',
            templateUrl: '/calendar_types/show',
            resolve: {
                calendarTypesData: function (CalendarTypeService) {
                    return CalendarTypeService.list();
                },
                dayTypesData: function (DayTypeService) {
                    return DayTypeService.list();
                }
            },
            controller: 'CalendarTypeCtrl'
        });

    $urlRouterProvider.otherwise("/employees/list");

}).run(function ($rootScope, $state, $log) {
    $rootScope.$state = $state;
//    $rootScope.$on('$stateChangeError', function (ev, current, previous, rejection) {
////        if (rejection && rejection.needsAuthentication === true) {
//        if (rejection) {
//            var returnUrl = $location.url();
//            $log.log('returnUrl=' + returnUrl);
//            console.log('returnUrl=' + returnUrl);
//        }
//    });
    $rootScope.$on("$stateChangeError", function (event, toState, toParams, fromState, fromParams) {
        console.log("You are not authenticated.");
//        $state.go("login");
    });
    $rootScope.$on("$stateNotFound", function (event, toState, toParams, fromState, fromParams) {
        console.log("Page not found.");
    });
});

