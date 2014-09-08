angular.module('app').controller('EmployeeController', function ($scope, EmployeeService, ngTableParams, employeesData) {

    $scope.employees = employeesData;

    $scope.employeeTableParams = new ngTableParams({
        page: 1,            // show first page
        count: 10           // count per page
    }, {
        total: $scope.employees.length, // length of data
        getData: function ($defer, params) {
            $defer.resolve($scope.employees.slice((params.page() - 1) * params.count(), params.page() * params.count()));
        }
    });

})
