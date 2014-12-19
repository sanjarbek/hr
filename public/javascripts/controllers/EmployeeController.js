angular.module('app').controller('EmployeeController', function ($scope, EmployeeService, ngTableParams, employeesData, contractsData, structuresData) {

    $scope.employees = employeesData;
    $scope.structures = structuresData;

    $scope.itemsByPage = 15;

    $scope.search = false;

    $scope.showSearch = function () {
        $scope.search = !$scope.search;
    }

    $scope.searchedOffice = {id: 1, name: 'test'};

    $scope.contracts = contractsData;

    $scope.officeChildrens = [];

    $scope.preSearchByOfficeId = function () {
        $scope.officeChildrens = [];
        if (isNaN($scope.searchedStructureId) || $scope.searchedStructureId == null)
            return $scope.officeChildrens;

        var getChildren = function (searchedOfficeId) {
            for (var i = 0; i < $scope.structures.length; i++) {
                var item = $scope.structures[i];
                if (item.parent_id == searchedOfficeId) {
                    $scope.officeChildrens.push(item.id);
                    getChildren(item.id);
                }
            }
        }
        getChildren($scope.searchedStructureId);
        $scope.officeChildrens.push($scope.searchedStructureId);
    }

    $scope.searchByOfficeId = function (employee) {
        if (isNaN($scope.searchedStructureId) || $scope.searchedStructureId == null)
            return true;

        for (var i = 0; i < $scope.contracts.length; i++) {
            var contract = $scope.contracts[i];
            if (employee.id == contract.employee_id) {
                for (var j = 0; j < $scope.officeChildrens.length; j++)
                    if ($scope.officeChildrens[j] == contract.position_id)
                        return true;
            }
        }
        return false;
    }

    $scope.employeeTableParams = new ngTableParams({
        page: 1,            // show first page
        count: 10           // count per page
    }, {
        total: $scope.employees.length, // length of data
        getData: function ($defer, params) {
            $defer.resolve($scope.employees.slice((params.page() - 1) * params.count(), params.page() * params.count()));
        }
    });

});

angular.module('app').directive('stringToTimestamp', function () {
    return {
        require: 'ngModel',
        link: function (scope, ele, attr, ngModel) {
            ngModel.$parsers.push(function (value) {
                var test = Date.parse(new Date(Date.parse(value) - (6 * 3600 * 1000)).toUTCString());
                if (isNaN(test)) {
                    return '';
                }
                return test;
            })
        }
    }
});

angular.module('app').controller('EmployeeCreateController', function ($scope, $modal, $log, EmployeeService, nationalitiesData, relationshipStatusesData) {

    $scope.relationshipStatuses = relationshipStatusesData;
    $scope.nationalities = nationalitiesData;

    $scope.newEmployeeForm = {
        id: 0,
        surname: null,
        firstname: null,
        lastname: null,
        birthday: null,
        citizenship: null,
        insurance_number: null,
        tax_number: null,
        sex: true,
        relationshipStatus: null,
        nationalityId: null,
        employmentOrderId: null,
        created_at: '2011-01-01 00:00:00',
        updated_at: '2011-01-01 00:00:00'
    };

    $scope.saveEmployee = function () {
        EmployeeService.save($scope.newEmployeeForm);
        $scope.newEmployeeForm = {
            id: 0,
            surname: null,
            firstname: null,
            lastname: null,
            birthday: null,
            citizenship: null,
            insurance_number: null,
            tax_number: null,
            sex: true,
            relationshipStatus: null,
            nationalityId: null,
            employmentOrderId: null,
            created_at: '2011-01-01 00:00:00',
            updated_at: '2011-01-01 00:00:00'
        };
    };

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
});

angular.module('app').controller('ModalRelationshipStatusController', function ($scope, $modalInstance, RelationshipStatusService) {

    $scope.newRelationshipStatusForm = {
        id: 0,
        name: null,
        created_at: '2014-01-01 00:00:00',
        updated_at: '2014-01-01 00:00:00'
    };

    $scope.saveRelationshipStatus = function () {

        $scope.ok(RelationshipStatusService.save($scope.newRelationshipStatusForm));

        $scope.newRelationshipStatusForm = {
            id: 0,
            name: null,
            created_at: '2014-01-01 00:00:00',
            updated_at: '2014-01-01 00:00:00'
        };
    }

    $scope.ok = function (result) {
        $modalInstance.close(result);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('app').controller('ModalNationalityController', function ($scope, $modalInstance, NationalityService) {

    $scope.newNationalityForm = {
        id: 0,
        name: null,
        created_at: '2014-01-01 00:00:00',
        updated_at: '2014-01-01 00:00:00'
    };

    $scope.saveNationality = function () {

        $scope.ok(NationalityService.save($scope.newNationalityForm));

        $scope.newNationalityForm = {
            id: 0,
            name: null,
            created_at: '2014-01-01 00:00:00',
            updated_at: '2014-01-01 00:00:00'
        };
    }

    $scope.ok = function (result) {
        $modalInstance.close(result);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
