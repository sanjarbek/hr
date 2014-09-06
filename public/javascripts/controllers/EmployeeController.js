angular.module('app').controller('EmployeeController', function ($scope, EmployeeService, ngTableParams, employeesData) {

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
