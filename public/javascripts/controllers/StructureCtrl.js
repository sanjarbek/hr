angular.module('app').controller('StructureCtrl', function ($scope, $modal, $log, structuresData, StructureService, FunctionsService, structureTypesData, positionTypesData) {
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
            fullname: null,
            salary: 0,
            coefficient: 0,
            structure_type: null,
            positionHistoryId: null,
            position_type: null,
            status: null
        }
    }

    $scope.saveStructure = function () {
        console.log($scope.newStructureForm);

        $scope.newStructureForm.id = 0;
        $scope.newStructureForm.employment_order_id = null;

        if ($scope.parentStructure != null)
            $scope.newStructureForm.parent_id = $scope.parentStructure.id;
        else
            $scope.newStructureForm.parent_id = null;

        $scope.newStructureForm.position_type = ($scope.newStructureForm.position_type == undefined)
            ? null
            : $scope.newStructureForm.position_type;

        StructureService.save($scope.newStructureForm).then(function (result) {
            if ($scope.parentStructure != null) {
                if (!$scope.parentStructure.hasOwnProperty('children')) {
                    $scope.parentStructure.children = [];
                }
                $scope.parentStructure.children.push(result.data);
            }
            else {
                $scope.dataForTheTree.push(result.data);
            }

            PNotify.desktop.permission();
            (new PNotify({
                title: 'Статус сохранения',
                text: 'Новая запись успешно сохранена.',
                desktop: {
                    desktop: true
                }
            })).get().click(function (e) {
                    if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                });
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

    $scope.openPositionTypeModal = function (size) {

        var modalInstance = $modal.open({
            templateUrl: 'positionTypeModalContent.html',
            controller: 'ModalPositionTypeController',
            size: size
        });

        modalInstance.result.then(function (result) {
            console.log(result.data);
            $scope.position_types.push(result.data);
        }, function () {
            console.info('Modal dismissed at: ' + new Date());
        });
    }

});


angular.module('app').controller('ModalPositionTypeController', function ($scope, $modalInstance, PositionCategoryService) {

    $scope.newPositionTypeForm = {
        id: 0,
        name: null
    };

    $scope.savePositionType = function () {
        $scope.ok(PositionCategoryService.save($scope.newPositionTypeForm));
    }

    $scope.ok = function (result) {
        $modalInstance.close(result);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
