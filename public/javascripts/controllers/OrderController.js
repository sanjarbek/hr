angular.module('app').controller('ModalLeavingReasonController', function ($scope, $modalInstance, LeavingReasonService) {

    $scope.newLeavingReasonForm = {
        id: 0,
        punkt: null,
        name: null,
        created_at: '2014-01-01T00:00:00',
        updated_at: '2014-01-01T00:00:00'
    };

    $scope.saveLeavingReason = function () {
        $scope.ok(LeavingReasonService.save($scope.newLeavingReasonForm));
    }

    $scope.ok = function (result) {
        $modalInstance.close(result);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
