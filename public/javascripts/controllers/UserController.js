angular.module('app').controller('UserController', function ($scope, $filter, UserService, $modal, $log, usersData) {

    $scope.users = usersData;

    $scope.openUserCreateModal = function (size) {

        var modalInstance = $modal.open({
            templateUrl: 'userCreateModal.html',
            resolve: {
                employeesData: function (EmployeeService) {
                    return EmployeeService.list();
                }
            },
            controller: 'ModalUserCreateController',
            size: size

        });

        modalInstance.result.then(function (result) {
            $scope.users.push(result.data);
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };

});

angular.module('app').controller('ModalUserEditController', function ($scope, $modalInstance, UserService) {

    $scope.user = {
        created_at: '2014-01-01T00:00:00',
        updated_at: '2014-01-01T00:00:00'
    };

    $scope.save = function () {
        $scope.ok(UserService.save($scope.user));
    }

    $scope.ok = function (result) {
        $modalInstance.close(result);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('app').controller('ModalUserCreateController', function ($scope, $modalInstance, UserService, employeesData) {

    $scope.employees = employeesData;

    $scope.user = {
        passwordResetToken: null,
        lastActivityTime: null,
        created_at: '2014-01-01T00:00:00',
        updated_at: '2014-01-01T00:00:00'
    };

    $scope.save = function () {
        $scope.ok(UserService.save($scope.user));
    }

    $scope.ok = function (result) {
        $modalInstance.close(result);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
