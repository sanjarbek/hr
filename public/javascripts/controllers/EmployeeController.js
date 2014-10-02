angular.module('app').controller('EmployeeController', function ($scope, EmployeeService, ngTableParams, employeesData, contractsData, structuresData) {

    $scope.employees = employeesData;
    $scope.structures = structuresData;

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