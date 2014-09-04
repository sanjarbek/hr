angular.module('app').controller('EmployeeController', function ($scope, EmployeeService, RelationshipService) {

    $scope.delete = function (id) {
        EmployeeService.delete(id);
        if ($scope.newEmployee.id == id) $scope.newEmployee = {};
    }

    $scope.edit = function (id) {
        $scope.newEmployee = angular.copy(EmployeeService.get(id));
    }

})
