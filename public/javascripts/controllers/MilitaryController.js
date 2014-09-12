angular.module('app').controller('MilitaryController', function ($scope, $filter, militariesData, MilitaryService, $modal, $log) {

    $scope.militaries = militariesData;

    $scope.isCollapsed = true;

    $scope.resetMilitary = function () {
        $scope.selectAction = $scope.saveMilitary;
        $scope.isCollapsed = false;
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
    }

    $scope.saveMilitary = function () {

        $scope.militaryForm.id = 0;
        $scope.militaryForm.employee_id = $scope.activeEmployee.id;

        MilitaryService.save($scope.militaryForm).then(function (result) {
            $scope.militaries.push(result.data);
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

        $scope.militaryForm = {};
    };

    $scope.selectMilitary = function (military) {
        $scope.selectAction = $scope.updateMilitary;
        $scope.militaryForm = military;
        $scope.militaryForm.open_date = $filter("date")(military.open_date, 'yyyy-MM-dd');
        $scope.militaryForm.end_date = $filter("date")(military.end_date, 'yyyy-MM-dd');
        $scope.isCollapsed = false;
    };

    $scope.updateMilitary = function () {

        MilitaryService.update($scope.militaryForm).then(function (result) {

            for (i = 0; i < $scope.militaries.length; i++) {
                if ($scope.militaries[i].id == $scope.militaryForm.id)
                    $scope.militaries[i] = $scope.militaryForm;
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
                });
        });
    }

    $scope.deleteMilitary = function (military) {
        var result = confirm("Вы действительно хотите удалить данную запись?");
        if (result) {
            MilitaryService.delete(military).then(function (result) {
                for (i = 0; i < $scope.militaries.length; i++) {
                    if ($scope.militaries[i].id == military.id)
                        $scope.militaries.splice(i, 1);
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
                    });
            });
        }
    }

})

