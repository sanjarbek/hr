angular.module('app').controller('ModalStructuresCtrl', function ($scope, $modalInstance, FunctionsService, structuresData) {

    $scope.dataTheTree = FunctionsService.getTree(structuresData, 'id', 'parent_id');

    $scope.treeOptions = {
        nodeChildren: "children",
        dirSelectable: true,
        isLeaf: function (node) {
            return node.structure_type == 4;
        },
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

    $scope.selectNode = function (position) {
        $scope.position = position;
    };

    $scope.ok = function (result) {
        if ($scope.position.structure_type == 4)
            $modalInstance.close($scope.position);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('app').controller('ModalStructuresController', function ($scope, $modalInstance, FunctionsService, structuresData) {

    $scope.dataTheTree = FunctionsService.getTree(structuresData, 'id', 'parent_id');

    $scope.treeOptions = {
        nodeChildren: "children",
        dirSelectable: true,
        isLeaf: function (node) {
            return node.structure_type == 4;
        },
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

    $scope.selectNode = function (position) {
        $scope.position = position;
    };

    $scope.ok = function (result) {
        if ($scope.position.structure_type != 4)
            $modalInstance.close($scope.position);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
