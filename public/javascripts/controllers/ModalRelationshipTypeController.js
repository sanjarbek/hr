angular.module('app').controller('ModalRelationshipTypeController', function ($scope, $modalInstance, RelationshipTypeService) {

    $scope.newRelationshipTypeForm = {
        name: ''
    };
    $scope.saveRelationshipType = function () {
        var data = {
            id: 0,
            name: $scope.newRelationshipTypeForm.name
        };

        $scope.ok(RelationshipTypeService.save(data));

        $scope.newRelationshipTypeForm = {};
    }

    $scope.ok = function (result) {
        console.log(result);
        $modalInstance.close(result);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
