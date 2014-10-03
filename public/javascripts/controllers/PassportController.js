angular.module('app').controller('PassportController', function ($scope, $filter, passportData, PassportService, $modal, $log) {

    if (passportData != "null") {
        $scope.passportForm = passportData;
        $scope.passportForm.open_date = $filter("date")(passportData.open_date, 'yyyy-MM-dd');
        $scope.passportForm.end_date = $filter("date")(passportData.end_date, 'yyyy-MM-dd');
        $scope.isNew = false;
    } else {
        $scope.passportForm = {
            id: null,
            employee_id: null,
            serial: null,
            number: null,
            organ: null,
            open_date: null,
            end_date: null
        };
        $scope.isNew = true;
    }

    $scope.editMode = true;

    $scope.resetPassport = function () {
        $scope.selectAction = $scope.savePassport;
        $scope.isCollapsed = false;
        $scope.passportForm = {
            id: null,
            employee_id: null,
            serial: null,
            number: null,
            organ: null,
            open_date: null,
            end_date: null
        };
    }

    $scope.savePassport = function () {

        if ($scope.isNew) {
            $scope.passportForm.id = 0;
            $scope.passportForm.employee_id = $scope.activeEmployee.id;

            PassportService.save($scope.passportForm).then(function (result) {

                $scope.isNew = false;
                $scope.passportForm.id = result.data.id;

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
            PassportService.update($scope.passportForm).then(function (result) {

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

})
