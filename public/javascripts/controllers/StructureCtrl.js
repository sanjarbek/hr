angular.module('app').controller('StructureCtrl', function ($scope, structuresData, StructureService, FunctionsService, structureTypesData, positionTypesData) {
    $scope.dataForTheTree = FunctionsService.getTree(structuresData, 'id', 'parent_id');
    $scope.position_types = positionTypesData;
    $scope.structure_types = structureTypesData;

    $scope.statuses = [
        {id: 1, name: 'Активна'},
        {id: 2, name: 'Не активна'}
    ];

    $scope.selectNode = function (structure) {
        $scope.current_structure = structure;
    };

    $scope.saveStructure = function () {
        var parentId = null;
        if ($scope.current_structure)
            parentId = $scope.current_structure.id;

        var data = {
            id: 0,
            parent_id: parentId,
            name: $scope.newStructureForm.name,
            structure_type: $scope.newStructureForm.structure_type,
            position_type: ($scope.newStructureForm.position_type == undefined || Boolean(!$scope.newStructureForm.is_position))
                ? null
                : $scope.newStructureForm.position_type,
            status: $scope.newStructureForm.status
        };

        StructureService.save(data).then(function (result) {
            if (!$scope.current_structure.hasOwnProperty('children'))
                $scope.current_structure.children = [];
            $scope.current_structure.children.push(result.data);
        });
        $scope.newStructureForm = {};
    }

    $scope.hasChildren = function (structure_type) {
        for (i = 0; i < $scope.structure_types.length; i++)
            if ($scope.structure_types[i].id == structure_type)
                return $scope.structure_types[i].has_children;
        return false;
    }

    $scope.treeOptions = {
        nodeChildren: "children",
        dirSelectable: true,
        isLeaf: function (node) {
            return !$scope.hasChildren(node.structure_type);
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
});