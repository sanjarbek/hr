angular.module('app').controller('UserController', function ($scope, $filter, UserService, $modal, $log, usersData) {

    $scope.users = usersData;

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