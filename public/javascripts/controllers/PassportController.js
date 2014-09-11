angular.module('app').controller('PassportController', function ($scope, $filter, passportsData, PassportService, $modal, $log) {

    $scope.passports = passportsData;

    $scope.isCollapsed = true;

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

        $scope.passportForm.id = 0;
        $scope.passportForm.employee_id = $scope.activeEmployee.id;

        PassportService.save($scope.passportForm).then(function (result) {
            $scope.passports.push(result.data);
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

        $scope.passportForm = {};
    };

    $scope.selectPassport = function (passport) {
        $scope.selectAction = $scope.updatePassport;
        $scope.passportForm = passport;
        $scope.passportForm.open_date = $filter("date")(passport.open_date, 'yyyy-MM-dd');
        $scope.passportForm.end_date = $filter("date")(passport.end_date, 'yyyy-MM-dd');
        $scope.isCollapsed = false;
    };

    $scope.updatePassport = function () {

        PassportService.update($scope.passportForm).then(function (result) {

            for (i = 0; i < $scope.passports.length; i++) {
                if ($scope.passports[i].id == $scope.passportForm.id)
                    $scope.passports[i] = $scope.passportForm;
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

    $scope.deletePassport = function (passport) {
        var result = confirm("Вы действительно хотите удалить данную запись?");
        if (result) {
            PassportService.delete(passport).then(function (result) {
                for (i = 0; i < $scope.passports.length; i++) {
                    if ($scope.passports[i].id == passport.id)
                        $scope.passports.splice(i, 1);
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
