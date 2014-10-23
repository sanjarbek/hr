angular.module('app').controller('StructureCtrl', function ($scope, structuresData, StructureService, FunctionsService, structureTypesData, positionTypesData) {
    $scope.dataForTheTree = FunctionsService.getTree(structuresData, 'id', 'parent_id');
    $scope.position_types = positionTypesData;
    $scope.structure_types = structureTypesData;

    $scope.statuses = [
        {id: 1, name: 'Активна'},
        {id: 2, name: 'Не активна'}
    ];

    $scope.addChild = function (node) {
        $scope.clearForm();
        $scope.parentStructure = node;
        $scope.actionMode = $scope.saveStructure;
    }

    $scope.editNode = function (node, $parentNode) {
        $scope.parentStructure = $parentNode;
        $scope.newStructureForm = node;
        $scope.actionMode = $scope.updateStructure;
    }

    $scope.createParent = function () {
        $scope.parentStructure = null;
        $scope.clearForm();
        $scope.actionMode = $scope.saveStructure;
    }

    $scope.parentStructure = null;

    $scope.clearForm = function () {
        $scope.newStructureForm = {
            id: 0,
            parent_id: null,
            name: null,
            structure_type: null,
            position_type: null,
            status: null
        }
    }

    $scope.saveStructure = function () {
        $scope.newStructureForm.id = 0;
        if ($scope.parentStructure)
            $scope.newStructureForm.parent_id = $scope.parentStructure.id;

        $scope.newStructureForm.position_type = ($scope.newStructureForm.position_type == undefined)
                ? null
            : $scope.newStructureForm.position_type;

        StructureService.save($scope.newStructureForm).then(function (result) {
            if ($scope.parentStructure != null) {
                if (!$scope.parentStructure.hasOwnProperty('children')) {
                    $scope.parentStructure.children = [];
                    $scope.parentStructure.children.push(result.data);
                }
            }
        });

        $scope.clearForm();
    }

    $scope.updateStructure = function () {
        $scope.newStructureForm.position_type = ($scope.newStructureForm.position_type == undefined)
            ? null
            : $scope.newStructureForm.position_type;

        StructureService.update($scope.newStructureForm).then(function (result) {
            console.log("Updated");
        });

        $scope.clearForm();
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