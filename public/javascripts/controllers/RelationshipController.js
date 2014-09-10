angular.module('app').controller('RelationshipController', function ($scope, $filter, relationshipsData, ngTableParams, relationshipTypesData, RelationshipService, $modal, $log) {

    $scope.relationships = relationshipsData;
    $scope.types = relationshipTypesData;

    $scope.relationshipTableParams = new ngTableParams({
        page: 2,            // show first page
        count: 10           // count per page
    }, {
        total: $scope.relationships.length, // length of data
        getData: function ($defer, params) {
            $defer.resolve($scope.relationships.slice((params.page() - 1) * params.count(), params.page() * params.count()));
        }
    });

    $scope.saveRelationship = function () {

        $scope.relationshipForm.id = 0;
        $scope.relationshipForm.employee_id = $scope.activeEmployee.id;

        RelationshipService.save($scope.relationshipForm).then(function (result) {
            $scope.relationships.push(result.data);
            PNotify.desktop.permission();
            (new PNotify({
                title: 'Статус сохранения',
                text: 'Новая запись успешно сохранена.',
                desktop: {
                    desktop: true
                }
            })).get().click(function (e) {
                    if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                    alert('Hey! You clicked the desktop notification!');
                });
        });

        $scope.relationshipForm = {};
    };

    $scope.isCollapsed = true;


    $scope.resetRelationship = function () {
        $scope.selectAction = $scope.saveRelationship;
        $scope.isCollapsed = false;
        $scope.relationshipForm = {
            id: null,
            employee_id: null,
            degree: null,
            surname: null,
            firstname: null,
            lastname: null,
            birthday: null
        };
    }

    $scope.selectRelationship = function (relationship) {
        $scope.selectAction = $scope.updateRelationship;
        $scope.relationshipForm = relationship;
        $scope.relationshipForm.birthday = $filter("date")(relationship.birthday, 'yyyy-MM-dd');
        $scope.isCollapsed = false;
    };

    $scope.updateRelationship = function () {

        RelationshipService.update($scope.relationshipForm).then(function (result) {

            for (i = 0; i < $scope.relationships.length; i++) {
                if ($scope.relationships[i].id == $scope.relationshipForm.id)
                    $scope.relationships[i] = $scope.relationshipForm;
            }

            PNotify.desktop.permission();
            (new PNotify({
                title: 'Статус изменений',
                text: 'Изменения успешно сохранены.',
                desktop: {
                    desktop: true
                }
            })).get().click(function (e) {
                    if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                    alert('Hey! You clicked the desktop notification!');
                });
        });

        $scope.newRelationshipForm = {};
    }

    $scope.deleteRelationship = function (relationship) {
        var result = confirm("Вы действительно хотите удалить данную запись?");
        if (result) {
            RelationshipService.delete(relationship).then(function (result) {
                for (i = 0; i < $scope.relationships.length; i++) {
                    if ($scope.relationships[i].id == relationship.id)
                        $scope.relationships.splice(i, 1);
                }
                PNotify.desktop.permission();
                (new PNotify({
                    title: 'Статус удаления',
                    text: 'Удаление успешно выполнен.',
                    desktop: {
                        desktop: true
                    }
                })).get().click(function (e) {
                        if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                        alert('Hey! You clicked the desktop notification!');
                    });
            });
        }
    }

    $scope.open = function (size) {

        var modalInstance = $modal.open({
            templateUrl: 'myModalContent.html',
            controller: 'ModalRelationshipTypeController',
            size: size
        });

        modalInstance.result.then(function (result) {
            $scope.types.push(result.data);
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };

})
