angular.module('app').controller('CalendarTypeCtrl', function ($scope, $filter, calendarTypesData, CalendarTypeService, $modal, $log) {
    $scope.calendarTypeList = calendarTypesData;

    $scope.resetCalendarTypeForm = function () {
        $scope.newCalendarTypeForm = {
            id: 0,
            name: null,
            created_at: '2011-01-01 00:00:00',
            updated_at: '2011-01-01 00:00:00'
        }
    }

    $scope.resetCalendarTypeForm();

    $scope.saveCalendarType = function () {
        $scope.newCalendarTypeForm.id = 0;

        CalendarTypeService.save($scope.newCalendarTypeForm).then(function (result) {
            $scope.calendarTypeList.push(result.data);
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

        $scope.resetCalendarTypeForm();
    }
});


