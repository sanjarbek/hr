angular.module('app').controller('MilitaryController', function ($scope, $filter, militaryData, MilitaryService, $modal, $log) {

    if (militaryData != "null") {
        $scope.militaryForm = militaryData;
        $scope.isNew = false;
    } else {
        $scope.militaryForm = {
            id: null,
            employee_id: null,
            category: null,
            military_rank: null,
            structure: null,
            full_code: null,
            validity_category: null,
            commissariat: null,
            removal_mark: null
        };
        $scope.isNew = true;
    }

    $scope.editMode = true;

    $scope.saveMilitary = function () {

        if ($scope.isNew) {
            $scope.militaryForm.id = 0;
            $scope.militaryForm.employee_id = $scope.activeEmployee.id;

            MilitaryService.save($scope.militaryForm).then(function (result) {

                $scope.isNew = false;
                $scope.militaryForm.id = result.data.id;

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

        } else {
            MilitaryService.update($scope.militaryForm).then(function (result) {

                $scope.isNew = false;
                PNotify.desktop.permission();
                (new PNotify({
                    title: 'Статус изменений',
                    text: 'Изменения успешно сохранены.',
                    desktop: {
                        desktop: true
                    }
                })).get().click(function (e) {
                        if ($('.ui-pnotify-closer, .ui-pnotify-sticker, .ui-pnotify-closer *, .ui-pnotify-sticker *').is(e.target)) return;
                    });
            });
        }

        $scope.editMode = !$scope.editMode;
    };

});

